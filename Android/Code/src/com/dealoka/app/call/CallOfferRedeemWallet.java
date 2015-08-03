package com.dealoka.app.call;

import java.util.ArrayList;
import java.util.Iterator;

import com.dealoka.lib.General;
import com.dealoka.app.DealokaApp;
import com.dealoka.app.Home;
import com.dealoka.app.Main;
import com.dealoka.app.Wallet;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.hack.Hack;
import com.dealoka.app.model.OfferRedeemWallet;

public class CallOfferRedeemWallet {
	public static ArrayList<OfferRedeemWallet> offer_redeem_wallet_db;
	private static int wallet_point;
	public static void init() {
		CallOfferRedeemWallet.wallet_point = 0;
		CallOfferRedeemWallet.initOfferRedeemWallet();
	}
	public static boolean addOfferRedeemWallet(final String id, final String result) {
		final OfferRedeemWallet offer_redeem_wallet = new OfferRedeemWallet(id, result);
		CallOfferRedeemWallet.addOfferRedeemWallet(offer_redeem_wallet, result);
		return true;
	}
	public static boolean addOfferRedeemWallet(final String result) {
		final OfferRedeemWallet offer_redeem_wallet = new OfferRedeemWallet(result);
		if(offer_redeem_wallet.c_id == null) {
			return false;
		}
		CallOfferRedeemWallet.addOfferRedeemWallet(offer_redeem_wallet, result);
		return true;
	}
	public static boolean isOfferNotDownloaded(final String offer_id) {
		if(!(CallOfferRedeemWallet.offer_redeem_wallet_db.size() > 0)) {
			return false;
		}
		boolean not_downloaded = false;
		final Iterator<OfferRedeemWallet> iterator = CallOfferRedeemWallet.offer_redeem_wallet_db.iterator();
		while(iterator.hasNext()) {
			OfferRedeemWallet offer_redeem_wallet = iterator.next();
			if(offer_redeem_wallet.c_fk_offer_id.$value.equals(offer_id)) {
				if(!offer_redeem_wallet.validated && !offer_redeem_wallet.expired) {
					not_downloaded = true;
					break;
				}
			}
		}
		return not_downloaded;
	}
	public static int getWalletPoint() {
		return CallOfferRedeemWallet.wallet_point;
	}
	public static void setWalletPoint(final int value) {
		CallOfferRedeemWallet.wallet_point = value;
	}
	public static void increaseWalletPoint() {
		CallOfferRedeemWallet.wallet_point++;
	}
	public static void decreaseWalletPoint() {
		CallOfferRedeemWallet.wallet_point--;
	}
	public static boolean isOfferRedeemWalletDBExisted(final String id, final boolean delete) {
		boolean duplicated = false;
		Iterator<OfferRedeemWallet> iterator = CallOfferRedeemWallet.offer_redeem_wallet_db.iterator();
		while(iterator.hasNext()) {
			if(iterator.next().id.equals(id)) {
				if(delete) {
					iterator.remove();
				}
				duplicated = true;
				break;
			}
		}
		return duplicated;
	}
	public static void initOfferRedeemWallet() {
		CallOfferRedeemWallet.offer_redeem_wallet_db = GlobalVariables.query_controller.getWallet();
		if(Home.instance != null) {
			Home.instance.updateWalletPoint(CallOfferRedeemWallet.wallet_point);
		}
		General.setBadgeCount(DealokaApp.getAppContext(), CallOfferRedeemWallet.wallet_point, Main.class.getName());
	}
	private static void addOfferRedeemWallet(final OfferRedeemWallet offer_redeem_wallet, final String result) {
		if(!CallOfferRedeemWallet.isOfferRedeemWalletDBExisted(offer_redeem_wallet.id, false)) {
			CallOfferRedeemWallet.offer_redeem_wallet_db.add(0, offer_redeem_wallet);
			GlobalVariables.query_controller.addWallet(
					offer_redeem_wallet.id,
					result,
					offer_redeem_wallet.validated,
					offer_redeem_wallet.expired);
			if(!offer_redeem_wallet.validated && !offer_redeem_wallet.expired) {
				CallOfferRedeemWallet.wallet_point++;
				if(Home.instance != null) {
					Home.instance.updateWalletPoint(CallOfferRedeemWallet.wallet_point);
				}
			}
			if(Hack.isTemporaryAllExpired()) {
				Hack.addOfferRedeemWalletExpired(offer_redeem_wallet.id, offer_redeem_wallet.expired, offer_redeem_wallet.redeem_time, offer_redeem_wallet.day_multiplier, offer_redeem_wallet.c_offer_rec.validity_end);
				CallOfferRedeemWallet.offer_redeem_wallet_db.get(0).expired = true;
			}
			if(Wallet.instance != null) {
				Wallet.instance.SyncAll();
			}
			General.setBadgeCount(DealokaApp.getAppContext(), CallOfferRedeemWallet.wallet_point, Main.class.getName());
		}
	}
}