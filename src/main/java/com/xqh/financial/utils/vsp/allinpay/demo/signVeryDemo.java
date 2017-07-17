package com.xqh.financial.utils.vsp.allinpay.demo;

import com.xqh.financial.utils.vsp.allinpay.XmlTools;
import com.xqh.financial.utils.vsp.allinpay.security.CryptNoRestrict;

/**
 * 张广海
 * 签名和验签demo
 * 2012-11-09
 */

public class signVeryDemo {

	private static String pathPfx="config/allinpay-pds.pfx";
//	private static String pathPfx="config/lufax_20029000001128304.pfx";
//	private static String cerFile="config/20060400000070803.cer";
	private static String cerFile="config/allinpay-pds.cer";
//	private static String pathPfx="cer/20060400000044502.p12";
//	private static String cerFile="cer/20060400000044502.cer";
//	private static String cerFile="cer/allinpay.cer";
	private static String pass="allinpay-pds";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String signmsg="",strData="苏州银行";
		boolean result=false;
//		File file=new File("documents/signtext");
//		InputStream in=new FileInputStream(file);
//		strData=IOUtils.toString(in);
		strData="<?xml version=\"1.0\" encoding=\"GBK\"?><AIPG><BODY><TRANS_SUM><BUSINESS_CODE>09900</BUSINESS_CODE><TOTAL_ITEM>1</TOTAL_ITEM><SUBMIT_TIME>20140512132555</SUBMIT_TIME><TOTAL_SUM>1</TOTAL_SUM><MERCHANT_ID>200290000011263</MERCHANT_ID><SETTDAY></SETTDAY></TRANS_SUM><TRANS_DETAILS><TRANS_DETAIL><SUMMARY>其他费用</SUMMARY><ACCOUNT_NO>6228480038258717875</ACCOUNT_NO><UNION_BANK></UNION_BANK><ACCOUNT_NAME>张洪妍</ACCOUNT_NAME><REMARK></REMARK><ID_TYPE>0</ID_TYPE><SN>0001</SN><SETTGROUPFLAG></SETTGROUPFLAG><AMOUNT>1</AMOUNT><ACCOUNT_TYPE>00</ACCOUNT_TYPE><PROTOCOL_USERID></PROTOCOL_USERID><BANK_NAME></BANK_NAME><CUST_USERID>201405120000000056090017</CUST_USERID><E_USER_CODE></E_USER_CODE><CURRENCY>CNY</CURRENCY><TEL></TEL><SETTACCT></SETTACCT><PROVINCE></PROVINCE><ID>210202198811040022</ID><BANK_CODE>0103</BANK_CODE><ACCOUNT_PROP>0</ACCOUNT_PROP><CITY></CITY><PROTOCOL></PROTOCOL></TRANS_DETAIL></TRANS_DETAILS></BODY><INFO><VERSION>03</VERSION><USER_PASS>111111</USER_PASS><SIGNED_MSG>6baf3b273af461baf2f63333c9da6bc657f6171df4e85671b626b6c1f119527e0443516adfaa32f25794f5387b9a63734796bfa053db9183228fea474dfd620c21d52dcfea27ab0cac592a80a0e986ebf5382363f3f420aa8d662c9c4da32219043ed12c560406f3562c2d07f45bd538708e8bd6ebf3b5f125c340be62351e6adb3b1a711f932fdf4b4d6089b5a14181709e4ac28bb76acf28368aeb5dfcce4e9c8c3103e6f28511b848e03c8b43ab5f2d434fcbc93a3b1a3e2c1d59b6b5e30f9812ce47e44dc173fd37520c84d3f7c1b6089940cfbd905d71da5e8ba26443fcdfde8a77e106c3db71805c485a9b5a9cd79a2a624811152d9d1f9180d535e3cd</SIGNED_MSG><USER_NAME>20029000001126304</USER_NAME><LEVEL>5</LEVEL><TRX_CODE>100002</TRX_CODE><REQ_SN>200290000011263_00000000000000025016</REQ_SN><DATA_TYPE>2</DATA_TYPE></INFO></AIPG>";
//		strData="<?xml version=\"1.0\" encoding=\"GBK\"?><AIPG><INFO><TRX_CODE>100001</TRX_CODE><VERSION>03</VERSION><DATA_TYPE>2</DATA_TYPE><LEVEL>5</LEVEL><USER_NAME>20060400000044502</USER_NAME><USER_PASS>111111</USER_PASS><REQ_SN>00000000000000700076</REQ_SN><SIGNED_MSG>606eb3e7277cef85443eb2d0ebe51f62dfb3ad86ed602e6cf8c4f9d5c062f30f9bfe17cc6f3f11a708486ff75c4ec54db31e3be87337bf7e6261f5b1e4a55588d00ff8a7f9361efed9ab43514b896547d94ad598d6a6247fad37d1a08e2dc3803aee0def4ab522a393fcdf30353033a87a9c463015739e7cad89613eeadb58c6</SIGNED_MSG></INFO><BODY><TRANS_SUM><BUSINESS_CODE>10600</BUSINESS_CODE><MERCHANT_ID>200604000000445</MERCHANT_ID><SETTDAY /><SUBMIT_TIME>20160509175005</SUBMIT_TIME><TOTAL_ITEM>1</TOTAL_ITEM><TOTAL_SUM>1267100</TOTAL_SUM></TRANS_SUM><TRANS_DETAILS><TRANS_DETAIL><SN>0001</SN><E_USER_CODE /><BANK_CODE>301</BANK_CODE><ACCOUNT_TYPE /><ACCOUNT_NO>123478901234</ACCOUNT_NO><ACCOUNT_NAME>张力</ACCOUNT_NAME><PROVINCE /><CITY /><BANK_NAME /><ACCOUNT_PROP>0</ACCOUNT_PROP><AMOUNT>1267100</AMOUNT><CURRENCY /><PROTOCOL /><PROTOCOL_USERID /><ID_TYPE>0</ID_TYPE><ID>370101830401125</ID><TEL /><CUST_USERID /><REMARK>86370199000566</REMARK><RESERVE1 /><RESERVE2 /></TRANS_DETAIL></TRANS_DETAILS></BODY></AIPG>";
		String mersign=null;
		int iStart = strData.indexOf("<SIGNED_MSG>");
		if(iStart==-1) throw new Exception("XML报文中不存在<SIGNED_MSG>");
		int end = strData.indexOf("</SIGNED_MSG>");
		if(end==-1) throw new Exception("XML报文中不存在</SIGNED_MSG>");	
		mersign = strData.substring(iStart + 12, end);
		strData =strData.substring(0, iStart) + strData.substring(end + 13);
		strData="64563425345325345|13434245847|245245|745635267|46346346346|345";
		System.out.println("签名原文：\n"+strData);
		System.out.println("原签名信息："+signmsg);
		System.out.println("原签名信息长度："+signmsg.length());
		signmsg= XmlTools.signPlain(strData, pathPfx, pass, false);
		System.out.println("签名后的字符串："+signmsg);
		System.out.println("签名后的字符长度："+signmsg.length());
		if(mersign.equalsIgnoreCase(signmsg)){
			System.out.println("商户签名信息与通联签名信息一致");
		}
		CryptNoRestrict crypt=new CryptNoRestrict("GBK");
		result=crypt.VerifyMsg(signmsg.toLowerCase(), strData,cerFile);
		System.out.println("验签结果："+result);

	}

}
