/**
 * @Filename：HttpUtil.java
 * @Author：caiqf
 * @Date�?013-9-23
 */
package com.xqh.financial.utils.pingan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

//import org.apache.commons.logging.LogFactory;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;

/**
 * @Class：HttpsUtil.java
 * @Description�?
 * @Author：caiqf
 * @Date�?013-9-23
 */
@SuppressWarnings("all")
public class HttpUtil {
	private static final Log log = LogFactory.getLog(HttpUtil.class);

	/**
	 * 
	 * HTTP协议GET请求方法
	 */
	public static String httpMethodGet(String url, String gb) {
		if (null == gb || "".equals(gb)) {
			gb = "UTF-8";
		}
		StringBuffer sb = new StringBuffer();
		URL urls;
		HttpURLConnection uc = null;
		BufferedReader in = null;
		try {
			urls = new URL(url);
			uc = (HttpURLConnection) urls.openConnection();
			uc.setRequestMethod("GET");
			uc.connect();
			in = new BufferedReader(new InputStreamReader(uc.getInputStream(),
					gb));
			String readLine = "";
			while ((readLine = in.readLine()) != null) {
				sb.append(readLine);
			}
			if (in != null) {
				in.close();
			}
			if (uc != null) {
				uc.disconnect();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (uc != null) {
				uc.disconnect();
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * HTTP协议POST请求方法
	 */
	public static String httpMethodPost(String url, String params, String gb) {
		if (null == gb || "".equals(gb)) {
			gb = "UTF-8";
		}
		StringBuffer sb = new StringBuffer();
		URL urls;
		HttpURLConnection uc = null;
		BufferedReader in = null;
		try {
			urls = new URL(url);
			uc = (HttpURLConnection) urls.openConnection();
			uc.setRequestMethod("POST");
			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setUseCaches(false);
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			uc.connect();
			DataOutputStream out = new DataOutputStream(uc.getOutputStream());
			out.write(params.getBytes(gb));
			out.flush();
			out.close();
			in = new BufferedReader(new InputStreamReader(uc.getInputStream(),
					gb));
			String readLine = "";
			while ((readLine = in.readLine()) != null) {
				sb.append(readLine);
			}
			if (in != null) {
				in.close();
			}
			if (uc != null) {
				uc.disconnect();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (uc != null) {
				uc.disconnect();
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * HTTP协议POST请求方法
	 */
	public static String httpMethodPost(String url,
                                        TreeMap<String, String> paramsMap, String gb) {
		if (null == gb || "".equals(gb)) {
			gb = "UTF-8";
		}
		String params = null;
		if (null != paramsMap) {
			params = getParamStr(paramsMap);
		}
		System.out.println("====post请求参数= "+params);
		StringBuffer sb = new StringBuffer();
		URL urls;
		HttpURLConnection uc = null;
		BufferedReader in = null;
		try {
			urls = new URL(url);
			uc = (HttpURLConnection) urls.openConnection();
			uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setRequestMethod("POST");
			uc.setUseCaches(false);
			uc.connect();
			DataOutputStream out = new DataOutputStream(uc.getOutputStream());
			out.write(params.getBytes(gb));
			out.flush();
			out.close();
			in = new BufferedReader(new InputStreamReader(uc.getInputStream(),
					gb));
			String readLine = "";
			while ((readLine = in.readLine()) != null) {
				sb.append(readLine);
			}
			if (in != null) {
				in.close();
			}
			if (uc != null) {
				uc.disconnect();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (uc != null) {
				uc.disconnect();
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * HTTP协议POST请求添加参数的封装方�?
	 */
	private static String getParamStr(TreeMap<String, String> paramsMap) {
		StringBuilder param = new StringBuilder();
		for (Iterator<Map.Entry<String, String>> it = paramsMap.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<String, String> e = it.next();
			param.append("&").append(e.getKey()).append("=")
					.append(e.getValue());
		}
		return param.toString().substring(1);
	}

//	/**
//	 * 
//	 * Author：tingzw
//	 * 
//	 * @Description:微信摇一�?----上传图片素材
//	 * @param @param url
//	 * @param @param paramsMap
//	 * @param @param fileName
//	 * @param @param file
//	 * @param @return
//	 * @return String
//	 * @throws
//	 */
//	@SuppressWarnings("deprecation")
//	public static String httpMethodPost(String url,
//			TreeMap<String, String> paramsMap, String fileName, File file) {
//		try {
//			HttpPost post = new HttpPost(url);
//			HttpResponse httpResponse;
//			MultipartEntity reEntity = new MultipartEntity();
//			FileBody filebody = new FileBody(file);
//
//			reEntity.addPart(fileName, filebody);
//			for (Iterator<Map.Entry<String, String>> it = paramsMap.entrySet()
//					.iterator(); it.hasNext();) {
//				Map.Entry<String, String> e = it.next();
//				reEntity.addPart(e.getKey(), new StringBody(e.getValue()));
//			}
//
//			post.setEntity(reEntity);
//			httpResponse = new DefaultHttpClient().execute(post);
//			HttpEntity httpEntity = httpResponse.getEntity();
//			String ret = EntityUtils.toString(httpEntity, "UTF-8");
//			return ret;
//		} catch (ClientProtocolException e) {
//			log.error(e.getMessage(), e);
//		} catch (IOException e) {
//			log.error(e.getMessage(), e);
//		}
//		return "";
//	}
}
