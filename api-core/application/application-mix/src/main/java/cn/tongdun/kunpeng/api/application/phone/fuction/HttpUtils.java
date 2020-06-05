package cn.tongdun.kunpeng.api.application.phone.fuction;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * http工具类
 * 
 * @author enhao.wang
 * @date 2018-08-08
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static volatile CloseableHttpClient localHttpClient;

    /**
     * 默认超时时间:10分钟，获取连接的timeout和获取socket数据的timeout都是10分钟
     */
    private static int timeOutMilliSecond = 1000;

    private static String charset = "utf-8";

    private static final int MAX_TOTAL = 1000;

    /**
     * 连接池链接耗尽等待时间
     */
    private static final int CONNECTION_REQUEST_TIMEOUT = 2000;

    private static final int MAX_PER_ROUTE = 10;

    private static final Object SYNC_LOCK = new Object();

    private static HttpClientBuilder httpBuilder;

    private static Boolean HTTP_PROXY_ENABLE;

    private static String HTTP_PROXY_HOST;

    private static Integer HTTP_PROXY_PORT;

    private static CommonConfig commonConfig;
    static {
        // 初始化连接池管理器，设置http的状态参数
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        httpBuilder = HttpClients.custom();
        httpBuilder.setConnectionManager(connectionManager);
    }

    /**
     * 获取连接
     * 
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient getHttpClient() {
        if (localHttpClient == null) {
            synchronized (SYNC_LOCK) {
                    localHttpClient = createHttpClient();
            }
        }
        return localHttpClient;
    }

    /**
     * 创建带有超时时间的连接
     * 
     * @param invokeTimeout 超时时间
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient createHttpClient(int invokeTimeout) {
        if (invokeTimeout > 0 && invokeTimeout < 60 * 30 * 1000) {
            timeOutMilliSecond = invokeTimeout;
        }
        Builder builder = RequestConfig.custom().setSocketTimeout(timeOutMilliSecond).setConnectTimeout(timeOutMilliSecond).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
        RequestConfig defaultRequestConfig = builder.build();

        HttpClientBuilder httpClientBuilder = httpBuilder.setDefaultRequestConfig(defaultRequestConfig);

        // 添加代理配置
        if (HTTP_PROXY_ENABLE) {
            httpClientBuilder = httpClientBuilder.setProxy(new HttpHost(HTTP_PROXY_HOST, HTTP_PROXY_PORT));
        }

        // 添加了检测系统级代理
        CloseableHttpClient httpClient = httpClientBuilder.build();

        return httpClient;
    }

    /**
     * 获取带有超时的连接
     * 
     * @param invokeTimeout 超时时间
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient getHttpClient(int invokeTimeout) {
        synchronized (SYNC_LOCK) {
            localHttpClient = createHttpClient(invokeTimeout);
        }
        return localHttpClient;
    }

    /**
     * 创建连接
     * 
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient createHttpClient() {
        Builder builder = RequestConfig.custom().setSocketTimeout(timeOutMilliSecond).setConnectTimeout(timeOutMilliSecond);
        RequestConfig defaultRequestConfig = builder.build();
        HttpClientBuilder httpClientBuilder = httpBuilder.setDefaultRequestConfig(defaultRequestConfig);

        // 添加代理配置
        if (HTTP_PROXY_ENABLE) {
            httpClientBuilder = httpClientBuilder.setProxy(new HttpHost(HTTP_PROXY_HOST, HTTP_PROXY_PORT));
        }

        // 添加了检测系统级代理
        CloseableHttpClient httpClient = httpClientBuilder.build();

        return httpClient;
    }

    /**
     * GET方式调用
     * 
     * @param url url
     * @return String
     */
    public static String get(String url, Integer timeout) {
        logger.info("[HttpUtilsGet-request:]url:{}", url);
        HttpGet httpGet = new HttpGet(url);
        String response = "";
        if (null != timeout && timeout > 0) {
            // 设置超时时间50毫秒
            Builder builder = RequestConfig.custom();
            builder.setSocketTimeout(timeout).setConnectTimeout(timeout).setConnectionRequestTimeout(timeout);// 设置请求和传输超时时间
            RequestConfig requestConfig = builder.build();
            httpGet.setConfig(requestConfig);
        }
        try {
            response = handleGetResponseToString(getHttpClient(), httpGet);

        } catch (Exception e) {
            logger.error("HttpUtils-get异常:" + url, e);
        }
        logger.info("[HttpUtilsGet-response:]url:{},response:{}", url, response);
        return response;
    }

    /**
     * POST方式调用-参数MAP
     * 
     * @param url url
     * @param paramMap 参数map
     * @return string
     * @throws Exception
     */
    public static String post(String url, Map<String, String> paramMap) throws Exception {
        logger.info("[HttpUtilsPost-request:]url:{},paramMap:{}", url, paramMap);
        CloseableHttpClient httpClient = getHttpClient();

        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> paramsPair = new ArrayList<>();
        for (Entry<String, String> entry : paramMap.entrySet()) {
            paramsPair.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(paramsPair, charset));

        String response = handleResponseToString(httpClient, httpPost);
        logger.info("[HttpUtilsPost-response1:]url:{},response:{}", url, response);
        return response;
    }

    /**
     * POST方式调用-字符串
     * 
     * @param url url
     * @param postContent 入参字符串
     * @return string
     * @throws Exception
     */
    public static String post(String url, String postContent) throws Exception {
        logger.info("[HttpUtilsPost-request:]url:{},postContent:{}", url, postContent);
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(postContent, charset));
        String response = handleResponseToString(httpClient, httpPost);
        logger.info("[HttpUtilsPost-response:]url:{},postContent:{},response:{}", url, postContent, response);
        return response;
    }

    public static String post(String url, Map<String, String> paramMap, Map<String, String> headerMap) throws Exception {
        logger.info("[HttpUtilsPost-request:]url:{},paramMap:{},headerMap:{}", url, paramMap, headerMap);
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        headerMap.forEach((key, value) -> {
            httpPost.setHeader(key, value);
        });
        List<NameValuePair> paramsPair = new ArrayList<>();
        for (Entry<String, String> entry : paramMap.entrySet()) {
            paramsPair.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(paramsPair, charset));
        String response = handleResponseToString(httpClient, httpPost);
        logger.info("[HttpUtilsPost-response2:]url:{},response:{}", url, response);
        return response;
    }

    /**
     * 处理POST的返回结果
     * 
     * @param httpClient httpClient
     * @param httpPost post
     * @return string
     * @throws Exception
     */
    private static String handleResponseToString(CloseableHttpClient httpClient, HttpPost httpPost) throws Exception {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, charset);
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            logger.error("[HttpUtils-handleResponseToString异常]uri:" + httpPost.getURI(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return "";
    }

    /**
     * 处理GET的返回结果
     * 
     * @param httpClient httpClient
     * @param httpGet get
     * @return string
     * @throws Exception
     */
    private static String handleGetResponseToString(CloseableHttpClient httpClient, HttpGet httpGet) throws Exception {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, charset);
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            logger.error("[HttpUtils-handleGetResponseToString异常]uri:" + httpGet.getURI(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return "";
    }

    /**
     * @param commonConfig the commonConfig to set
     */
    public static void setCommonConfig(CommonConfig commonConfig) {
        HttpUtils.commonConfig = commonConfig;
        HttpUtils.HTTP_PROXY_ENABLE = Boolean.valueOf(commonConfig.getProxyEnable());
        HttpUtils.HTTP_PROXY_HOST = commonConfig.getProxyEnable();
        HttpUtils.HTTP_PROXY_PORT = Integer.valueOf(commonConfig.getProxyPort());
    }
}
