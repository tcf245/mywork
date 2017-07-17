package com.bfd;

import com.bfd.crawler.GatherProxy;
import com.bfd.crawler.Kuaidaili;
import com.bfd.crawler.Xicidaili;
import com.bfd.model.ProxyIp;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import sun.awt.windows.ThemeReader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by BFD_303 on 2017/7/7.
 */
public class ServerStart {
    private static final Log LOG = LogFactory.getLog(ServerStart.class);
    private final BlockingQueue<ProxyIp> PRO_QUEUE = new LinkedBlockingQueue<>();

    public void run(){
//        new Thread(new Xicidaili(PRO_QUEUE)).start();
//        new Thread(new Kuaidaili(PRO_QUEUE)).start();
//        new Thread(new GatherProxy(PRO_QUEUE)).start();

        try {
            List<String> lines = FileUtils.readLines(new File("etc/proxies.txt"),"utf-8");
            lines.forEach(l -> {
                String[] strs = l.split(":");
                PRO_QUEUE.add(new ProxyIp(strs[0],Integer.parseInt(strs[1])));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 1;i < 20 ;i++){
        new Thread(new ProxyTestWorker(PRO_QUEUE)).start();
    }
}

    public static void main(String[] args) {
        PropertyConfigurator.configureAndWatch(ServerStart.class.getClassLoader().getResource("log4j.properties").getFile());
        ServerStart server = new ServerStart();
        server.run();
    }
}
