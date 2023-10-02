package com.example.common.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpDetail implements Serializable {
    private String ip;
    private String isp;//运营商
    private String isp_id;//运营商id
    private String city;//城市
    private String city_id;//城市id
    private String county;//区县
    private String county_id;//区县id
    private String region;//省份
    private String region_id;//省份id
}
