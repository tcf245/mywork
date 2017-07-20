package com.bfd.ele;

import com.bfd.crawler.utils.JsonUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by BFD-229 on 2017/7/12.
 */
public class ParseContent {
    public static void parseContent(Connection conn, String json) throws Exception{
        try {
            List<Map<String,Object>> list = JsonUtils.parseArray(json);
            for (Map<String,Object> map :list) {
                String description = ""; //描述
                if(map.containsKey("description")){
                    description = (String) map.get("description");
                }
                String name = ""; //分类名称
                if(map.containsKey("name")){
                    name = (String) map.get("name");
                }
                List<Map<String,Object>> foods = new ArrayList<Map<String,Object>>();
                if(map.containsKey("foods") & map.get("foods") instanceof List){
                    foods = (List<Map<String, Object>>) map.get("foods");
                    for (Map<String,Object> food:foods) {
                        String foodName = "";//名称
                        if(food.containsKey("name")){
                            foodName = (String) food.get("name");
                        }
                        Integer month_sales = 0;//月销量
                        if(food.containsKey("month_sales")){
                            month_sales = (Integer) food.get("month_sales");
                        }
                        Double foodRating = 0.0;//评分
                        if(food.containsKey("rating")){
                            foodRating = Double.parseDouble(food.get("rating").toString());
                        }
                        Integer rating_count = 0 ;//本月销量
                        if(food.containsKey("rating_count")){
                            rating_count = (Integer) food.get("rating_count");
                        }
                        List<Map<String,Object>> specfoods = new ArrayList<Map<String,Object>>();
                        Integer restaurant_id = 0;//店铺ID
                        if(food.containsKey("restaurant_id")){
                            restaurant_id = (Integer) food.get("restaurant_id");
                        }
                        Double price = 0.0 ;//价格
                        Integer food_id = 0;//食品ID
                        if(food.containsKey("specfoods") && food.get("specfoods") instanceof List){
                            specfoods = (List<Map<String, Object>>) food.get("specfoods");
                            Map<String,Object> spec = specfoods.get(0);
                            System.out.println(spec);
                            price = Double.parseDouble(spec.get("price").toString());
                            food_id = Integer.parseInt(spec.get("food_id").toString());
                        }

                        if(conn == null){
                            System.out.println("连不上");
                            return ;
                        }else {
                            String findsql = "select foodid from foodmessage where foodid="+food_id ;
                            PreparedStatement pst1 = conn.prepareStatement(findsql);
                            ResultSet rs1 = pst1.executeQuery();
                            if(rs1.next()){
                                System.out.println("id="+rs1.getString(1) + "已存在");
                                continue;
                            }else {
                                String shopurl = "https://www.ele.me/shop/"+restaurant_id;
                                String foodurl = "https://www.ele.me/restapi/shopping/v2/menu?restaurant_id=" +restaurant_id;
                                String sql = "insert into foodmessage (foodid,shopid,shopurl,foodurl,catename,description,foodname,price," +
                                        "month_sales,foodRating,rating_count,status,crawltime,starttime) " +
                                        "values("+food_id+","+restaurant_id+",'"+shopurl+"','"+foodurl+"','"+name+"','"+description+"','"+foodName+"',"+price+"," +
                                        ""+month_sales+","+foodRating+","+rating_count+",0,now(),now())";

                                PreparedStatement pst = null;
                                try {
                                    pst = conn.prepareStatement(sql);
                                    boolean rs = pst.execute();
                                    while (rs) {
                                        System.out.println("成功");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
