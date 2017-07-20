package com.bfd.model;

import com.bfd.WorkCache;

import java.net.Proxy;

/**
 * Created by BFD_303 on 2017/7/7.
 */
public class ProxyIp {
    private String ip;
    private int port;
    /**
     * 状态：
     *  0 : 不可用
     *  1 : 可用
     */
    private int status;
    private int score;
    private Proxy.Type type = Proxy.Type.HTTP;

    public ProxyIp(String ip, int port, int status, int score,String type) {
        this.ip = ip;
        this.port = port;
        this.status = status;
        this.score = score;
        setType(type);

    }

    public ProxyIp(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.score = 0;
        this.status = 0;
        this.type = Proxy.Type.HTTP;
    }

    public Proxy.Type getType() {
        return type;
    }

    public void setType(Proxy.Type type) {
        this.type = type;
    }

    public void setType(String type) {
        if ("SOCKS".equals(type))
            this.type = Proxy.Type.SOCKS;
        else if ("DIRECT".equals(type))
            this.type = Proxy.Type.DIRECT;
        else
            this.type = Proxy.Type.HTTP;
    }

    public void scoreAdd(int num){
        setScore(this.score + num);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ProxyIp{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", status=" + status +
                ", score=" + score +
                '}';
    }

    public String toJson(){
        return WorkCache.gson.toJson(this);
    }

}
