package com.example.common.user.service.cache;

import com.example.common.user.dao.ItemConfigDao;
import com.example.common.user.domain.entity.ItemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemCache {
    @Autowired
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = "item",key = "'itemsTypeByType:'+#itemType")
    public List<ItemConfig> getByType(Integer itemType) {
        return itemConfigDao.getByType(itemType);
    }

    @CacheEvict(cacheNames = "item",key = "'itemsTypeByType:'+#itemType")
    public List<ItemConfig> evictByType(Integer itemType) {
        return null;
    }


}
