package com.example.common.user.domain.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class IpInfo implements Serializable {
    //注册时的ip
    private String createIp;
    //注册时的ip详情
    private IpDetails createIpDetails;
    //最新登录的ip
    private String updateIp;


    public void refreshIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return;
        }
        if (StringUtils.isBlank(createIp)) {
            createIp = ip;
        }
        updateIp = ip;
    }
}
