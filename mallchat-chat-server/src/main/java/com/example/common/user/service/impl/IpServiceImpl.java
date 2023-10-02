package com.example.common.user.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.example.common.common.domain.vo.resp.ApiResult;
import com.example.common.common.utils.JsonUtils;
import com.example.common.user.dao.UserDao;
import com.example.common.user.domain.entity.IpDetail;
import com.example.common.user.domain.entity.IpInfo;
import com.example.common.user.domain.entity.User;
import com.example.common.user.service.IpService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class IpServiceImpl implements IpService {
    private static ExecutorService executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(500), new NamedThreadFactory("refresh-ipDetail", false));
    @Autowired
    private UserDao userDao;

    @Override
    public void refreshIpDetailAsync(Long id) {
        executor.execute(() -> {
            User user = userDao.getById(id);
            IpInfo ipInfo = user.getIpInfo();
            if (Objects.isNull(ipInfo)) {
                return;
            }
            final String refreshIp = ipInfo.needRefreshIp();
            if (StringUtils.isBlank(refreshIp)) {
                return;
            }
            IpDetail ipDetail = tryGetIpDetailOrNullTreeTimes(refreshIp);
            if (Objects.nonNull(ipDetail)) {
                ipInfo.refreshIpDetail(ipDetail);
                final User update = new User();
                update.setId(id);
                update.setIpInfo(ipInfo);
                userDao.updateById(update);
            }
        });
    }

    private static IpDetail tryGetIpDetailOrNullTreeTimes(String refreshIp) {
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = getIpDetailOrNull(refreshIp);
            if (Objects.nonNull(ipDetail)) {
                return ipDetail;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("tryGetIpDetailOrNullTreeTimes error", e);
            }
        }
        return null;
    }

    public static IpDetail getIpDetailOrNull(String ip) {
        String body = HttpUtil.get("https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc");
        final ApiResult<IpDetail> result = JsonUtils.toObj(body, new TypeReference<ApiResult<IpDetail>>() {
        });
        return result.getData();
      /*  try {
            IpResult<IpDetail> result = JSONUtil.toBean(body, new TypeReference<IpResult<IpDetail>>() {
            }, false);
            if (result.isSuccess()) {
                return result.getData();
            }
        } catch (Exception ignored) {
        }
        return null;*/
    }
}
