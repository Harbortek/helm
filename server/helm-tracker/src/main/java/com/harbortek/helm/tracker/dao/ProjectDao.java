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

package com.harbortek.helm.tracker.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.ProjectPermissions;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.project.RecentProjectEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.util.FilterUtils;
import com.harbortek.helm.tracker.vo.RecentProjectVo;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.parboiled.common.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ProjectDao extends BaseJdbcDao {

    public ProjectEntity createProject(ProjectEntity project) {
        return save(project);
    }

    public ProjectEntity updateProject(ProjectEntity project) {
        return save(project);
    }

    public void deleteProject(Long id) {
        markAsDeleted(id, ProjectEntity.class);
    }


    public ProjectEntity findOneProject(Long id) {
        SelectConditionStep<?> query = getDslContext().selectFrom(getTable(ProjectEntity.class))
                .where(getField(BaseEntity.Fields.id).eq(id))
                .and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and("project_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'"+ ProjectPermissions.PROJECT_VIEW+"') ");
        return findFirst(query.getSQL(ParamType.INLINED),null, ProjectEntity.class);
//        return findById(id, ProjectEntity.class);
    }

    public List<ProjectEntity> findProjectsByOwner(Long ownerId) {
        SelectConditionStep<?> query = getDslContext().selectFrom(getTable(ProjectEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(getField(ProjectEntity.Fields.ownerId).eq(ownerId))
                .and("project_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'"+ ProjectPermissions.PROJECT_VIEW+"') ");

        return find(query.getSQL(ParamType.INLINED),null, ProjectEntity.class);

//        Criteria criteria = Criteria.empty();
//        criteria = criteria.and(Criteria.where(ProjectEntity.Fields.ownerId).is(ownerId));
//        if(ObjectUtils.isNotEmpty(projectIds)){
//            criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).in(projectIds));
//        }
//        Query query = Query.query(criteria);
//        return find(query, ProjectEntity.class);
    }

    public List<ProjectEntity> findAll() {
        SelectConditionStep<?> query = getDslContext().selectFrom(getTable(ProjectEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and("project_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'"+ ProjectPermissions.PROJECT_VIEW+"') ");

        return find(query.getSQL(ParamType.INLINED),null, ProjectEntity.class);
//        Criteria criteria = Criteria.empty();
//        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
//        Query query = Query.query(criteria);
//        return find(query, ProjectEntity.class);
    }

//	public Collection<ProjectEntity> findByWorkspace(WorkSpace workSpace) {
//		Criteria criteria = Criteria.empty();
//		criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
//		criteria.and(Criteria.where(BaseEntity.Fields.id).in(ObjectUtils.ids(workSpace.getProjects())));
//		return find(query, ProjectEntity.class);
//	}
//
//	public Collection<ProjectEntity> findByProjectSet(ProjectSetEntity projectSet) {
//		Criteria criteria = Criteria.empty();
//		criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
//		criteria.and(Criteria.where(BaseEntity.Fields.id).in(projectSet.getProjects()));
//		return find(query, ProjectEntity.class);
//	}

    public Page<ProjectEntity> findByCategory(Long categoryId, String keyword, Long statusId, ObjectFilter filter,
                                              Pageable pageable) {
        List<Condition> conditionList =new ArrayList<>();
        conditionList.add(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE));

        if (categoryId != null && categoryId != 0) {
            conditionList.add(getField(ProjectEntity.Fields.categoryId).eq(categoryId));
        }
        if (StringUtils.isNotEmpty(keyword)) {
            conditionList.add(getField(BaseEntity.Fields.name).like("%"+keyword+"%"));
        }
        if (statusId != null) {
            conditionList.add(getField(ProjectEntity.Fields.statusId).eq(statusId));
        }
        //filter
        if (filter!=null){
            conditionList.add(FilterUtils.getCondition(filter));
        }
        //排序
        List<SortField<?>> sortFields = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(pageable.getSort())) {
            pageable.getSort().forEach(item -> {
                Field<?> field = getField(item.getProperty());
                if (item.getDirection() == Sort.Direction.DESC) {
                    sortFields.add(field.sort(SortOrder.DESC));
                }else {
                    sortFields.add(field.sort(SortOrder.ASC));
                }
            });
        }

        Long userId = SecurityUtils.getCurrentUser().getId();

        SelectWithTiesAfterOffsetStep<?> limit = getDslContext().selectFrom(getTable(ProjectEntity.class))
                .where(DSL.and(conditionList))
                .and("project_has_permission(id," + userId + ",'"+ ProjectPermissions.PROJECT_VIEW+"') ")
                .orderBy(sortFields).limit(pageable.getOffset(), pageable.getPageSize());
        List<ProjectEntity> trackerItemEntities = find(limit.getSQL(ParamType.INLINED), null, ProjectEntity.class);

        Integer count = getDslContext().selectCount().from(getTable(ProjectEntity.class))
                .where(DSL.and(conditionList))
                .and("project_has_permission(id," + userId + ",'"+ ProjectPermissions.PROJECT_VIEW+"') ")
                .fetchOneInto(Integer.class);

        return new PageImpl<>(trackerItemEntities, pageable, count);

    }

    public Boolean checkDuplicateProjectName(Long projectId, String name) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        if (ObjectUtils.isValid(projectId)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).not(projectId));
        }
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).is(name));
        Query query = Query.query(criteria);
        return exists(query, ProjectEntity.class);
    }

    public Boolean checkDuplicateProjectShortName(Long projectId, String shortName) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        if (projectId != null && projectId != 0) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).not(projectId));
        }
        criteria = criteria.and(Criteria.where(ProjectEntity.Fields.keyName).is(shortName));
        Query query = Query.query(criteria);
        return exists(query, ProjectEntity.class);
    }

    public long countUnFinishedProjects(Long userId, List<Long> projectIds, Collection<Long> statusIds) {

        Integer count = getDslContext().selectCount().from(getTable(ProjectEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(getField(ProjectEntity.Fields.statusId).in(statusIds))
                .and(getField(ProjectEntity.Fields.ownerId).eq(userId).or(getField(BaseEntity.Fields.id).in(projectIds)))
                .and("project_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'" + ProjectPermissions.PROJECT_VIEW + "') ")
                .fetchOneInto(Integer.class);
        if(ObjectUtils.isEmpty(count)){
            return 0;
        }
        return count;
    }

    public String getNextItemNo(Long projectId) {
        Map<String,Object> params = new HashMap<>();
        params.put("id", projectId);

        String updateSql = "update projects set max_item_no = max_item_no + 1 where id = :id";
        update(updateSql, params);


        String sql = "select max_item_no as maxItemNo from projects where id = :id";
        Long maxItemNo = count(sql, params);

        return String.valueOf(maxItemNo) ;
    }

    public List<RecentProjectEntity> findRecentProjects(Long userId, Long limit) {
//        UserVo currentUser = SecurityUtils.getCurrentUser();
//
//
//        Criteria criteria = Criteria.empty();
//        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
//
//        Query query = Query.query(criteria);
//        if(ObjectUtils.isNotEmpty(limit)){
//            query.limit(5);
//        }
//        query.sort(Sort.by(Sort.Direction.DESC, BaseEntity.Fields.lastModifiedDate));
//        return find(query, ProjectEntity.class);

        SelectLimitPercentStep<?> sql = getDslContext().selectFrom(getTable(RecentProjectEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and("project_has_permission(project_id," + userId + ",'" + ProjectPermissions.PROJECT_VIEW + "') ")
                .orderBy(getField(RecentProjectEntity.Fields.lastAccessDate).desc()).limit(limit);
        return find(sql.getSQL(ParamType.INLINED), null, RecentProjectEntity.class);

    }

    public RecentProjectEntity findOneRecentProject(Long userId, Long projectId) {
        SelectConditionStep<?> query = getDslContext().selectFrom(getTable(RecentProjectEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(getField(RecentProjectEntity.Fields.userId).eq(userId))
                .and(getField(RecentProjectEntity.Fields.projectId).eq(projectId))
                .and("project_has_permission(project_id," + userId + ",'"+ ProjectPermissions.PROJECT_VIEW+"') ");
        return findFirst(query.getSQL(ParamType.INLINED),null, RecentProjectEntity.class);
    }

    public RecentProjectEntity recordRecentProject(RecentProjectEntity recentProjectEntity) {
        return save(recentProjectEntity);
    }


    public List<Long> findProjectIdByCreateBy(Long userId) {

        SelectConditionStep<Record1<Object>> sql = getDslContext().selectDistinct(getField(BaseEntity.Fields.id))
                .from(getTable(ProjectEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(getField(BaseEntity.Fields.createBy).eq(userId))
                .and("project_has_permission(id," + userId + ",'" + ProjectPermissions.PROJECT_VIEW + "') ");
        List<ProjectEntity> projectEntities = find(sql.getSQL(ParamType.INLINED), null, ProjectEntity.class);
        return projectEntities.stream().map(ProjectEntity::getId).toList();
    }
}
