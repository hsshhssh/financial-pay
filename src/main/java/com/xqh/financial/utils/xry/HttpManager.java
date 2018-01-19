package com.xqh.financial.utils.xry;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpManager {

	public static synchronized String sendGetRequest(String url){
		String result = "";
		CloseableHttpClient httpclient = HttpClients.createDefault(); 
		//实例化get方法  
		HttpGet httpget = new HttpGet(url);   
		//请求结果  
		CloseableHttpResponse response = null;  
		try {  
			//执行get方法  
			response = httpclient.execute(httpget);  
			if(response.getStatusLine().getStatusCode()==200){  
				result = EntityUtils.toString(response.getEntity(),"utf-8");  
				System.out.println(result);  
			}  
		} catch (ClientProtocolException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		return result;
	}

	public static String post(String url,Map<String, String> params){  
		//实例化httpClient  
		CloseableHttpClient httpclient = HttpClients.createDefault();  
		//实例化post方法  
		HttpPost httpPost = new HttpPost(url);   
		//处理参数  
		List<NameValuePair> nvps = new ArrayList <NameValuePair>();    
		Set<String> keySet =  params.keySet();    
		for(String key : keySet) {    
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		//结果  
		CloseableHttpResponse response = null;  
		String content="";  
		try {  
			//提交的参数  
			UrlEncodedFormEntity uefEntity  = new UrlEncodedFormEntity(nvps, "UTF-8");  
			//将参数给post方法  
			httpPost.setEntity(uefEntity);  
			//执行post方法  
			response = httpclient.execute(httpPost);  
			System.out.println("状态码："+response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode()==200){  
				content = EntityUtils.toString(response.getEntity(),"utf-8");  
			}  
		} catch (ClientProtocolException e) {  
			e.printStackTrace();  
		} catch (IOException e) {
			e.printStackTrace();  
		}   
		System.out.println(content);
		return content;
	}


	//public static synchronized Map<String, String> postXml(String url,Map<String, String> map,String key) throws Exception{
	//	CloseableHttpResponse response = null;
	//	CloseableHttpClient client = null;
	//	Map<String, String> res = new HashMap<>();
	//	try {
	//		HttpPost httpPost = new HttpPost(url);
	//		StringEntity entityParams = new StringEntity(XmlUtils.toXml(map),"utf-8");
	//		httpPost.setEntity(entityParams);
	//		httpPost.setHeader("Content-Type", "text/xml;charset=utf-8");
	//		client = HttpClients.createDefault();
	//		response = client.execute(httpPost);
	//		if(response != null && response.getEntity() != null){
	//			Map<String,String> resultMap = XmlUtils.toMap(EntityUtils.toByteArray(response.getEntity()), "utf-8");
	//			System.out.println("请求结果：" + resultMap.toString());
	//				res = resultMap;
	//		}else{
	//			res.put("status", "1000");
	//			res.put("msg", "请求失败");
	//		}
	//	} catch (Exception e) {
	//		e.printStackTrace();
	//		res.put("status", "2000");
	//		res.put("msg", "请求出现异常");
	//	} finally {
	//		if(response != null){
	//			response.close();
	//		}
	//		if(client != null){
	//			client.close();
	//		}
	//	}
	//	return res;
	//}

	
	/**
	 * 
	 * @param url
	 * @param json
	 * @return res 0会员开通成功
	 * @throws IOException
	 */
	public static synchronized String postJson(String url,String json) throws IOException{
		CloseableHttpResponse response = null;
		CloseableHttpClient client = null;
		String res = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			StringEntity entityParams = new StringEntity(json,"utf-8");
			httpPost.setEntity(entityParams);
			httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
			client = HttpClients.createDefault();
			response = client.execute(httpPost);
			if(response != null && response.getEntity() != null){
				JSONObject jsonResult = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
				System.out.println("jsonResult=="+jsonResult.toJSONString());
				//关闭HttpEntity流
				EntityUtils.consume(response.getEntity());
				if(jsonResult!=null){
					res = jsonResult.toJSONString();
				}
			}else{
				res = "操作失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
			res = "系统异常";
		} finally {
			if(response != null){
				response.close();
			}
			if(client != null){
				client.close();
			}
		}
		return res;
	}
}
