package com.xqh.financial.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hssh on 2017/5/1.
 */
public class CommonUtils {

    private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static String getFormatDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long times = System.currentTimeMillis();
        System.out.println(times);
        Date date = new Date(times);
        String tim = sdf.format(date);
        return tim;
    }

    //静态方法，便于作为工具类
    public static String getMd5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void sendError(HttpServletResponse resp, int status, String msg) {
        try {
            resp.sendError(status, msg);
        } catch (IOException e) {
            logger.error("sendError error:{}", e.toString());
        }
    }

}
