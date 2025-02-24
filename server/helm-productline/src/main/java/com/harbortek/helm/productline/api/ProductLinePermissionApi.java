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

package com.harbortek.helm.productline.api;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.productline.service.ProductLineService;
import com.harbortek.helm.productline.vo.IdentityByGroupVo;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Tag(name = "产品线权限管理")
@RequestMapping(value = "/product-line/{productLineId}/permission")
public class ProductLinePermissionApi {

    @Autowired
    ProductLineService productLineService;
    @Autowired
    PermissionService permissionService;


    @Parameter(name="查询产品线权限授权列表")
    @RequestMapping(value = "/list-grant", method = RequestMethod.GET)
    ResponseEntity<List<PermissionGrantVo>> findPermissionGrants(
            @PathVariable(value = "productLineId", required = true) Long productLineId) {
        List<PermissionGrantVo> permissions = productLineService.findPermissionGrants(productLineId);
        return ResponseEntity.ok(permissions);
    }

    @Parameter(name="增加产品线权限授权")
    @RequestMapping(value = "/grant", method = RequestMethod.POST)
    ResponseEntity<Void> grantPermission(@PathVariable(value = "productLineId", required = true) Long productLineId,
                                         @RequestParam(value = "permission", required = true) String permission,
                                         @RequestBody BaseIdentity identity) {


        permissionService.grant(permission, identity, productLineId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除产品线权限授权")
    @RequestMapping(value = "/unGrant", method = RequestMethod.POST)
    ResponseEntity<Void> unGrantPermission(@PathVariable(value = "productLineId", required = true) Long productLineId,
                                           @RequestParam(value = "permission", required = true) String permission,
                                           @RequestBody BaseIdentity identity) {
        permissionService.unGrant(permission, identity, productLineId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询产品线关联角色/特殊角色/项目成员")
    @RequestMapping(value = "/rolesAndMembers", method = RequestMethod.GET)
    ResponseEntity<IdentityByGroupVo> findProjectRolesAndMembers(
            @PathVariable(value = "productLineId", required = true) Long productLineId,
            @RequestParam(value = "scope", required = true) String scope) {

        IdentityByGroupVo vo = productLineService.findRolesAndMembers(productLineId, scope);

        return ResponseEntity.ok(vo);
    }
}
