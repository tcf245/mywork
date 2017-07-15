package com.bfd.http;

import com.bfd.model.ProxyIp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by BFD_303 on 2017/7/7.
 */
public class OkHttpUtils {
    private static final MyCookieManager cookieJar = new MyCookieManager();
    private static final OkHttpClient dClient = new OkHttpClient().newBuilder()
            .cookieJar(cookieJar)
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();

    private static String doGet(Request request,OkHttpClient client) throws Exception{
        if (client == null)
            client = dClient;

        Response response = null;
        try{
            response = client.newCall(request).execute();
            System.out.println(request.url() + " => get status code is " + response.code());
//            cookieJar.loadForRequest(request.url()).forEach(c -> System.out.println(c.name() + " -> " + c.value()));
            return response.isSuccessful() ? response.body().string() : "";
        }finally {
            if (response != null)
                response.close();
        }
    }

    private static String doGet(String url,Map<String,String> headers,OkHttpClient client) throws Exception{
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();
        return doGet(request,client);
    }

    public static String doGet(String url,Map<String,String> headers)throws Exception{
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();
        return doGet(request,dClient);
    }

    public static String doGet(String url, Map<String,String> headers, ProxyIp ip) throws Exception {
        if (ip == null)
            return doGet(url,headers);

        OkHttpClient client = dClient.newBuilder()
                .cookieJar(cookieJar)
                .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(ip.getIp(),ip.getPort())))
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        return doGet(url,headers,client);
    }
}
