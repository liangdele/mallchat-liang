package com.example.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BlackReq {
    @ApiModelProperty(value = "拉黑用户的uid")
    @NotNull
    private Long uid;
}
