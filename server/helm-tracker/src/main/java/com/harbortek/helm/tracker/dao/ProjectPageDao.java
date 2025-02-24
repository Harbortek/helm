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
import com.harbortek.helm.tracker.constants.ProjectPageTypes;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.JsonUtils;
import com.harbortek.helm.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ProjectPageDao extends BaseJdbcDao {

    public ProjectPageEntity createProjectPage(ProjectPageEntity page) {
        return save(page);
    }

    public void updateProjectPage(ProjectPageEntity page) {
        save(page);
        incRevision(page.getId());
        CacheUtils.evict(page.getId(), ProjectPageEntity.class);
    }

    public void updateProjectPageBasicInfo(ProjectPageEntity page) {
        getDslContext().update(getTable(ProjectPageEntity.class))
                .set(getField(BaseEntity.Fields.name), page.getName())
                .set(getField(BaseEntity.Fields.description), page.getDescription())
                .set(getField(BaseEntity.Fields.icon), page.getIcon())
                .set(getField(ProjectPageEntity.Fields.type), page.getType())
                .set(getField(ProjectPageEntity.Fields.componentType), page.getComponentType())
                .set(getField(ProjectPageEntity.Fields.trackerId), page.getTrackerId())
                .set(getField(ProjectPageEntity.Fields.pageSettingTrackers), JsonUtils.toJSONString(
                        page.getPageSettingTrackers()))
                .set(getField(ProjectPageEntity.Fields.url), page.getUrl())
                .set(getField(ProjectPageEntity.Fields.parentId), page.getParentId())
                .set(getField(ProjectPageEntity.Fields.smartDocId), page.getSmartDocId())
                .set(getField(ProjectPageEntity.Fields.smartPageId), page.getSmartPageId())
                .where(getField(BaseEntity.Fields.id).eq(page.getId()))
                .and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .execute();

        CacheUtils.evict(page.getId(), ProjectPageEntity.class);
    }

    public ProjectPageEntity findOneProjectPage(Long pageId) {
        return findById(pageId, ProjectPageEntity.class);
    }

    public List<ProjectPageEntity> findByProject(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.projectId).is(projectId));
//        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.level).is(1));
        Query query = Query.query(criteria);
        query.columns(BaseEntity.Fields.id, BaseEntity.Fields.name, BaseEntity.Fields.icon, BaseEntity.Fields.deleted,
                BaseEntity.Fields.createBy, BaseEntity.Fields.createDate, BaseEntity.Fields.lastModifiedBy,
                BaseEntity.Fields.lastModifiedDate,
                ProjectPageEntity.Fields.projectId,
                ProjectPageEntity.Fields.folder,
                ProjectPageEntity.Fields.url
                , ProjectPageEntity.Fields.blockTrackerItemRefs, ProjectPageEntity.Fields.trackerId,
                ProjectPageEntity.Fields.level, ProjectPageEntity.Fields.order,
                ProjectPageEntity.Fields.componentType, ProjectPageEntity.Fields.pageSettingTrackers,
                ProjectPageEntity.Fields.parentId, ProjectPageEntity.Fields.revision,
                ProjectPageEntity.Fields.smartPageId, ProjectPageEntity.Fields.type
                , ProjectPageEntity.Fields.watchers
        );

        query = query.sort(Sort.by(Sort.Direction.ASC, ProjectPageEntity.Fields.parentId, ProjectPageEntity.Fields.order));
        return find(query, ProjectPageEntity.class);
    }

    public List<ProjectPageEntity> findPagesByProject(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        return find(query, ProjectPageEntity.class);
    }

    public void deleteByProject(Long projectId) {
        delete(Query.query(Criteria.where(ProjectPageEntity.Fields.projectId).is(projectId)),
                ProjectPageEntity.class);
        CacheUtils.evictAll(ProjectPageEntity.class);
    }

    public void createProjectPages(Collection<ProjectPageEntity> pages) {
        saveAll(pages);
    }

    public void incRevision(Long pageId) {
        String sql = "update project_pages set revision = revision + 1 where id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", pageId);
        update(sql, params, ProjectPageEntity.class);
    }

    public List<ProjectPageEntity> findDocumentsByProject(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.folder).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.type).is(ProjectPageTypes.SMART_DOCUMENT));
        Query query = Query.query(criteria);
        return find(query, ProjectPageEntity.class);
    }

    public void deleteProjectPage(Long id) {
        markAsDeleted(id, ProjectPageEntity.class);
    }

    public void changeProjectPageOrder(Collection<ProjectPageEntity> pages) {
        String sql = "update project_pages set `order` = :order, parent_id = :parentId where id = :id";
        Map[] paramSource = pages.stream().map(page -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", page.getId());
            map.put("parentId", page.getParentId());
            map.put("order", page.getOrder());
            return map;
        }).toArray(Map[]::new);

        batchUpdate(sql, paramSource, ProjectPageEntity.class);

//        BulkOperations bulkInsertion =
//                getMongoTemplate().bulkOps(BulkOperations.BulkMode.UNORDERED, ProjectPageEntity.class);
//        pages.forEach(page -> {
//            Query query = new Query().addCriteria(Criteria.where(BaseEntity.Fields.id).is(page.getId()));
//            Update update =
//                    new Update().set(ProjectPageEntity.Fields.order, page.getOrder())
//                            .set(ProjectPageEntity.Fields.parentId, page.getParentId());
//            bulkInsertion.updateOne(query, update);
//        });
//        bulkInsertion.execute();
    }

    public List<ProjectPageEntity> findOwnChildrenByProjectPage(Long id) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.parentId).is(id));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.createBy).is(SecurityUtils.getCurrentUser().getId()));
        Query query = Query.query(criteria);
        return find(query, ProjectPageEntity.class);
    }

    public List<ProjectPageEntity> findPageByTrackerId(Long trackerId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.trackerId).is(trackerId));
        Query query = Query.query(criteria);
        return find(query, ProjectPageEntity.class);
    }

    public List<ProjectPageEntity> findPagesByParentId(Long parentId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.parentId).is(parentId));
        Query query = Query.query(criteria);
        return find(query, ProjectPageEntity.class);
    }

    public ProjectPageEntity findPageByComponentType(Long projectId, String componentType) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.componentType).is(componentType));
        Query query = Query.query(criteria);
        return findOne(query, ProjectPageEntity.class);
    }

    public List<ProjectPageEntity> findByDocIds(List<Long> docIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.smartDocId).in(docIds));
        Query query = Query.query(criteria);
        return find(query, ProjectPageEntity.class);
    }

    public List<Long> findPageIdsByProjectId(Long projectId) {
        String sql = "select id from project_pages where project_id = :projectId and deleted = false";
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        return findIds(sql, params);
    }

    public ProjectPageEntity findByDocId(Long docId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectPageEntity.Fields.smartDocId).is(docId));
        Query query = Query.query(criteria);
        return findOne(query, ProjectPageEntity.class);
    }

}