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

package com.harbortek.helm.productline.service.impl;

import com.harbortek.helm.productline.dao.ReportGroupDao;
import com.harbortek.helm.productline.entity.ReportGroupEntity;
import com.harbortek.helm.productline.service.ReportGroupService;
import com.harbortek.helm.productline.vo.ReportGroupVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
@Service
@Slf4j
public class ReportGroupServiceImpl implements ReportGroupService {

    @Autowired
    ReportGroupDao reportGroupDao;

    @Override
    public ReportGroupVo createReportGroup(ReportGroupVo reportGroupVo) {
        reportGroupVo.setId(IDUtils.getId());
        ReportGroupEntity entity = DataUtils.toEntity(reportGroupVo, ReportGroupEntity.class);
        entity = reportGroupDao.createReportGroup(entity);
        return DataUtils.toVo(entity,ReportGroupVo.class);
    }

    @Override
    public ReportGroupVo updateReportGroup(ReportGroupVo reportGroupVo) {
        ReportGroupEntity entity = DataUtils.toEntity(reportGroupVo, ReportGroupEntity.class);
        entity = reportGroupDao.updateReportGroup(entity);
        return DataUtils.toVo(entity,ReportGroupVo.class);
    }

    @Override
    public void deleteReportGroup(Long id) {
        reportGroupDao.deleteReportGroup(id);
    }

    @Override
    public ReportGroupVo findOneReportGroup(Long id) {
        ReportGroupEntity entity = reportGroupDao.findOneReportGroup(id);
        return DataUtils.toVo(entity,ReportGroupVo.class);
    }

    @Override
    public Collection<ReportGroupVo> findReportGroups(Long productLineId) {
        List<ReportGroupEntity> entities = reportGroupDao.findReportGroups(productLineId);
        return DataUtils.toVo(entities,ReportGroupVo.class);
    }
}
