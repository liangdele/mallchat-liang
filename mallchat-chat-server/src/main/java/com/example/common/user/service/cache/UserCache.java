package com.example.common.user.service.cache;

import com.example.common.user.dao.BlackDao;
import com.example.common.user.dao.ItemConfigDao;
import com.example.common.user.dao.UserRoleDao;
import com.example.common.user.domain.entity.Black;
import com.example.common.user.domain.entity.ItemConfig;
import com.example.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserCache {

    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private BlackDao blackDao;

    @Cacheable(cacheNames = "user", key = "'roles:'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        final List<UserRole> userRoles = userRoleDao.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
    @Cacheable(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        final Map<Integer, List<Black>> collect = blackDao.list().stream()
                .collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>();
        collect.forEach((k, v) -> {
            result.put(k, v.stream().map(Black::getTarget).collect(Collectors.toSet()));
        });
        return result;
    }

    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> clearBlackMap() {
        return null;
    }
}
