package com.example.common.user.service.impl;

import com.example.common.user.domain.enums.RoletEnum;
import com.example.common.user.service.IRoleService;
import com.example.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl implements IRoleService {
    @Autowired
    private UserCache userCache;

    @Override
    public boolean hasPower(Long uid, RoletEnum roletEnum) {
        final Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains(roletEnum.getId());
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return roleSet.contains(RoletEnum.ADMIN.getId());
    }
}
