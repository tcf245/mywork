package com.bfd.crawler;

import com.bfd.WorkCache;
import com.bfd.http.OkHttpUtils;
import com.bfd.model.ProxyDao;
import com.bfd.model.ProxyIp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by BFD_303 on 2017/7/7.
 */
public class Xicidaili implements Runnable{
    private static final Log LOG = LogFactory.getLog(Xicidaili.class);
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        String url = "";
        for (int i = 1; i <= 200; i++) {
            queue.add("http://www.xicidaili.com/nn/" + i);
            queue.add("http://www.xicidaili.com/nt/" + i);
            queue.add("http://www.xicidaili.com/wn/" + i);
            queue.add("http://www.xicidaili.com/wt/" + i);
        }

//        BlockingQueue<ProxyIp> ips = null;
//        try {
//            ips = new LinkedBlockingDeque<>(new ProxyDao().query(100));
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        while (queue.size() > 0){
            try {
                url = queue.poll();
                String html = OkHttpUtils.doGet(url, WorkCache.headers);
                parse(html);
                Thread.sleep(2000 + ((long)Math.random()*2000));
            } catch (Exception e) {
                LOG.error(url + " error! ");
                queue.add(url);
                e.printStackTrace();
            }
        }
    }

    public void parse(String html){
        Document doc = Jsoup.parse(html,"http://www.xicidaili.com");

        Elements es = doc.select("table#ip_list tbody tr");
        LOG.debug("get proxyip size is : " + es.size());
        if (es.size() > 0){
            es.remove(0);
        }

        es.forEach(e -> {
            Elements tds = e.select("td");
            if (tds.size() == 10){
                String ip = tds.get(1).text();
                int port = Integer.parseInt(tds.get(2).text());
                //todo 消重判断

                WorkCache.PROXY_QUEUE.add(new ProxyIp(ip,port));
            }
        });
    }


    public static void main(String[] args) {
        for (int i = 0; i < 2000; i++) {
            String url = "http://www.xicidaili.com/nn/" + i;
            Map<String,String> headers = WorkCache.headers;
            headers.put("Upgrade-Insecure-Requests","1");
            headers.put("remoteAddress","180.118.241." + i%255 );
            headers.put("x-forwarded-for","123.57.17." + i%255 );
            headers.put("x-real-ip","123.55.15." + i%255 );
            try {
                String content = OkHttpUtils.doGet(url, headers);
                System.out.println(i++ + " ==> " + content);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + "  <=  failed");
            }
        }
    }

}
