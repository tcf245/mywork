package com.bfd.tmall;

import com.bfd.WorkCache;
import com.bfd.http.OkHttpUtils;
import com.bfd.model.ProxyIp;
import org.apache.commons.io.FileUtils;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BFD_303 on 2017/7/12.
 */
public class TmallTest {
    public static void main(String[] args) throws Exception {
        int i = 1;

        for (int j = 0; j < 30; j++) {



        String listpage = FileUtils.readFileToString(new File("etc/tmalllist.txt"),"utf-8");
        Matcher m = Pattern.compile("data-id=\"(\\d+)\"").matcher(listpage);
        while (m.find()){
            String id = m.group(1);
            String url = "https://mdskip.taobao.com/core/initItemDetail.htm?itemId=" + id;
            Map<String,String> headers = WorkCache.headers;
            headers.put("Referer","https://detail.tmall.com/item.htm?scene=taobao_shop&id=" + id);
            headers.put("Upgrade-Insecure-Requests","1");
            headers.put("remoteAddress","180.118.241." + i%255 );
            headers.put("x-forwarded-for","123.57.17." + i%255 );
            headers.put("x-real-ip","180.10.241." + i%255 );
            try {
                String content = OkHttpUtils.doGet(url, headers);
                System.out.println(i++ + " ==> " + content);
                System.out.println(id + " ==> " + content.substring(content.indexOf("\"price\""),content.indexOf("\"price\"") + 16));
//                Thread.sleep(1000 + 23);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + "  <=  failed");
            }
        }


                }
    }
}
