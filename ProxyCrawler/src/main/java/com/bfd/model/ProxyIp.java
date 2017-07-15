package com.bfd.model;

import com.bfd.WorkCache;

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

    public ProxyIp(String ip, int port, int status, int score) {
        this.ip = ip;
        this.port = port;
        this.status = status;
        this.score = score;
    }

    public ProxyIp(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.score = 0;
        this.status = 1;
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
