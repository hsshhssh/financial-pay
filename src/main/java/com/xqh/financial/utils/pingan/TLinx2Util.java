package com.xqh.financial.utils.pingan;

//~--- non-JDK imports --------------------------------------------------------

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class TLinx2Util
 * Description
 * Create 2017-03-07 14:01:23
 * @author Benny.YEE
 */
@Slf4j
public class TLinx2Util {



    /**
     * 签名
     * @param postMap
     * @return
     */
    public static String sign(Map<String, String> postMap) {
        String sortStr = null;
        String sign    = null;

        try {

            /**
             * 1 A~z排序(加上open_key)
             */
            sortStr = TLinxUtil.sort(postMap);
            log.info("====排序后的待签名字符串= "+sortStr);
            /**
             * 2 sha1加密(小写)
             */
            String sha1 = TLinxSHA1.SHA1(sortStr);
            log.info("====sha1加密后的待签名字符串= " + sha1);
            /**
             * 3 md5加密(小写)
             */
            sign = MD5.MD5Encode(sha1).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sign;
    }

    /**
     * 验签
     * @param respObject
     * @return
     */
    public static Boolean verifySign(JSONObject respObject, String key) {
        String respSign = respObject.get("sign").toString();

        respObject.remove("sign");    // 删除sign节点
        respObject.put("open_key", key);
        System.out.println("==========开始验签==========");
        String veriSign = sign(respObject);    // 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名

        if (respSign.equals(veriSign)) {
            System.out.println("==========验签成功==========");

            return true;
        }

        return false;
    }

    /**
     * AES加密，再二进制转十六进制(bin2hex)
     * @param postmap 说明：
     * @throws Exception
     */
    public static void handleEncrypt(TreeMap<String, String> datamap, TreeMap<String, String> postmap, String key) throws Exception {

        JSONObject dataobj = JSONObject.fromObject(datamap);
        String data    = TLinxAESCoder.encrypt(dataobj.toString(), key);    // AES加密，并bin2hex
        log.info("====加密后的data= "+data);
        postmap.put("data", data);
    }

    /**
     * 签名
     * @param postmap
     */
    public static void handleSign(TreeMap<String, String> postmap, String key) {
        Map<String, String> veriDataMap = new HashMap<String, String>();

        veriDataMap.putAll(postmap);
        veriDataMap.put("open_key", key);

        // 签名
        String sign = sign(veriDataMap);

        log.info("====已签名字符串= " + sign);
        postmap.put("sign", sign);
    }

    /**
     * 请求接口
     * @param postmap
     * @return      响应字符串
     */
    public static String handlePost(TreeMap<String, String> postmap, String interfaceName, String baseUrl) {
        String url = baseUrl + interfaceName;
        log.info("====请求地址= " + url);
        if (url.contains("https")) {
            return HttpsUtil.httpMethodPost(url, postmap, "UTF-8");
        } else {
            return HttpUtil.httpMethodPost(url, postmap, "UTF-8");
        }
    }

    /**
     * Method main
     * Description 说明：
     *
     * @param args 说明：
     */
    //public static void main(String[] args) {
    //
    //    // 初始化参数
    //    String pmtType   = "0,1,2,3";
    //    String timestamp = new Date().getTime() / 1000 + "";    // 时间
    //
		//try {
		//	// 固定参数
		//	TreeMap<String, String> postmap = new TreeMap<String, String>();//请求参数的map
		//	postmap.put("open_id", TestParams.OPEN_ID);
		//	postmap.put("timestamp", timestamp);
    //
		//	TreeMap<String, String> datamap = new TreeMap<String, String>();//data参数的map
		//	datamap.put("pmt_type", pmtType);
    //
		//	/**
		//	 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		//	 */
		//	handleEncrypt(datamap, postmap);
    //
    //        /**
    //         * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
    //         */
    //        handleSign(postmap);
    //
    //        /**
    //         * 3 请求、响应
    //         */
    //        String rspStr = handlePost(postmap, TestParams.PAYLIST);
    //
    //
    //        /**
    //         * 4 验签  有data节点时才验签
    //         */
    //        JSONObject respObject = JSONObject.fromObject(rspStr);
    //
    //        Object dataStr    = respObject.get("data");
		//	System.out.println("返回data字符串="+dataStr);
    //
    //        if (!rspStr.isEmpty() || (dataStr != null)) {
    //            if (verifySign(respObject)) {    // 验签成功
    //
    //                /**
    //                 * 5 AES解密，并hex2bin
    //                 */
    //                String respData = TLinxAESCoder.decrypt(dataStr.toString(), TestParams.OPEN_KEY);
    //
    //                System.out.println("==================响应data内容:" + respData);
    //            } else {
    //                System.out.println("=====验签失败=====");
    //            }
    //        } else {
    //            System.out.println("=====没有返回data数据=====");
    //        }
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //}
}


//~ Formatted by Jindent --- http://www.jindent.com
