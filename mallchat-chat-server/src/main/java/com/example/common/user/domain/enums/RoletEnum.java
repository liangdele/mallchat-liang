package com.example.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum RoletEnum {
    ADMIN(1L, "超级管理员"),
    CHAT_MANAGER(2L, "抹茶群聊管理员");
    private final Long id;
    private final String desc;
    private static final Map<Long, RoletEnum> cache;

    static {
        cache = Arrays.stream(RoletEnum.values()).collect(Collectors.toMap(RoletEnum::getId, Function.identity()));
    }

    public static RoletEnum of(Long id) {
        return cache.get(id);
    }
}
