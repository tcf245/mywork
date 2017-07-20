package com.bfd.ele;

import com.bfd.WorkCache;
import com.bfd.db.DBManager;
import com.bfd.http.OkHttpUtils;
import com.bfd.model.ProxyIp;
import com.sun.javaws.progress.PreloaderDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by BFD_303 on 2017/7/18.
 */
public class Worker implements Runnable{
    private static final Log LOG = LogFactory.getLog(Worker.class);

    private BlockingQueue<String> TASK_QUEUE;

    public Worker(BlockingQueue<String> TASK_QUEUE) {
        this.TASK_QUEUE = TASK_QUEUE;
    }

    @Override
    public void run() {
        String url = "";

        while (true){
            try (Connection connection = DBManager.getConnection()){
                url = TASK_QUEUE.take();
//                ProxyIp proxyIp = new ProxyIp("127.0.0.1",1080);
                ProxyIp proxyIp = ServerStart.PRO_QUEUE.take();
                LOG.info("Get pro queue size is : " + ServerStart.PRO_QUEUE.size());
                String json = OkHttpUtils.doGet(url, WorkCache.headers,proxyIp);
                if (json == null) {
                    LOG.info(Thread.currentThread().getName() + url + " task failed!");
                    TASK_QUEUE.add(url);
                }

                ParseContent.parseContent(connection,json);
                ServerStart.PRO_QUEUE.addFirst(proxyIp);
                LOG.info(Thread.currentThread().getName() + url + " success !");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                LOG.info(Thread.currentThread().getName() + url + " task failed!");
                TASK_QUEUE.add(url);
            }
        }
    }
}
