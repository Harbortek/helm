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

package com.harbortek.helm.system.service.impl;

import com.harbortek.helm.common.constants.CacheConstants;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.dao.UserDao;
import com.harbortek.helm.system.entity.RoleMemberEntity;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.system.service.RoleMemberService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.RoleMemberVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.LogUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMemberService roleMemberService;

    @Override
    public UserVo createUser(UserVo user) {
        if (!ObjectUtils.isValid(user.getId())) {
            user.setId(IDUtils.getId());
        }
        UserEntity userEntity = DataUtils.toEntity(user, UserEntity.class);
        UserEntity entity = userDao.createUser(userEntity);

        updateRoles(user.getId(), ObjectUtils.ids(user.getRoles()) );

        LogUtils.log("系统管理", "用户管理", "用户 {0} 创建成功", user.getName());
        return DataUtils.toVo(entity, UserVo.class);
    }

    @Override
    @CacheEvict(value = CacheConstants.OBJECT_CACHE_NAME, key = "'objects_UserVo_'+#user.userId")
    public UserVo updateUser(UserVo user) {
        UserEntity userEntity = DataUtils.toEntity(user, UserEntity.class);
        UserEntity entity = userDao.updateUser(userEntity);
        updateRoles(user.getId(), ObjectUtils.ids(user.getRoles()) );
        LogUtils.log("系统管理", "用户管理", "用户 {0} 更新成功", user.getName());
        return DataUtils.toVo(entity, UserVo.class);
    }

    @Override
    public void batchDelete(List<Long> userIds) {
        userDao.deleteUsers(userIds);
        roleMemberService.deleteRoleMembers(null, null, userIds, null);

        LogUtils.log("系统管理", "用户管理", "用户批量删除成功");
    }

    @Override
    @CacheEvict(value = CacheConstants.OBJECT_CACHE_NAME, key = "'objects_UserVo_'+#userId")
    public void deleteUser(Long userId) {
        UserEntity userEntity = userDao.findOneUser(userId);
        userDao.deleteUser(userId);
        roleMemberService.deleteRoleMembers(null, null, List.of(userId), null);
        LogUtils.log("系统管理", "用户管理", "用户 {0} 删除成功", userEntity.getName());
    }


    @Override
    @Cacheable(value = CacheConstants.OBJECT_CACHE_NAME, key = "'objects_UserVo_'+#userId")
    public UserVo findOneUser(Long userId) {
        return DataUtils.toVo(userDao.findOneUser(userId), UserVo.class);
    }

    @Override
    public Page<UserVo> findUsers(String keyword, Pageable pageable, List<Long> orgIds) {
        return DataUtils.toVo(userDao.findUsers(keyword, pageable, orgIds), UserVo.class);
    }

    @Override
    public UserVo findOneUserByPhoneNumber(String phoneNumber) {

        return DataUtils.toVo(userDao.findOneUserByPhoneNumber(phoneNumber), UserVo.class);
    }

    @Override
    public List<UserVo> findUsersForExport() {
        return DataUtils.toVo(userDao.findUsersForExport(), UserVo.class) ;
    }


    @Override
    public UserVo findOneUserByName(String userName) {
        return DataUtils.toVo(userDao.findOneUserByName(userName), UserVo.class);
    }

    @Override
    public UserVo findOneUserByLoginName(String loginName) {
        UserEntity user = userDao.findOneUserByLoginName(loginName);
        if(ObjectUtils.isNotEmpty(user)){
            return DataUtils.toVo(user, UserVo.class);
        }
        return null;
    }

    @Override
    public Boolean checkExistsByName(String name) {
        return userDao.checkExistsByName(name);
    }

    @Override
    public void resetPwd(Long userId) {
        userDao.resetPwd(userId);
        UserEntity user = userDao.findOneUser(userId);
        LogUtils.log("系统管理", "用户管理", "用户 {0} 密码重置成功", user.getName());
    }


    @Override
    @CacheEvict(key = "USER-#userId")
    public void updateRoles(Long userId, List<Long> roleIds) {
        roleMemberService.deleteRoleMembers(SpecialRole.SCOPE_GLOBAL, null, List.of(userId), null);
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                RoleMemberVo roleMember = new RoleMemberVo();
                roleMember.setId(IDUtils.getId());
                roleMember.setScope(SpecialRole.SCOPE_GLOBAL);
                roleMember.setOwnerResourceId(null);
                roleMember.setRoleId(roleId);
                roleMember.setUserId(userId);
                roleMemberService.createRoleMember(roleMember);
            }
        }
    }

    @Override
    public void updateUserLastLogin(Long userId) {
        userDao.updateUserLastLogin(userId);
    }

    @Override
    public List<UserVo> findUsersByIds(List<Long> userIds) {
        return DataUtils.toVo(userDao.findUsersByIds(userIds), UserVo.class);
    }

    @Override
    public List<RoleVo> findGlobalRolesByUserId(Long userId) {
        List<Long> ids = ObjectUtils.ids(
                roleMemberService.findSystemRoleMembersByUserId(userId),
                RoleMemberEntity.Fields.roleId);
        return roleService.findRolesByIds(ids);
    }
}
