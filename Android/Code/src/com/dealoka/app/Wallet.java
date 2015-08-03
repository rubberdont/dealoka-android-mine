package com.dealoka.app;

import java.util.Iterator;

import com.dealoka.app.R;
import com.dealoka.app.adapter.WalletListAdapter;
import com.dealoka.app.adapter.WalletListAdapter.WalletListCallback;
import com.dealoka.app.call.CallOfferRedeemWallet;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.model.OfferRedeemWallet;
import com.dealoka.lib.General;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Wallet extends Fragment {
	public static Wallet instance;
	public static final String WalletFragment = "WALLET_FRAGMENT";
	private ImageButton btn_header;
	private TextView lbl_no_data;
	private ListView lst_wallet;
	private WalletDetails wallet_details_fragment;
	private WalletListAdapter list_adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		try {
			final View view = inflater.inflate(R.layout.wallet, container, false);
			setInitial(view);
			return view;
		}catch(InflateException ex) {}
		return null;
	}
	@Override
	public void onDestroyView() {
		instance = null;
		if(Home.instance != null) {
			Home.instance.closeWalletFragment();
		}
		super.onDestroyView();
	}
	public void SyncAll() {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lst_wallet.setAdapter(list_adapter);
			}
		});
		showNoData();
	}
	public void Update(final String offer_id) {
		final Iterator<OfferRedeemWallet> iterator = CallOfferRedeemWallet.offer_redeem_wallet_db.iterator();
		while(iterator.hasNext()) {
			OfferRedeemWallet offer_redeem_wallet = iterator.next();
			if(offer_redeem_wallet.id.equals(offer_id)) {
				offer_redeem_wallet.validated = true;
				CallOfferRedeemWallet.decreaseWalletPoint();
				General.setBadgeCount(DealokaApp.getAppContext(), CallOfferRedeemWallet.getWalletPoint(), Main.class.getName());
				break;
			}
		}
		updateData();
	}
	public void closeWalletDetailsFragment() {
		wallet_details_fragment = null;
	}
	private void setInitial(final View view) {
		btn_header = (ImageButton)view.findViewById(R.id.btn_header);
		lbl_no_data = (TextView)view.findViewById(R.id.lbl_no_data);
		lst_wallet = (ListView)view.findViewById(R.id.lst_wallet);
		setEventListener();
		btn_header.setSelected(Home.instance.isPopHeader());
		populateData();
	}
	private void setEventListener() {
		btn_header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalController.pop();
			}
		});
		lst_wallet.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				doSelected(arg2);
			}
		});
	}
	private void populateData() {
		showNoData();
		prefetchData();
	}
	private void doSelected(final int index) {
		if(wallet_details_fragment != null) {
			return;
		}
		if(CallOfferRedeemWallet.offer_redeem_wallet_db.size() <= index) {
			return;
		}
		if(CallOfferRedeemWallet.offer_redeem_wallet_db.get(index).c_offer_rec == null) {
			return;
		}
		Bundle args = new Bundle();
		args.putInt("index", index);
		wallet_details_fragment = new WalletDetails();
		wallet_details_fragment.setArguments(args);
		final FragmentTransaction fragment_transaction = getFragmentManager().beginTransaction();
		fragment_transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		fragment_transaction.add(R.id.lay_child, wallet_details_fragment, WalletDetails.WalletDetailsFragment).addToBackStack(null).commit();
	}
	private void showNoData() {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(CallOfferRedeemWallet.offer_redeem_wallet_db.size() <= 0) {
					lbl_no_data.setVisibility(View.VISIBLE);
				}else {
					lbl_no_data.setVisibility(View.GONE);
				}
			}
		});
	}
	private void prefetchData() {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(CallOfferRedeemWallet.offer_redeem_wallet_db == null) {
					return;
				}
				list_adapter = new WalletListAdapter(Home.instance, CallOfferRedeemWallet.offer_redeem_wallet_db, new WalletListCallback() {
					@Override
					public void didDeleted(OfferRedeemWallet offer_redeem_wallet) {
						if(!offer_redeem_wallet.expired && !offer_redeem_wallet.validated) {
							CallOfferRedeemWallet.decreaseWalletPoint();
						}
						GlobalVariables.query_controller.deleteWallet(offer_redeem_wallet.id);
						CallOfferRedeemWallet.offer_redeem_wallet_db.remove(offer_redeem_wallet);
						updateData();
						General.setBadgeCount(DealokaApp.getAppContext(), CallOfferRedeemWallet.getWalletPoint(), Main.class.getName());
						if(Home.instance != null) {
							Home.instance.updateWalletPoint(CallOfferRedeemWallet.getWalletPoint());
						}
					}
					@Override
					public void didSelected(int position) {
						doSelected(position);
					}
				});
				lst_wallet.setAdapter(list_adapter);
			}
		});
	}
	private void updateData() {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				list_adapter.notifyDataSetChanged();
			}
		});
	}
}