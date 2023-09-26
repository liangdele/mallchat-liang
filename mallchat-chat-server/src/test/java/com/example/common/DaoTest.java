package com.example.common;

import com.example.common.common.utils.JwtUtils;
import com.example.common.user.dao.UserDao;
import com.example.common.user.domain.entity.User;
import com.example.common.user.domain.enums.IdempotentEnum;
import com.example.common.user.domain.enums.ItemEnum;
import com.example.common.user.service.IUserBackpackService;
import com.example.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {
    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private LoginService loginService;

    @Test
    public void jwt() {
        String login = loginService.login(11000L);
        String s = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjExMDAwLCJjcmVhdGVUaW1lIjoxNjk1MTM0MTM5fQ.X9Ekwk9fiSXPecVi9fsIuYcdY5QKA-nFBSlLcJ_usu0";
        System.out.println(login);
    }

    @Autowired
    private IUserBackpackService iUserBackpackService;

    @Test
    public void acquireItem() {
        iUserBackpackService.acquireItem(11000L, ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, "11000");
    }

    @Test
    public void test() throws WxErrorException {
        WxMpQrcodeService accessToken = wxMpService.getQrcodeService();
        WxMpQrCodeTicket wxMpQrCodeTicket = accessToken.qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);

    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void redis() {
        redisTemplate.opsForValue().set("name", "卷心菜");
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name); //卷心菜

        RLock lock = redissonClient.getLock("123");
        lock.lock();
        System.out.println();
        lock.unlock();
    }


    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    public void thread() throws Exception {
        threadPoolTaskExecutor.execute(() -> {
            if (1 == 1) {
                log.error("123");
                throw new RuntimeException("11111");
            }
        });
        Thread.sleep(200);
    }

}
