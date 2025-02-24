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

import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.system.vo.UserVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserVo createUser(UserVo user);

    UserVo updateUser(UserVo user);


    void batchDelete(List<Long> userIds);


    void deleteUser(Long userId);

    UserVo findOneUser(Long userId);

    Page<UserVo> findUsers(String keyword, Pageable pageable, List<Long> orgIds);

    UserVo findOneUserByPhoneNumber(String phoneNumber);

    List<UserVo> findUsersForExport();

    UserVo findOneUserByName(String userName);

    UserVo findOneUserByLoginName(String loginName);

    Boolean checkExistsByName(String name);

    void resetPwd(Long userId);

    void updateRoles(Long userId, List<Long> roleIds);

    void updateUserLastLogin(Long userId);

    List<UserVo> findUsersByIds(List<Long> userIds);

    List<RoleVo> findGlobalRolesByUserId(Long userId);
}
