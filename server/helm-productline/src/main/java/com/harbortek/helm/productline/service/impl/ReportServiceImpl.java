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

import com.harbortek.helm.productline.dao.ReportDao;
import com.harbortek.helm.productline.entity.ReportEntity;
import com.harbortek.helm.productline.service.ReportService;
import com.harbortek.helm.productline.vo.ReportVo;
import com.harbortek.helm.tracker.constants.PageScopes;
import com.harbortek.helm.tracker.service.SmartPageService;
import com.harbortek.helm.tracker.vo.smartpage.SmartPageVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    ReportDao reportDao;

    @Autowired
    SmartPageService smartPageService;

    @Override
    public ReportVo createReport(ReportVo reportVo) {
        reportVo.setId(IDUtils.getId());
        ReportEntity entity = DataUtils.toEntity(reportVo, ReportEntity.class);

        //创建smart page
        SmartPageVo smartPage = new SmartPageVo();
        smartPage.setObjectId(reportVo.getId());
        smartPage.setName(reportVo.getName());
        smartPage.setScope(PageScopes.SCOPE_PRODUCT_LINE);
        smartPageService.createSmartPage(smartPage);

        entity.setSmartPageId(smartPage.getId());

        entity = reportDao.createReport(entity);
        return DataUtils.toVo(entity, ReportVo.class);
    }

    @Override
    public ReportVo updateReport(ReportVo reportVo) {
        ReportEntity entity = DataUtils.toEntity(reportVo, ReportEntity.class);
        reportDao.updateReport(entity);

        return reportVo;
    }

    @Override
    public void deleteReport(Long id) {
        ReportEntity entity = reportDao.findOneReport(id);
        if (entity!=null && entity.getSmartPageId()!=null){
            smartPageService.deleteSmartPage(entity.getSmartPageId());
        }
        reportDao.deleteReport(id);
    }

    @Override
    public ReportVo findOneReport(Long id) {
        ReportEntity entity = reportDao.findOneReport(id);
        return DataUtils.toVo(entity, ReportVo.class);
    }

    @Override
    public Collection<ReportVo> findReports(Long reportLineId, String keyword) {
        List<ReportEntity> entities = reportDao.findReports(reportLineId,keyword);
        return DataUtils.toVo(entities, ReportVo.class);
    }
}
