package com.bfd.http;

import com.bfd.WorkCache;
import com.bfd.model.ProxyIp;
import okhttp3.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
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

    private static String doExecute(Request request, OkHttpClient client) throws Exception{
        if (client == null)
            client = dClient;

        Response response = null;
        try{
            response = client.newCall(request).execute();
            System.out.println(request.url() + " => get status code is " + response.code());
            return response.isSuccessful() ? response.body().string() : "";
        }finally {
            if (response != null)
                response.close();
        }
    }

    public static String doGet(String url, Map<String,String> headers, @Nullable ProxyIp ip) throws Exception {
        OkHttpClient client ;
        if (ip == null)
            client = dClient;
        else
        client = dClient.newBuilder()
                .cookieJar(cookieJar)
                .proxy(new Proxy(ip.getType(),new InetSocketAddress(ip.getIp(),ip.getPort())))
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();
        return doExecute(request,client);
    }

    public static String doPost(String url, Map<String,String> headers,Map<String,String> params, ProxyIp ip) throws Exception {
        OkHttpClient client;
        if (ip == null)
            client = dClient;
        else
            client = dClient.newBuilder()
                .cookieJar(cookieJar)
                .proxy(new Proxy(ip.getType(),new InetSocketAddress(ip.getIp(),ip.getPort())))
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();

        FormBody.Builder bodyBuilder= new FormBody.Builder();
        if (params != null){
            params.forEach((k,v) -> bodyBuilder.add(k,v));
        }
        RequestBody body = bodyBuilder.build();

        Request.Builder builder = new Request.Builder().url(url).post(body);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();
        return doExecute(request,client);
    }

    public static byte[] doPostByte(String url,Map<String,String> headers,Map<String,String> params,ProxyIp ip) throws IOException {
        OkHttpClient client;
        if (ip == null)
            client = dClient;
        else
            client = dClient.newBuilder()
                .cookieJar(cookieJar)
                .proxy(new Proxy(ip.getType(),new InetSocketAddress(ip.getIp(),ip.getPort())))
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        FormBody.Builder bodyBuilder= new FormBody.Builder();
        if (params != null){
            params.forEach((k,v) -> bodyBuilder.add(k,v));
        }
        RequestBody body = bodyBuilder.build();

        Request.Builder builder = new Request.Builder().url(url).post(body);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();

        Response response = null;
        try{
            response = client.newCall(request).execute();
            System.out.println(request.url() + " => get status code is " + response.code());
//            cookieJar.loadForRequest(request.url()).forEach(c -> System.out.println(c.name() + " -> " + c.value()));
            return response.isSuccessful() ? response.body().bytes() : null;
        }finally {
            if (response != null)
                response.close();
        }
    }

    public static byte[] doGetByte(String url,Map<String,String> headers) throws IOException {
        Request.Builder rBuilder = new Request.Builder().url(url);

        if (headers != null){
            headers.forEach((k,v) -> rBuilder.addHeader(k,v));
        }

        Request request = rBuilder.build();
        Response response = null;
        try{
            response = dClient.newCall(request).execute();
            System.out.println(request.url() + " => get status code is " + response.code());
            return response.isSuccessful() ? response.body().bytes() : null;
        }finally {
            if (response != null)
                response.close();
        }
    }

}
