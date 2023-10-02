package com.example.common.user.controller;


import com.example.common.common.utils.AssertUtil;
import com.example.common.user.domain.enums.RoletEnum;
import com.example.common.user.domain.vo.req.BlackReq;
import com.example.common.user.domain.vo.req.ModifyNameReq;
import com.example.common.common.domain.vo.resp.ApiResult;
import com.example.common.common.utils.RequestHolder;
import com.example.common.user.domain.vo.req.WearingBadgeReq;
import com.example.common.user.domain.vo.resp.BadgeResp;
import com.example.common.user.domain.vo.resp.UserInfoResp;
import com.example.common.user.service.IRoleService;
import com.example.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户相关接口")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private IRoleService roleService;

    @GetMapping("/userInfo")
    @ApiOperation("获取用户信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/name")
    @ApiOperation("获取用户信息")
    public ApiResult<UserInfoResp> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req.getName());
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("获取用户徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(), req.getItemId());
        return ApiResult.success();
    }

    @PutMapping("/black")
    @ApiOperation("拉黑用户")
    public ApiResult<Void> black(@Valid @RequestBody BlackReq req) {
        final Long uid = RequestHolder.get().getUid();
        final boolean hasPower = roleService.hasPower(uid, RoletEnum.ADMIN);
        AssertUtil.isTrue(hasPower, "抹茶管理员没有权限");
        userService.black(req);
        return ApiResult.success();
    }
}

