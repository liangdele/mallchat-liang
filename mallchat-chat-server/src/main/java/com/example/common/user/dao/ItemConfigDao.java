package com.example.common.user.dao;

import com.example.common.user.domain.entity.ItemConfig;
import com.example.common.user.mapper.ItemConfigMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-09-18
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> {

    public List<ItemConfig> getByType(Integer itemType) {
        return lambdaQuery()
                .eq(ItemConfig::getType, itemType)
                .list();
    }
}
