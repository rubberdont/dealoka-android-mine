package com.dealoka.app.receivers;

import java.util.ArrayList;
import java.util.Iterator;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.Main;
import com.dealoka.app.Redeem;
import com.dealoka.app.Wallet;
import com.dealoka.app.WalletDetails;
import com.dealoka.app.call.CallOfferRedeemWallet;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.hack.Hack;
import com.dealoka.app.model.OfferRedeemWallet;
import com.dealoka.lib.General;
import com.dealoka.lib.GeneralCalendar;
import com.dealoka.lib.Logger;
import com.dealoka.lib.manager.PhoneManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CheckBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		checkTimeClient();
		checkWallet();
		checkLACCID();
	}
	private void checkTimeClient() {
		if(GlobalVariables.time > 0) {
			if(GeneralCalendar.getUTCTimestamp() < GlobalVariables.time) {
				/*Logger.i(
						"UTC Timestamp : " + GeneralCalendar.getUTCTimestamp()
						+ " " +
						"Time : " + GlobalVariables.time);*/
				if(!Hack.isTemporaryAllExpired()) {
					Hack.setOfferRedeemWalletToExpired();
					if(Wallet.instance != null) {
						Wallet.instance.SyncAll();
					}
				}
			}else {
				GlobalVariables.time = GeneralCalendar.getUTCTimestamp();
				if(Hack.isTemporaryAllExpired()) {
					Hack.setOfferRedeemWalletToReal();
					if(Wallet.instance != null) {
						Wallet.instance.SyncAll();
					}
				}
			}
		}else {
			if(GlobalVariables.time_session == null) {
				return;
			}
			if(GlobalVariables.time_session.isSession()) {
				GlobalVariables.time = GlobalVariables.time_session.GetTime();
			}else {
				if(GlobalVariables.user_session == null) {
					return;
				}
				if(GlobalVariables.user_session.isSession()) {
					GlobalVariables.time = GeneralCalendar.getUTCTimestamp();
					GlobalVariables.time_session.Time(GlobalVariables.time);
				}
			}
		}
	}
	private void checkWallet() {
		if(CallOfferRedeemWallet.offer_redeem_wallet_db == null) {
			return;
		}
		if(CallOfferRedeemWallet.offer_redeem_wallet_db.size() <= 0) {
			return;
		}
		final ArrayList<OfferRedeemWallet> temp = new ArrayList<OfferRedeemWallet>(CallOfferRedeemWallet.offer_redeem_wallet_db);
		Iterator<OfferRedeemWallet> iterator = temp.iterator();
		while(iterator.hasNext()) {
			final OfferRedeemWallet offer_redeem_wallet = iterator.next();
			if(!offer_redeem_wallet.expired) {
				final long next_day = offer_redeem_wallet.redeem_time + (offer_redeem_wallet.day_multiplier * 60 * 60 * 24 * 1000);
				if(GeneralCalendar.getUTCTimestamp() >= offer_redeem_wallet.c_offer_rec.validity_end) {
					if(Config.log_enabled) { Logger.d("Delete Wallet because of Validity End : " + offer_redeem_wallet.id); }
					setExpired(offer_redeem_wallet);
				}else if(GeneralCalendar.getUTCTimestamp() >= next_day) {
					if(Config.log_enabled) { Logger.d("Delete Wallet because of Next Day : " + offer_redeem_wallet.id); }
					setExpired(offer_redeem_wallet);
				}
			}
		}
	}
	private void checkLACCID() {
		final ArrayList<Integer> lac_cid = PhoneManager.getLACCID(DealokaApp.getAppContext());
		if(lac_cid.size() > 0) {
			GlobalVariables.lac = lac_cid.get(0);
			GlobalVariables.cid = lac_cid.get(1);
		}
	}
	private void setExpired(OfferRedeemWallet offer_redeem_wallet) {
		GlobalVariables.query_controller.expiredWallet(offer_redeem_wallet.id);
		offer_redeem_wallet.expired = true;
		CallOfferRedeemWallet.decreaseWalletPoint();
		if(WalletDetails.instance != null) {
			WalletDetails.instance.setExpired();
		}
		if(Redeem.instance != null) {
			Redeem.instance.SyncExpired(offer_redeem_wallet.id);
		}
		General.setBadgeCount(DealokaApp.getAppContext(), CallOfferRedeemWallet.getWalletPoint(), Main.class.getName());
	}
}