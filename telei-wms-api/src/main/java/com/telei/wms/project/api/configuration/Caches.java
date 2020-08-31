package com.telei.wms.project.api.configuration;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.telei.wms.project.api.configuration.constants.CacheNameConstant;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 缓存实例
 * @author: leo
 * @date: 2020/7/15 14:57
 */
@Component
@Getter
public class Caches {
    /**
     * goods 缓存实例
     */
    @CreateCache(name = CacheNameConstant.GOODS_CACHE_NAME,cacheType = CacheType.BOTH)
    private Cache<String,String> goodsCache;


}
