package com.xqh.financial;

import com.google.common.collect.Lists;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.DoubleUtils;
import com.xqh.financial.utils.UrlUtils;
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



    }

}
