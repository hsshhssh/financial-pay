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
				"<SIGNED_MSG>aefe262129d2a7ab498bf87bd64ed4c01dbf61f3e63b148db465066a3496f1fe2a67e89f9aaba791a7406eb41c514e50ceefa9940143e681b561bf3a849fde08e294c88dcdb806647fa84768c1631124dd969524988b304a5f324f7c6e194463d9ba6f1632b13729fffacbd65ed47133d3d3c8d55b22f12ad59c2bee8f205299</SIGNED_MSG>\n" +
                "  </INFO>\n" +
                "  <TRANS>\n" +
                "    <BUSINESS_CODE>09900</BUSINESS_CODE>\n" +
                "    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
                "    <SUBMIT_TIME>20170705220930</SUBMIT_TIME>\n" +
                "    <BANK_CODE>102</BANK_CODE>\n" +
                "    <ACCOUNT_NO>6222023602099939977</ACCOUNT_NO>\n" +
                "    <ACCOUNT_NAME>test</ACCOUNT_NAME>\n" +
                "    <ACCOUNT_PROP>0</ACCOUNT_PROP>\n" +
                "    <AMOUNT>1</AMOUNT>\n" +
                "    <CURRENCY>CNY</CURRENCY>\n" +
                "    <TEL>13580517072</TEL>\n" +
                "    <CUST_USERID>252523524253xx</CUST_USERID>\n" +
                "  </TRANS>\n" +
                "</AIPG>";
		System.out.println(RSAUtils.sign(xml, "utf-8"));

		Header[] headers = { new BasicHeader("Content-Type", "application/xml") };

		//StringEntity stringEntity = new StringEntity(signXml, "GBK");
		StringEntity stringEntity = new StringEntity(xml, "GBK");

        String result = HttpsUtils.post("https://tlt.allinpay.com/aipg/ProcessServlet", headers, stringEntity);

        System.out.println(result);
    }

}
