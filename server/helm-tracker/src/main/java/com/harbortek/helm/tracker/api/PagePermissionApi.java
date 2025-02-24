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

package com.harbortek.helm.tracker.api;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.tracker.constants.PagePermissions;
import com.harbortek.helm.tracker.vo.permission.PagePermissionVo;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.PermissionCacheUtils;
import com.harbortek.helm.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@Tag(name = "项目页面权限管理")
@RequestMapping(value = "/tracker/project/page/permission/")
public class PagePermissionApi {

    @Autowired
    PermissionService permissionService;



    @Parameter(name="批量更新项目页面权限")
    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    ResponseEntity<Void> batchUpdatePagePermission(@RequestBody List<PagePermissionVo> pagePermissions) {

        HashMap<Long, List<Long>> unGrantMap = new HashMap<>();
        List<PermissionVo> permissionVos = new ArrayList<>();
        pagePermissions.forEach(pagePermissionVo -> {
            List<Long> longs = unGrantMap.get(pagePermissionVo.getIdentityId());
            if(longs==null||longs.isEmpty()){
                longs = new ArrayList<>();
                unGrantMap.put(pagePermissionVo.getIdentityId(),longs);
            }
            longs.add(pagePermissionVo.getProjectPageId());

            pagePermissionVo.getPermission().forEach(permission -> {
                PermissionVo permissionVo = new PermissionVo();
                permissionVo.setId(IDUtils.getId());
                BaseIdentity baseIdentity = new BaseIdentity();
                baseIdentity.setId(pagePermissionVo.getIdentityId());
                baseIdentity.setType(IdentityTypes.ROLE);
                baseIdentity.setName(pagePermissionVo.getName());
                permissionVo.setIdentity(baseIdentity);
                permissionVo.setName(permission);
                permissionVo.setResourceId(pagePermissionVo.getProjectPageId());
                permissionVos.add(permissionVo);
            });
        });

        unGrantMap.keySet().forEach(identityId -> {
            permissionService.unGrantByIdentityIdAndResourceIds(identityId,unGrantMap.get(identityId));
        });
        if(!permissionVos.isEmpty()){
            permissionService.grant(permissionVos);
        }
        Long projectId = (Long) SecurityUtils.get(SecurityUtils.PROJECT_ID);
        PermissionCacheUtils.evictGrantedPermissions(projectId);
        pagePermissions.forEach(pagePerm->{
            PermissionCacheUtils.evictPermissions(pagePerm.getProjectPageId());
        });

        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询项目页面权限")
    @RequestMapping(value = "/perm", method = RequestMethod.GET)
    ResponseEntity<List<String>> findPagePermissionPerm() {
        List<String> allPagePermissions = PagePermissions.ALL_PAGE_PERMISSIONS;
        return ResponseEntity.ok(allPagePermissions);
    }
}
