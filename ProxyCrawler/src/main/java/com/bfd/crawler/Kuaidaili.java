package com.bfd.crawler;

import com.bfd.WorkCache;
import com.bfd.http.OkHttpUtils;
import com.bfd.model.ProxyIp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by BFD_303 on 2017/7/13.
 */
public class Kuaidaili implements Runnable{
    private static final Log LOG = LogFactory.getLog(Kuaidaili.class);
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        String url = "";
//        queue.add(url);
        for (int i = 1; i <= 500; i++) {
            queue.add("http://www.kuaidaili.com/free/inha/" + i);
        }

        while (queue.size() > 0){
            try {
                url = queue.poll();
                String html = OkHttpUtils.doGet(url, WorkCache.headers);
                parse(html);
                Thread.sleep(6000 + ((long)Math.random()*2000));
            } catch (Exception e) {
                LOG.error(url + " error! ");
                queue.add(url);
                e.printStackTrace();
            }
        }
    }

    private void parse(String html) {
        Document doc = Jsoup.parse(html,"http://www.kuaidaili.com");

        Elements es = doc.select("table.table tbody tr");
        LOG.debug("get proxyip size is : " + es.size());

        es.forEach(e -> {
            String ip = e.select("td[data-title=\"IP\"]").text();
            int port = Integer.valueOf(e.select("td[data-title=\"PORT\"]").text());
            WorkCache.PROXY_QUEUE.add(new ProxyIp(ip,port));
        });
    }

    public static void main(String[] args) throws Exception {
        String html = OkHttpUtils.doGet("http://www.kuaidaili.com/free/inha/1",WorkCache.headers);
        new Kuaidaili().parse(html);

    }

}
