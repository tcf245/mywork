package com.bfd;

import com.bfd.model.ProxyDao;
import com.bfd.model.ProxyIp;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BFD_303 on 2017/7/20.
 */

@Controller
@EnableAutoConfiguration
public class ProxyController {
    ProxyDao proxyDao = new ProxyDao();
    Gson gson = new Gson();

    @RequestMapping("/")
    @ResponseBody
    String test() {
        return "Test OK!";
    }

    @RequestMapping("/proxy")
    @ResponseBody
    String getProxy(@RequestParam(value = "id",required = true) String id, @RequestParam(value = "type",required =false) String type, @RequestParam(value = "page",required =false,defaultValue = "1") int page){
        if (!"crawler".equalsIgnoreCase(id))
           return "error id : " + id;

        if (!"SOCKS".equalsIgnoreCase(type))
            type = "HTTP";

        if (page < 1)
            page = 1;

        try {
            List<ProxyIp> proxyIpList = proxyDao.query(1000,type);

            return gson.toJson(proxyIpList.size() > 0 ? proxyIpList.subList(0,100) : new ArrayList<>());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ProxyController.class, args);
    }
}
