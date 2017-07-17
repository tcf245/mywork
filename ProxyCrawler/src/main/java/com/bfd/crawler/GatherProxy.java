package com.bfd.crawler;

import com.bfd.WorkCache;
import com.bfd.http.OkHttpUtils;
import com.bfd.model.ProxyIp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tcf24 on 2017/7/16.
 */
public class GatherProxy implements Runnable {
    private static final Log LOG = LogFactory.getLog(Xicidaili.class);
    private static Map<String, String> headers = WorkCache.headers;

    @Override
    public void run() {
        String url = "http://www.gatherproxy.com/zh/proxylist/country/?c=United%20States";

        headers.put("Referer","http://www.gatherproxy.com/zh/proxylist/country/?c=China");
        headers.put("Content-Type","application/x-www-form-urlencoded");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("Host","www.gatherproxy.com");

        ProxyIp proxyIp = new ProxyIp("127.0.0.1", 1080);
        Map<String, String> params = new HashMap<>();
        params.put("Country", "united states");
        params.put("Filter", "");
        params.put("Uptime", "0");

        for (int i = 1; i <= 15; i++) {
            try {
                params.put("PageIdx", i + "");
                String html = OkHttpUtils.doPost(url, headers, params, proxyIp);
                parse(html);
                Thread.sleep(2000 + ((long) Math.random() * 2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void parse(String html) {
        Document doc = Jsoup.parse(html);

        Elements es = doc.select("table#tblproxy tbody tr");
        LOG.debug("get proxyip size is : " + es.size());

        es.forEach(e -> {
            Elements tds = e.select("td script");
            if (tds.size() == 2) {
                String ip = tds.get(0).html().replace("document.write('","").replace("')","");
                String h = tds.get(1).html().replace("document.write(gp.dep('","").replace("'))","");
                // 16进制转10进制
                if (h != null && !"".equals(h)){
                    BigInteger bi = new BigInteger(h,16);
                    int port = Integer.parseInt(bi.toString());
                    //todo 消重判断

                    WorkCache.PROXY_QUEUE.add(new ProxyIp(ip, port));
                }

            }
        });
    }

    public static void main(String[] args) {
        GatherProxy gatherProxy = new GatherProxy();
        gatherProxy.run();

    }

}
