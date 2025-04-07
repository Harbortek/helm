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

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.smartpage.components.Component;
import com.harbortek.helm.smartpage.dao.TableTraceabilityDao;
import com.harbortek.helm.smartpage.service.DatasetService;
import com.harbortek.helm.smartpage.utils.ExpressionUtils;
import com.harbortek.helm.smartpage.vo.DataRequest;
import com.harbortek.helm.smartpage.vo.ItemVo;
import com.harbortek.helm.smartpage.vo.TableTraceabilityRequest;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.constants.EnumCodes;
import com.harbortek.helm.tracker.constants.PlanTypes;
import com.harbortek.helm.tracker.constants.TrackerStatusMeaning;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.dao.SprintDao;
import com.harbortek.helm.tracker.dao.TrackerDao;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.tracker.entity.plan.PlanEntity;
import com.harbortek.helm.tracker.entity.plan.SprintEntity;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.comparator.Comparators;

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
        TrackerEntity mainTracker = trackerDao.findOneTracker(mainTrackerId);

        ProjectEntity project = projectDao.findOneProject(mainTracker.getProjectId(), SecurityUtils.getCurrentUser().getId());
        String prefix = StringUtils.upperCase(project.getKeyName())+"-";

        Long linkTrackerId = config.getLinkTrackerId();
        Long linkTypeId = config.getLinkTypeId();
        if (!ObjectUtils.isValid(mainTrackerId) || !ObjectUtils.isValid(linkTrackerId) ||
                !ObjectUtils.isValid(linkTypeId)) {
            return emptyData();
        }

        List<SprintEntity> sprintEntities = sprintDao.findSprintByTargetVersion(projectId, versionId);

        List<ItemVo> mainTrackerItems =  DataUtils.toVo(trackerItemDao.findBySprintIds(projectId, ObjectUtils.ids(sprintEntities))
                .stream().filter(item->item.getTrackerId().equals(mainTrackerId))
                .sorted(Comparator.comparing(TrackerItemEntity::getItemNo)).collect(Collectors.toList()),ItemVo.class);

        Map<Long,ItemVo> mainTrackerItemMap = mainTrackerItems.stream()
                .collect(Collectors.toMap(
                        ItemVo::getId,
                        item->item,
                        (existing, replacement) -> existing // 处理重复key的情况
                ));


        if (ObjectUtils.isValid(linkTrackerId) && ObjectUtils.isValid(linkTypeId)) {
            List<TrackerLinkEntity> firstLevelLinks = tableTraceabilityDao.findLinksByTarget(ObjectUtils.ids(mainTrackerItems), linkTypeId);
            Collection<Long> sourceItemIds = ObjectUtils.ids(firstLevelLinks, TrackerLinkEntity.Fields.sourceItemId);
            List<ItemVo> linkedTrackerItems = DataUtils.toVo(trackerDao.findByIds(sourceItemIds, TrackerItemEntity.class), ItemVo.class);
            Map<Long, ItemVo> linkedTrackerItemMap = linkedTrackerItems.stream().collect(Collectors.toMap(
                    ItemVo::getId,
                    item -> item,
                    (existing, replacement) -> existing
            ));

            for (TrackerLinkEntity firstLink : firstLevelLinks) {
                ItemVo mainTrackerItem = mainTrackerItemMap.get(firstLink.getTargetItemId());
                if (mainTrackerItem != null) {
                    ItemVo linkedTrackerItem = linkedTrackerItemMap.get(firstLink.getSourceItemId());
                    if (linkedTrackerItem != null) {
                        mainTrackerItem.getChildren().add(linkedTrackerItem);
                    }
                }
            }

            Long secondLinkTrackerId = config.getSecondLinkTrackerId();
            Long secondLinkTypeId = config.getSecondLinkTypeId();

            if (ObjectUtils.isValid(secondLinkTrackerId) && ObjectUtils.isValid(secondLinkTypeId)) {
                List<TrackerLinkEntity> secondLevelLinks =
                        tableTraceabilityDao.findLinksByTarget(ObjectUtils.ids(linkedTrackerItems), secondLinkTypeId);
                Collection<Long> secondSourceItemIds = ObjectUtils.ids(secondLevelLinks, TrackerLinkEntity.Fields.sourceItemId);
                List<ItemVo> secondLinkedTrackerItems = DataUtils.toVo(trackerDao.findByIds(secondSourceItemIds, TrackerItemEntity.class), ItemVo.class);
                Map<Long, ItemVo> secondLinkedTrackerItemMap = secondLinkedTrackerItems.stream().collect(Collectors.toMap(
                        ItemVo::getId,
                        item -> item,
                        (existing, replacement) -> existing // 处理重复key的情况
                ));
                for (TrackerLinkEntity secondLink : secondLevelLinks) {
                    ItemVo linkedTrackerItem = linkedTrackerItemMap.get(secondLink.getTargetItemId());
                    if (linkedTrackerItem!= null) {
                        ItemVo secondLinkedTrackerItem = secondLinkedTrackerItemMap.get(secondLink.getSourceItemId());
                        if (secondLinkedTrackerItem!= null) {
                            linkedTrackerItem.getChildren().add(secondLinkedTrackerItem);
                        }
                    }
                }
            }
        }

//        String showType = config.getShowType();
//
//
        // 2. 处理数据
//        List<JSONObject> data = new ArrayList<>();
//
//        if ("SHOW_ALL".equals(showType)) {
//            for (TrackerItemEntity mainTrackerItem : mainTrackerItems) {
//                JSONObject row = new JSONObject();
//                row.set("id", mainTrackerItem.getId());
//                row.set("name", mainTrackerItem.getName());
//                row.set("itemNo", prefix+mainTrackerItem.getItemNo());
//                row.set("icon", mainTracker.getIcon());
//                row.set("parentId", null);
//                data.add(row);
//            }
//            linkedTrackerItems.forEach((key, value) -> {
//                value.forEach(item -> {
//                    JSONObject row = new JSONObject();
//                    row.set("id", item.getId());
//                    row.set("name", item.getName());
//                    row.set("itemNo", prefix+item.getItemNo());
//                    if (ObjectUtils.isValid(item.getTrackerId())) {
//                        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
//                        if (tracker!=null) {
//                            row.set("icon", tracker.getIcon());
//                        }
//                    }
//                    row.set("parentId", key);
//                    data.add(row);
//                });
//            });
//            secondLinkedTrackerItems.forEach((key, value) -> {
//                value.forEach(item -> {
//                    JSONObject row = new JSONObject();
//                    row.set("id", item.getId());
//                    row.set("name", item.getName());
//                    row.set("itemNo", prefix+item.getItemNo());
//                    if (ObjectUtils.isValid(item.getTrackerId())) {
//                        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
//                        if (tracker!=null) {
//                            row.set("icon", tracker.getIcon());
//                        }
//                    }
//                    row.set("parentId", key);
//                    data.add(row);
//                });
//            });
//        } else if ("SHOW_LINKS".equals(showType)) {
//            for (TrackerItemEntity mainTrackerItem : mainTrackerItems) {
//                if (linkedTrackerItems.containsKey(mainTrackerItem.getId())) {
//                    JSONObject row = new JSONObject();
//                    row.set("id", mainTrackerItem.getId());
//                    row.set("name", mainTrackerItem.getName());
//                    row.set("itemNo", prefix+mainTrackerItem.getItemNo());
//                    row.set("icon", mainTracker.getIcon());
//                    row.set("parentId", null);
//                    data.add(row);
//                }
//            }
//            linkedTrackerItems.forEach((key, value) -> {
//                value.forEach(item -> {
//                    JSONObject row = new JSONObject();
//                    row.set("id", item.getId());
//                    row.set("name", item.getName());
//                    row.set("itemNo", prefix+item.getItemNo());
//                    if (ObjectUtils.isValid(item.getTrackerId())) {
//                        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
//                        if (tracker!=null) {
//                            row.set("icon", tracker.getIcon());
//                        }
//                    }
//                    row.set("parentId", key);
//                    data.add(row);
//                });
//            });
//            secondLinkedTrackerItems.forEach((key, value) -> {
//                value.forEach(item -> {
//                    JSONObject row = new JSONObject();
//                    row.set("id", item.getId());
//                    row.set("name", item.getName());
//                    row.set("itemNo", prefix+item.getItemNo());
//                    if (ObjectUtils.isValid(item.getTrackerId())) {
//                        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
//                        if (tracker!=null) {
//                            row.set("icon", tracker.getIcon());
//                        }
//                    }
//                    row.set("parentId", key);
//                    data.add(row);
//                });
//            });
//        } else if ("SHOW_UNLINK".equals(showType)) {
//            for (TrackerItemEntity mainTrackerItem : mainTrackerItems) {
//                if (!linkedTrackerItems.containsKey(mainTrackerItem.getId())) {
//                    JSONObject row = new JSONObject();
//                    row.set("id", mainTrackerItem.getId());
//                    row.set("name", mainTrackerItem.getName());
//                    row.set("itemNo", prefix+mainTrackerItem.getItemNo());
//                    row.set("icon", mainTracker.getIcon());
//                    row.set("parentId", null);
//                    data.add(row);
//                }
//            }
//        } else if ("SHOW_PROBLEMS".equals(showType)) {
//
//        }


        JSONObject result = new JSONObject();

        result.set("tableColumns", new JSONArray());

        result.set("data", mainTrackerItems);
        result.set("total", mainTrackerItems.size());


        return JsonUtils.toJSONString(result);
    }

    private static String emptyData() {
        JSONObject result = new JSONObject();
        result.set("tableColumns", new JSONArray());
        result.set("data", new JSONArray());
        result.set("total", 0);
        return JsonUtils.toJSONString(result);
    }
}
