package com.bfd.model;

import com.bfd.db.DBManager;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BFD_303 on 2017/7/17.
 */
public class ItemDao {
    private QueryRunner runner = new QueryRunner();

//    public void add(Item p) throws SQLException {
//        try(Connection conn = DBManager.getConnection()){
//            runner.insert(conn
//                    ,"insert into infopages (ip,port,status,score) values(?,?,?,?)"
//                    , new ArrayListHandler()
//                    , p.getIp(), p.getPort(), p.getStatus(), p.getScore());
//
//        }
//    }

    public void update(Item item ) throws SQLException {
        try(Connection conn = DBManager.getConnection()) {
            runner.update(conn
                    , "update infopages set prommsg= ? , yue_salenum = ? , price = ? where id = ?"
                    , item.getPrommsg(),item.getYue_salenum(),item.getPrice(),item.getId());
        }
    }

    public List<Item> query(int limit) throws SQLException {
        try (Connection conn = DBManager.getConnection()) {
            return runner.query(conn
                    , "select * from infopages where info_status=0 and price='' limit " + (limit > 0 ? limit : 100)
                    , new ResultSetHandler<List<Item>>() {
                        @Override
                        public List<Item> handle(ResultSet resultSet) throws SQLException {
                            List<Item> list = new ArrayList<>();
                            while (resultSet.next()) {
                                int id = resultSet.getInt("id");
                                String url = resultSet.getString("url");
                                list.add(new Item(id, url));
                            }
                            return list;
                        }
                    });
        }
    }

    public static void main(String[] args) {
        ItemDao dao = new ItemDao();

        try {
            List<Item> items = dao.query(1000);

            items.forEach(i -> System.out.println(i.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
