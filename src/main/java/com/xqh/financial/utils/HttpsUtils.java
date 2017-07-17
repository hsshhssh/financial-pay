package com.xqh.financial.utils;

import com.xqh.financial.entity.other.HttpResult;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author zhangminpeng on 2016-06-13 14:50
 */
public class HttpsUtils {

    /**
     * 创建 HTTPS 链接客户端,默认信任证书,不跟随重定向
     *
     * @return HTTP 连接
     */
    private static CloseableHttpClient createHttpsClient()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = new SSLContextBuilder()
                //.loadTrustMaterial(null, (chain, authType) -> true).build();
                .loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        return true;
                    }
                }).build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
        return HttpClients.custom()
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36")
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
    }

    ///**
    // * 发送 get 请求
    // *
    // * @param url     请求 url
    // * @param headers 请求头数组
    // * @return 响应结果字符串
    // * @throws NoSuchAlgorithmException
    // * @throws KeyStoreException
    // * @throws KeyManagementException
    // * @throws IOException
    // */
    //public static String get(String url, Header[] headers)
    //        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
    //    CloseableHttpClient httpClient = createHttpsClient();
    //    HttpGet httpGet = new HttpGet(url);
    //    httpGet.setHeaders(headers);
    //    HttpResponse httpResponse = httpClient.execute(httpGet);
    //    String result = entity2String(httpResponse.getEntity());
    //
    //    // 关闭资源
    //    httpClient.close();
    //    httpGet.releaseConnection();
    //    return result;
    //}
    //
    ///**
    // * 发送 get 请求
    // *
    // * @param url 请求 url
    // * @return 响应结果字符串
    // * @throws NoSuchAlgorithmException
    // * @throws KeyStoreException
    // * @throws KeyManagementException
    // * @throws IOException
    // */
    //public static String get(String url)
    //        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
    //    return get(url, null);
    //}

    /**
     * 发送 post 请求
     *
     * @param url     请求 url
     * @param headers 请求头
     * @param entity  请求实体
     * @return 响应结果字符串
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static HttpResult post(String url, Header[] headers, HttpEntity entity, String charset)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        CloseableHttpClient httpClient = createHttpsClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(headers);
        httpPost.setEntity(entity);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        return genHttpResult(httpResponse, charset);
    }

    private static HttpResult genHttpResult(CloseableHttpResponse response, String charset) {
        HttpResult httpResult = new HttpResult();

        if(response != null) {
            try {
                httpResult.setStatus(response.getStatusLine().getStatusCode());
                InputStream inputStream = response.getEntity().getContent();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer, charset);
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
}