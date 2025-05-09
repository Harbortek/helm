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
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.constants.SystemFields;
import com.harbortek.helm.tracker.entity.tracker.DocItemEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.util.FilterUtils;
import com.harbortek.helm.tracker.vo.chart.DailyTrend;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.tracker.vo.view.FilterCondition;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import com.harbortek.helm.util.*;
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
import org.springframework.data.relational.core.query.Update;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class TrackerItemDao extends BaseJdbcDao {

    public TrackerItemEntity createTrackerItemEntity(TrackerItemEntity trackerItemEntity) {
        return insert(trackerItemEntity);
    }

    public void createTrackerItems(List<TrackerItemEntity> trackerItems) {

        insertAll(trackerItems);
    }

    public void updateTrackerItemEntity(TrackerItemEntity item) {
        if (ObjectUtils.isValid(item.getId())) {
            update(item);
            incRevision(item.getId());
            CacheUtils.evict(item.getId(), TrackerItemEntity.class);
        }
    }

    public void deleteTrackerItemEntity(Long id) {
        markAsDeleted(id, TrackerItemEntity.class);
    }


    public TrackerItemEntity findOneById(Long id) {
        SelectConditionStep<?> query = getDslContext().selectFrom(getTable(TrackerItemEntity.class))
                .where(getField(BaseEntity.Fields.id).eq(id))
                .and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and("tracker_id<0 or tracker_item_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'ITEM_VIEW') ");
        return findFirst(query.getSQL(ParamType.INLINED),new HashMap<>(), TrackerItemEntity.class);
    }

    public List<TrackerItemEntity> findByIds(List<Long> itemIds) {
        if (ObjectUtils.isEmpty(itemIds)){
            return new ArrayList<>();
        }
        SelectConditionStep<?> query = getDslContext().selectFrom(getTable(TrackerItemEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(getField(BaseEntity.Fields.id).in(itemIds))
                .and("tracker_id<0 or tracker_item_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'ITEM_VIEW') ");
        return find(query.getSQL(ParamType.INLINED),null, TrackerItemEntity.class);
    }

    public List<TrackerItemEntity> findDocItemsByIds(List<Long> itemIds) {

        SelectConditionStep<?> query = getDslContext()
                .select(DSL.field("*"),
                        DSL.field("if(tracker_id<0,'rw'," +
                                "CONCAT(if(tracker_item_has_permission(id,'1','item_view'),'r','')," +
                                "if(tracker_item_has_permission(id,'1','ITEM_EDIT'),'w',''))) permission"))
                .from(getTable(TrackerItemEntity.class))
                .where(getField(BaseEntity.Fields.id).in(itemIds))
                .and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE));
        return new ArrayList<>(find(query.getSQL(ParamType.INLINED), null, DocItemEntity.class));
    }


    public void updateStatus(TrackerItemEntity item) {
        Update update = Update.from(new HashMap<>());
        update = update.set(TrackerItemEntity.Fields.statusId, item.getStatusId());
        update = update.set(TrackerItemEntity.Fields.meaningId, item.getMeaningId());
        updateById(item.getId(), update, TrackerItemEntity.class);
        CacheUtils.evict(item.getId(), TrackerItemEntity.class);
        incRevision(item.getId());
    }

    public void updateCustomField(TrackerItemEntity item) {

        Update update = Update.from(new HashMap<>());
        update = update.set(TrackerItemEntity.Fields.values, item.getValues());
        updateById(item.getId(), update, TrackerItemEntity.class);
        CacheUtils.evict(item.getId(), TrackerItemEntity.class);
        incRevision(item.getId());
    }

    public Page<TrackerItemEntity> findTrackerItems(Long projectId, Long trackerId, Long sprintId,
                                                    ObjectFilter filter,
                                                    String keyword, List<Long> filterTrackerIds,List<Long> sprintIds,
                                                    Pageable pageable) {
        List<Condition> conditionList =new ArrayList<>();
        conditionList.add(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE));
        if (projectId != null && projectId != 0) {
            conditionList.add(getField(TrackerItemEntity.Fields.projectId).eq(projectId));
        }
        if(filterTrackerIds != null && !filterTrackerIds.isEmpty()) {
            conditionList.add(getField(TrackerItemEntity.Fields.trackerId).in(filterTrackerIds));
        }else if(trackerId != null && trackerId != 0) {
            conditionList.add(getField(TrackerItemEntity.Fields.trackerId).eq(trackerId));
        }
        if (sprintId != null) {
            if(sprintId == -1){ //未规划至迭代
                Field<JSON> externalArray = DSL.jsonArray(sprintIds.stream().map(t->DSL.inline(String.valueOf(t))).toList());
                conditionList.add(DSL.field("JSON_OVERLAPS({0},COALESCE(JSON_EXTRACT({1},'$.*'),'[]'))",
                        externalArray,getField(TrackerItemEntity.Fields.values)).eq(false));
            }else if(sprintId != -2){ //指定迭代
                Field<JSON> externalArray = DSL.jsonArray(DSL.inline(String.valueOf(sprintId)));
                conditionList.add(DSL.field("JSON_OVERLAPS({0},COALESCE(JSON_EXTRACT({1},'$.*'),'[]'))",
                        externalArray,getField(TrackerItemEntity.Fields.values)).eq(true));
            }
        }
        if (StringUtils.isNotEmpty(keyword)) {
            conditionList.add(getField(BaseEntity.Fields.name).like("%" + keyword + "%"));
        }
        if (filter != null) {//筛选
            conditionList.add(FilterUtils.getCondition(filter,sprintIds));
        }
//        if (filter == null || filter.getGroupCondition() == null) {
//            return null;
//        }
        //排序
        List<SortField<?>> sortFields = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(pageable.getSort())) {
            pageable.getSort().forEach(item -> {
                Field<?> field = getField(item.getProperty());
                if(SystemFields.ITEM_NO.equals(item.getProperty())){
                    field=DSL.cast(field,Integer.class);
                }
                if (item.getDirection() == Sort.Direction.DESC) {
                    sortFields.add(field.sort(SortOrder.DESC));
                }else {
                    sortFields.add(field.sort(SortOrder.ASC));
                }
            });
        }

        Long userId = SecurityUtils.getCurrentUser().getId();

        SelectSeekStepN<?> objects = getDslContext().selectFrom(getTable(TrackerItemEntity.class))
                .where(DSL.and(conditionList))
                .and("tracker_item_has_permission(id," + userId + ",'ITEM_VIEW') ")
                .orderBy(sortFields);
        String sql=objects.getSQL(ParamType.INLINED);
        if(!pageable.isUnpaged()){
            sql =  objects.limit(pageable.getOffset(), pageable.getPageSize()).getSQL(ParamType.INLINED);
        }
        List<TrackerItemEntity> trackerItemEntities = find(sql, null, TrackerItemEntity.class);

        Integer count = getDslContext().selectCount().from(getTable(TrackerItemEntity.class))
                .where(DSL.and(conditionList))
                .and("tracker_item_has_permission(id," + userId + ",'ITEM_VIEW') ")
                .fetchOneInto(Integer.class);

        return new PageImpl<>(trackerItemEntities, pageable, count);

    }

    public List<FilterCondition> findGroupItems(Long projectId, Long trackerId, Long sprintId, ObjectFilter filter,
                                                String keyword,List<Long> sprintIds) {
        List<Condition> conditionList =new ArrayList<>();
        conditionList.add(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE));
        if (projectId != null && projectId.longValue() != 0) {
            conditionList.add(getField(TrackerItemEntity.Fields.projectId).eq(projectId));
        }
        if(trackerId != null && trackerId != 0) {
                conditionList.add(getField(TrackerItemEntity.Fields.trackerId).eq(trackerId));
        }
        if (sprintId != null) {
            if(sprintId == -1){ //未规划至迭代
                Field<JSON> externalArray = DSL.jsonArray(sprintIds.stream().map(t->DSL.inline(String.valueOf(t))).toList());
                conditionList.add(DSL.field("JSON_OVERLAPS({0},COALESCE(JSON_EXTRACT({1},'$.*'),'[]'))",
                        externalArray,getField(TrackerItemEntity.Fields.values)).eq(false));
            }else if(sprintId != -2){ //指定迭代
                Field<JSON> externalArray = DSL.jsonArray(DSL.inline(String.valueOf(sprintId)));
                conditionList.add(DSL.field("JSON_OVERLAPS({0},COALESCE(JSON_EXTRACT({1},'$.*'),'[]'))",
                        externalArray,getField(TrackerItemEntity.Fields.values)).eq(true));
            }
        }
        if (StringUtils.isNotEmpty(keyword)) {
            conditionList.add(getField(BaseEntity.Fields.name).like("%" + keyword + "%"));
        }
        String groupField = "";
        if (filter != null) {//筛选
            groupField = filter.getGroupCondition().getField();
            filter.getGroupCondition().setField(null);
            conditionList.add(FilterUtils.getCondition(filter,sprintIds));
        }else {
            return null;
        }
        Long userId = SecurityUtils.getCurrentUser().getId();

        Result<Record2<Object, Integer>> value = getDslContext().select(getField(groupField), DSL.count().as("value"))
                .from(getTable(TrackerItemEntity.class))
                .where(DSL.and(conditionList))
                .and("tracker_item_has_permission(id," + userId + ",'ITEM_VIEW') ")
                .groupBy(getField(groupField)).fetch();

        log.info("bbbsql:{}",DSL.and(conditionList));
        List<FilterCondition> list = new ArrayList<>();
        for (Record2<Object, Integer> record : value) {
            FilterCondition filterCondition = new FilterCondition();
            if(ObjectUtils.isEmpty(record.getValue(0))){
                filterCondition.setField(null);
            }else{
                filterCondition.setField(record.getValue(0).toString());
            }
            filterCondition.setValue(record.getValue(1).toString());
            list.add(filterCondition);
        }
        return list;
    }

    public void updateSystemField(TrackerItemEntity item, String systemProperty, Object newValue) {
        updateById(item.getId(), Update.update(systemProperty, newValue), TrackerItemEntity.class);
        CacheUtils.evict(item.getId(), TrackerItemEntity.class);
        incRevision(item.getId());
    }

    public void incRevision(Long itemId) {
        String sql = "update tracker_items set revision = revision + 1 where id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", itemId);
        update(sql, params);

    }

    public List<TrackerItemEntity> findBySprintIds(Long projectId,List<Long> sprintIds) {
        if(ObjectUtils.isEmpty(sprintIds)){
            return null;
        }

        Field<JSON> externalArray = DSL.jsonArray(sprintIds.stream().map(t->DSL.inline(String.valueOf(t))).toList());
        Select<?> query = getDslContext().selectFrom(getTable(TrackerItemEntity.class))
                .where(getField(TrackerItemEntity.Fields.projectId).eq(projectId))
                .and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(DSL.field("JSON_OVERLAPS({0},COALESCE(JSON_EXTRACT({1},'$.*'),'[]'))",
                        externalArray,getField(TrackerItemEntity.Fields.values)).eq(true))
                .and("tracker_item_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'ITEM_VIEW') ")
                .orderBy(getField(TrackerItemEntity.Fields.trackerId).asc(),getField(TrackerItemEntity.Fields.itemNo).asc());
        return find(query.getSQL(ParamType.INLINED),null, TrackerItemEntity.class);

    }
    public List<Long> findIdsBySprintId(Long projectId, Long sprintId, Long excludeMeaningId) {
        if(ObjectUtils.isEmpty(sprintId)){
            return null;
        }

        Field<JSON> externalArray = DSL.jsonArray(DSL.inline(String.valueOf(sprintId)));
        SelectConditionStep<?> query = getDslContext().selectFrom(getTable(TrackerItemEntity.class))
                .where(getField(TrackerItemEntity.Fields.projectId).eq(projectId))
                .and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(DSL.field("JSON_OVERLAPS({0},COALESCE(JSON_EXTRACT({1},'$.*'),'[]'))",
                        externalArray,getField(TrackerItemEntity.Fields.values)).eq(true))
                .and("tracker_item_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'ITEM_VIEW') ");
        if(ObjectUtils.isValid(excludeMeaningId)){
            query = query.and(getField(TrackerItemEntity.Fields.meaningId).ne(excludeMeaningId));
        }
        return find(query.getSQL(ParamType.INLINED),null, TrackerItemEntity.class)
                .stream().map(BaseEntity::getId).toList();
    }

    public List<TrackerItemEntity> findByTargetVersionIds(Long projectId,List<Long> targetVersionIds) {
        if(ObjectUtils.isEmpty(targetVersionIds)){
            return null;
        }

        Field<JSON> externalArray = DSL.jsonArray(targetVersionIds.stream().map(t->DSL.inline(String.valueOf(t))).toList());
        SelectConditionStep<?> query = getDslContext().selectFrom(getTable(TrackerItemEntity.class))
                .where(getField(TrackerItemEntity.Fields.projectId).eq(projectId))
                .and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(DSL.field("JSON_OVERLAPS({0},COALESCE(JSON_EXTRACT({1},'$.*'),'[]'))",
                        externalArray,getField(TrackerItemEntity.Fields.values)).eq(true))
                .and("tracker_item_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'ITEM_VIEW') ");
        return find(query.getSQL(ParamType.INLINED),null, TrackerItemEntity.class);

    }

    public TrackerItemEntity findOneByItemNo(Long projectId, String itemNo) {
        SelectConditionStep<?> query = getDslContext().selectFrom(getTable(TrackerItemEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(getField(TrackerItemEntity.Fields.projectId).eq(projectId))
                .and(getField(TrackerItemEntity.Fields.itemNo).eq(itemNo))
                .and("tracker_item_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'ITEM_VIEW') ");
        return findFirst(query.getSQL(ParamType.INLINED),null, TrackerItemEntity.class);
    }

    public void deleteByTrackerIds(List<Long> trackerIds) {
        getDslContext().update(getTable(TrackerItemEntity.class))
                .set(getField(BaseEntity.Fields.deleted), Boolean.TRUE)
                .where(getField(TrackerItemEntity.Fields.trackerId).in(trackerIds))
                .and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .execute();
    }

    public void batchUpdateTrackerItem(List<TrackerItemEntity> trackerItemEntities) {
        String sql =
                "update tracker_items set name = :name," +
                        "owner_id = :ownerId," +
                        "watchers = :watchers," +
                        "priority_id = :priorityId," +
                        "severity_id = :severityId," +
                        "status_id = :statusId," +
                        "assigned_to_id = :assignedToId," +
                        "assigned_date = :assignedDate," +
                        "tracker_id = :trackerId," +
                        "plan_start_date = :planStartDate," +
                        "plan_end_date = :planEndDate," +
                        "real_start_date = :realStartDate," +
                        "real_end_date = :realEndDate," +
                        "progress = :progress," +
                        "close_date = :closeDate," +
                        "estimate_working_hours = :estimateWorkingHours," +
                        "registered_working_hours = :registeredWorkingHours," +
                        "remaining_working_hours = :remainingWorkingHours," +
                        "`values` = :values," +
                        "revision = revision + 1 where id = :id";
        List<SqlParameterSource> parameterSources = new ArrayList<>();

        trackerItemEntities.forEach(item -> {
            Map<String,Object> params = new HashMap<>();
            params.put("id", item.getId());
            params.put("name", item.getName());
            params.put("ownerId", item.getOwnerId());
            params.put("watchers", JsonUtils.toJSONString(item.getWatchers()));
            params.put("priorityId", item.getPriorityId());
            params.put("severityId", item.getSeverityId());
            params.put("statusId", item.getStatusId());
            params.put("assignedToId", item.getAssignedToId());
            params.put("assignedDate", item.getAssignedDate());
            params.put("trackerId", item.getTrackerId());
            params.put("planStartDate", item.getPlanStartDate());
            params.put("planEndDate", item.getPlanEndDate());
            params.put("realStartDate", item.getRealStartDate());
            params.put("realEndDate", item.getRealEndDate());
            params.put("progress", item.getProgress());
            params.put("closeDate", item.getCloseDate());
            params.put("estimateWorkingHours", item.getEstimateWorkingHours());
            params.put("registeredWorkingHours", item.getRegisteredWorkingHours());
            params.put("remainingWorkingHours", item.getRemainingWorkingHours());
            params.put("values", JsonUtils.toJSONString(item.getValues()));
            parameterSources.add(new MapSqlParameterSource(params));
        });
        batchUpdate(sql, parameterSources.toArray(new SqlParameterSource[0]), TrackerItemEntity.class);
    }

    public void batchUpdateCustomField(List<TrackerItemEntity> trackerItemEntities) {
        String sql =
                "update tracker_items set `values` = :values,revision = revision + 1 where id = :id";
        List<SqlParameterSource> parameterSources = new ArrayList<>();

        trackerItemEntities.forEach(item -> {
            Map<String,Object> params = new HashMap<>();
            params.put("id", item.getId());
            params.put("values", JsonUtils.toJSONString(item.getValues()));
            parameterSources.add(new MapSqlParameterSource(params));
        });

        batchUpdate(sql, parameterSources.toArray(new SqlParameterSource[0]), TrackerItemEntity.class);
    }

    public Date findLastModified(Long projectId) {
        SelectLimitPercentStep<Record1<Object>> limit = getDslContext().select(getField(BaseEntity.Fields.lastModifiedDate))
                .from(getTable(TrackerItemEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(getField(TrackerItemEntity.Fields.projectId).eq(projectId))
                .and("tracker_item_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'ITEM_VIEW') ")
                .orderBy(getField(BaseEntity.Fields.lastModifiedDate).desc())
                .limit(1);
        return findFirst(limit.getSQL(ParamType.INLINED),null, Date.class);
    }

    public List<DailyTrend> findDailyTrend(Long projectId, EnumItemVo enumItemVo) {
        Date endDate = DateUtils.getTodayStartTime();
        Date startDate = DateUtils.addDay(endDate, -30);



        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                             .and(TrackerItemEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        long total = count(query, TrackerItemEntity.class);


        LinkedHashMap<String, DailyTrend> map = new LinkedHashMap<>();
        for (int i = 0; i < 30; i++) {
            Date date = DateUtils.addDay(startDate, i);
            DailyTrend dailyTrend = new DailyTrend();
            dailyTrend.setId(DateUtils.toDefDateString(date, "yyyy-MM-dd"));
            dailyTrend.setAmount((int) total);
            map.put(dailyTrend.getId(), dailyTrend);
        }

        String sql =
                """
                select DATE_FORMAT(last_modified_date,'YYY-MM-DD') as day,count(*) as amount from tracker_items
                where last_modified_date >= :startDate and last_modified_date <= :endDate and project_id = :projectId 
                and meaning_id = :meaningId and :itemPerm    
                group by DATE_FORMAT(last_modified_date,'YYY-MM-DD')
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("meaningId", enumItemVo.getId());
        params.put("itemPerm", "tracker_item_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'ITEM_VIEW') ");

        List<DailyTrend> results = find(sql,params, DailyTrend.class);
        for (DailyTrend trend : results) {
            String date = trend.getId();
            if (ObjectUtils.isNotEmpty(map.get(date))) {
                map.get(date).setAmount((int) total - trend.getAmount());
            }
        }

        return new ArrayList<>(map.values());
    }

    public List<Long> findItemIdsByTrackerId(Long trackerId, Long statusId) {
        SelectConditionStep<?> query = getDslContext().selectDistinct(getField(BaseEntity.Fields.id))
                .from(getTable(TrackerItemEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(getField(TrackerItemEntity.Fields.trackerId).eq(trackerId))
                .and(getField(TrackerItemEntity.Fields.statusId).eq(statusId))
                .and("tracker_id<0 or tracker_item_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'ITEM_VIEW') ");
        List<TrackerItemEntity> trackerItemEntities = find(query.getSQL(ParamType.INLINED), null, TrackerItemEntity.class);
        return trackerItemEntities.stream().map(TrackerItemEntity::getId).toList();
    }

    public List<TrackerItemEntity> findItemByTrackerIds(Long projectId, List<Long> trackerIds) {
        SelectConditionStep<?> query = getDslContext().selectFrom(getTable(TrackerItemEntity.class))
                .where(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE))
                .and(getField(TrackerItemEntity.Fields.projectId).eq(projectId))
                .and(getField(TrackerItemEntity.Fields.trackerId).in(trackerIds))
                .and("tracker_id<0 or tracker_item_has_permission(id," + SecurityUtils.getCurrentUser().getId() + ",'ITEM_VIEW') ");
        return find(query.getSQL(ParamType.INLINED),null, TrackerItemEntity.class);
    }

    public void batchDeleteTrackerItem(List<Long> itemIds) {
        Condition condition = DSL.noCondition();
        condition = condition.and(getField(BaseEntity.Fields.id).in(itemIds));
        getDslContext().update(getTable(TrackerItemEntity.class))
                        .set(getField(BaseEntity.Fields.deleted),true).where(condition).execute();
        CacheUtils.evict(itemIds, TrackerItemEntity.class);
    }

    public void replaceWorkItemStatus(Long trackerId, TrackerStatus oldTrackerStatus,
                                      TrackerStatus newTrackerStatus) {

        getDslContext().update(getTable(TrackerItemEntity.class))
                .set(getField(TrackerItemEntity.Fields.statusId), newTrackerStatus.getId())
                .set(getField(TrackerItemEntity.Fields.meaningId), newTrackerStatus.getMeaning().getId())
                .where(getField(TrackerItemEntity.Fields.trackerId).eq(trackerId))
                .and(getField(TrackerItemEntity.Fields.statusId).eq(oldTrackerStatus.getId()))
                .execute();
    }

    public void deleteTrackerItemsByPageId(Long pageId) {
        getDslContext().update(getTable(TrackerItemEntity.class))
                .set(getField(BaseEntity.Fields.deleted),true)
                .where(getField(TrackerItemEntity.Fields.relatedWikis).contains(pageId))
                .execute();
    }
}
