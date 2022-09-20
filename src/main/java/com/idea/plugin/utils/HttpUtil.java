package com.idea.plugin.utils;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;


public class HttpUtil {
    private static final int CONNECT_TIMEOUT = 300;
    private static final int SOCKET_TIMEOUT = 1000;

    private HttpUtil() {
    }

    public static <T> T get(String url, Class<T> clazz) {
        return JsonUtil.fromJson(HttpUtil.get(url), clazz);
    }

    public static String get(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String result = null;
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
             HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build());
            response = httpclient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpclient);
        }
        return result;
    }

    public static String get(String url, Map<String, Object> params) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String paramStr = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + encode(String.valueOf(e.getValue()))).collect(Collectors.joining("&"));
        url = url.contains("?") ? url + "&" + paramStr : url + "?" + paramStr;
        return get(url);
    }

    public static String encode(String word) {
        try {
            return URLEncoder.encode(word, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return StringUtils.EMPTY;
        }
    }

}
