package com.xqh.financial.utils;

import com.xqh.financial.entity.other.HttpResult;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by hssh on 2017/5/7.
 */
public class HttpUtils {

    private static CloseableHttpClient genHttpClient() {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setConnectTimeout(5000).setConnectionRequestTimeout(10000);
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfigBuilder.build()).build();

        return httpclient;
    }

    private static HttpResult genHttpResult(CloseableHttpResponse response) {
        HttpResult httpResult = new HttpResult();

        if(response != null) {
            try {
                httpResult.setStatus(response.getStatusLine().getStatusCode());
                InputStream inputStream = response.getEntity().getContent();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer, "utf-8");
                httpResult.setContent(writer.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpResult;
    }

    public static HttpResult get(String url) {

        // 设置连接时间为5秒
        CloseableHttpClient httpClient = genHttpClient();

        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return genHttpResult(response);

    }


    public static HttpResult post(String url, List<NameValuePair> nvps) {
        CloseableHttpClient httpClient = genHttpClient();

        HttpPost httpPost = new HttpPost(url);
        //拼接参数
        //List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        //nvps.add(new BasicNameValuePair("username", "vip"));
        //nvps.add(new BasicNameValuePair("password", "secret"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return genHttpResult(response);
    }

    public static HttpResult post(String url, Header[] headers, HttpEntity entity) {
        CloseableHttpClient httpClient = genHttpClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(headers);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return genHttpResult(response);
    }

}
