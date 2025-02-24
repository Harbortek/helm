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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.util.TrackerUtils;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.layout.TrackerLayout;
import com.harbortek.helm.tracker.vo.tracker.nofitication.TrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransition;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class TrackerDao extends BaseJdbcDao {



    public TrackerEntity createTracker(TrackerEntity tracker) {
        TrackerUtils.fillId(tracker);

        return save(tracker);
    }

    public TrackerEntity updateTracker(TrackerEntity tracker) {
        return save(tracker);
    }

    public void deleteTracker(TrackerEntity tracker) {
       markAsDeleted(tracker.getId(), TrackerEntity.class);
    }


    public TrackerEntity findOneTracker(Long id) {
        return findById(id, TrackerEntity.class);
    }


    public List<TrackerEntity> findByProject(Long projectId) {
        String sql = "select id from trackers where project_id=:projectId and deleted = false order by ordinary";
        Map<String,Object> params = new HashMap<>();
        params.put("projectId", projectId);
        List<TrackerEntity> entities = find(sql, params, TrackerEntity.class);
        List<TrackerEntity> trackerEntityList = findByIds(ObjectUtils.ids(entities), TrackerEntity.class, true);
        if(ObjectUtils.isNotEmpty(trackerEntityList)){
            trackerEntityList.sort(Comparator.comparing(TrackerEntity::getOrdinary, Comparator.nullsLast(Comparator.naturalOrder())));
        }
        return trackerEntityList;
    }

    public void updateTrackerLayout(Long trackerId, TrackerLayout trackerLayout) {
        TrackerEntity trackerEntity = findById(trackerId, TrackerEntity.class);
        List<TrackerLayout> trackerLayouts = trackerEntity.getTrackerLayouts();

        AtomicBoolean flag= new AtomicBoolean(true);
        trackerLayouts.stream().forEach(t -> {
            if(ObjectUtils.isNotEmpty(t.getName())&&ObjectUtils.isNotEmpty(trackerLayout.getName())){
                if(t.getName().equals(trackerLayout.getName())){
                    t.setFields(trackerLayout.getFields());
                    t.setKeyFields(trackerLayout.getKeyFields());
                    t.setSections(trackerLayout.getSections());
                    flag.set(false);
                }
            }

        });
        if (flag.get()){ //未匹配，则增加
            trackerLayouts.add(trackerLayout);
        }
        Update update = Update.from(new HashMap<>()).set(TrackerEntity.Fields.trackerLayouts, trackerLayouts);
        updateById(trackerId, update, TrackerEntity.class);
    }



//    public void updateTrackerPermissions(Long trackerId, List<TrackerRolePermission> trackerRolePermissions) {
//
//        Update update = new Update().set(TrackerEntity.Fields.trackerPermissions, trackerRolePermissions);
//        updateById(trackerId, update, TrackerEntity.class);
//    }

    public TrackerLayout findOneTrackerLayout(Long trackerId, String layoutName) {
        TrackerEntity trackerEntity = findById(trackerId, TrackerEntity.class);
        List<TrackerLayout> trackerLayouts = trackerEntity.getTrackerLayouts();
        return trackerLayouts.stream().filter(t -> {return StringUtils.compare(t.getName(), layoutName) == 0;})
                     .findFirst().orElse(TrackerLayout.builder().name(layoutName).build());
    }

    public List<TrackerStateTransition> findTrackerStateTransitions(Long trackerId) {
        TrackerEntity trackerEntity = findById(trackerId, TrackerEntity.class);


        List<TrackerStatus> trackerStatuses = trackerEntity.getTrackerStatuses();
        Map<String,TrackerStatus> nameMap = new HashMap<>();
        Map<Long,TrackerStatus> idMap = new HashMap<>();
        AtomicLong index = new AtomicLong(1);
        trackerStatuses.forEach(s->{
            if (s.getId() == null || s.getId() == 0) s.setId(index.getAndIncrement());
            if (s.getOrdinary() == null || s.getOrdinary() == 0) {
                int max =
                        trackerStatuses.stream().mapToInt(ss -> {return ss.getOrdinary() != null ? ss.getOrdinary() : 0;})
                                .max().orElse(0);
                s.setOrdinary(max + 1);
            }
            nameMap.put(s.getName(),s);
            idMap.put(s.getId(),s);
        });

        List<TrackerStateTransition> trackerStateTransitions = trackerEntity.getTrackerStateTransitions();
        trackerStateTransitions.forEach(t->{
            TrackerStatus from = idMap.get(t.getTransitionFrom().getId());
            if (from==null){
                from = nameMap.get(t.getTransitionFrom().getName());
            }
            TrackerStatus to = idMap.get(t.getTransitionTo().getId());
            if (to==null){
                to = nameMap.get(t.getTransitionTo().getName());
            }

            t.setTransitionFrom(new IdNameReference<>(from));
            t.setTransitionTo(new IdNameReference<>(to));
        });

        return trackerStateTransitions;
    }

    public void updateTrackerStateTransitions(Long trackerId, List<TrackerStateTransition> trackerStateTransitions) {

        Update update = Update.update(TrackerEntity.Fields.trackerStateTransitions, trackerStateTransitions);
        updateById(trackerId, update, TrackerEntity.class);

    }

    public List<TrackerStatus> findTrackerStatus(Long trackerId) {
        TrackerEntity trackerEntity = findById(trackerId, TrackerEntity.class);
        List<TrackerStatus> statuses = trackerEntity.getTrackerStatuses();

        AtomicLong index = new AtomicLong(1);
        statuses.forEach(s->{
            if (s.getId() == null || s.getId() == 0) s.setId(index.getAndIncrement());
            if (s.getOrdinary() == null || s.getOrdinary() == 0) {
                int max =
                        statuses.stream().mapToInt(ss -> {return ss.getOrdinary() != null ? ss.getOrdinary() : 0;})
                                .max().orElse(0);
                s.setOrdinary(max + 1);
            }
        });
        return statuses;
    }

    public void updateTrackerStatus(Long trackerId, List<TrackerStatus> trackerStatuses) {
        Update update = Update.update(TrackerEntity.Fields.trackerStatuses, trackerStatuses);
        updateById(trackerId, update, TrackerEntity.class);

    }

    public void updateTrackerFields(Long trackerId, List<TrackerField> trackerFields) {
        Update update = Update.update(TrackerEntity.Fields.trackerFields, trackerFields);
        updateById(trackerId, update, TrackerEntity.class);
    }

    public List<TrackerField> findTrackerFields(Long trackerId) {

        TrackerEntity trackerEntity = findById(trackerId, TrackerEntity.class);
        return trackerEntity.getTrackerFields();
    }

//    public List<TrackerRolePermission> findTrackerPermissions(Long trackerId) {
//        TrackerEntity trackerEntity = findById(trackerId, TrackerEntity.class);
//        return trackerEntity.getTrackerPermissions();
//    }

    public TrackerNotification findTrackerNotification(Long trackerId) {
        TrackerEntity trackerEntity = findById(trackerId, TrackerEntity.class);
        return trackerEntity.getTrackerNotification();
    }

    public void updateTrackerNotification(Long trackerId, TrackerNotification trackerNotification) {

        Update update = Update.update(TrackerEntity.Fields.trackerNotification, trackerNotification);
        updateById(trackerId, update, TrackerEntity.class);

    }

    public void createTrackers(List<TrackerEntity> trackerEntities) {
        trackerEntities.forEach(TrackerUtils::fillId);
        saveAll(trackerEntities);
    }

    public void updateTrackerOrdinary(Collection<TrackerEntity> trackerEntities) {
        trackerEntities.forEach(tracker -> {
            Update update =
                    Update.update(TrackerEntity.Fields.ordinary, tracker.getOrdinary());
            CacheUtils.evict(tracker.getId(), TrackerEntity.class);
            updateById(tracker.getId(), update, TrackerEntity.class);
        });
    }

    public void updateNameAndType(TrackerEntity tracker) {
        Update update =
                Update.from(new HashMap<>()).set(BaseEntity.Fields.name, tracker.getName()).set(TrackerEntity.Fields.trackerTypeId,
                                                                                        tracker.getTrackerTypeId());
        updateById(tracker.getId(), update, TrackerEntity.class);
    }

    public TrackerEntity findOneTrackerByName(Long projectId, String trackerName) {
        Query query = Query.query(Criteria.where(BaseEntity.Fields.name).is(trackerName)
                                          .and(TrackerEntity.Fields.projectId).is(projectId)
                                          .and(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        return find(query, TrackerEntity.class).stream().findFirst().orElse(null);
    }
}
