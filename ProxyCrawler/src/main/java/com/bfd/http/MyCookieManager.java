package com.bfd.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by BFD_303 on 2017/7/7.
 */
class MyCookieManager implements CookieJar {
    private final Map<String,List<Cookie>> cookieStore = new ConcurrentHashMap<String,List<Cookie>>();

    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        if (list != null && list.size() > 0){
            cookieStore.put(httpUrl.host(),list);
        }
    }

    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl.host());
        return cookies == null ? new ArrayList<Cookie>() : cookies;
    }
}
