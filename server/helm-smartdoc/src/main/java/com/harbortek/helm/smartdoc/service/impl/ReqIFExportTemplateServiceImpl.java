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

package com.harbortek.helm.smartdoc.service.impl;

import com.harbortek.helm.smartdoc.dao.ReqIFExportTemplateDao;
import com.harbortek.helm.smartdoc.entity.ReqIFExportTemplateEntity;
import com.harbortek.helm.smartdoc.service.ReqIFExportTemplateService;
import com.harbortek.helm.smartdoc.vo.ReqIFExportTemplateVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ReqIFExportTemplateServiceImpl implements ReqIFExportTemplateService {

    @Autowired
    ReqIFExportTemplateDao exportTemplateDao;
    @Override
    public Collection<ReqIFExportTemplateVo> findTemplates(Long projectId, String keyword) {
        return DataUtils.toVo(exportTemplateDao.findTemplates(projectId, keyword), ReqIFExportTemplateVo.class);
    }

    @Override
    public ReqIFExportTemplateVo findOneTemplate(Long id) {
        return DataUtils.toVo(exportTemplateDao.findOneTemplate(id), ReqIFExportTemplateVo.class);
    }

    @Override
    public ReqIFExportTemplateVo createTemplate(ReqIFExportTemplateVo configVo) {
        configVo.setId(IDUtils.getId());
        ReqIFExportTemplateEntity entity = DataUtils.toEntity(configVo, ReqIFExportTemplateEntity.class);
        exportTemplateDao.createTemplate(entity);
        return configVo;
    }

    @Override
    public ReqIFExportTemplateVo updateTemplate(ReqIFExportTemplateVo configVo) {
        ReqIFExportTemplateEntity entity = DataUtils.toEntity(configVo, ReqIFExportTemplateEntity.class);
        exportTemplateDao.updateTemplate(entity);
        return configVo;
    }

    @Override
    public void deleteTemplate(Long id) {
        exportTemplateDao.deleteTemplate(id);
    }
}
