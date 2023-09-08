package com.example.common.websocket.domain.vo.req;

import lombok.Data;

@Data
public class WSBaseReq {
    /**
     * @see com.example.common.websocket.domain.enums.WSReqTypeEnum
     */
    private Integer type;
    private String data;
}
