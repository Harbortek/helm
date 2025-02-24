/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.util;

import cn.hutool.core.util.ObjectUtil;
import com.harbortek.helm.common.constants.CacheConstants;
import com.harbortek.helm.common.entity.IdName;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class CacheUtils {
    public static <T> void evict(Long id, Class<T> clz) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = CacheConstants.OBJECT_CACHE_KEY_PREFIX + clz.getName() + "_" + id;
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.OBJECT_CACHE_NAME))
               .evict(key);
    }

    public static <T> void evict(Collection<Long> ids, Class<T> clz) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        ids.forEach(id -> {
            String key = CacheConstants.OBJECT_CACHE_KEY_PREFIX + clz.getName() + "_" + id;
            Objects.requireNonNull(cacheManager.getCache(CacheConstants.OBJECT_CACHE_NAME))
                   .evict(key);
        });
    }

    public static <T> void evictAll(Class<T> clz) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        if (cacheManager instanceof CaffeineCacheManager){
            CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager)cacheManager;
            Cache cache = caffeineCacheManager.getCache(CacheConstants.OBJECT_CACHE_NAME);
            cache.invalidate();
        }
//        else if (cacheManager instanceof RedisCacheManager){
//            String pattern = CacheConstants.OBJECT_CACHE_KEY_PREFIX + clz.getName()+"_";
//            RedisTemplate redisTemplate = (RedisTemplate) SpringContextUtil.getBean("redisTemplate");
//            Set keys = redisTemplate.keys(pattern + "*");
//            redisTemplate.delete(keys);
//        }
    }

    public static <T> void put(T object) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        if (object instanceof IdName) {
            String key =
                    CacheConstants.OBJECT_CACHE_KEY_PREFIX + object.getClass().getName() + "_" + ((IdName)object).getId();
            Objects.requireNonNull(cacheManager.getCache(CacheConstants.OBJECT_CACHE_NAME))
                   .put(key, object);
        }
    }

    public static <T> void put(Collection<T> objects) {
        objects.forEach(CacheUtils::put);
    }


    public static <T> T get(Long id, Class<T> clz) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = CacheConstants.OBJECT_CACHE_KEY_PREFIX + clz.getName() + "_" + id;
        Cache.ValueWrapper valueWrapper =
                Objects.requireNonNull(cacheManager.getCache(CacheConstants.OBJECT_CACHE_NAME))
                       .get(key);
        if (valueWrapper != null) {
            //使用内存缓存的，必须深拷贝一份，否则数据会被修改
            if (cacheManager instanceof CaffeineCacheManager){
                return ObjectUtil.clone( (T) valueWrapper.get());
            }
            return (T) valueWrapper.get();
        }
        return null;
    }

}
