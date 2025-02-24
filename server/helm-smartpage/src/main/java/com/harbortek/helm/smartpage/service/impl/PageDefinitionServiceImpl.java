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

package com.harbortek.helm.smartpage.service.impl;

import com.harbortek.helm.smartpage.service.PageDefinitionService;
import com.harbortek.helm.tracker.dao.SmartPageDao;
import com.harbortek.helm.tracker.vo.smartpage.PageDefinitionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PageDefinitionServiceImpl implements PageDefinitionService {
    @Autowired
    SmartPageDao smartPageDao;


    @Override
    public Long getObjectId(Long pageId) {
        return smartPageDao.getObjectId(pageId);
    }

    @Override
    public PageDefinitionVo loadPageDefinition(Long pageId) {
        return smartPageDao.findPageDefinition(pageId);
    }

    @Override
    public void savePageDefinition(Long pageId, PageDefinitionVo pageDefinitionVo) {
        smartPageDao.updatePageDefinition(pageId,pageDefinitionVo);
    }
}
