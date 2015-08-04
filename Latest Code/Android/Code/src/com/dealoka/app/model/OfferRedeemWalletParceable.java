package com.dealoka.app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OfferRedeemWalletParceable implements Parcelable {
	public String id;
	public String unique_code;
	public String md5hash;
	public String merchant_code;
	public String claimed_state;
	public long claimed_time;
	public String image_prefix;
	public boolean validated;
	public boolean expired;
	public long start_time;
	public long end_time;
	public long redeem_time;
	public int day_multiplier;
	public String go_to_url;
	public String button_name;
	public boolean online_flag;
	public fk_offer_id c_fk_offer_id;
	public offer_rec c_offer_rec;
	public fk_merchant_id c_fk_merchant_id;
	public _id c_id;
	public OfferRedeemWalletParceable(final OfferRedeemWallet offer_redeem_wallet) {
		id = offer_redeem_wallet.id;
		unique_code = offer_redeem_wallet.unique_code;
		md5hash = offer_redeem_wallet.md5hash;
		merchant_code = offer_redeem_wallet.merchant_code;
		claimed_state = offer_redeem_wallet.claimed_state;
		claimed_time = offer_redeem_wallet.claimed_time;
		image_prefix = offer_redeem_wallet.image_prefix;
		start_time = offer_redeem_wallet.start_time;
		end_time = offer_redeem_wallet.end_time;
		redeem_time = offer_redeem_wallet.redeem_time;
		day_multiplier = offer_redeem_wallet.day_multiplier;
		go_to_url = offer_redeem_wallet.go_to_url;
		button_name = offer_redeem_wallet.button_name;
		online_flag = offer_redeem_wallet.online_flag;
		c_fk_offer_id = offer_redeem_wallet.c_fk_offer_id;
		c_offer_rec = offer_redeem_wallet.c_offer_rec;
		c_fk_merchant_id = offer_redeem_wallet.c_fk_merchant_id;
		c_id = offer_redeem_wallet.c_id;
		validated = offer_redeem_wallet.validated;
		expired = offer_redeem_wallet.expired;
	}
	protected OfferRedeemWalletParceable(Parcel in) {
		id = in.readString();
		unique_code = in.readString();
		md5hash = in.readString();
		merchant_code = in.readString();
		claimed_state = in.readString();
		claimed_time = in.readLong();
		image_prefix = in.readString();
		start_time = in.readLong();
		end_time = in.readLong();
		redeem_time = in.readLong();
		day_multiplier = in.readInt();
		c_fk_offer_id = (fk_offer_id)in.readParcelable(fk_offer_id.class.getClassLoader());
		c_offer_rec = (offer_rec)in.readParcelable(offer_rec.class.getClassLoader());
		c_fk_merchant_id = (fk_merchant_id)in.readParcelable(fk_merchant_id.class.getClassLoader());
		c_id = (_id)in.readParcelable(_id.class.getClassLoader());
		validated = in.readByte() != 0;
		expired = in.readByte() != 0;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(unique_code);
		dest.writeString(md5hash);
		dest.writeString(merchant_code);
		dest.writeString(claimed_state);
		dest.writeLong(claimed_time);
		dest.writeString(image_prefix);
		dest.writeLong(start_time);
		dest.writeLong(end_time);
		dest.writeLong(redeem_time);
		dest.writeInt(day_multiplier);
		dest.writeParcelable(c_fk_offer_id, flags);
		dest.writeParcelable(c_offer_rec, flags);
		dest.writeParcelable(c_fk_merchant_id, flags);
		dest.writeParcelable(c_id, flags);
		dest.writeByte((byte)(validated ? 1 : 0));
		dest.writeByte((byte)(expired ? 1 : 0));
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public OfferRedeemWalletParceable createFromParcel(Parcel in) {
			return new OfferRedeemWalletParceable(in);
		}
		@Override
		public OfferRedeemWalletParceable[] newArray(int size) {
			return new OfferRedeemWalletParceable[size];
		}
	};
}