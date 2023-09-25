package com.example.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum {
    BUSINESS_ERROR(0, "{0}"),
    SYSTEM_ERROR(-1, "系统异常"),
    PARAM_INVALID(-2, "参数校验失败");

    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}
