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

import cn.hutool.core.comparator.CompareUtil;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.tracker.constants.FieldTypes;
import com.harbortek.helm.tracker.constants.SystemFields;
import com.harbortek.helm.tracker.dao.ProjectPageDao;
import com.harbortek.helm.tracker.dao.TrackerDao;
import com.harbortek.helm.tracker.dao.ViewDao;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.view.ViewEntity;
import com.harbortek.helm.tracker.service.ViewService;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.tracker.vo.view.*;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class ViewServiceImpl implements ViewService {
    private final Logger logger = LoggerFactory.getLogger(ViewServiceImpl.class);
    @Autowired
    ViewDao viewDao;
    @Autowired
    TrackerDao trackerDao;
    @Autowired
    ProjectPageDao projectPageDao;

    @Override
    public ViewVo createView(ViewVo vo) {
        vo.setId(IDUtils.getId());
        vo.setSystem(Boolean.FALSE);
        ViewEntity view = ViewEntity.builder().id(vo.getId())
                                    .name(vo.getName())
                                    .objectId(vo.getObjectId())
                                    .viewType(vo.getViewType())
                                    .display(vo.getDisplay())
                                    .system(vo.getSystem())
                                    .ordinary(vo.getOrdinary())
                                    .viewConfig(vo.getViewConfig())
                                    .build();
        viewDao.createView(view);
        return vo;
    }

    @Override
    public void createDefaultView(Long trackerId) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<ViewEntity> views = new ArrayList<>();
        if(ObjectUtils.isNotEmpty(trackerEntity)){
            ViewEntity viewAll = ViewEntity.builder().id(IDUtils.getId()).name("全部 " + trackerEntity.getName())
                    .objectId(trackerEntity.getId()).viewType("PUBLIC").display(true).system(true)
                    .build();
            views.add(viewAll);
            trackerEntity.getTrackerStatuses().forEach(s -> {
                ViewEntity view = ViewEntity.builder().id(IDUtils.getId()).name(s.getName())
                        .objectId(trackerEntity.getId()).viewType("PUBLIC").display(true).system(true)
                        .viewConfig(buildViewConfig(trackerEntity,s))
                        .build();
                views.add(view);
            });
        }else{
            ProjectPageEntity projectPage = projectPageDao.findOneProjectPage(trackerId);
            ViewEntity viewAll = ViewEntity.builder().id(IDUtils.getId()).name("全部 " + projectPage.getName())
                    .objectId(projectPage.getId()).viewType("PUBLIC").display(true).system(true)
                    .build();
            views.add(viewAll);
        }

        viewDao.createViews(views);
    }

    private TrackerItemViewConfig buildViewConfig(TrackerEntity tracker, TrackerStatus s) {
        TrackerField trackerCalcField = findField(tracker.getTrackerFields(), SystemFields.STATUS_TYPE);
        IdNameReference reference = new IdNameReference<>(trackerCalcField);
        TrackerOrderBy orderBy = TrackerOrderBy.builder()
                .field(reference)
                .orderBy("ASC")
                .build();
        ObjectFilter trackerItemsFilter=new ObjectFilter();
        if(s!=null){
            ConditionGroup conditionGroup = new ConditionGroup();
            conditionGroup.getConditions().add(FilterCondition.builder().field(SystemFields.STATUS).type(FieldTypes.STATUS).operator("INCL")
                    .value(Arrays.asList(s.getId())).build());
            trackerItemsFilter.getConditionGroups().add(conditionGroup);
        }

        return TrackerItemViewConfig.builder().filter(trackerItemsFilter)
                .orderBys(Arrays.asList(orderBy))
                .build();
    }

    private TrackerField findField(List<TrackerField> calcFields, String propertyName) {
        return calcFields.stream()
                .filter(f -> f.isSystem() && (CompareUtil.compare(f.getSystemProperty(), propertyName) == 0))
                .findFirst().get();
    }
    @Override
    public void createMattersDefaultView(Long pageId, Long userId) {
        List<ViewEntity> views=new ArrayList<>();
        Arrays.asList("全部工作项","我负责的","我关注的","分配给我的").forEach(s -> {
            ViewEntity view = ViewEntity.builder().id(IDUtils.getId()).name(s)
                    .objectId(pageId).createBy(userId).viewType("PRIVATE").display(true)
                    .system(true).build();
            views.add(view);
        });
        viewDao.createViews(views);
    }

    @Override
    public void updateView(ViewVo vo) {
        ViewEntity oldView = viewDao.findOneView(vo.getId());
        ViewEntity viewEntity = DataUtils.toEntity(vo, ViewEntity.class);
        viewEntity.setObjectId(oldView.getObjectId());
        if(ObjectUtils.isEmpty(viewEntity.getViewConfig())){
            viewEntity.setViewConfig(oldView.getViewConfig());
        }
        viewDao.updateView(viewEntity);
    }

    @Override
    public void deleteView(Long viewId) {
        viewDao.deleteView(viewId);
    }

    @Override
    public ViewVo findById(Long viewId) {
        ViewEntity viewEntity = viewDao.findOneView(viewId);
        return DataUtils.toVo(viewEntity, ViewVo.class);
    }

    @Override
    public List<ViewVo> findByObjectId(Long objectId,Long userId,Boolean display,Boolean isMatter) {
        List<ViewEntity> viewEntities = viewDao.findByObjectId(objectId,userId,display);
        return DataUtils.toVo(viewEntities, ViewVo.class);
    }

    @Override
    public void renameView(ViewVo viewVo) {
        ViewEntity viewEntity = viewDao.findOneView(viewVo.getId());
        viewEntity.setName(viewVo.getName());
        viewDao.updateView(viewEntity);
    }

    @Override
    public List<ViewVo> findByObjectId(Long objectId) {
        List<ViewEntity> viewEntities = viewDao.findByObjectId(objectId,false);
        return DataUtils.toVo(viewEntities, ViewVo.class);
    }

    @Override
    public void batchCreateViews(List<ViewVo> views) {
        Collection<ViewEntity> entities = DataUtils.toEntity(views, ViewEntity.class);
        viewDao.createViews(new ArrayList<>(entities));
    }

    @Override
    public void updateViewsOrdinary(List<ViewVo> viewVos) {
        viewDao.batchUpdateOrdinary(viewVos);
    }
}
