package com.bfd;

import com.bfd.model.ProxyIp;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by BFD_303 on 2017/7/7.
 */
public class WorkCache {
    public static final Map<String,String> headers = new HashMap<>();
    static{
        headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
    }

    public static final BlockingQueue<ProxyIp> PROXY_QUEUE = new LinkedBlockingDeque<>();
    public static final BlockingQueue<Future<ProxyIp>> FUTURE_LIST = new LinkedBlockingDeque<>();
    public static final Gson gson = new Gson();
}
