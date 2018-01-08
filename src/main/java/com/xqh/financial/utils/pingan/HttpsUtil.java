/**
 * @Filename：HttpsUtil.java
 * @Author：caiqf
 * @Date�?013-9-23
 */
package com.xqh.financial.utils.pingan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Class：HttpsUtil.java
 * @Description�?
 * @Author：caiqf
 * @Date�?013-9-23
 */
public class HttpsUtil {
	private static final Log log = LogFactory.getLog(HttpsUtil.class);

	private static class MyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class MyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

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
		HttpsURLConnection uc = null;
		BufferedReader in = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new java.security.SecureRandom());
			urls = new URL(url);
			uc = (HttpsURLConnection) urls.openConnection();
			uc.setSSLSocketFactory(sc.getSocketFactory());
			uc.setHostnameVerifier(new MyHostnameVerifier());
			uc.setRequestMethod("GET");
			uc.connect();
			in = new BufferedReader(new InputStreamReader(uc.getInputStream(),
					"utf-8"));
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
	 * HTTP协议GET请求方法
	 */
	public static byte[] httpMethodGetFile(String url) {
		byte[] btFile = null;
		URL urls = null;
		HttpsURLConnection uc = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new java.security.SecureRandom());
			urls = new URL(url);
			uc = (HttpsURLConnection) urls.openConnection();
			uc.setSSLSocketFactory(sc.getSocketFactory());
			uc.setHostnameVerifier(new MyHostnameVerifier());
			uc.setRequestMethod("GET");
			uc.connect();
			InputStream inStream = uc.getInputStream(); // 获取文件流二进制数据
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			inStream.close();
			btFile = outStream.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (uc != null) {
				uc.disconnect();
			}
		}
		return btFile;
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
		HttpsURLConnection uc = null;
		BufferedReader in = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new java.security.SecureRandom());
			urls = new URL(url);
			uc = (HttpsURLConnection) urls.openConnection();
			uc.setSSLSocketFactory(sc.getSocketFactory());
			uc.setHostnameVerifier(new MyHostnameVerifier());
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
	public static byte[] httpMethodPostFile(String url, String params, String gb) {
		if (null == gb || "".equals(gb)) {
			gb = "UTF-8";
		}
		byte[] btFile = null;
		URL urls = null;
		HttpsURLConnection uc = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new java.security.SecureRandom());
			urls = new URL(url);
			uc = (HttpsURLConnection) urls.openConnection();
			uc.setSSLSocketFactory(sc.getSocketFactory());
			uc.setHostnameVerifier(new MyHostnameVerifier());
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
			InputStream inStream = uc.getInputStream(); // 获取文件流二进制数据
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			inStream.close();
			btFile = outStream.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (uc != null) {
				uc.disconnect();
			}
		}
		return btFile;
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
		HttpsURLConnection uc = null;
		BufferedReader in = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new java.security.SecureRandom());
			urls = new URL(url);
			uc = (HttpsURLConnection) urls.openConnection();
			uc.setSSLSocketFactory(sc.getSocketFactory());
			uc.setHostnameVerifier(new MyHostnameVerifier());
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
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (uc != null) {
				uc.disconnect();
			}
		}
		return URLDecoder.decode(sb.toString());
	}

	/**
	 * 
	 * HTTP协议POST请求方法
	 */
	public static byte[] httpMethodPostFile(String url,
                                            TreeMap<String, String> paramsMap, String gb) {
		if (null == gb || "".equals(gb)) {
			gb = "UTF-8";
		}
		String params = null;
		if (null != paramsMap) {
			params = getParamStr(paramsMap);
		}

		byte[] btFile = null;
		URL urls = null;
		HttpsURLConnection uc = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new java.security.SecureRandom());
			urls = new URL(url);
			uc = (HttpsURLConnection) urls.openConnection();
			uc.setSSLSocketFactory(sc.getSocketFactory());
			uc.setHostnameVerifier(new MyHostnameVerifier());
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
			InputStream inStream = uc.getInputStream(); // 获取文件流二进制数据
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			inStream.close();
			btFile = outStream.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (uc != null) {
				uc.disconnect();
			}
		}
		return btFile;
	}

	/**
	 * 
	 * HTTP协议POST请求添加参数的封装方�?
	 */
	public static String getParamStr(TreeMap<String, String> paramsMap) {
		StringBuilder param = new StringBuilder();
		for (Iterator<Map.Entry<String, String>> it = paramsMap.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<String, String> e = it.next();
			param.append("&").append(e.getKey()).append("=")
					.append(e.getValue());
		}
		return param.toString().substring(1);
	}
}
