package com.dealoka.app.hack;

import java.util.ArrayList;
import java.util.Iterator;

import com.dealoka.app.call.CallOfferRedeemWallet;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.model.OfferRedeemWallet;
import com.dealoka.app.model.WalletTemp;
import com.dealoka.lib.GeneralCalendar;

public class Hack {
	private static boolean temporary_all_expired = false;
	private static ArrayList<String> temporary_id;
	private static ArrayList<WalletTemp> temporary;
	public static void setOfferRedeemWalletToExpired() {
		if(CallOfferRedeemWallet.offer_redeem_wallet_db == null) {
			return;
		}
		if(temporary_all_expired) {
			return;
		}
		temporary_all_expired = true;
		temporary_id = new ArrayList<String>();
		temporary = new ArrayList<WalletTemp>();
		final Iterator<OfferRedeemWallet> iterator = CallOfferRedeemWallet.offer_redeem_wallet_db.iterator();
		while(iterator.hasNext()) {
			OfferRedeemWallet offer_redeem_wallet = iterator.next();
			GlobalVariables.query_controller.expiredWallet(offer_redeem_wallet.id);
			temporary_id.add(offer_redeem_wallet.id);
			WalletTemp wallet_temp = new WalletTemp();
			wallet_temp.Expired = offer_redeem_wallet.expired;
			wallet_temp.RedeemTime = offer_redeem_wallet.redeem_time;
			wallet_temp.DayMultiplier = offer_redeem_wallet.day_multiplier;
			wallet_temp.ValidityEnd = offer_redeem_wallet.c_offer_rec.validity_end;
			temporary.add(wallet_temp);
			offer_redeem_wallet.expired = true;
		}
	}
	public static void setOfferRedeemWalletToReal() {
		temporary_all_expired = false;
		final Iterator<OfferRedeemWallet> iterator = CallOfferRedeemWallet.offer_redeem_wallet_db.iterator();
		while(iterator.hasNext()) {
			OfferRedeemWallet offer_redeem_wallet = iterator.next();
			if(temporary_id.contains(offer_redeem_wallet.id)) {
				final int index = temporary_id.indexOf(offer_redeem_wallet.id);
				WalletTemp wallet_temp = temporary.get(index);
				offer_redeem_wallet.expired = wallet_temp.Expired;
				final long next_day = wallet_temp.RedeemTime + (wallet_temp.DayMultiplier * 60 * 60 * 24 * 1000);
				if(GeneralCalendar.getUTCTimestamp() >= wallet_temp.ValidityEnd) {
					offer_redeem_wallet.expired = true;
				}else if(GeneralCalendar.getUTCTimestamp() >= next_day) {
					offer_redeem_wallet.expired = true;
				}else {
					GlobalVariables.query_controller.unexpiredWallet(offer_redeem_wallet.id);
					offer_redeem_wallet.expired = false;
				}
			}
		}
	}
	public static void addOfferRedeemWalletExpired(
			final String id,
			final boolean expired,
			final long redeem_time,
			final long day_multiplier,
			final long validity_end) {
		if(temporary_id == null || temporary == null) {
			return;
		}
		WalletTemp wallet_temp = new WalletTemp();
		wallet_temp.Expired = expired;
		wallet_temp.RedeemTime = redeem_time;
		wallet_temp.DayMultiplier = day_multiplier;
		wallet_temp.ValidityEnd = validity_end;
		temporary_id.add(id);
		temporary.add(wallet_temp);
	}
	public static boolean isTemporaryAllExpired() {
		return temporary_all_expired;
	}
}