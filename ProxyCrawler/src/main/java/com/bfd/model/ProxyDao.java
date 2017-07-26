package com.bfd.model;

import com.bfd.db.DBManager;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import java.net.Proxy;
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
                ,"insert into proxy.proxy (ip,port,status,score,type) values(?,?,?,?,?)"
                , new ArrayListHandler()
                , p.getIp(), p.getPort(), p.getStatus(), p.getScore(),p.getType().toString());

        }
    }

    public void update(ProxyIp p ) throws SQLException {
        try(Connection conn = DBManager.getConnection()) {
            runner.update(conn
                    , "update proxy.proxy set port= ? , status = ? , score = ? ,type = ?, update_time = now() where ip = ?"
                    , p.getPort(), p.getStatus(), p.getScore(),p.getType().toString(), p.getIp());
        }
    }

    public List<ProxyIp> query(int limit,String type) throws SQLException {
        try(Connection conn = DBManager.getConnection()) {
            return runner.query(conn
                    , "select * from proxy.proxy where status = 1 and score > 0 " +
                            "and type = ?" +
                            "order by update_time limit " + (limit > 0 ? limit : 100)
                    , new ProxyListRSHandler()
                    ,"SOCKS".equals(type) ? type : "HTTP");
        }
    }

    public List<ProxyIp> queryAll() throws SQLException {
        try(Connection conn = DBManager.getConnection()) {
            return runner.query(conn
                    , "select * from proxy.proxy order by update_time"
                    , new ProxyListRSHandler());
        }
    }

    public class ProxyListRSHandler implements ResultSetHandler<List<ProxyIp>>{
        @Override
        public List<ProxyIp> handle(ResultSet resultSet) throws SQLException {
            List<ProxyIp> list = new ArrayList<>();
            while (resultSet.next()) {
                String ip = resultSet.getString("ip");
                int port = resultSet.getInt("port");
                int status = resultSet.getInt("status");
                int score = resultSet.getInt("score");
                String type = resultSet.getString("type");
                list.add(new ProxyIp(ip, port, status, score,type));
            }
            return list;
        }
    }

    public static void main(String[] args) {
        ProxyDao dao = new ProxyDao();
        ProxyIp p = new ProxyIp("10.0.0.1",1080);
        p.setStatus(1);
        p.setScore(111);
        p.setType(Proxy.Type.SOCKS);

        try {

            List<ProxyIp> list = dao.queryAll();
            list.forEach( l -> System.out.println(l.toJson()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
