package com.bfd;

import com.bfd.http.OkHttpUtils;
import com.bfd.model.ProxyDao;
import com.bfd.model.ProxyIp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by BFD_303 on 2017/7/7.
 */
public class ProxyTestWorker implements Runnable {
    private static final Log LOG = LogFactory.getLog(ProxyTestWorker.class);
    private ProxyDao proxyDao = new ProxyDao();

    private BlockingQueue<ProxyIp> PRO_QUEUE;

    public ProxyTestWorker(BlockingQueue<ProxyIp> PRO_QUEUE) {
        this.PRO_QUEUE = PRO_QUEUE;
    }

    @Override
    public void run() {
        while (true) {
            ProxyIp proxyIp = null;
            try {
                LOG.debug("get proxy queue size is " + PRO_QUEUE.size());
                proxyIp = PRO_QUEUE.take();

                if (getScore("http://www.baidu.com", "百度一下", proxyIp))
                    proxyIp.scoreAdd(1);
                if (getScore("http://www.sina.com.cn", "新浪", proxyIp))
                    proxyIp.scoreAdd(10);
                if (getScore("https://www.taobao.com", "淘宝", proxyIp))
                    proxyIp.scoreAdd(100);
                if (getScore("https://www.ele.me/", "饿了么", proxyIp))
                    proxyIp.scoreAdd(1000);

                if (proxyIp.getScore() > 0) {
                    LOG.info(proxyIp.toJson());
                    proxyDao.add(proxyIp);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                try {
                    proxyDao.update(proxyIp);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public boolean getScore(String url, String defaultField, ProxyIp proxyIp) {
        String html = null;
        try {
            html = OkHttpUtils.doGet(url, WorkCache.headers, proxyIp);
            if (html != null) {
                if (html.contains(defaultField))
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
