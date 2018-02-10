/**
 * @Filename: TLinxSHA1.java
 * @Author：caiqf
 * @Date�?016-4-12
 */
package com.xqh.financial.utils.pingan;

import java.security.MessageDigest;

/**
 * @Class: TLinxSHA1.java
 * @Description:  SHA-1������
 */
public class TLinxSHA1 {
	public static String SHA1(String decript) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte[] messageDigest = digest.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte message : messageDigest) {
				String shaHex = Integer.toHexString(message & 0xFF);
				if (shaHex.length() < 2)
					hexString.append(0);

				hexString.append(shaHex);
			}
			return hexString.toString().toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
