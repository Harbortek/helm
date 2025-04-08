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

package com.harbortek.helm.smartpage.components.table;

import com.harbortek.helm.smartpage.components.Component;
import com.harbortek.helm.smartpage.dao.TableTraceabilityDao;
import com.harbortek.helm.smartpage.vo.DataRequest;
import com.harbortek.helm.smartpage.vo.ItemVo;
import com.harbortek.helm.smartpage.vo.TableTraceabilityRequest;
import com.harbortek.helm.smartpage.vo.TraceabilityResult;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.dao.SprintDao;
import com.harbortek.helm.tracker.dao.TrackerDao;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.tracker.entity.plan.SprintEntity;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableTraceability implements Component {
    @Autowired
    TableTraceabilityDao tableTraceabilityDao;

    @Autowired
    TrackerDao trackerDao;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    SprintDao sprintDao;

    @Autowired
    TrackerItemDao trackerItemDao;

    @Override
    public String getName() {
        return "table-traceability";
    }


    @Override
    public String getData(Long pageId, DataRequest request) {
        TableTraceabilityRequest config = JsonUtils.toObject(request.getConfig(), TableTraceabilityRequest.class);
        assert config != null;
        Long projectId = config.getProjectId();
        Long versionId = config.getTargetVersionId();

        Long mainTrackerId = config.getMainTrackerId();

        Long linkTrackerId = config.getLinkTrackerId();
        String linkTypeIdWithDirection = config.getLinkTypeId();
        if (StringUtils.isEmpty(linkTypeIdWithDirection) || StringUtils.containsNone(linkTypeIdWithDirection,"-")) {
            return emptyData();
        }

        Long linkTypeId = Long.parseLong(linkTypeIdWithDirection.split("-")[0]);
        int linkDirection = Integer.parseInt(linkTypeIdWithDirection.split("-")[1]);
        if (!ObjectUtils.isValid(mainTrackerId) || !ObjectUtils.isValid(linkTrackerId) ||
                !ObjectUtils.isValid(linkTypeId)) {
            return emptyData();
        }

        List<SprintEntity> sprintEntities = sprintDao.findSprintByTargetVersion(projectId, versionId);

        List<ItemVo> mainTrackerItems = DataUtils.toVo(trackerItemDao.findBySprintIds(projectId, ObjectUtils.ids(sprintEntities))
                .stream().filter(item -> item.getTrackerId().equals(mainTrackerId))
                .sorted(Comparator.comparing(TrackerItemEntity::getItemNo)).collect(Collectors.toList()), ItemVo.class);

        if (ObjectUtils.isValid(linkTrackerId) && ObjectUtils.isValid(linkTypeId)) {
            List<ItemVo> linkedTrackerItems = tableTraceabilityDao.findLinkedTrackerItems(mainTrackerItems, linkTypeId,linkDirection);
            Long secondLinkTrackerId = config.getSecondLinkTrackerId();
            String secondLinkTypeIdWithDirection = config.getSecondLinkTypeId();

            if (StringUtils.isNotEmpty(secondLinkTypeIdWithDirection) || StringUtils.contains(secondLinkTypeIdWithDirection,"-")) {
                Long secondLinkTypeId = Long.parseLong(secondLinkTypeIdWithDirection.split("-")[0]);
                int secondLinkDirection = Integer.parseInt(secondLinkTypeIdWithDirection.split("-")[1]);

                if (ObjectUtils.isValid(secondLinkTrackerId) && ObjectUtils.isValid(secondLinkTypeId)) {
                    tableTraceabilityDao.findLinkedTrackerItems(linkedTrackerItems, secondLinkTypeId,secondLinkDirection);
                }
            }
        }

        String showType = config.getShowType();
        List<ItemVo> data = new ArrayList<>();
        if ("SHOW_ALL".equals(showType)) {
           data = mainTrackerItems;
        } else if ("SHOW_LINKS".equals(showType)) {
            for (ItemVo mainTrackerItem : mainTrackerItems) {
                if (mainTrackerItem.getChildren() != null && !mainTrackerItem.getChildren().isEmpty()) {
                    data.add(mainTrackerItem);
                }
            }
        } else if ("SHOW_UNLINK".equals(showType)) {
            for (ItemVo mainTrackerItem : mainTrackerItems) {
                if (mainTrackerItem.getChildren() == null || mainTrackerItem.getChildren().isEmpty()) {
                    data.add(mainTrackerItem);
                }
            }
        }

        TraceabilityResult result = new TraceabilityResult();

        result.setData(data);
        result.setTotal(data.size());


        return JsonUtils.toJSONString(result);
    }

    private static String emptyData() {
        TraceabilityResult result = new TraceabilityResult();
        return JsonUtils.toJSONString(result);
    }
}
