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

import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.tracker.dao.TrackerLinkDao;
import com.harbortek.helm.tracker.dao.TrackerLinkTypeDao;
import com.harbortek.helm.tracker.entity.link.TrackerLinkTypeEntity;
import com.harbortek.helm.tracker.service.TrackerLinkTypeService;
import com.harbortek.helm.tracker.util.TrackerUtils;
import com.harbortek.helm.tracker.vo.link.TrackerLinkTypeVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class TrackerLinkTypeServiceImpl implements TrackerLinkTypeService {
    @Autowired
    TrackerLinkTypeDao trackerLinkTypeDao;

    @Autowired
    TrackerLinkDao trackerLinkDao;

    @Override
    public List<TrackerLinkTypeVo> findLinkTypes(Long projectId) {
        List<TrackerLinkTypeEntity> entities = trackerLinkTypeDao.findLinkTypes(projectId);
        return DataUtils.toVo(entities,TrackerLinkTypeVo.class);
    }

    @Override
    public void createLinkType(TrackerLinkTypeVo linkTypeVo) {
        TrackerLinkTypeEntity entity = DataUtils.toEntity(linkTypeVo, TrackerLinkTypeEntity.class);
        entity.setId(IDUtils.getId());
        if(entity.getSystem()==null){
            entity.setSystem(Boolean.FALSE);
        }
        trackerLinkTypeDao.createLinkType(entity);
    }

    @Override
    public void updateLinkType(TrackerLinkTypeVo linkTypeVo) {
        TrackerLinkTypeEntity entity = DataUtils.toEntity(linkTypeVo, TrackerLinkTypeEntity.class);
        trackerLinkTypeDao.updateLinkType(entity);
    }

    @Override
    public void deleteLinkType(Long linkTypeId) {
        Long count = trackerLinkDao.findCountByLinkTypeId(linkTypeId);
        if(count!=0){
            ServiceException.throwException("删除失败！工作项关联类型存在已关联的工作项！");
        }
        trackerLinkTypeDao.markAsDeleted(linkTypeId, TrackerLinkTypeEntity.class);
    }

    @Override
    public void changeOrder(List<TrackerLinkTypeVo> linkTypeVos) {
        Collection<TrackerLinkTypeEntity> entities = DataUtils.toEntity(linkTypeVos, TrackerLinkTypeEntity.class);
        trackerLinkTypeDao.updateOrdinary(entities);
    }

    @Override
    public void batchCreateLinkTypes(List<TrackerLinkTypeVo> linkTypeVos) {
        List<TrackerLinkTypeEntity> entities = DataUtils.toEntity(linkTypeVos, TrackerLinkTypeEntity.class);
        trackerLinkTypeDao.batchCreateLinkTypes(entities);
    }

    @Override
    public TrackerLinkTypeVo findOneLinkType(Long id) {
        return DataUtils.toVo(trackerLinkTypeDao.findById(id,TrackerLinkTypeEntity.class), TrackerLinkTypeVo.class);
    }

    @Override
    public TrackerLinkTypeVo findOneLinkTypeByCode(Long projectId, String code) {
        return DataUtils.toVo(trackerLinkTypeDao.findOneLinkTypeByCode(projectId,code),
                              TrackerLinkTypeVo.class);
    }
}
