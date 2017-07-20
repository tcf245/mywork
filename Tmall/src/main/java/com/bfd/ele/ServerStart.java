package com.bfd.ele;

import com.bfd.db.DBManager;
import com.bfd.jdbc.DBConnectionManager;
import com.bfd.model.ProxyIp;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by BFD_303 on 2017/7/18.
 */
public class ServerStart {
    public static final BlockingDeque<ProxyIp> PRO_QUEUE = new LinkedBlockingDeque<>();
    private static final BlockingQueue<String> TASK_QUEUE = new LinkedBlockingQueue<>();

    public void run() {
        try {
            List<String> lines = FileUtils.readLines(new File("etc/proxies.txt"), "utf-8");
            lines.forEach(l -> {
                String[] strs = l.split(":");
                PRO_QUEUE.add(new ProxyIp(strs[0], Integer.parseInt(strs[1])));
            });

            lines = FileUtils.readLines(new File("etc/food.txt"), "utf-8");


            try (Connection conn = DBManager.getConnection()) {
                List<String> urls = new QueryRunner().query(conn, "select distinct foodurl from foodmessage", new ResultSetHandler<List<String>>() {
                    @Override
                    public List<String> handle(ResultSet resultSet) throws SQLException {
                        List<String> urls = new ArrayList<>();
                        while (resultSet.next()) {
                            urls.add(resultSet.getString("foodurl"));
                        }
                        return urls;
                    }
                });
                System.out.println("get urls size is : " + urls.size());
                for (String s :urls) {
                    if (lines.contains(s))
                        lines.remove(s);
                }
                System.out.println("get lines size is : " + lines.size());
            } catch (SQLException e) {
                e.printStackTrace();
            }



            lines.forEach(l -> {
                TASK_QUEUE.add(l);
            });

            for (int i = 0; i < 10; i++) {
                new Thread(new Worker(TASK_QUEUE)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        PropertyConfigurator.configureAndWatch(ServerStart.class.getClassLoader().getResource("log4j.properties").getFile());
        new ServerStart().run();
    }

}
