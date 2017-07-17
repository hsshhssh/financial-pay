package com.xqh.financial.utils.vsp.allinpay.demo;

import com.xqh.financial.utils.vsp.allinpay.XmlTools;
import com.xqh.financial.utils.vsp.allinpay.security.CryptNoRestrict;

/**
 * 张广海
 * 签名和验签demo
 * 2012-11-09
 */

public class SignTest {

	//private static String cerFile="cert/allinpay-pds.cer";
	private static String cerFile="cert/allinpay-pds2.cer";
	//private static String pathPfx="cert/20060400000044502.p12";
	private static String pathPfx="cert/20058100001175504.p12";
	//private static String pathPfx="H:\\project\\github\\financial-pay\\src\\test\\resources\\20058100001175504.p12";
//	private static String cerFile="cer/20065100000518104.cer";
//	private static String pathPfx="cer/20065100000518104.p12";
 	private static String pass="111111";
	/**
	 * @param args
	 * @throws Exception com.allinpay.demoSignTest.java
	 */
	public static void main(String[] args) throws Exception {
		String signmsg="",strData;
		boolean result=false;
		//你们系统发往通联的请求报文
		strData= "<?xml version=\"1.0\" encoding=\"GBK\"?><AIPG>\n" +
				"  <INFO>\n" +
				"    <TRX_CODE>100014</TRX_CODE>\n" +
				"    <VERSION>03</VERSION>\n" +
				"    <DATA_TYPE>2</DATA_TYPE>\n" +
				"    <LEVEL>5</LEVEL>\n" +
				"    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
				"    <USER_NAME>20058100001175504</USER_NAME>\n" +
				"    <USER_PASS>111111</USER_PASS>\n" +
				"    <REQ_SN>2005810000117551377051780610</REQ_SN>\n" +
				"<SIGNED_MSG>676918ec6ba0cbc24f073aa308f9000fd6421d39ba36769069cfe06c61579a85583c205f1c95bc04433e2140965ed86b9c58d6e7a75ac19663da920660f6ef1c2e27a91866f717d8fe9d3271ecdfdb938e8f3c8b0b6966f9b19b1b6f796bb5c02653e21ec7b95d8105681f9c6a5f6e3b34f2007afaad2d959bffcab6272bb335</SIGNED_MSG>\n" +
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
		//strData = "<?xml version=\"1.0\" encoding=\"GBK\"?><AIPG>\n" +
		//		"  <INFO>\n" +
		//		"    <TRX_CODE>100014</TRX_CODE>\n" +
		//		"    <VERSION>03</VERSION>\n" +
		//		"    <DATA_TYPE>2</DATA_TYPE>\n" +
		//		"    <LEVEL>5</LEVEL>\n" +
		//		"    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
		//		"    <USER_NAME>20058100001175504</USER_NAME>\n" +
		//		"    <USER_PASS>111111</USER_PASS>\n" +
		//		"    <REQ_SN>2005810000117551377051780610</REQ_SN>\n" +
		//		"    <SIGNED_MSG>71356e3319c62d2b3e038f8d56bd2e64f53cd46b5d3d67748afa62aeb958d344034093d4e6ec41b12f2e643ce5d3f6608ce42c315e0ebb5924028547553b7c0a0fae4f6f9f26cfae3b80f45e1c84b7a76a73acb8b48a962b1e43abd19ed412f3abce5740ed0db7cf529e9673edbcd015b44a8b0263bf85667862a19a7d4d543b</SIGNED_MSG>\n" +
		//		"  </INFO>\n" +
		//		"  <QTRANSREQ>\n" +
		//		"    <QUERY_SN>2005810000117551377051780610</QUERY_SN>\n" +
		//		"    <MERCHANT_ID>200581000011755</MERCHANT_ID>\n" +
		//		"    <STATUS>2</STATUS>\n" +
		//		"    <TYPE>1</TYPE>\n" +
		//		"  </QTRANSREQ>\n" +
		//		"</AIPG>";
		String mersign=null;//商户系统生成的签名结果
		int iStart = strData.indexOf("<SIGNED_MSG>");
		if(iStart==-1) throw new Exception("XML报文中不存在<SIGNED_MSG>");
		int end = strData.indexOf("</SIGNED_MSG>");
		if(end==-1) throw new Exception("XML报文中不存在</SIGNED_MSG>");	
		mersign = strData.substring(iStart + 12, end);
		strData =strData.substring(0, iStart) + strData.substring(end + 13);
//		strData = "7cf89d6b611504da73964af11ac4e9e9f95535d95569be2595f2f9952cf923698a4e72a12c0ded1ab754379bce28e62e2c628709a5a871042f0f343993717adfc7cae49d1cc0f2e0846ee982f38357996a48bc728a85ceae6e7e6585cc51efe82fa451ac44848182df7064e26eaeb445974a23a108ab8b07a894300d4dc3c148";
		System.out.println("签名原文：\n"+strData);
		System.out.println("商户系统生成的签名信息："+signmsg);
		System.out.println("商户系统生成的签名信息长度："+signmsg.length());
		signmsg= XmlTools.signPlain(strData, pathPfx, pass, false);
		System.out.println("通联生成的签名的字符串："+signmsg);
		System.out.println("通联生成的签名的字符长度："+signmsg.length());
		if(mersign.equalsIgnoreCase(signmsg)){
			System.out.println("商户签名信息与通联签名信息一致");
		}
		CryptNoRestrict crypt=new CryptNoRestrict("GBK");
		result=crypt.VerifyMsg(signmsg.toLowerCase(), strData,cerFile);
		System.out.println("验签结果："+result);

	}

}
