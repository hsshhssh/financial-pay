package com.xqh.financial;

import com.google.common.collect.Lists;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.DoubleUtils;
import com.xqh.financial.utils.UrlUtils;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class FinancialPayApplicationTests {

	@Test
	public void contextLoads() throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

		String key = "_aa";
		System.out.println("_aa".lastIndexOf("_"));
		System.out.println("aa".lastIndexOf("_"));
		System.out.println("aa_bb".lastIndexOf("_"));

		System.out.println("aa_bb".substring(0, 2));
		System.out.println("aa_bb".substring(3, 5));


		List<String> fields = Lists.newArrayList("aa", "bb");
		String[] properties = fields.toArray(new String[fields.size()]);
		System.out.println(properties);


		System.out.println(UrlUtils.UrlPage("aaa?"));

		System.out.println(CommonUtils.getMd5("1" + "1" + "1" + "1494510734" + "A3F4A7E77AD7474E9105AD5B7DFB8240"));

		System.out.println(String.format("%010d", 1));
		System.out.println(String.format("%010d", 10));
		System.out.println(String.format("%010d", 100));
		System.out.println(String.format("%010d", 1000));
		System.out.println(String.format("%010d", 10000));
		System.out.println(String.format("%010d", 10000));
		System.out.println(String.format("%010d", 100000));
		System.out.println(String.format("%010d", 1000000));
		System.out.println(String.format("%010d", 10000000));
		System.out.println(String.format("%010d", 100000000));
		System.out.println(String.format("%010d", 1000000000));


		System.out.println(CommonUtils.getZeroHourTime(0));

		System.out.println(DoubleUtils.mul(1, DoubleUtils.div(200, 10000)));

		System.out.println(CommonUtils.getMd5("123456"));

		System.out.println(CommonUtils.getMd5(UUID.randomUUID().toString()).toUpperCase());

		String time = "1496677387";
		String s = "111" + time + "A3F4A7E77AD7474E9105AD5B7DFB8240";
		System.out.println(CommonUtils.getMd5(s));

		Calendar cal = Calendar.getInstance();
		System.out.println(cal.get(Calendar.YEAR));

		System.out.println(CommonUtils.getMonthStartEndTime(12, 2017));


		String xml = "<?xml version=\"1.0\" encoding=\"GBK\"?><AIPG>\n" +
                "  <INFO>\n" +
                "    <TRX_CODE>100014</TRX_CODE>\n" +
                "    <VERSION>03</VERSION>\n" +
                "    <DATA_TYPE>2</DATA_TYPE>\n" +
                "    <LEVEL>5</LEVEL>\n" +
                "    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
                "    <USER_NAME>20058100001175504</USER_NAME>\n" +
                "    <USER_PASS>111111</USER_PASS>\n" +
                "    <REQ_SN>2005810000117551377051780610</REQ_SN>\n" +
                "  </INFO>\n" +
                "  <TRANS>\n" +
                "    <BUSINESS_CODE>09100</BUSINESS_CODE>\n" +
                "    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
                "    <SUBMIT_TIME>20170629235500</SUBMIT_TIME>\n" +
                "    <BANK_CODE>102</BANK_CODE>\n" +
                "    <ACCOUNT_NO>6222023602099939977</ACCOUNT_NO>\n" +
                "    <ACCOUNT_NAME>test</ACCOUNT_NAME>\n" +
                "    <ACCOUNT_PROP>0</ACCOUNT_PROP>\n" +
                "    <AMOUNT>1</AMOUNT>\n" +
                "    <CURRENCY>CNY</CURRENCY>\n" +
                "    <TEL>13434245846</TEL>\n" +
                "    <CUST_USERID>252523524253xx</CUST_USERID>\n" +
                "  </TRANS>\n" +
                "</AIPG>";
		//System.out.println(RSAUtils.sign(xml, "utf-8"));


		String signXml = "<?xml version=\"1.0\" encoding=\"GBK\"?><AIPG>\n" +
                "  <INFO>\n" +
                "    <TRX_CODE>100014</TRX_CODE>\n" +
                "    <VERSION>03</VERSION>\n" +
                "    <DATA_TYPE>2</DATA_TYPE>\n" +
                "    <LEVEL>5</LEVEL>\n" +
                "    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
                "    <USER_NAME>20058100001175504</USER_NAME>\n" +
                "    <USER_PASS>111111</USER_PASS>\n" +
                "    <REQ_SN>2005810000117551377051780610</REQ_SN>\n" +
                "<SIGNED_MSG>7841c9862c1f1b82e7618a5f0c9f0cc53b739db77ed20ff81220c81d30d647bb313489d123523b4687f537977181c876a17e4740e227f3d9c5f66a9c69d1ac24aae6bcee74a835261b37b81e4da8b272ad9fd526344ad02582b218277dc043061b1337302f0a5e55428b2fe84bb9eb5044be1a25d35622751acafee2f458ba7d</SIGNED_MSG>\n" +
                "  </INFO>\n" +
                "  <TRANS>\n" +
                "    <BUSINESS_CODE>09100</BUSINESS_CODE>\n" +
                "    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
                "    <SUBMIT_TIME>20170629235500</SUBMIT_TIME>\n" +
                "    <BANK_CODE>102</BANK_CODE>\n" +
                "    <ACCOUNT_NO>6222023602099939977</ACCOUNT_NO>\n" +
                "    <ACCOUNT_NAME>test</ACCOUNT_NAME>\n" +
                "    <ACCOUNT_PROP>0</ACCOUNT_PROP>\n" +
                "    <AMOUNT>1</AMOUNT>\n" +
                "    <CURRENCY>CNY</CURRENCY>\n" +
                "    <TEL>13434245846</TEL>\n" +
                "    <CUST_USERID>252523524253xx</CUST_USERID>\n" +
                "  </TRANS>\n" +
                "</AIPG>";

		String queryXml = "<?xml version=\"1.0\" encoding=\"GBK\"?><AIPG>\n" +
				"  <INFO>\n" +
				"    <TRX_CODE>100014</TRX_CODE>\n" +
				"    <VERSION>03</VERSION>\n" +
				"    <DATA_TYPE>2</DATA_TYPE>\n" +
				"    <LEVEL>5</LEVEL>\n" +
				"    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
				"    <USER_NAME>20058100001175504</USER_NAME>\n" +
				"    <USER_PASS>111111</USER_PASS>\n" +
				"    <REQ_SN>2005810000117551377051780610</REQ_SN>\n" +
				"    <SIGNED_MSG>554779ca35ff92c3928a8d6b499c79d4126cf5b58040e341293bfa750c8b7aa12a07d8954fe1bcc6534ef03546b10bc6318d6238ab1bea213ce493f08589a757c82f1af9b9af1e73bcdf78746d1158d990416dee8cdddaeea595cd9ce9d3fff9d058a48c18399f58e090eb22872197d33d8418d2ab40903eb03d6ea36f366223</SIGNED_MSG>\n" +
				"  </INFO>\n" +
				"  <QTRANSREQ>\n" +
				"    <QUERY_SN>2005810000117551377051780610</QUERY_SN>\n" +
				"    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
				"    <STATUS>2</STATUS>\n" +
				"    <TYPE>1</TYPE>\n" +
				"  </QTRANSREQ>\n" +
				"</AIPG>";

		Header[] headers = { new BasicHeader("Content-Type", "application/xml") };

		//StringEntity stringEntity = new StringEntity(signXml, "GBK");
		StringEntity stringEntity = new StringEntity(queryXml, "GBK");

        String result = HttpsUtils.post("https://tlt.allinpay.com/aipg/ProcessServlet", headers, stringEntity);

        System.out.println(result);
    }

}
