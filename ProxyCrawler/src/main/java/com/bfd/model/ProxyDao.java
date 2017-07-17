package com.bfd.model;

import com.bfd.db.DBManager;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BFD_303 on 2017/7/13.
 */
public class ProxyDao {
    private QueryRunner runner = new QueryRunner();

    public void add(ProxyIp p) throws SQLException {
        try(Connection conn = DBManager.getConnection()){
            runner.insert(conn
                ,"insert into proxy.proxy (ip,port,status,score) values(?,?,?,?)"
                , new ArrayListHandler()
                , p.getIp(), p.getPort(), p.getStatus(), p.getScore());

        }
    }

//    public void addBatch(List<ProxyIp> list) throws SQLException {
//        try {
//            try(Connection conn = DBManager.getConnection()){
//                runner.insertBatch(conn,"insert into proxy.proxy (ip,port,status,score) values(?,?,?,?)"
//                        ,new ArrayListHandler()
//                        ,list.stream().forEach())
//            }
//        }
//    }

    public void update(ProxyIp p ) throws SQLException {
        try(Connection conn = DBManager.getConnection()) {
            runner.update(conn
                    , "update proxy.proxy set port= ? , status = ? , score = ? , update_time = now() where ip = ?"
                    , p.getPort(), p.getStatus(), p.getScore(), p.getIp());
        }
    }

    public List<ProxyIp> query(int limit) throws SQLException {
        try(Connection conn = DBManager.getConnection()) {
            return runner.query(conn
                    , "select * from proxy.proxy where status = 1 and score > 0 limit " + (limit > 0 ? limit : 100)
                    , new ResultSetHandler<List<ProxyIp>>() {
                        @Override
                        public List<ProxyIp> handle(ResultSet resultSet) throws SQLException {
                            List<ProxyIp> list = new ArrayList<>();
                            while (resultSet.next()) {
                                String ip = resultSet.getString("ip");
                                int port = resultSet.getInt("port");
                                int status = resultSet.getInt("status");
                                int score = resultSet.getInt("score");
                                list.add(new ProxyIp(ip, port, status, score));
                            }
                            return list;
                        }
                    });
        }
    }

    public static void main(String[] args) {
        ProxyDao dao = new ProxyDao();
        ProxyIp p = new ProxyIp("127.0.0.1",1080);
        p.setStatus(1);
        p.setScore(111);

        try {
            p.setStatus(1);
            dao.add(p);
            System.out.println("add");

//            dao.update(p);
//            System.out.println("update");

//            List<ProxyIp> list = dao.query(100);
//            list.forEach( l -> System.out.println(l.toJson()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
