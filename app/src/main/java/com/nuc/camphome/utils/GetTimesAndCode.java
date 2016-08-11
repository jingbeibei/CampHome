package com.nuc.camphome.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public  class GetTimesAndCode {
	public static String getCode1(String times) {
		String code = Md5("abc#@DIDKbkd" + times);
		return code;
	}
//587ABCaaYm76Z
	public static String getCode(String times) {
		String code = Md5("abc#@DIDKbkd" + times);
		return code;
	}
	public  static String getTimes() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String times = sf.format(new Date());
		return times;
	}

	public static String Md5(String plainText) {
		StringBuffer buf = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;

			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));

			}

		} catch (NoSuchAlgorithmException e) {

		} finally {
			return buf.toString();
		}
	}
}
