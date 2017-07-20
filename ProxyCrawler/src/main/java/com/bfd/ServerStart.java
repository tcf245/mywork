package com.bfd;

import com.bfd.model.ProxyDao;
import com.bfd.model.ProxyIp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by BFD_303 on 2017/7/7.
 */
public class ServerStart {
    private static final Log LOG = LogFactory.getLog(ServerStart.class);
    private final BlockingQueue<ProxyIp> PRO_QUEUE = new LinkedBlockingQueue<>();
    ProxyDao proxyDao = new ProxyDao();

    public void run(){
//        try {
//            List<String> lines = FileUtils.readLines(new File("etc/socks.txt"),"utf-8");
//            lines.forEach(l -> {
//                String[] strs = l.split(":");
//                ProxyIp ip = new ProxyIp(strs[0],Integer.parseInt(strs[1]));
//                ip.setType(Proxy.Type.SOCKS);
//                try {
//                    proxyDao.add(ip);
//                    LOG.info("add!");
//                } catch (SQLException e) {
//                    try {
//                        proxyDao.update(ip);
//                        LOG.info("update!");
//                    } catch (SQLException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        for (int i = 1;i < 30 ;i++){
            new Thread(new ProxyTestWorker(PRO_QUEUE)).start();
        }

        for(;;){
            if (PRO_QUEUE.size() <= 0){
                try {
                    List<ProxyIp> proxyIps = proxyDao.queryAll();
                    PRO_QUEUE.addAll(proxyIps);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            LOG.info(Thread.currentThread().getName() + " load tasks in proxy queue , queue size is " + PRO_QUEUE.size());
            try {
                Thread.sleep(10 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        PropertyConfigurator.configureAndWatch(ServerStart.class.getClassLoader().getResource("log4j.properties").getFile());
        SpringApplication.run(ProxyController.class, args);
        ServerStart server = new ServerStart();
        server.run();
    }
}
