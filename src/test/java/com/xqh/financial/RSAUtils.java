package com.xqh.financial;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by hssh on 2017/6/29.
 */

public class RSAUtils{

    public static final String  SIGN_ALGORITHMS = "SHA1WithRSA";

    public static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALDAo7k9CGfHOYT4\n" +
            "4ZulH0NZbFKHrlkQG2EpmRswYxyfOGlJvDkFPAdfmUgCPFI+HPRbTDMo8YBzGeIm\n" +
            "kdd8jyGvIAQhTz084B1opGGDBMx2fT5kkdbNaQXXlsqRGsmu7IY+U9jVXL89IYXD\n" +
            "qoaVNAzz4TbfVrfSfvuCKdj1KlqvAgMBAAECgYADhFG9pdE8/2HNm4Yhqq9sj6Lw\n" +
            "IXL+oqtoy4MYX2YZc7GTprvwBoKjQuux2xmIKWQ6GHbzraTggWnKbioIt5nBcVO1\n" +
            "4m4lrYONpnu85HsUR8eCCz5bAxh49wz2Ll/ymOpcNvBJyjTJD5gONaX7xvdM1viA\n" +
            "LAdPEEDah1WOJvmowQJBAOeWqCKP9K6FekPg/hsHNRumhO9QK0q2fGYlZC4eL1Gi\n" +
            "arEGbZ1upvoq7Y62vA3JPFdNpyBuuc6RKOC44cNs8WMCQQDDYkAXFaR/+jZgK/Pa\n" +
            "pX2qMXViVNvpq3UenFkq0G/KQT9rb9P0rQ+Mj16MFH4ruJhpRTPDuETGsZh7u0Zn\n" +
            "LPlFAkEAnhSRml4HWCWWisUGzu5BiylEbpKbqnkcOyFk27IQ/LlP+Jx5sE/6vKMW\n" +
            "2ybTANoPTUydOx85x84ASD0LJjm0HwJAKq4PqftET+BseF2hiyWTadYb+jrnPwgB\n" +
            "MYqdv8iAYwIVxHZNKqmTN/UphhgD57EPOg+v2xUpkO3CjWS/YFJmsQJBAMh+u35N\n" +
            "twDeNY/lvgLRg7abaUnFVeNUGRLMu6c51pA3jMrf2JCcuk1LxA0d9isWTrLOiID8\n" +
            "/KcLPyYzN7ThJAw=";

    /**
     * RSA签名
     * @param content 待签名数据
     * @param privateKey 商户私钥
     * @param input_charset 编码格式
     * @return 签名值
     */
    public static String sign(String content, String input_charset)
    {
        try
        {
            //PKCS8EncodedKeySpec priPKCS8    = new PKCS8EncodedKeySpec( Base64.decode(PRIVATE_KEY) );
            //KeyFactory keyf                 = KeyFactory.getInstance("RSA");
            //PrivateKey priKey               = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(P12Utils.getPriate());
            signature.update( content.getBytes(input_charset) );

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * RSA验签名检查
     * @param content 待签名数据
     * @param sign 签名值
     * @param ali_public_key 支付宝公钥
     * @param input_charset 编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String ali_public_key, String input_charset)
    {
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(ali_public_key);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update( content.getBytes(input_charset) );

            boolean bverify = signature.verify( Base64.decode(sign) );
            return bverify;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 解密
     * @param content 密文
     * @param private_key 商户私钥
     * @param input_charset 编码格式
     * @return 解密后的字符串
     */
    public static String decrypt(String content, String private_key, String input_charset) throws Exception {
        PrivateKey prikey = getPrivateKey(private_key);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, prikey);

        InputStream ins = new ByteArrayInputStream(Base64.decode(content));
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        //rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
        byte[] buf = new byte[128];
        int bufl;

        while ((bufl = ins.read(buf)) != -1) {
            byte[] block = null;

            if (buf.length == bufl) {
                block = buf;
            } else {
                block = new byte[bufl];
                for (int i = 0; i < bufl; i++) {
                    block[i] = buf[i];
                }
            }

            writer.write(cipher.doFinal(block));
        }

        return new String(writer.toByteArray(), input_charset);
    }


    /**
     * 得到私钥
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {

        byte[] keyBytes;

        keyBytes = Base64.decode(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return privateKey;
    }
}