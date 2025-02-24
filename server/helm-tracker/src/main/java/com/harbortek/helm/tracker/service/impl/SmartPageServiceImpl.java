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

import com.harbortek.helm.tracker.dao.SmartPageDao;
import com.harbortek.helm.tracker.entity.smartpage.SmartPageEntity;
import com.harbortek.helm.tracker.service.SmartPageService;
import com.harbortek.helm.tracker.vo.smartpage.SmartPageVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SmartPageServiceImpl implements SmartPageService {

    @Autowired
    SmartPageDao smartPageDao;

    @Override
    public SmartPageVo createSmartPage(SmartPageVo smartPage) {
        smartPage.setId(IDUtils.getId());
        SmartPageEntity entity = DataUtils.toEntity(smartPage, SmartPageEntity.class);
        entity = smartPageDao.createSmartPage(entity);
        return DataUtils.toVo(entity,SmartPageVo.class);
    }

    @Override
    public List<SmartPageVo> findSmartPageByIds(List<Long> ids) {
        List<SmartPageEntity> pageList = smartPageDao.findByIds(ids, SmartPageEntity.class);
        return DataUtils.toVo(pageList,SmartPageVo.class);
    }

    @Override
    public SmartPageVo findOneSmartPage(Long id) {
        SmartPageEntity entity = smartPageDao.findById(id,SmartPageEntity.class);
        return DataUtils.toVo(entity,SmartPageVo.class);
    }

    @Override
    public SmartPageVo updateSmartPage(SmartPageVo smartPage) {
        SmartPageEntity entity = DataUtils.toEntity(smartPage, SmartPageEntity.class);
        entity = smartPageDao.updateSmartPage(entity);
        return DataUtils.toVo(entity,SmartPageVo.class);
    }

    @Override
    public void deleteSmartPage(Long id) {
        smartPageDao.deleteSmartPage(id);
    }

    @Override
    public void createSmartPages(List<SmartPageVo> smartPageVos) {
        List<SmartPageEntity> smartPageEntities = DataUtils.toEntity(smartPageVos, SmartPageEntity.class);
        smartPageEntities.forEach(smartPageEntity -> {
            if(ObjectUtils.isEmpty(smartPageEntity.getId())){
                smartPageEntity.setId(IDUtils.getId());
            }
        });
        List<SmartPageEntity> entity = DataUtils.toEntity(smartPageVos, SmartPageEntity.class);
        smartPageDao.createSmartPages(entity);
    }
}
