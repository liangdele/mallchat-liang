package com.example.common.user.service.impl;

import com.example.common.common.annotation.RedissonLock;
import com.example.common.common.event.UserBlackEvent;
import com.example.common.common.event.UserRegisterEvent;
import com.example.common.common.utils.AssertUtil;
import com.example.common.user.dao.BlackDao;
import com.example.common.user.dao.ItemConfigDao;
import com.example.common.user.domain.entity.*;
import com.example.common.user.domain.enums.BlackTypeEnum;
import com.example.common.user.domain.enums.ItemEnum;
import com.example.common.user.dao.UserBackpackDao;
import com.example.common.user.dao.UserDao;
import com.example.common.user.domain.enums.ItemTypeEnum;
import com.example.common.user.domain.vo.req.BlackReq;
import com.example.common.user.domain.vo.resp.BadgeResp;
import com.example.common.user.domain.vo.resp.UserInfoResp;
import com.example.common.user.service.UserService;
import com.example.common.user.service.adapter.UserAdapter;
import com.example.common.user.service.cache.ItemCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private ItemConfigDao itemConfigDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private BlackDao blackDao;

    @Override
    @Transactional
    public Long register(User user) {
        userDao.save(user);
        //TODO:用户注册事件
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, user));
        return user.getId();
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        Integer modifyNameCount = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfo(user, modifyNameCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser, "用户名已存在,请换一个试试");
        //改名卡数量
        UserBackpack modifyNameItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(modifyNameItem, "改名卡数量不足");
        //使用改名卡
        boolean success = userBackpackDao.useItem(modifyNameItem);
        if (success) {
            userDao.modifyName(uid, name);
        }
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        //查询所有的徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        //查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //当前佩戴的徽章
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);
    }

    @Override
    public void wearingBadge(Long uid, Long itemId) {
        //确保徽章存在
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, itemId);
        AssertUtil.isNotEmpty(firstValidItem, "徽章不存在");
        //确保这个物品是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "该物品不是徽章");
        userDao.wearingBadge(uid, itemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void black(BlackReq req) {
        final Long uid = req.getUid();
        Black insert = new Black();
        insert.setType(BlackTypeEnum.UID.getType());
        insert.setTarget(uid.toString());
        blackDao.save(insert);
        final User user = userDao.getById(uid);
        blackIp(Optional.ofNullable(user).map(User::getIpInfo).map(IpInfo::getCreateIp).orElse(null));
        blackIp(Optional.ofNullable(user).map(User::getIpInfo).map(IpInfo::getUpdateIp).orElse(null));
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, user));
    }

    private void blackIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return;
        }
        try {
            Black insert = new Black();
            insert.setType(BlackTypeEnum.UID.getType());
            insert.setTarget(ip);
            blackDao.save(insert);
        } catch (Exception e) {
            //ignore
        }
    }
}
