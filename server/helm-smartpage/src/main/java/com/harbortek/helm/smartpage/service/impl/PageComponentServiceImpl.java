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

import cn.hutool.json.JSONObject;
import com.harbortek.helm.smartpage.components.Component;
import com.harbortek.helm.smartpage.components.ComponentRegistry;
import com.harbortek.helm.smartpage.config.SmartPageMessages;
import com.harbortek.helm.smartpage.service.PageComponentService;
import com.harbortek.helm.smartpage.vo.DataRequest;
import com.harbortek.helm.tracker.dao.SmartPageDao;
import com.harbortek.helm.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class PageComponentServiceImpl implements PageComponentService {
    private static final Logger logger = LoggerFactory.getLogger(PageComponentServiceImpl.class);

    @Autowired
    SmartPageMessages livePageMessages;
    @Autowired
    SmartPageDao smartPageDao;

    @Override
    public String getData(Long pageId, Long componentId, DataRequest request) {
        JSONObject object = JsonUtils.parseObject(request.getConfig());
        String type = object.getStr("type");
        Component component = ComponentRegistry.getComponent(type);
        if (component!=null){
            return component.getData(pageId,request);
        }

        return JsonUtils.toJSONString(new ArrayList<>());
    }

}
