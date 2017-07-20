package com.bfd.http;

import com.bfd.model.ProxyIp;
import okhttp3.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.bfd.WorkCache.headers;

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
//            cookieJar.loadForRequest(request.url()).forEach(c -> System.out.println(c.name() + " -> " + c.value()));
            return response.isSuccessful() ? response.body().string() : "";
        }finally {
            if (response != null)
                response.close();
        }
    }

    private static String doGet(String url, Map<String,String> headers, OkHttpClient client) throws Exception{
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();
        return doExecute(request,client);
    }

    public static String doGet(String url, Map<String,String> headers)throws Exception{
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();
        return doExecute(request,dClient);
    }

    public static String doGet(String url, Map<String,String> headers, ProxyIp ip) throws Exception {
        if (ip == null)
            return doGet(url,headers);

        OkHttpClient client = dClient.newBuilder()
                .cookieJar(cookieJar)
                .proxy(new Proxy(ip.getType(),new InetSocketAddress(ip.getIp(),ip.getPort())))
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        return doGet(url,headers,client);
    }

    public static String doPost(String url, Map<String,String> headers,Map<String,String> params) throws Exception {
        FormBody.Builder bodyBuilder= new FormBody.Builder();
        params.forEach((k,v) -> bodyBuilder.add(k,v));
        RequestBody body = bodyBuilder.build();

        Request.Builder builder = new Request.Builder().url(url).post(body);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();
        return doExecute(request,dClient);
    }

    public static String doPost(String url, Map<String,String> headers,Map<String,String> params, ProxyIp ip) throws Exception {
        OkHttpClient client = null;
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

        OkHttpClient client = dClient.newBuilder()
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

    public static void main(String[] args) throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Accept-Encoding","gzip, deflate");
        headers.put("Accept-Language","en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
        headers.put("Cache-Control","max-age=0");
        headers.put("Content-Length","0");
        headers.put("Content-Type","application/x-www-form-urlencoded");
        headers.put("Cookie","sunc=tcf245@gmail.com; spwc=chaofan123; ASP.NET_SessionId=qhwrqkwf0eytrebxuunbztj2; _gat=1; _lang=en-US; _ga=GA1.2.346254641.1499336848; _gid=GA");
        headers.put("Host","www.gatherproxy.com");
        headers.put("Origin","http://www.gatherproxy.com");
        headers.put("Proxy-Connection","keep-alive");
        headers.put("Referer","http://www.gatherproxy.com/sockslist");
        headers.put("Upgrade-Insecure-Requests","1");
        headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");

        byte[] content = doPostByte("http://www.gatherproxy.com/sockslist/plaintext",headers,null,new ProxyIp("127.0.0.1",1080));
        FileUtils.writeByteArrayToFile(new File("etc/socks.txt"),content);
    }
}
