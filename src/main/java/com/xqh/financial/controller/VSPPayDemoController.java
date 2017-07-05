package com.xqh.financial.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.vsp.HttpConnectionUtil;
import com.xqh.financial.utils.vsp.HttpsUtils;
import com.xqh.financial.utils.vsp.SybUtil;
import com.xqh.financial.utils.vsp.allinpay.XmlTools;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hssh on 2017/6/19.
 */
@RestController
@RequestMapping("vsp")
public class VSPPayDemoController
{
    private static Logger logger = LoggerFactory.getLogger(VSPPayDemoController.class);

    //@GetMapping("pay")
    //public void pay(@RequestParam("money") int money, HttpServletRequest req, HttpServletResponse resp)
    //{
    //    try
    //    {
    //        //req.setAttribute("test", 123);
    //        req.getRequestDispatcher("/vsp/pay/html").forward(req, resp);
    //    }
    //    catch (ServletException e)
    //    {
    //        e.printStackTrace();
    //    } catch (IOException e)
    //    {
    //        e.printStackTrace();
    //    }
    //}

    @GetMapping("pay")
    public void payJsp(HttpServletRequest req, HttpServletResponse resp, @RequestParam("money") int money) throws Exception {

        HttpConnectionUtil http = new HttpConnectionUtil("https://vsp.allinpay.com/apiweb/unitorder"+"/pay");
        http.init();
        TreeMap<String, String> params = Maps.newTreeMap();

        params.put("cusid", "335581048165186");
        params.put("appid", "00010835");
        params.put("version", "11");
        params.put("trxamt", String.valueOf(money));
        params.put("reqsn", String.valueOf(System.currentTimeMillis()));
        params.put("paytype", "A01");
        params.put("randomstr", SybUtil.getValidatecode(8));
        params.put("body", "测试应用");
        params.put("remark", "mark");
        //params.put("acct", acct);
        params.put("notify_url", "http://139.196.51.152:8080/vsp/callback");
        params.put("sign", SybUtil.sign(params,"gzxqhpay1qa@WS#ED"));

        byte[] bys = http.postParams(params, true);
        String result = new String(bys,"UTF-8");
        Map<String,String> map = SybUtil.handleResult(result);
        String payUrl = map.get("payinfo");

        resp.sendRedirect(payUrl);
    }


    @PostMapping("callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = SybUtil.getParams(req);
        logger.info("params:{}", JSONObject.toJSON(params));

        CommonUtils.writeResponse(resp, "success");
    }

    @PostMapping("notify")
    public void notify(HttpServletRequest req, HttpServletResponse resp)
    {
        Map<String, String[]> params = req.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        logger.info("/mch/notify param: " + queryString.substring(0, queryString.length() - 1));
    }

    @GetMapping("/pay/withdraw")
    public String withdraw() throws Exception {
        String bankCode = "102";
        String accountNo = "6222023602099939977";
        String accountName = "黄松深";
        String accountProp = "0";
        String amount = "1";
        String tel = "13580517072";

        return withdrawParam(bankCode, accountNo, accountName, accountProp, amount, tel);
    }

    @GetMapping("/pay/withdraw/param")
    public String withdrawParam(@RequestParam(name = "bankCode") String bankCode,
                                @RequestParam(name = "accountNo") String accountNo,
                                @RequestParam(name = "accountName") String accountName,
                                @RequestParam(name = "accountProp") String accountProp,
                                @RequestParam(name = "amount") String amount,
                                @RequestParam(name = "tel") String tel) throws Exception
    {

        long nowTime = System.currentTimeMillis();
        String xmlStr = "<?xml version=\"1.0\" encoding=\"GBK\"?><AIPG>\n" +
                "  <INFO>\n" +
                "    <TRX_CODE>100014</TRX_CODE>\n" +
                "    <VERSION>03</VERSION>\n" +
                "    <DATA_TYPE>2</DATA_TYPE>\n" +
                "    <LEVEL>5</LEVEL>\n" +
                "    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
                "    <USER_NAME>20058100001175504</USER_NAME>\n" +
                "    <USER_PASS>111111</USER_PASS>\n" +
                "    <REQ_SN>200581000011755" + nowTime + "</REQ_SN>\n" +
                "    <SIGNED_MSG>%s</SIGNED_MSG>\n" +
                "  </INFO>\n" +
                "  <TRANS>\n" +
                "    <BUSINESS_CODE>09900</BUSINESS_CODE>\n" +
                "    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
                "    <SUBMIT_TIME>" + CommonUtils.getFormatDate("YYYYMMDDHHMMSS") + "</SUBMIT_TIME>\n" +
                "    <BANK_CODE>" + bankCode + "</BANK_CODE>\n" +
                "    <ACCOUNT_NO>" + accountNo + "</ACCOUNT_NO>\n" +
                "    <ACCOUNT_NAME>" + accountName + "</ACCOUNT_NAME>\n" +
                "    <ACCOUNT_PROP>" + accountProp + "</ACCOUNT_PROP>\n" +
                "    <AMOUNT>" + amount + "</AMOUNT>\n" +
                "    <CURRENCY>CNY</CURRENCY>\n" +
                "    <TEL>" + tel + "</TEL>\n" +
                "    <CUST_USERID>252523524253xx</CUST_USERID>\n" +
                "  </TRANS>\n" +
                "</AIPG>";

        int iStart = xmlStr.indexOf("<SIGNED_MSG>");
        int end = xmlStr.indexOf("</SIGNED_MSG>");

        String _tempXmlStr =xmlStr.substring(0, iStart) + xmlStr.substring(end + 13);
        logger.info("待加密xml: {}", _tempXmlStr);

        String signStr = XmlTools.signPlain(_tempXmlStr, VSPPayDemoController.class.getClassLoader().getResource("file/20058100001175504.p12").getPath(), "111111", false);
        xmlStr = String.format(xmlStr, signStr);
        logger.info("加密结果 sign:{}", signStr);
        logger.info("请求报文 xml:{}", xmlStr);

        Header[] headers = { new BasicHeader("Content-Type", "application/xml") };

        StringEntity stringEntity = new StringEntity(xmlStr, "GBK");

        String result = HttpsUtils.post("https://tlt.allinpay.com/aipg/ProcessServlet", headers, stringEntity);

        logger.info("请求结果 result:{}", result);

        return result;
    }

}
