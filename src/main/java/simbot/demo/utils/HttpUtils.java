package simbot.demo.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpUtils {

    private HttpUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static int REP_CODE = 200;

    /**
     * http连接池
     **/
    private static ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
            .getSocketFactory();
    private static LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
            .getSocketFactory();
    private static Registry<ConnectionSocketFactory> registry = RegistryBuilder
            .<ConnectionSocketFactory>create().register("http", plainsf)
            .register("https", sslsf).build();

    private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
            registry);

    static {
        // 将最大连接数增加到200
        cm.setMaxTotal(200);
        // 将每个路由基础的连接增加到20
        cm.setDefaultMaxPerRoute(20);
    }

    // 请求重试处理
    private static HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
        public boolean retryRequest(IOException exception,
                                    int executionCount, HttpContext context) {
            if (executionCount >= 3) {// 如果已经重试了3次，就放弃
                return false;
            }
            if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                return true;
            }
            if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                return false;
            }
            if (exception instanceof InterruptedIOException) {// 超时
                return false;
            }
            if (exception instanceof UnknownHostException) {// 目标服务器不可达
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                return false;
            }
            if (exception instanceof SSLException) {// ssl握手异常
                return false;
            }

            HttpClientContext clientContext = HttpClientContext
                    .adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        }
    };

    /**
     * ---end http连接池---
     **/
    private static CloseableHttpClient client = HttpClients.custom()
            .setConnectionManager(cm)
            .setRetryHandler(httpRequestRetryHandler).build();
    private static CloseableHttpClient sslClient = HttpClients.custom()
            .setConnectionManager(cm)
            .setRetryHandler(httpRequestRetryHandler).setSSLSocketFactory(new SSLConnectionSocketFactory(getSSLContext())).build();


    private static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setConnectionRequestTimeout(200000)
                .setConnectTimeout(200000).setSocketTimeout(200000).build();
    }

    private static String parseURL(String url) {
        if (StringUtils.isNotBlank(url)) {
            if (url.startsWith("http")) {
                return url;
            } else {
                return "http://" + url;
            }
        }
        return null;
    }

    private static String encodeParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        if (null != params) {
            Set<String> keys = params.keySet();
            int first = 0;
            for (String key : keys) {
                Object value = params.get(key);
                if (first > 0) {
                    sb.append("&");
                }
                first++;
                sb.append(key);
                sb.append("=");
                String v = String.valueOf(value);
                try {
                    String encodeValue = URLEncoder.encode(v, "UTF-8");
                    sb.append(encodeValue);
                } catch (UnsupportedEncodingException e) {
                    logger.error("UnsupportedEncoding:" + "UTF-8");
                }
            }
        }
        return sb.toString();
    }

    private static void setHeaders(HttpRequestBase request,
                                   Map<String, Object> headers) {
        if (null != request && null != headers) {
            for (Entry<String, Object> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), (String) entry.getValue());
            }
        }
    }

    private static String getEncoding(String contentType) {
        if (contentType != null) {
            String[] strs = contentType.split(";");
            if (strs != null && strs.length > 1) {
                String charSet = strs[1].trim();
                String[] charSetKeyValues = charSet.split("=");
                if (charSetKeyValues.length == 2
                        && charSetKeyValues[0].equalsIgnoreCase("charset")) {
                    return charSetKeyValues[1];
                }
            }
        }
        return "UTF-8";
    }


    public static String httpGet(String url, Map<String, Object> headers, Map<String, Object> params) {
        String newUrl = parseURL(url);
        if (newUrl == null) {
            return null;
        }
        if (params != null) {
            newUrl = newUrl + "?" + encodeParams(params);
        }
        HttpGet httpGet = new HttpGet(newUrl);
        setHeaders(httpGet, headers);
        return httpGet(newUrl, httpGet);
    }

    private static String httpGet(String url, HttpGet httpGet) {
        try {
            CloseableHttpClient client = getInstance(url);
            HttpResponse response = client.execute(httpGet);
            //return getResponse(response);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (ClientProtocolException e) {
            logger.error("httpPost ClientProtocolException:" + e.getMessage());
        } catch (IOException e) {
            logger.error("httpPost IOException:" + e.getMessage());
        } finally {
            httpGet.releaseConnection();
        }
        return null;
    }

    public static String httpPost(String url, Map<String, Object> headers, Map<String, Object> params) {
        logger.info("[NimServer post] url=" + url + ",params=" + JSON.toJSONString(params));
        String newUrl = parseURL(url);
        HttpPost httpPost = new HttpPost(newUrl);
        setHeaders(httpPost, headers);
        try {
            if (params != null) {
                List<NameValuePair> list = new LinkedList<NameValuePair>();
                for (Entry<String, Object> entry : params.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
                HttpEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                httpPost.setEntity(entity);
            }
            return httpPost(newUrl, httpPost);
        } catch (UnsupportedEncodingException e) {
            logger.error("httpPost UnsupportedEncodingException:" + e.getMessage());
        }
        return null;
    }

    public static String httpPostWithJson(String url, String jsonParams) {
        String newUrl = parseURL(url);
        if (newUrl == null) {
            return null;
        }
        HttpPost httpPost = new HttpPost(newUrl);
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        if (StringUtils.isNotBlank(jsonParams)) {
            httpPost.setEntity(new StringEntity(jsonParams, Charset.forName("UTF-8")));
        }
        return httpPostResString(newUrl, httpPost);
    }

    public static String httpPost(String url, StringEntity entity,
                                  Map<String, Object> headers, Map<String, Object> params) {
        String newUrl = parseURL(url);
        if (newUrl == null) {
            return null;
        }
        if (params != null) {
            newUrl = newUrl + "?" + encodeParams(params);
        }
        HttpPost httpPost = new HttpPost(newUrl);
        if (null != entity) {
            httpPost.setEntity(entity);
        }
        setHeaders(httpPost, headers);
        return httpPost(newUrl, httpPost);
    }

    public static String httpPostStringEntity(String url, StringEntity params) {
        String newUrl = parseURL(url);
        if (newUrl == null) {
            return null;
        }
        HttpPost httpPost = new HttpPost(newUrl);
        if (null != params) {
            httpPost.setEntity(params);
        }
        return httpPost(newUrl, httpPost);
    }

    public static String httpPost(String url, Map<String, Object> params) {
        String newUrl = parseURL(url);
        HttpPost httpPost = new HttpPost(newUrl);
        try {
            if (params != null) {
                List<NameValuePair> list = new LinkedList<NameValuePair>();
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    list.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
                HttpEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                httpPost.setEntity(entity);
            }
            return httpPost(newUrl, httpPost);
        } catch (UnsupportedEncodingException e) {
            logger.error("httpPost UnsupportedEncodingException:" + e.getMessage());
        }
        return null;
    }


    private static String httpPost(String url, HttpPost httpPost) {
        CloseableHttpClient client = getInstance(url);
        try {

            httpPost.setConfig(getRequestConfig());
            HttpResponse response = client.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
            //return getResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("httpPost ClientProtocolException:" + e.getMessage());
        } catch (IOException e) {
            logger.error("httpPost IOException:" + e.getMessage());
        } finally {
            httpPost.releaseConnection();
        }
        return null;
    }

    private static String httpPostResString(String url, HttpPost httpPost) {
        CloseableHttpClient client = getInstance(url);
        try {
            httpPost.setConfig(getRequestConfig());
            HttpResponse response = client.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (ClientProtocolException e) {
            logger.error("httpPost ClientProtocolException:" + e.getMessage());
        } catch (IOException e) {
            logger.error("httpPost IOException:" + e.getMessage());
        } finally {
            httpPost.releaseConnection();
        }
        return null;
    }

    private static HttpRespMeta getResponse(HttpResponse response) {
        InputStream inputStream = null;
        if (response != null) {
            StatusLine line = response.getStatusLine();
            if (line != null) {
                HttpRespMeta responseMeta = new HttpRespMeta();
                int code = line.getStatusCode();
                logger.info(">>>>>>>>code:" + code);
                responseMeta.setCode(code);
                if (code == REP_CODE) {
                    try {
                        inputStream = response.getEntity().getContent();
                        if (inputStream != null) {
                            byte[] bs = IOUtils.toByteArray(inputStream);
                            responseMeta.setResponse(bs);
                            Header contentType = response.getEntity().getContentType();
                            responseMeta.setContentType(contentType.getValue());
                            responseMeta.setEncode(getEncoding(contentType.getValue()));
                        }
                    } catch (IllegalStateException e) {
                        logger.error("getResponse IllegalStateException:" + e.getLocalizedMessage());
                    } catch (IOException e) {
                        logger.error("getResponse IOException:" + e.getLocalizedMessage());
                    } finally {
                        IOUtils.closeQuietly(inputStream);
                    }
                }
                return responseMeta;
            }
        }
        return null;
    }

    private static CloseableHttpClient getInstance(String url) {
        if (StringUtils.isNotBlank(url) && url.startsWith("https")) {
            return sslClient;
        }
        return client;
    }

    private static SSLContext getSSLContext() {
        try {
            return new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (KeyManagementException e) {
            logger.error("HttpClient KeyManagementException:" + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            logger.error("HttpClient NoSuchAlgorithmException:" + e.getMessage());
        } catch (KeyStoreException e) {
            logger.error("HttpClient KeyStoreException:" + e.getMessage());
        }
        return null;
    }

    public static class HttpRespMeta {
        private String encode;
        private int code;
        private String contentType;
        private byte[] response;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public ByteArrayInputStream getResponseAsInputStream() {
            return new ByteArrayInputStream(response);
        }

        public long getResponseLength() {
            return response.length;
        }

        public String getResponseAsString() throws UnsupportedEncodingException {
            return new String(response, encode);
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public byte[] getResponseAsBytes() {
            return response;
        }

        public String getEncode() {
            return encode;
        }

        public void setEncode(String encode) {
            this.encode = encode;
        }

        public void setResponse(byte[] response) {
            this.response = response;
        }
    }

}
