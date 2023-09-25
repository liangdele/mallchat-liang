package com.example.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ModifyNameReq {
    @ApiModelProperty(value = "用户名")
    @NotBlank
    @Length(max = 6, message = "用户名长度不能超过6位")
    private String name;
}
