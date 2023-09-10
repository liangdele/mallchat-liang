package com.example.common;

import com.example.common.user.dao.UserDao;
import com.example.common.user.domain.entity.User;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {
    @Autowired
    private WxMpService wxMpService;

    @Test
    public void test() throws WxErrorException {
        WxMpQrcodeService accessToken = wxMpService.getQrcodeService();
        WxMpQrCodeTicket wxMpQrCodeTicket = accessToken.qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);

    }
}
