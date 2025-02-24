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

package com.harbortek.helm.system.service;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.system.vo.GrantedPermission;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.UserVo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PermissionService {



    List<PermissionVo> findPermissions(List<String> names, List<Long> identityIds,
                                       String identityType, List<Long> resourceIds);


    List<GrantedPermission> loadPermissions(UserVo user);
    List<PermissionVo> findPermissionsByResourceId(Long resourceId);

    List<PermissionVo> findPermissionsByResourceIds(@NotNull List<Long> resourceIds);

    List<PermissionVo> findGlobalPermissionsByIdentityId(Long identityId);

    boolean hasPermission(String permission, Long resourceId);

    void grant(String permission, BaseIdentity identity, Long resourceId);

    void unGrant(String permission, BaseIdentity identity, Long resourceId);

    void grant(String permissionName, List<BaseIdentity> granted, Long resourceId);

    void grant(List<PermissionVo> projectPermissions);

    void unGrantByResourceId(Long resourceId);

    void unGrantByIdentityId(Long identityId);

    void unGrantByIdentityIdAndResourceIds(Long identityId, List<Long> resourceIds);

    List<PermissionVo> findGlobalPermissionsByIdentities(List<Long> identityId, String identityType);
}
