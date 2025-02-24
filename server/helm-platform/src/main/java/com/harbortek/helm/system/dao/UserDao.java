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

package com.harbortek.helm.system.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.util.BeanCopyUtils;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class UserDao extends BaseJdbcDao {

    public UserEntity createUser(UserEntity user) {
        user.setPassword(MD5Utils.string2MD5(user.getPassword()));
        return save(user);
    }

    public UserEntity updateUser(UserEntity user) {
        UserEntity oldUser = this.findOneUser(user.getId());
        BeanCopyUtils.copyWithoutNullProperties(user, oldUser);
        return save(oldUser);
    }

    public void deleteUser(Long userId) {
        markAsDeleted(userId, UserEntity.class);
    }

    public void deleteUsers(List<Long> userIds) {
        markAdDeleted(userIds, UserEntity.class);
    }

    public UserEntity findOneUser(Long userId) {
        return findById(userId, UserEntity.class);
    }

    public Page<UserEntity> findUsers(String keyword, Pageable pageable, List<Long> orgIds) {
        Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
        if (StringUtils.isNotEmpty(keyword)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like(keyword));
        }
        if (orgIds != null && !orgIds.isEmpty()) {
            criteria = criteria.and(Criteria.where(UserEntity.Fields.orgId).in(orgIds));
        }

        Query query = Query.query(criteria).with(pageable);

        return find(query,pageable, UserEntity.class);
    }

    public UserEntity findOneUserByPhoneNumber(String phoneNumber) {
        return null;
    }

    public List<UserEntity> findUsersForExport() {

        Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
        Query query = Query.query(criteria);
        return find(query, UserEntity.class);
    }

    public UserEntity findOneUserByName(String userName) {
        Criteria criteria = Criteria.where(BaseEntity.Fields.name).is(userName);
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        Query query = Query.query(criteria);
        return findOne(query, UserEntity.class);
    }


    public UserEntity findOneUserByLoginName(String loginName) {
        Criteria criteria = Criteria.where(UserEntity.Fields.loginName).is(loginName);
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        criteria = criteria.and(Criteria.where(UserEntity.Fields.disabled).is(false));
        Query query = Query.query(criteria);
        return findOne(query, UserEntity.class);
    }


    public Boolean checkExistsByName(String name) {
        Criteria criteria = Criteria.where(UserEntity.Fields.loginName).is(name);
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));

        Query query = Query.query(criteria);
        long count = count(query, UserEntity.class);
        return count > 0;
    }

    public void resetPwd(Long userId) {

        getDslContext().update(getTable(UserEntity.class))
                       .set(getField(UserEntity.Fields.password), MD5Utils.string2MD5("123456"))
                       .where(getField(BaseEntity.Fields.id).eq(userId))
                       .execute();


        CacheUtils.evict(userId, UserEntity.class);
    }


    public List<UserEntity> findUsersByIds(List<Long> userIds) {
        return findByIds(userIds, UserEntity.class,true);
    }

    public void updateUserLastLogin(Long userId) {

        getDslContext().update(getTable(UserEntity.class))
                       .set(getField(UserEntity.Fields.lastLogin), new Date())
                       .where(getField(BaseEntity.Fields.id).eq(userId))
                       .execute();

        CacheUtils.evict(userId, UserEntity.class);
    }
}
