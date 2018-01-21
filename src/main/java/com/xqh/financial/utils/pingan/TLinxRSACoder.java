/**
 * @Filename: TLinxRSACoder.java
 * @Author锛歝aiqf
 * @Date锛�016-4-12
 */
package com.xqh.financial.utils.pingan;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Class: TLinxRSACoder.java
 * @Description: RSA加解密类
 * @Author：caiqf
 * @Date：2016-4-12
 */
@SuppressWarnings("all")
public class TLinxRSACoder {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    public static Map<String, Object> initKey() throws Exception {
        Map keyMap = new HashMap(2);
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        keyMap.put("RSAPublicKey", (RSAPublicKey) keyPair.getPublic());
        keyMap.put("RSAPrivateKey", (RSAPrivateKey) keyPair.getPrivate());
        return keyMap;
    }

    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get("RSAPrivateKey");
        return new BASE64Encoder().encodeBuffer(key.getEncoded());
    }

    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get("RSAPublicKey");
        return new BASE64Encoder().encodeBuffer(key.getEncoded());
    }

    public static byte[] encryptByPrivateKey(byte[] data, String key)
            throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(1, privateKey);

        return cipher.doFinal(data);
    }

    public static byte[] decryptByPrivateKey(byte[] data, String key)
            throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(2, privateKey);

        return cipher.doFinal(data);
    }

    public static byte[] encryptByPublicKey(byte[] data, String key)
            throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(1, publicKey);

        return cipher.doFinal(data);
    }

    public static byte[] decryptByPublicKey(byte[] data, String key)
            throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(2, publicKey);

        return cipher.doFinal(data);
    }

    public static String sign(byte[] data, String privateKey) throws Exception {

    	byte[] keyBytes = new BASE64Decoder().decodeBuffer(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(priKey);
        signature.update(data);

        return  byte2hex(signature.sign());
    }

    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(publicKey);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(pubKey);
        signature.update(data);

        //sign 16进制转换成byte数组
        return signature.verify(hexStrToBytes(sign));
    }

    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    private static byte[] hex2byte(String strhex) {
        if (strhex == null)
            return null;

        int l = strhex.length();
        if (l % 2 == 1)
            return null;

        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; ++i)
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);

        return b;
    }

    private static String byte2hex(byte[] b) {
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
  /**
  * 将16进制字符串还原为字节数组.
  */
 public static final byte[] hexStrToBytes(String s) {

     byte[] bytes;

     bytes = new byte[s.length() / 2];

     for (int i = 0; i < bytes.length; i++) {
         bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
     }

     return bytes;
 }


}
