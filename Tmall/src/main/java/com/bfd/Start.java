package com.bfd;

import com.bfd.model.Item;
import com.bfd.model.ItemDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by BFD_303 on 2017/7/17.
 */
public class Start {
    private static final Log LOG = LogFactory.getLog(Start.class);
    public static final BlockingQueue<Item> QUEUE = new LinkedBlockingQueue<>();
    private ItemDao dao = new ItemDao();

    public void run() {

        for (int i = 0; i < 10; i++) {
            new Thread(new Worker()).start();
        }

        for (; ; ) {
            try {
                if (QUEUE.size() <= 0) {
                    QUEUE.addAll(dao.query(10000));
                }
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        PropertyConfigurator.configureAndWatch(Start.class.getClassLoader().getResource("log4j.properties").getFile());
        new Start().run();
    }
}
