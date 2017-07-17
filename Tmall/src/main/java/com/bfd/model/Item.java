package com.bfd.model;

/**
 * Created by BFD_303 on 2017/7/17.
 */
public class Item {
    private int id;
    private String url;
    private String price;
    private int yue_salenum;
    private String prommsg = "";


    public Item(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getYue_salenum() {
        return yue_salenum;
    }

    public void setYue_salenum(int yue_salenum) {
        this.yue_salenum = yue_salenum;
    }

    public String getPrommsg() {
        return prommsg;
    }

    public void setPrommsg(String prommsg) {
        this.prommsg = prommsg;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", price='" + price + '\'' +
                ", yue_salenum='" + yue_salenum + '\'' +
                ", prommsg='" + prommsg + '\'' +
                '}';
    }
}
