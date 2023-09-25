package com.example.common.common.exception;

import com.example.common.common.domain.vo.resp.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorMsg.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append(",");
        });
        String message = errorMsg.toString();
        message = message.substring(0, message.length() - 1);
        return ApiResult.fail(CommonErrorEnum.PARAM_INVALID.getCode(), message);
    }

    @ExceptionHandler(value = Throwable.class)
    public ApiResult<?> handleThrowable(Throwable e) {
        log.error("系统异常!!!原因是:{}", e.getMessage());
        return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(value = BusinessException.class)
    public ApiResult<?> handleBusinessException(BusinessException e) {
        log.info("业务异常!!!原因是:{}", e.getMessage(), e);
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }
}
