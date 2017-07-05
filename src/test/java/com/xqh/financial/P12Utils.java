package com.xqh.financial;
/**
 * Read a p12 format digital certificate. Be careful about the file format.
 * Sometimes, it might be incompatible. If it happens, import/export again
 * using netscape(p12) or IE(pfx).
 */
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class P12Utils
{
    public static PrivateKey getPriate()
    {
        final String KEYSTORE_FILE     = "20058100001175504.p12";
        final String KEYSTORE_PASSWORD = "111111";
        final String KEYSTORE_ALIAS    = "alias";

        try
        {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(P12Utils.class.getClassLoader().getResource(KEYSTORE_FILE).getPath());

            // If the keystore password is empty(""), then we have to set
            // to null, otherwise it won't work!!!
            char[] nPassword = null;
            if ((KEYSTORE_PASSWORD == null) || KEYSTORE_PASSWORD.trim().equals(""))
            {
                nPassword = null;
            }
            else
            {
                nPassword = KEYSTORE_PASSWORD.toCharArray();
            }
            ks.load(fis, nPassword);
            fis.close();

            System.out.println("keystore type=" + ks.getType());

            // Now we loop all the aliases, we need the alias to get keys.
            // It seems that this value is the "Friendly name" field in the
            // detals tab <-- Certificate window <-- view <-- Certificate
            // Button <-- Content tab <-- Internet Options <-- Tools menu
            // In MS IE 6.
            Enumeration _enum = ks.aliases();
            String keyAlias = null;
            if (_enum.hasMoreElements()) // we are readin just one certificate.
            {
                keyAlias = (String)_enum.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
            }

            // Now once we know the alias, we could get the keys.
            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey pubkey = cert.getPublicKey();

            System.out.println("cert class = " + cert.getClass().getName());
            System.out.println("cert = " + cert);
            System.out.println("public key = " + pubkey);
            System.out.println("private key = " + prikey);

            return prikey;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}