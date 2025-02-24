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
import com.harbortek.helm.system.entity.OrgEntity;
import com.harbortek.helm.util.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class OrgDao extends BaseJdbcDao {

    public void createOrg(OrgEntity org) {
        save(org);
    }

    public void updateOrg(OrgEntity org) {
        save(org);
    }

    public void deleteOrg(OrgEntity org) {
        markAsDeleted(org.getId(), OrgEntity.class);
    }

    public OrgEntity findOneOrg(Long orgId) {
        return findById(orgId, OrgEntity.class);
    }

    public Page<OrgEntity> findOrgies(String keyword, Pageable pageable) {

        Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
        if (StringUtils.isNotEmpty(keyword)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like(keyword));
        }
        Query query = Query.query(criteria).with(pageable);
        query.sort(Sort.by(OrgEntity.Fields.hierarchyCode).ascending());


        return find(query,pageable, OrgEntity.class);
    }

    public Page<OrgEntity> findOrgiesWithCount(Pageable pageable) {

        Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
        Query query = Query.query(criteria).with(pageable);
        return find(query,pageable, OrgEntity.class);
    }

    public String findMAXCode(Long parentId) {

        Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
        criteria = criteria.and(Criteria.where(OrgEntity.Fields.parentId).is(parentId));
        Query query = Query.query(criteria);
        query.sort(Sort.by(OrgEntity.Fields.hierarchyCode).descending());
        OrgEntity org = findOne(query, OrgEntity.class);
        if (org == null) {
            return null;
        }
        return org.getHierarchyCode();
    }

    public void deleteAllChild(String hierarchyCode) {

        Condition condition = DSL.noCondition();
        if (StringUtils.isEmpty(hierarchyCode)) {
            condition = condition.and(getField(BaseEntity.Fields.deleted).eq(false));
        }else{
            condition = condition.and(getField(BaseEntity.Fields.deleted).eq(false));
            condition = condition.and(getField(OrgEntity.Fields.hierarchyCode).like(hierarchyCode + "%"));
        }
        getDslContext().update(getTable(OrgEntity.class)).set(getField(BaseEntity.Fields.deleted), true).where(condition).execute();
        CacheUtils.evictAll(OrgEntity.class);
    }

    public List<OrgEntity> findOrgByHierarchyCode(String hierarchyCode) {
        Criteria criteria = Criteria.empty();
        if (StringUtils.isEmpty(hierarchyCode)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        } else {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
            criteria = criteria.and(Criteria.where(OrgEntity.Fields.hierarchyCode).like(hierarchyCode + "%"));
        }
        Query query = Query.query(criteria);
        query.sort(Sort.by("hierarchyCode").ascending());

        return find(query, OrgEntity.class);
    }

    public List<OrgEntity> findBrother(Long parentId, String hierarchyCode) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(OrgEntity.Fields.parentId).is(parentId));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        criteria = criteria.and(Criteria.where(OrgEntity.Fields.hierarchyCode).greaterThan(hierarchyCode));
        Query query = Query.query(criteria);
        query.sort(Sort.by("hierarchyCode").ascending());

        return find(query, OrgEntity.class);
    }

    public void changeHierarchyCode(OrgEntity parent, String newHierCode, List<OrgEntity> childList) {
        if (childList != null) {
            for (OrgEntity child : childList) {
                String childHierCode = child.getHierarchyCode();
                childHierCode = childHierCode.replaceFirst(parent.getHierarchyCode(), newHierCode);
                child.setHierarchyCode(childHierCode);
                updateOrg(child);
            }
        }
        parent.setHierarchyCode(newHierCode);
        updateOrg(parent);
    }

    public void changeHierarchyCodeByInset(OrgEntity childOrg, OrgEntity parentOrg, String parentHierarchyCode,
                                           Long parentId, Integer len, List<OrgEntity> childOrgOfChild) {
        String append = String.format("%03d", len);
        String hierarchyCode = parentHierarchyCode + append;
        childOrg.setParentId(parentId);
        childOrg.setHierarchyCode(hierarchyCode);
        updateOrg(childOrg);
    }

    public OrgEntity findOneOrgByHierarchyCode(String hierarchyCode) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        criteria = criteria.and(Criteria.where(OrgEntity.Fields.hierarchyCode).is(hierarchyCode));
        Query query = Query.query(criteria);
        return findOne(query, OrgEntity.class);
    }

    public OrgEntity findOneOrgByOrgName(String orgName) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).is(orgName));
        Query query = Query.query(criteria);
        return findOne(query, OrgEntity.class);
    }

    public List<OrgEntity> findByParent(Long parentOrgId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(OrgEntity.Fields.parentId).is(parentOrgId));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        Query query = Query.query(criteria);
        query.sort(Sort.by(OrgEntity.Fields.hierarchyCode).ascending());
        return find(query, OrgEntity.class);
    }

    public Boolean checkExistsByName(String name) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).is(name));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        Query query = Query.query(criteria);
        List<OrgEntity> orgies = find(query, OrgEntity.class);
        return !orgies.isEmpty();
    }
}
