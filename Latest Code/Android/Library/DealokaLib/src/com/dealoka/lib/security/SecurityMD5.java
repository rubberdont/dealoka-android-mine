package com.dealoka.lib.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.dealoka.lib.General;

public class SecurityMD5 {
	public static final String get(final String value) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(value.getBytes());
			byte message_digest[] = digest.digest();
			StringBuilder hex_string = new StringBuilder();
			for(byte message_digest_byte : message_digest) {
				String h = Integer.toHexString(0xFF & message_digest_byte);
				while (h.length() < 2)
					h = "0" + h;
				hex_string.append(h);
			}
			return hex_string.toString();
		}catch(NoSuchAlgorithmException ex) {}
		return General.TEXT_BLANK;
	}
}