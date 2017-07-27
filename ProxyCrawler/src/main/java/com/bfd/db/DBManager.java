package com.bfd.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by BFD_303 on 2017/7/13.
 */
public class DBManager {
    private static final Log LOG = LogFactory.getLog(DBManager.class);
    private static DruidDataSource dds = null;
    static{
        Properties p = new Properties();
        try {
//            p.load(FileUtils.openInputStream(new File(DBManager.class.getClassLoader().getResource("db.properties").getFile())));
            p.load(FileUtils.openInputStream(new File("etc/db.properties")));
            dds = (DruidDataSource) DruidDataSourceFactory.createDataSource(p);
        } catch (IOException e) {
            LOG.error("db config file error !");
            e.printStackTrace();
        } catch (Exception e) {
            LOG.error("create datasource exception !");
            e.printStackTrace();
        }
    }

    public static DruidPooledConnection getConnection() throws SQLException {
        return dds.getConnection();
    }
}
