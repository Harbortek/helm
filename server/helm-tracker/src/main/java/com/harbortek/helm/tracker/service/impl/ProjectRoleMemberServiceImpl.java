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

package com.harbortek.helm.tracker.service.impl;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleMemberService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.RoleMemberVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.PagePermissions;
import com.harbortek.helm.tracker.dao.ProjectPageDao;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.tracker.service.ProjectRoleMemberService;
import com.harbortek.helm.tracker.vo.ProjectRoleMemberVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.permission.PagePermissionVo;
import com.harbortek.helm.tracker.vo.users.IdentityByGroupVo;
import com.harbortek.helm.util.BeanCopyUtils;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.tree.TreeNode;
import com.harbortek.helm.util.tree.TreeNodeAction;
import com.harbortek.helm.util.tree.TreeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectRoleMemberServiceImpl implements ProjectRoleMemberService {
    @Autowired
    ProjectPageDao projectPageDao;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    RoleMemberService roleMemberService;

    @Autowired
    PermissionService permissionService;


    @Override
    public void createProjectRoleMembers(Long projectId, Long roleId, List<UserVo> users) {
        List<RoleMemberVo> projectRoleMembers = new ArrayList<>();
        users.forEach(item -> {
            if(item!=null){
                RoleMemberVo roleMemberVo = new RoleMemberVo();
                roleMemberVo.setScope(SpecialRole.SCOPE_PROJECT);
                roleMemberVo.setOwnerResourceId(projectId);
                roleMemberVo.setRoleId(roleId);
                roleMemberVo.setUserId(item.getId());
                projectRoleMembers.add(roleMemberVo);
            }
        });
        if (!users.isEmpty()) {
            roleMemberService.createRoleMembers(projectRoleMembers);
        }
    }

    public void createProjectRoleMembers(Long projectId, List<ProjectRoleMemberVo> roleMemberVos) {
        roleMemberVos.forEach(r -> {
            createProjectRoleMembers(projectId, r.getId(), r.getMembers());
        });
    }

    @Override
    public void deleteProjectRoleMembers(Long projectId, Long roleId,
                                         List<UserVo> users) {
        List<Long> userIds = ObjectUtils.ids(users);
        roleMemberService.deleteRoleMembers(SpecialRole.SCOPE_PROJECT, projectId, userIds,List.of(roleId));
    }

    @Override
    public List<ProjectRoleMemberVo> findProjectRoleMembers(Long projectId, boolean includeSpecialRole) {

        List<ProjectPageEntity> projectPages = projectPageDao.findByProject(projectId);
        List<ProjectPageVo> pages = DataUtils.toVo(projectPages, ProjectPageVo.class);
        List<TreeNode<ProjectPageVo>> treeNodes = TreeUtils.listToTree(pages, "id", "parentId");
        TreeUtils.traversTree(null, treeNodes, new TreeNodeAction<ProjectPageVo>() {
            @Override
            public void doAction(TreeNode<ProjectPageVo> parent, int index, TreeNode<ProjectPageVo> node) {
                if (parent != null) {
                    parent.getObject().getChildren().add(node.getObject());
                }
            }
        });

        List<ProjectPageVo> pageVos = TreeUtils.treeToList(treeNodes);


        //获取角色
        List<RoleVo> projectRoles = roleService.findRoles(SpecialRole.SCOPE_PROJECT, projectId);
        if (!includeSpecialRole) {
            projectRoles = projectRoles.stream().filter(r -> !r.getSpecialRole()).collect(Collectors.toList());
        }

        List<ProjectRoleMemberVo> projectRoleMemberVos = projectRoles.stream().map(role -> {
            ProjectRoleMemberVo projectRole = new ProjectRoleMemberVo();
            BeanCopyUtils.copyWithoutNullProperties(role, projectRole);

            //获取角色成员
            List<RoleMemberVo> projectRoleMembers =
                    roleMemberService.findRoleMembers(SpecialRole.SCOPE_PROJECT, projectId, null,
                                                      List.of(role.getId()));
            List<Long> userIds =
                    projectRoleMembers.stream().map(RoleMemberVo::getUserId).distinct().collect(Collectors.toList());
            List<UserVo> users = userService.findUsersByIds(userIds);
            projectRole.setMembers(users);

            //获取页面权限
            List<PermissionVo> pagePermissions = permissionService.findPermissions(PagePermissions.ALL_PAGE_PERMISSIONS,
                                                                                   List.of(role.getId()),
                                                                                   IdentityTypes.ROLE,
                                                                                   ObjectUtils.ids(projectPages));
            Map<Long, List<PermissionVo>> pagePermissionMap =
                    pagePermissions.stream().collect(Collectors.groupingBy(PermissionVo::getResourceId));

            List<PagePermissionVo> pagePermissionVos = pageVos.stream().map(projectPageVo -> {
                PagePermissionVo pagePermissionVo =
                        PagePermissionVo.builder().id(projectPageVo.getId()).parentId(projectPageVo.getParentId())
                                        .name(projectPageVo.getName()).projectId(projectId).identityId(role.getId())
                                        .projectPageId(projectPageVo.getId()).build();
                pagePermissionVo.setPermission(
                        pagePermissionMap.getOrDefault(projectPageVo.getId(), Collections.emptyList()).stream()
                                         .map(PermissionVo::getName).collect(Collectors.toList()));
                return pagePermissionVo;
            }).collect(Collectors.toList());
            projectRole.setPermissions(pagePermissionVos);
            return projectRole;
        }).collect(Collectors.toList());
        return projectRoleMemberVos;


//		List<UserEntity> distinctUsersByProjectId = projectRoleMemberDao.findDistinctUsersByProjectId(projectId);
//		result.stream().forEach(item -> {
//			//成员
//			for (ProjectRoleMemberEntity projectRoleMember : projectRoleMembers) {
//				if(item.getId().equals(projectRoleMember.getRoleId())){
//					for (UserEntity userEntity : distinctUsersByProjectId) {
//						if(projectRoleMember.getUserId().equals(userEntity.getId())){
//							item.getMembers().add(DataUtils.toVo(userEntity,UserVo.class));
//						}
//					}
//				}
//			}
//			//页面权限
//			List<PagePermissionVo> permissionsVos = item.getPermissions();
//			for (ProjectPageVo projectPage : pageVos) {
//				if(projectPage.getParentId()!=null){
//					continue;
//				}
//				List<ProjectPageVo> childs=new ArrayList<>();
//				if(projectPage.getChildren().size()==0){
//					childs.add(projectPage);
//				}else{
//					permissionsVos.add(PagePermissionVo.builder().id(projectPage.getId())
//							.name(projectPage.getName()).build());
//					childs=projectPage.getChildren();
//				}
//				for (ProjectPageVo child : childs) {
//					boolean flag=true;
//					Long parentId=projectPage.getId();
//					if(parentId.equals(child.getId())){
//						parentId=0L;
//					}
//					for (PagePermissionEntity permission : permissions) {
//						if(item.getId().equals(permission.getIdentityId())&&
//								child.getId().equals(permission.getProjectPageId())){
//							item.getPermissions().add(PagePermissionVo.builder().id(permission.getId())
//									.name(child.getName()).parentId(parentId)
//									.projectPageId(child.getId()).identityId(item.getId())
//									.permission(permission.getPermission()).build());
//							flag=false;
//							break;
//						}
//					}
//					if(flag){
//						item.getPermissions().add(PagePermissionVo.builder().id(child.getId())
//								.name(child.getName()).parentId(parentId)
//								.projectPageId(child.getId()).identityId(item.getId())
//								.permission(new ArrayList<>()).build());
//					}
//				}
//
//			}
//		});
//		return result;
    }

    private boolean underScope(String current, String required) {
        if (SpecialRole.SCOPE_PROJECT.equals(required)) {
            return SpecialRole.SCOPE_PROJECT.equals(current);
        } else if (SpecialRole.SCOPE_TRACKER.equals(required)) {
            return true;
        }
        return false;
    }

    @Override
    public IdentityByGroupVo findProjectRolesAndMembers(Long projectId, String scope) {
        List<BaseIdentity> projectRoles = new ArrayList<>();
        List<BaseIdentity> specialRoles = new ArrayList<>();
        List<BaseIdentity> members = new ArrayList<>();

        List<RoleVo> roles = roleService.findRoles(SpecialRole.SCOPE_PROJECT, projectId);
        if(SpecialRole.SCOPE_TRACKER.equals(scope)){
            roles.addAll(roleService.findRoles(SpecialRole.SCOPE_TRACKER, projectId));
        }

        roles.forEach(r -> {
            if (r.getSpecialRole() && underScope(r.getScope(), scope)) {
                BaseIdentity identity = new BaseIdentity(new IdNameReference(r));
                identity.setType(IdentityTypes.SPECIAL_ROLE);
                specialRoles.add(identity);
            } else if (!r.getSpecialRole()) {
                BaseIdentity identity = new BaseIdentity(new IdNameReference(r));
                identity.setType(IdentityTypes.ROLE);
                projectRoles.add(identity);
            }
        });
        Map<Long, Integer> countMap = roleMemberService.countRoleMembersGroupByRoleId(SpecialRole.SCOPE_PROJECT,
                                                                                      projectId, null, null);
        projectRoles.forEach(r -> {
            if (countMap.get(r.getId()) != null) {
                r.setCount(countMap.get(r.getId()));
            } else {
                r.setCount(0);
            }
            r.setDescription("(" + r.getCount() + "人)");
        });

        //获取角色成员
        List<RoleMemberVo> projectRoleMembers =
                roleMemberService.findRoleMembersByResourceId(projectId);
        List<Long> userIds =
                projectRoleMembers.stream().map(RoleMemberVo::getUserId).distinct().collect(Collectors.toList());
        List<UserVo> users = userService.findUsersByIds(userIds);

        users.forEach(u -> {
            BaseIdentity identity = new BaseIdentity(new IdNameReference(u));
            identity.setIcon(u.getIcon());
            identity.setDescription("(" + u.getEmail() + ")");
            identity.setType(IdentityTypes.USER);
            members.add(identity);
        });

        IdentityByGroupVo vo = new IdentityByGroupVo();
        vo.setProjectRoles(projectRoles);
        vo.setSpecialRoles(specialRoles);
        vo.setMembers(members);
        return vo;
    }


    @Override
    public List<UserVo> findProjectUsers(Long projectId) {
        //获取角色成员
        List<RoleMemberVo> projectRoleMembers =
                roleMemberService.findRoleMembersByResourceId(projectId);
        List<Long> userIds =
                projectRoleMembers.stream().map(RoleMemberVo::getUserId).distinct().collect(Collectors.toList());
        return userService.findUsersByIds(userIds);
    }

    @Override
    public List<UserVo> findProjectUsers(Long projectId, List<Long> roleIds) {
        List<RoleMemberVo> projectRoleMembers =
                roleMemberService.findRoleMembers(SpecialRole.SCOPE_PROJECT, projectId, null, roleIds);
        List<Long> userIds =
                projectRoleMembers.stream().map(RoleMemberVo::getUserId).distinct().collect(Collectors.toList());
        return userService.findUsersByIds(userIds);
    }

    @Override
    public List<UserVo> findProjectUsersByProjectIds(List<Long> ids) {
        return userService.findUsersByIds(ids);
    }

    @Override
    public List<Long> findProjectIdsByUserId(Long userId) {
        //获取角色成员
        List<RoleMemberVo> projectRoleMembers =
                roleMemberService.findRoleMembers(SpecialRole.SCOPE_PROJECT, null, List.of(userId), null);
        return projectRoleMembers.stream().map(RoleMemberVo::getOwnerResourceId).distinct()
                                 .collect(Collectors.toList());
    }

    @Override
    public boolean existsUserRoles(Long projectId, List<Long> roleIds, Long userId) {
        List<RoleMemberVo> projectRoleMembers =
                roleMemberService.findRoleMembers(SpecialRole.SCOPE_PROJECT, projectId, List.of(userId), roleIds);
        return !projectRoleMembers.isEmpty();
    }

    @Override
    public void createDefaultRoles(Long projectId, Long userId) {
        RoleVo role =
                roleService.createRole(
                        RoleVo.builder().id(IDUtils.getId()).name("项目管理员").scope(SpecialRole.SCOPE_PROJECT)
                              .ownerResourceId(projectId).build());
        roleMemberService.createRoleMember(
                RoleMemberVo.builder().scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).userId(userId)
                            .roleId(role.getId()).build());

        role =
                roleService.createRole(
                        RoleVo.builder().id(IDUtils.getId()).name("需求工程师").scope(SpecialRole.SCOPE_PROJECT)
                              .ownerResourceId(projectId).build());
        roleMemberService.createRoleMember(
                RoleMemberVo.builder().scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).userId(userId)
                            .roleId(role.getId()).build());

        role =
                roleService.createRole(
                        RoleVo.builder().id(IDUtils.getId()).name("质量保证工程师").scope(SpecialRole.SCOPE_PROJECT)
                              .ownerResourceId(projectId).build());
        roleMemberService.createRoleMember(
                RoleMemberVo.builder().scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).userId(userId)
                            .roleId(role.getId()).build());

        role =
                roleService.createRole(
                        RoleVo.builder().id(IDUtils.getId()).name("功能安全工程师").scope(SpecialRole.SCOPE_PROJECT)
                              .ownerResourceId(projectId).build());
        roleMemberService.createRoleMember(
                RoleMemberVo.builder().scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).userId(userId)
                            .roleId(role.getId()).build());

        role =
                roleService.createRole(
                        RoleVo.builder().id(IDUtils.getId()).name("硬件工程师").scope(SpecialRole.SCOPE_PROJECT)
                              .ownerResourceId(projectId).build());
        roleMemberService.createRoleMember(
                RoleMemberVo.builder().scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).userId(userId)
                            .roleId(role.getId()).build());

        role =
                roleService.createRole(
                        RoleVo.builder().id(IDUtils.getId()).name("软件工程师").scope(SpecialRole.SCOPE_PROJECT)
                              .ownerResourceId(projectId).build());
        roleMemberService.createRoleMember(
                RoleMemberVo.builder().scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).userId(userId)
                            .roleId(role.getId()).build());

        role =
                roleService.createRole(
                        RoleVo.builder().id(IDUtils.getId()).name("外部合作伙伴").scope(SpecialRole.SCOPE_PROJECT)
                              .ownerResourceId(projectId).build());
        roleMemberService.createRoleMember(
                RoleMemberVo.builder().scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).userId(userId)
                            .roleId(role.getId()).build());

        role =
                roleService.createRole(RoleVo.builder().id(IDUtils.getId()).name("项目负责人").specialRole(true)
                                             .specialRoleType(SpecialRole.PROJECT_OWNER)
                                             .scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).build());

        role =
                roleService.createRole(RoleVo.builder().id(IDUtils.getId()).name("所有项目成员").specialRole(true)
                                             .specialRoleType(SpecialRole.PROJECT_ALL_MEMBERS)
                                             .scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).build());

        role =
                roleService.createRole(RoleVo.builder().id(IDUtils.getId()).name("所有人").specialRole(true)
                                             .specialRoleType(SpecialRole.ALL_USERS).scope(SpecialRole.SCOPE_PROJECT)
                                             .ownerResourceId(projectId).build());

        role =
                roleService.createRole(RoleVo.builder().id(IDUtils.getId()).name("工作项负责人").specialRole(true)
                                             .specialRoleType(SpecialRole.TRACKER_OWNER)
                                             .scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).build());

        role =
                roleService.createRole(RoleVo.builder().id(IDUtils.getId()).name("工作项创建者").specialRole(true)
                                             .specialRoleType(SpecialRole.TRACKER_CREATOR)
                                             .scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).build());

        role =
                roleService.createRole(RoleVo.builder().id(IDUtils.getId()).name("工作项关注者").specialRole(true)
                                             .specialRoleType(SpecialRole.TRACKER_WATCHER)
                                             .scope(SpecialRole.SCOPE_PROJECT).ownerResourceId(projectId).build());

    }

}
