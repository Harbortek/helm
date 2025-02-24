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

import com.harbortek.helm.common.constants.CacheConstants;
import com.harbortek.helm.system.vo.GrantedPermission;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.RoleMemberVo;
import com.harbortek.helm.system.vo.RoleVo;
import jakarta.validation.constraints.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author: hujun
 */
public class PermissionCacheUtils {

    private static Object get(String key) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        Cache.ValueWrapper valueWrapper =
                Objects.requireNonNull(cacheManager.getCache(CacheConstants.PERMISSION_CACHE_NAME))
                       .get(key);
        if (valueWrapper != null) {
            return valueWrapper.get();
        }
        return null;
    }

    public static void evictGrantedPermissions(Long resourceId) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String resourceKey = "GRANTED_";
        if (ObjectUtils.isValid(resourceId)) {
            resourceKey += "_" + resourceId;
        } else {
            resourceKey += "_" + "GLOBAL";
        }
        Set<Long> userIds = (Set<Long>) get(resourceKey);
        if (userIds == null) {
            userIds = new HashSet<>();
        }
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.PERMISSION_CACHE_NAME))
               .evict(resourceKey);
        for (Long userId : userIds) {
            String userKey = resourceKey + "_" + userId;
            Objects.requireNonNull(cacheManager.getCache(CacheConstants.PERMISSION_CACHE_NAME))
                   .evictIfPresent(userKey);
        }
    }

    public static void putGrantedPermissions(Long resourceId, Long userId, List<GrantedPermission> permissions) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String resourceKey = "GRANTED_";
        if (ObjectUtils.isValid(resourceId)) {
            resourceKey += "_" + resourceId;
        } else {
            resourceKey += "_" + "GLOBAL";
        }
        Set<Long> userIds = (Set<Long>) get(resourceKey);
        if (userIds == null) {
            userIds = new HashSet<>();
            Objects.requireNonNull(cacheManager.getCache(CacheConstants.PERMISSION_CACHE_NAME))
                   .put(resourceKey, userIds);
        }
        userIds.add(userId);

        String userKey = resourceKey + "_" + userId;
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.PERMISSION_CACHE_NAME))
               .put(userKey, permissions);
    }

    public static List<GrantedPermission> getGrantedPermissions(Long resourceId, Long userId) {
        String resourceKey = "GRANTED_";
        if (ObjectUtils.isValid(resourceId)) {
            resourceKey += "_" + resourceId;
        } else {
            resourceKey += "_" + "GLOBAL";
        }
        String userKey = resourceKey + "_" + userId;

        return (List<GrantedPermission>) get(userKey);
    }


    @SuppressWarnings("unchecked")
    public static List<RoleMemberVo> getRoleMembers(Long resourceId) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = "ROLE_MEMBER_";
        key += Objects.requireNonNullElse(resourceId, "GLOBAL");
        Cache.ValueWrapper cache =
                Objects.requireNonNull(cacheManager.getCache(CacheConstants.ROLE_CACHE_NAME)).get(key);
        if (cache != null) {
            return (List<RoleMemberVo>) cache.get();
        }
        return null;
    }
    public static void putRoleMembers(@NotNull Long resourceId, List<RoleMemberVo> roleMemberVos) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = "ROLE_MEMBER_";
        key += Objects.requireNonNullElse(resourceId, "GLOBAL");
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.ROLE_CACHE_NAME)).put(key, roleMemberVos);
    }

    public static void evictRoleMembers(Long resourceId) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = "ROLE_MEMBER_";
        key += Objects.requireNonNullElse(resourceId, "GLOBAL");
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.ROLE_CACHE_NAME))
               .evict(key);
    }


    @SuppressWarnings("unchecked")
    public static  List<RoleVo> getRoles(Long resourceId) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = "ROLE_";
        key += Objects.requireNonNullElse(resourceId, "GLOBAL");
        Cache.ValueWrapper cache =
                Objects.requireNonNull(cacheManager.getCache(CacheConstants.ROLE_CACHE_NAME)).get(key);
        if (cache != null) {
            return (List<RoleVo>) cache.get();
        }
        return null;
    }

    public static  void putRoles(Long resourceId,List<RoleVo> roles) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = "ROLE_";
        key += Objects.requireNonNullElse(resourceId, "GLOBAL");
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.ROLE_CACHE_NAME)).put(key, roles);
    }

    public static void evictRoles(Long resourceId) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = "ROLE_";
        key += Objects.requireNonNullElse(resourceId, "GLOBAL");
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.ROLE_CACHE_NAME))
               .evict(key);
    }


    @SuppressWarnings("unchecked")
    public static List<PermissionVo> getPermissions(Long resourceId) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = "PERMISSION_";
        key += Objects.requireNonNullElse(resourceId, "GLOBAL");
        Cache.ValueWrapper cache =
                Objects.requireNonNull(cacheManager.getCache(CacheConstants.ROLE_CACHE_NAME)).get(key);
        if (cache != null) {
            return (List<PermissionVo>) cache.get();
        }
        return null;
    }
    public static void putPermissions(@NotNull Long resourceId, List<PermissionVo> permissions) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = "PERMISSION_";
        key += Objects.requireNonNullElse(resourceId, "GLOBAL");
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.ROLE_CACHE_NAME)).put(key, permissions);
    }

    public static void evictPermissions(Long resourceId) {
        CacheManager cacheManager = SpringContextUtil.getBean(CacheManager.class);
        String key = "PERMISSION_";
        key += Objects.requireNonNullElse(resourceId, "GLOBAL");
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.ROLE_CACHE_NAME))
               .evict(key);
    }

}
