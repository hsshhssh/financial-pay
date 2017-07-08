package com.xqh.financial.utils.ruixun;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;
import java.util.TreeMap;

public class SignUtils {
    public static String signData(List<BasicNameValuePair> nvps) throws Exception {
        TreeMap<String, String> tempMap = new TreeMap<String, String>();
        for (BasicNameValuePair pair : nvps) {
            if (StringUtils.isNotBlank(pair.getValue())) {
                tempMap.put(pair.getName(), pair.getValue());
            }
        }
        StringBuffer buf = new StringBuffer();
        for (String key : tempMap.keySet()) {
            buf.append(key).append("=").append((String) tempMap.get(key)).append("&");
        }
        String signatureStr = buf.substring(0, buf.length() - 1);
        String signData = RSAUtil.signByPrivate(signatureStr, RSAUtil.readFile("file/ruixun/rsa_private_key.pem", "UTF-8"), "UTF-8");
        //System.out.println("请求数据：" + signatureStr + "&signature=" + signData);
        return signData;
    }

    public static boolean verferSignData(String str) {
        //System.out.println("响应数据：" + str);
        String data[] = str.split("&");
        StringBuffer buf = new StringBuffer();
        String signature = "";
        for (int i = 0; i < data.length; i++) {
            String tmp[] = data[i].split("=", 2);
            if ("signature".equals(tmp[0])) {
                signature = tmp[1];
            } else {
                buf.append(tmp[0]).append("=").append(tmp[1]).append("&");
            }
        }
        String signatureStr = buf.substring(0, buf.length() - 1);
        //System.out.println("验签数据：" + signatureStr);
        return RSAUtil.verifyByKeyPath(signatureStr, signature, "file/ruixun/rsa_public_key.pem", "UTF-8");
    }
}
