package com.xqh.financial.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hssh on 2017/5/1.
 */
public class CommonUtils {

    private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static String getFormatDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long times = System.currentTimeMillis();
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
            return "";
        }

    }

    public static void sendError(HttpServletResponse resp, ErrorResponseEunm errorResponseEunm)
    {
        try
        {
            resp.sendError(errorResponseEunm.status, errorResponseEunm.msg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void sendError(HttpServletResponse resp, int status, String msg) {
        try {
            resp.sendError(status, msg);
        } catch (IOException e) {
            logger.error("sendError error:{}", e.toString());
        }
    }

    /**
     * 打印请求参数
     * @param rep
     * @param url
     */
    public static void printRequestParam(HttpServletRequest rep, String url) {
        Map<String, String[]> params = rep.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        if(StringUtils.isNotBlank(queryString)) {
            logger.info("{} Param:  {}" ,url, queryString.substring(0, queryString.length() - 1));
        } else {
            logger.info("{} Param: no param");
        }
    }

    public static void writeResponse(HttpServletResponse resp, Object object) {
        try {
            resp.getWriter().print(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得零点时间
     */
    public static int getZeroHourTime(int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        return (int) (date.getTime()/1000);
    }

    public static int getCurrentMonth()
    {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) + 1;
    }


    /**
     * 取得某月的其实时间和结束时间
     */
    public static List<Integer> getMonthStartEndTime(int month, int year )
    {
        List<Integer> res = Lists.newArrayList();

        if(month <= 1)
        {
            month = 1;
        } else if(month >= 12)
        {
            month = 12;
        }

        if(month < 12 )
        {
            month -= 1;
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            res.add((int) (cal.getTime().getTime()/1000));

            cal.add(Calendar.MONTH, 1);
            res.add((int) (cal.getTime().getTime()/1000));
            return res;
        }
        else
        {
            // month == 12
            Calendar cal = Calendar.getInstance();
            cal.set(year, 11, cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            res.add((int) (cal.getTime().getTime()/1000));


            cal.add(Calendar.YEAR, 1);
            cal.set(Calendar.MONTH, 0);
            res.add((int) (cal.getTime().getTime()/1000));
        }
        return res;
    }


    /**
     * 取得请求参数
     * @param request
     * @return
     */
    public static TreeMap<String, String> getParams(HttpServletRequest request){
        TreeMap<String, String> map = new TreeMap<String, String>();
        Map reqMap = request.getParameterMap();
        for(Object key:reqMap.keySet()){
            String value = ((String[])reqMap.get(key))[0];
            //System.out.println(key+";"+value);
            map.put(key.toString(),value);
        }
        return map;
    }

    /**
     * 获取post参数
     */
    public static String getPostParams(HttpServletRequest request) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader( request.getInputStream(), "utf-8"));
        StringBuffer sb = new StringBuffer("");
        String temp;
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        br.close();
        return sb.toString();
    }

    public static String getIp(HttpServletRequest req)
    {
        String ip = req.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            logger.info("X-Real-IP :{}", ip);
            return ip;
        }
        ip = req.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            logger.info("X-Forwarded-For :{}", ip);
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            logger.info("remoteAddr: {}", req.getRemoteAddr());
            return req.getRemoteAddr();
        }
    }

    public static String generateRandom(int num) {
        String chars = "0123456789";
        char[] rands = new char[num];
        for (int i = 0; i < num; i++) {
            int rand = (int) (Math.random() * 10);
            rands[i] = chars.charAt(rand);
        }
        return String.valueOf(rands);
    }
}
