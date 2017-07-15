package com.bfd.model;

import com.bfd.db.DBManager;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

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
        runner.insert(DBManager.getConnection()
                ,"insert into proxy (ip,port,status,score) values(?,?,?,?)"
                , new ArrayListHandler()
                , p.getIp(), p.getPort(), p.getStatus(), p.getScore());
    }

    public void update(ProxyIp p ) throws SQLException {
        runner.update(DBManager.getConnection()
                ,"update proxy set port= ? , status = ? , score = ? , update_time = now() where ip = ?"
                ,p.getPort(),p.getStatus(),p.getScore(),p.getIp());
    }

    public List<ProxyIp> query(int limit) throws SQLException {
        return runner.query(DBManager.getConnection()
                , "select * from proxy where status = 1 and score > 0 limit " + (limit > 0 ? limit : 20)
                , new ResultSetHandler<List<ProxyIp>>() {
                    @Override
                    public List<ProxyIp> handle(ResultSet resultSet) throws SQLException {
                        List<ProxyIp> list = new ArrayList<>();
                        while (resultSet.next()){
                            String ip = resultSet.getString("ip");
                            int port = resultSet.getInt("port");
                            int status = resultSet.getInt("status");
                            int score = resultSet.getInt("score");
                            list.add(new ProxyIp(ip,port,status,score));
                        }
                        return list;
                    }
                });
    }

    public static void main(String[] args) {
        ProxyDao dao = new ProxyDao();
        ProxyIp p = new ProxyIp("12.12.123.123",8081);
        p.setStatus(0);
        p.setScore(111);

        try {
//            dao.add(p);
//            System.out.println("add");
//
//            p.setStatus(1);
//            dao.update(p);
//            System.out.println("update");

            List<ProxyIp> list = dao.query(100);
            list.forEach( l -> System.out.println(l.toJson()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
