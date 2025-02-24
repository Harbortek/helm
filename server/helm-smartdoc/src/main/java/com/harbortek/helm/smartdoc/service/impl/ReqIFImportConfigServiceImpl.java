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

import com.harbortek.helm.smartdoc.dao.ReqIFImportConfigDao;
import com.harbortek.helm.smartdoc.entity.ReqIFImportConfigEntity;
import com.harbortek.helm.smartdoc.service.ReqIFImportConfigService;
import com.harbortek.helm.smartdoc.vo.ReqIFImportConfigVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ReqIFImportConfigServiceImpl implements ReqIFImportConfigService {

    @Autowired
    ReqIFImportConfigDao wordImportConfigDao;
    @Override
    public Collection<ReqIFImportConfigVo> findConfigs(Long projectId, String keyword) {
        return DataUtils.toVo(wordImportConfigDao.findConfigs(projectId,keyword),ReqIFImportConfigVo.class);
    }

    @Override
    public ReqIFImportConfigVo findOneConfig(Long id) {
        return DataUtils.toVo(wordImportConfigDao.findOneConfig(id),ReqIFImportConfigVo.class);
    }

    @Override
    public ReqIFImportConfigVo createConfig(ReqIFImportConfigVo configVo) {
        configVo.setId(IDUtils.getId());
        ReqIFImportConfigEntity entity = DataUtils.toEntity(configVo, ReqIFImportConfigEntity.class);
        wordImportConfigDao.createConfig(entity);
        return configVo;
    }

    @Override
    public ReqIFImportConfigVo updateConfig(ReqIFImportConfigVo configVo) {
        ReqIFImportConfigEntity entity = DataUtils.toEntity(configVo, ReqIFImportConfigEntity.class);
        wordImportConfigDao.updateConfig(entity);
        return configVo;
    }

    @Override
    public void deleteConfig(Long id) {
        wordImportConfigDao.deleteConfig(id);
    }
}
