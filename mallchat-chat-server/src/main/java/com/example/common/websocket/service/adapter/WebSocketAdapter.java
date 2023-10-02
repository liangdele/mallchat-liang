package com.example.common.websocket.service.adapter;

import com.example.common.common.domain.enums.YesOrNoEnum;
import com.example.common.user.domain.entity.User;
import com.example.common.websocket.domain.enums.WSRespTypeEnum;
import com.example.common.websocket.domain.vo.resp.WSBaseResp;
import com.example.common.websocket.domain.vo.resp.WSBlack;
import com.example.common.websocket.domain.vo.resp.WSLoginSuccess;
import com.example.common.websocket.domain.vo.resp.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WebSocketAdapter {
    public static WSBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }

    public static WSBaseResp<?> buildResp(User user, String token, boolean power) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .power(power ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus())
                .build();
        resp.setData(wsLoginSuccess);
        return resp;
    }

    public static WSBaseResp<?> buildWaitAuthorizeResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }

    public static WSBaseResp<?> buildInvalidTokenResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return resp;
    }

    public static WSBaseResp<?> buildBlackReq(User user) {
        WSBaseResp<WSBlack> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.BLACK.getType());
        WSBlack wsLoginSuccess = WSBlack.builder()
                .uid(user.getId())
                .build();
        resp.setData(wsLoginSuccess);
        return resp;
    }
}
