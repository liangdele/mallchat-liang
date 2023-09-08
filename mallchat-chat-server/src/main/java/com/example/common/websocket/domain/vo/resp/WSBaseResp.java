package com.example.common.websocket.domain.vo.resp;

public class WSBaseResp<T> {
    /**
     * @see com.example.common.websocket.domain.enums.WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
