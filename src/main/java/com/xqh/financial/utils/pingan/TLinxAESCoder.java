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

	public static void main(String[] args) throws Exception
    {
        String encode = encrypt("aaa", "0e3b2bc65307296fa362108259989775");
        String decrypt = decrypt("E550EE5F5C423CEA1BFD0BA7DF18002EBD532080E26319A59CCB9912A89294252A291A50AF976666BCDED3B0D57BFFF425F4A1ECFCB60D64CCDDD2D7F5C35D3B3A4DA77C800A3575A5A150D99E332E7A02F220407995AF2FF91F2924F6E0C2FEB4E1BC4FD90771955E076678217796BE2893A1FD01BDAD58D3B828F50958E224E389F972D3177CE41A3AEB6B082B08869E50390B989C3C87298FCDC29AA4D66316E5E7AACC6C33DD4E5A4231C90D16AA44AE612747ADDC904B007D6789B44A8F3E5D563F7E09BE20D1D0E0946E6C8FEA7D22EE4233789A56C01295F6842E3A7787425FE22C8701D68454FE3BE3245105BB34F39253BAD82D6BE2B1FF17E1CE2A7DEA0D4ADD44FEC97DF6F808F847A749DB39AD127C072BEAC8B454ACE79B5A1B00237902CEE9ED8CF1554470CABFC5C4F6DB309BAC5F3542EFFCE9614CF56E4AEAA2E4B7D349CB47111006248F80667E6FB53CA9DE338407EF04A06295BE27A6AA036603E256A361DDA72FCA51AF3250F38D2BA30253DEF0EEEB30DC37A0F0AC6D8C914106F8DC07167002CC206DF58C2BA306DE72590949E5E013979E9DE41C36D58A16475F0C8833624DE11C6FACD30666A2BBBD8CF6D8F195EEA3E03E5C579DB48D800E75C61D800449A04395E18CD8D215ED5B0E4BC7E262682ED4A1004A35275004F3ACA0907355B5518739E74071FE8D066C69D83B19E5AAE49B35BE324C3EC61C24455F17D6AB143330300A22", "0e3b2bc65307296fa362108259989775");
        System.out.println("-------------" + decrypt);
    }
}
