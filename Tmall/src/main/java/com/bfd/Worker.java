package com.bfd;

import com.bfd.http.OkHttpUtils;
import com.bfd.model.Item;
import com.bfd.model.ItemDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 得到Tmall商品详情页连接，发送请求获取价格、月销量、促销等信息，写入数据库
 * Created by BFD_303 on 2017/7/17.
 */
public class Worker implements Runnable {
    private static final Log LOG = LogFactory.getLog(Worker.class);
    private BlockingQueue<Item> queue = Start.QUEUE;
    private final String REG = "id=(\\d+)&?";
    private ItemDao dao = new ItemDao();

    @Override
    public void run() {
        Item item = null;
        int i = 1;
        for (;;){
            try {
                item = queue.take();
                Matcher m = Pattern.compile(REG).matcher(item.getUrl());
                if (!m.find()){
                    continue;
                }
                String id = m.group(1);

                String url = "https://mdskip.taobao.com/core/initItemDetail.htm?itemId=" + id;
                Map<String, String> headers = WorkCache.headers;
                headers.put("Referer", "https://detail.tmall.com/item.htm?scene=taobao_shop&id=" + id);
                headers.put("Upgrade-Insecure-Requests", "1");
                headers.put("remoteAddress", "180.118.241." + i % 255);
                headers.put("x-forwarded-for", "123.57.17." + i % 255);
                headers.put("x-real-ip", "180.10.241." + i % 255);
                try {
                    String content = OkHttpUtils.doGet(url, headers);
                    LOG.trace(Thread.currentThread().getName() + "get content is  id -> " + id + ",content -> " + content);
                    int c = 0;
                    m = Pattern.compile("\"price\":\"([\\.\\d]+)\"").matcher(content);
                    while (m.find()){
                        item.setPrice(m.group(1));
                        c += 1;
                    }

                    m = Pattern.compile("\"sellCount\":(\\d+)").matcher(content);
                    while (m.find()){
                        item.setYue_salenum(Integer.parseInt(m.group(1)));
                        c += 10;
                    }

                    m = Pattern.compile("\"promPlanMsg\":\\[([^\\]]*)\\],").matcher(content);
                    while (m.find()){
                        item.setPrommsg(m.group(1));
                        c += 111;
                    }
                    if (c >= 1){
                        dao.update(item);
                        LOG.info(Thread.currentThread().getName() + id + "success !");
                    }
                    LOG.debug(Thread.currentThread().getName() + "get item is " + item.toString());
                } catch (Exception e) {
                    LOG.info(Thread.currentThread().getName() + "  <=  failed");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
