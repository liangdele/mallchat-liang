package com.example.common.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.example.common.common.domain.dto.RequestInfo;
import com.example.common.common.exception.HttpErrorEnum;
import com.example.common.common.utils.RequestHolder;
import com.example.common.user.domain.enums.BlackTypeEnum;
import com.example.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class BlackInterceptor implements HandlerInterceptor {
    @Autowired
    private UserCache userCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        final RequestInfo requestInfo = RequestHolder.get();
        // 1. 判断uid是否在黑名单中
        boolean inBlackListByUid = inBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.UID.getType()));
        if(inBlackListByUid){
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }

        // 2. 判断ip是否在黑名单中
        boolean inBlackListByIp = inBlackList(requestInfo.getIp(), blackMap.get(BlackTypeEnum.IP.getType()));
        if(inBlackListByIp){
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        return true;
    }

    private boolean inBlackList(Object target, Set<String> set) {
        if (Objects.isNull(target) || Objects.isNull(set)) {
            return false;
        }
        return set.contains(target.toString());
    }

}
