package com.bfd;

import com.bfd.crawler.Kuaidaili;
import com.bfd.crawler.Xicidaili;
import com.bfd.model.ProxyIp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import sun.awt.windows.ThemeReader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by BFD_303 on 2017/7/7.
 */
public class ServerStart {
    private static final Log LOG = LogFactory.getLog(ServerStart.class);

    public void run(){
//        new Thread(new Xicidaili()).start();
        new Thread(new Kuaidaili()).start();

        for (int i = 1;i < 10 ;i++){
        new Thread(new ProxyTestWorker()).start();
    }
}

    public static void main(String[] args) {
        PropertyConfigurator.configureAndWatch(ServerStart.class.getClassLoader().getResource("log4j.properties").getFile());
        ServerStart server = new ServerStart();
        server.run();
    }
}
