package com.dealoka.app.subs;

import com.dealoka.lib.security.SecurityMD5;
import com.dealoka.app.Home;
import com.dealoka.app.Wallet;
import com.dealoka.app.call.CallManager;
import com.dealoka.app.call.CallOfferRedeemWallet;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.model.MessagePopupDialog;
import com.dealoka.app.subs.SubsManager.SubsEnum;

public class SubsMessagePopupDialog {
	public static void subscribeMessagePopupDialog(final String user_token) {
		if(SubsManager.subs_reconnect_list.indexOf(SubsEnum.SubsMessagePopupDialog) != -1) {
			SubsManager.subs_reconnect_list.remove(SubsEnum.SubsMessagePopupDialog);
		}
		if(SubsManager.subs_controller == null) {
			return;
		}
		SubsMessagePopupDialog.resubscribeMessagePopupDialog(user_token);
	}
	public static void resubscribeMessagePopupDialog(final String user_token) {
		if(SubsManager.subs_controller.isConnected()) {
			SubsManager.subs_controller.subsMessagePopupDialog(user_token);
		}else {
			SubsManager.subs_reconnect_list.add(SubsEnum.SubsMessagePopupDialog);
		}
	}
	public static void addMessagePopDialog(final String id, final String result) {
		final MessagePopupDialog message_popup_dialog = new MessagePopupDialog(id, result);
		CallManager.callDeleteMessageDialog();
		if(SecurityMD5.get(message_popup_dialog.message_body.trim()).equals(Config.truncate_wallet_format)) {
			GlobalVariables.query_controller.truncateWallet();
			CallOfferRedeemWallet.initOfferRedeemWallet();
			if(Wallet.instance != null) {
				Wallet.instance.SyncAll();
			}
			return;
		}else if(SecurityMD5.get(message_popup_dialog.message_body.trim()).equals(Config.delete_wallet_format)) {
			GlobalVariables.query_controller.deleteWallet(message_popup_dialog.message_title);
			CallOfferRedeemWallet.initOfferRedeemWallet();
			if(Wallet.instance != null) {
				Wallet.instance.SyncAll();
			}
			return;
		}
		if(Home.instance != null) {
			if(Home.instance.isOpened()) {
				Home.instance.showNews(message_popup_dialog.message_body);
				return;
			}
		}
		GlobalController.sendNewsNotification(message_popup_dialog.message_title, message_popup_dialog.message_body);
	}
}