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
import com.harbortek.helm.system.entity.RoleMemberEntity;
import com.harbortek.helm.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.noCondition;

@Repository
public class RoleMemberDao extends BaseJdbcDao {
    public void createRoleMember(RoleMemberEntity roleMemberEntity) {
        save(roleMemberEntity);
    }

    public void createRoleMembers(List<RoleMemberEntity> roleMembers) {
        if (roleMembers == null || roleMembers.isEmpty()) {
            return;
        }
        saveAll(roleMembers);
    }

    public List<RoleMemberEntity> findRoleMembers(String scope, Long ownerResourceId, List<Long> userIds,
                                                  List<Long> roleIds) {

        Criteria criteria = Criteria.empty();
        if (StringUtils.isNotEmpty(scope)) {
            criteria = criteria.and(Criteria.where(RoleMemberEntity.Fields.scope).is(scope));
        }
        if (ObjectUtils.isValid(ownerResourceId)) {
            criteria = criteria.and(Criteria.where(RoleMemberEntity.Fields.ownerResourceId).is(ownerResourceId));
        }
        if (ObjectUtils.isNotEmpty(userIds)) {
            criteria = criteria.and(Criteria.where(RoleMemberEntity.Fields.userId).in(userIds));
        }
        if (ObjectUtils.isNotEmpty(roleIds)) {
            criteria = criteria.and(Criteria.where(RoleMemberEntity.Fields.roleId).in(roleIds));
        }
        Query query = Query.query(criteria);
        return find(query, RoleMemberEntity.class);
    }


    public void deleteRoleMember(Long userId, Long roleId) {

        Criteria criteria = Criteria.empty();
        if (ObjectUtils.isValid(userId)) {
            criteria = criteria.and(Criteria.where(RoleMemberEntity.Fields.userId).is(userId));
        }
        if (ObjectUtils.isValid(roleId)) {
            criteria = criteria.and(Criteria.where(RoleMemberEntity.Fields.roleId).is(roleId));
        }

        if (ObjectUtils.isValid(userId) || ObjectUtils.isValid(roleId)) {
            Query query = Query.query(criteria);
            delete(query, RoleMemberEntity.class);
        }
    }

    public void deleteRoleMembers(String scope, Long ownerResourceId, List<Long> userIds, List<Long> roleIds) {
        Criteria criteria = Criteria.empty();
        if (StringUtils.isNotEmpty(scope)) {
            criteria = criteria.and(Criteria.where(RoleMemberEntity.Fields.scope).is(scope));
        }
        if (ObjectUtils.isValid(ownerResourceId)) {
            criteria = criteria.and(Criteria.where(RoleMemberEntity.Fields.ownerResourceId).is(ownerResourceId));
        }
        if (ObjectUtils.isNotEmpty(userIds)) {
            criteria = criteria.and(Criteria.where(RoleMemberEntity.Fields.userId).in(userIds));
        }
        if (ObjectUtils.isNotEmpty(roleIds)) {
            criteria = criteria.and(Criteria.where(RoleMemberEntity.Fields.roleId).in(roleIds));
        }
        if (StringUtils.isNotEmpty(scope) || ObjectUtils.isValid(ownerResourceId) || ObjectUtils.isNotEmpty(userIds) ||
                ObjectUtils.isNotEmpty(roleIds)) {
            Query query = Query.query(criteria);
            delete(query, RoleMemberEntity.class);
        }
    }


    public Map<Long, Integer> countRoleMembersGroupByRoleId(String scope, Long ownerResourceId, List<Long> userIds,
                                                            List<Long> roleIds) {
        Condition condition = noCondition();
        if (StringUtils.isNotEmpty(scope)){
            condition = condition.and(getField(RoleMemberEntity.Fields.scope).eq(scope));
        }
        if (ObjectUtils.isValid(ownerResourceId)) {
            condition = condition.and(getField(RoleMemberEntity.Fields.ownerResourceId).eq(ownerResourceId));
        }
        if (ObjectUtils.isNotEmpty(userIds)) {
            condition = condition.and(getField(RoleMemberEntity.Fields.userId).in(userIds));
        }
        if (ObjectUtils.isNotEmpty(roleIds)) {
            condition = condition.and(getField(RoleMemberEntity.Fields.roleId).in(roleIds));
        }

        Result<Record2<Object,Integer>> results = getDslContext().select(getField(RoleMemberEntity.Fields.roleId),
                                                                DSL.count().as(
                                            "total"))
                                                .from(getTable(RoleMemberEntity.class))
                                                .where(condition)
                                                .groupBy(getField(RoleMemberEntity.Fields.roleId)).fetch();
        Map<Long, Integer> map = new HashMap<>();
        for (Record2<Object,Integer> record : results) {
            map.put((Long)record.getValue(0),(Integer) record.getValue(1));
        }
        return map;
    }
}
