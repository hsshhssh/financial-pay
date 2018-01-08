/**
 * @Filename: TLinxAESCoder.java
 * @Author锛歝aiqf
 * @Date锛�016-4-12
 */
package com.xqh.financial.utils.pingan;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Class: TLinxAESCoder.java
 * @Description: AES加解密类
 * @Author：caiqf
 * @Date：2016-4-12
 */
@Slf4j
public class TLinxAESCoder {
	private static String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
	private static String KEY_ALGORITHM = "AES";

	public static String decrypt(String sSrc, String sKey) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes("ASCII"), KEY_ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(2, skeySpec);
		byte[] encrypted1 = hex2byte(sSrc);
		byte[] original = cipher.doFinal(encrypted1);
		return new String(original, "UTF-8");
	}

	public static String encrypt(String sSrc, String sKey) throws Exception {
		log.info("====data加密前的明文= " + sSrc);
		SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes("ASCII"), KEY_ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(1, skeySpec);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));
		return byte2hex(encrypted);
	}

	private static byte[] hex2byte(String strhex) {
		if (strhex == null)
			return null;

		int l = strhex.length();
		if (l % 2 == 1)
			return null;

		byte[] b = new byte[l / 2];
		for (int i = 0; i != l / 2; ++i)
			b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);

		return b;
	}

	//二进制数组转十六进制字符串 （已优化）
	private static String byte2hex(byte[] result) {
		StringBuffer sb = new StringBuffer(result.length * 2);
		for (int i = 0; i < result.length; i++) {
			int hight = ((result[i] >> 4) & 0x0f);
			int low = result[i] & 0x0f;
			sb.append(hight > 9 ? (char) ((hight - 10) + 'a') : (char) (hight + '0'));
			sb.append(low > 9 ? (char) ((low - 10) + 'a') : (char) (low + '0'));
		}
		return sb.toString();
	}

	//二进制数组转十六进制字符串（速度慢）
	private static String byte2hex1(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; ++n) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}

		return hs.toUpperCase();
	}

	public static void main(String[] args) {
		String data = "";
		String str = null;
		try {
			str = decrypt(data,"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(str);
	}
}
