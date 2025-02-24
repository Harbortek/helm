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

package com.harbortek.helm.productline.service.impl;

import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.productline.vo.IdentityByGroupVo;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.productline.constants.ProductLinePermissions;
import com.harbortek.helm.productline.dao.*;
import com.harbortek.helm.productline.entity.*;
import com.harbortek.helm.productline.service.ProductLineService;
import com.harbortek.helm.productline.vo.ProductLineRoleMemberVo;
import com.harbortek.helm.productline.vo.ProductLineVo;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleMemberService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.RoleMemberVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductLineServiceImpl implements ProductLineService {
    @Autowired
    ProductLineDao productLineDao;


    @Autowired
    ProductDao productDao;

    @Autowired
    ReportDao reportDao;

    @Autowired
    ReportGroupDao reportGroupDao;

    @Autowired
    PermissionService permissionService;

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    @Autowired
    RoleMemberService roleMemberService;


    @Override
    public ProductLineVo createProductLine(ProductLineVo productLineVo) {
        productLineVo.setId(IDUtils.getId());
        ProductLineEntity entity = DataUtils.toEntity(productLineVo, ProductLineEntity.class);

        ProductLineEntity productLineEntity = productLineDao.createProductLine(entity);

        //3. 保存项目角色
        List<ProductLineRoleMemberVo> roleMemberVos = createDefaultRoleMembers(productLineEntity);
        List<RoleVo> roles = new ArrayList<>();
        List<RoleMemberVo> roleMembers = new ArrayList<>();

        roleMemberVos.forEach(r -> {
            RoleVo roleEntity = RoleVo.builder()
                                      .id(IDUtils.getId())
                                      .ownerResourceId(productLineEntity.getId())
                                      .name(r.getName())
                                      .description(r.getDescription())
                                      .specialRoleType(r.getSpecialRoleType())
                                      .specialRole(r.getSpecialRole())
                                      .scope(r.getScope())
                                      .build();
            roles.add(roleEntity);
            r.setId(roleEntity.getId());

            r.getMembers().forEach(u -> {
                RoleMemberVo roleMemberEntity = RoleMemberVo.builder()
                                                            .id(IDUtils.getId())
                                                            .userId(u.getId())
                                                            .roleId(roleEntity.getId())
                                                            .ownerResourceId(productLineEntity.getId())
                                                            .scope(SpecialRole.SCOPE_PRODUCT_LINE)
                                                            .build();
                roleMembers.add(roleMemberEntity);
            });
        });
        roleService.createRoles(roles);

        //2.保存项目角色相关人员
        roleMemberService.createRoleMembers(roleMembers);

        // 生成项目权限授权数据
        List<PermissionVo> grantEntities = new ArrayList<>();
        List<PermissionGrantVo> permissionGrantVos = createDefaultPermissionGrants(roles);
        permissionGrantVos.forEach(g -> {
            List<BaseIdentity> identityList = g.getGranted();
            identityList.forEach(identity -> {
                PermissionVo grant =
                        PermissionVo.builder().id(IDUtils.getId())
                                    .resourceId(productLineEntity.getId())
                                    .name(g.getPermissionName()).identity(identity).build();
                grantEntities.add(grant);
            });
        });
        if (ObjectUtils.isNotEmpty(grantEntities)) {
            permissionService.grant(grantEntities);
        }


        return DataUtils.toVo(entity, ProductLineVo.class);
    }

    private List<PermissionGrantVo> createDefaultPermissionGrants(List<RoleVo> roles) {
        PermissionGrantVo admin =
                PermissionGrantVo.builder().permissionName(ProductLinePermissions.PRODUCT_LINE_ADMIN).build();

        PermissionGrantVo view =
                PermissionGrantVo.builder().permissionName(ProductLinePermissions.PRODUCT_LINE_VIEW).build();

        for (RoleVo r : roles) {
            BaseIdentity identity = new BaseIdentity(new IdNameReference(r));
            identity.setType(IdentityTypes.SPECIAL_ROLE);
            if (SpecialRole.PRODUCT_LINE_OWNER.equals(r.getSpecialRoleType())) {
                admin.getGranted().add(identity);
            } else if (SpecialRole.ALL_USERS.equals(r.getSpecialRoleType())) {
                admin.getGranted().add(identity);
                view.getGranted().add(identity);
            }
        }


        return Arrays.asList(admin, view);
    }

    private List<ProductLineRoleMemberVo> createDefaultRoleMembers(ProductLineEntity productLineEntity) {
        List<ProductLineRoleMemberVo> specialRoles = Arrays.asList(
                ProductLineRoleMemberVo.builder().name("产品线负责人").specialRoleType(SpecialRole.PRODUCT_LINE_OWNER)
                                       .scope(SpecialRole.SCOPE_PRODUCT_LINE).build(),
                ProductLineRoleMemberVo.builder().name("所有人").specialRoleType(SpecialRole.ALL_USERS)
                                       .scope(SpecialRole.SCOPE_PRODUCT_LINE).build());
        specialRoles.forEach(f -> {
            f.setSpecialRole(true);
        });
        return specialRoles;
    }

    @Override
    public ProductLineVo updateProductLine(ProductLineVo productLineVo) {
        ProductLineEntity entity = DataUtils.toEntity(productLineVo, ProductLineEntity.class);
        productLineDao.updateProductLine(entity);
        return productLineVo;
    }

    @Override
    public void deleteProductLine(Long productLineId) {
        productLineDao.deleteProductLine(productLineId);
        //删除相关产品
        productDao.deleteByProductLineId(productLineId);

        //删除相关的报表
        reportGroupDao.deleteByProductLineId(productLineId);
        reportDao.deleteByProductLineId(productLineId);

        //删除相关的产品角色、人员
        roleService.deleteRolesByResourceId(productLineId);
        permissionService.unGrantByResourceId(productLineId);
    }

    @Override
    public ProductLineVo findOneProductLine(Long id) {
        ProductLineEntity entity = productLineDao.findOneProductLine(id);

        return DataUtils.toVo(entity, ProductLineVo.class);
    }

    @Override
    public Collection<ProductLineVo> findProductLines(String keyword) {
        List<ProductLineEntity> entities = productLineDao.findProductLines(keyword);
        return DataUtils.toVo(entities, ProductLineVo.class);
    }

    private boolean underScope(String current, String required) {
        if (SpecialRole.SCOPE_PRODUCT_LINE.equals(required)) {
            return SpecialRole.SCOPE_PRODUCT_LINE.equals(current);
        }
        return false;
    }

    public List<PermissionGrantVo> findPermissionGrants(Long productLineId) {
        List<String> permissionNames = ProductLinePermissions.ALL_PERMISSIONS;
        List<PermissionVo> permissionVos = permissionService.findPermissionsByResourceId(productLineId);
        Map<String, List<PermissionVo>> permissionMap =
                permissionVos.stream().collect(Collectors.groupingBy(PermissionVo::getName));

        List<PermissionGrantVo> permissions = new ArrayList<>();
        permissionNames.forEach(permissionName -> {
            List<PermissionVo> permissionVoList = permissionMap.get(permissionName);
            PermissionGrantVo grantVo = PermissionGrantVo.builder()
                                                         .permissionName(permissionName)
                                                         .granted(ObjectUtils.isEmpty(permissionVoList) ?
                                                                          new ArrayList<>() :
                                                                          permissionVoList.stream()
                                                                                          .map(PermissionVo::getIdentity)
                                                                                          .collect(Collectors.toList()))
                                                         .build();
            permissions.add(grantVo);
        });
        return permissions;
    }

    public IdentityByGroupVo findRolesAndMembers(Long productLineId, String scope) {
        List<BaseIdentity> productLineRoles = new ArrayList<>();
        List<BaseIdentity> specialRoles = new ArrayList<>();
        List<BaseIdentity> members = new ArrayList<>();

        List<RoleVo> roles = roleService.findRolesByResourceId(productLineId);
        roles.stream().forEach(r -> {
            if (r.getSpecialRole() && underScope(r.getScope(), scope)) {
                BaseIdentity identity = new BaseIdentity(new IdNameReference(r));
                identity.setType(IdentityTypes.SPECIAL_ROLE);
                specialRoles.add(identity);
            } else if (!r.getSpecialRole()) {
                BaseIdentity identity = new BaseIdentity(new IdNameReference(r));
                identity.setType(IdentityTypes.ROLE);
                productLineRoles.add(identity);
            }
        });
        List<RoleMemberVo> roleMembers = roleMemberService.findRoleMembersByResourceId(productLineId);
        Map<Long, Long> countMap = roleMembers.stream().collect(
                Collectors.groupingBy(RoleMemberVo::getRoleId, Collectors.counting()));
        productLineRoles.forEach(r -> {
            if (countMap.get(r.getId()) != null) {
                r.setCount(countMap.get(r.getId()).intValue());
            } else {
                r.setCount(0);
            }
            r.setDescription("(" + r.getCount() + "人)");
        });

        List<Long> userIds = roleMembers.stream().map(RoleMemberVo::getUserId).collect(Collectors.toList());

        List<UserVo> users = userService.findUsersByIds(userIds);
        users.forEach(u -> {
            BaseIdentity identity = new BaseIdentity(new IdNameReference(u));
            identity.setIcon(u.getIcon());
            identity.setDescription("(" + u.getEmail() + ")");
            identity.setType(IdentityTypes.USER);
            members.add(identity);
        });

        IdentityByGroupVo vo = new IdentityByGroupVo();
        vo.setProductLineRoles(productLineRoles);
        vo.setSpecialRoles(specialRoles);
        vo.setMembers(members);
        return vo;
    }
}
