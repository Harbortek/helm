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
import com.harbortek.helm.smartpage.vo.TableTraceabilityRequest;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.dao.TrackerDao;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.util.JsonUtils;
import com.harbortek.helm.util.ObjectUtils;
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

    @Override
    public String getName() {
        return "table-traceability";
    }


    @Override
    public String getData(Long pageId, DataRequest request) {
        TableTraceabilityRequest config = JsonUtils.toObject(request.getConfig(), TableTraceabilityRequest.class);
        assert config != null;
        Long mainTrackerId = config.getMainTrackerId();
        TrackerEntity mainTracker = trackerDao.findOneTracker(mainTrackerId);

        ProjectEntity project = projectDao.findOneProject(mainTracker.getProjectId());
        String prefix = StringUtils.upperCase(project.getKeyName())+"-";

        Long linkTrackerId = config.getLinkTrackerId();
        Long linkTypeId = config.getLinkTypeId();
        if (!ObjectUtils.isValid(mainTrackerId) || !ObjectUtils.isValid(linkTrackerId) ||
                !ObjectUtils.isValid(linkTypeId)) {
            return emptyData();
        }


        Long targetVersionId = null;
        if (StringUtils.isNotEmpty(config.getTargetVersionExpl())) {
            targetVersionId = (Long) ExpressionUtils.execute(config.getTargetVersionExpl());
        }

        List<TrackerItemEntity> mainTrackerItems = tableTraceabilityDao.findTrackerItemsByTrackerId(mainTrackerId,
                                                                                                    targetVersionId);

        Map<Long, List<TrackerItemEntity>> linkedTrackerItems =
                tableTraceabilityDao.findLinkedTrackerItems(ObjectUtils.ids(mainTrackerItems),
                                                            linkTrackerId,
                                                            linkTypeId);

        Long secondLinkTrackerId = config.getSecondLinkTrackerId();
        Long secondLinkTypeId = config.getSecondLinkTypeId();

        Map<Long, List<TrackerItemEntity>> secondLinkedTrackerItems = new HashMap<>();
        if (ObjectUtils.isValid(secondLinkTrackerId) && ObjectUtils.isValid(secondLinkTypeId)) {
            List<Long> linkedTrackerItemList =
                    linkedTrackerItems.values().stream().flatMap(Collection::stream)
                                      .mapToLong(BaseEntity::getId)
                                      .boxed().collect(Collectors.toList());
            secondLinkedTrackerItems =
                    tableTraceabilityDao.findLinkedTrackerItems(linkedTrackerItemList,
                                                                secondLinkTrackerId,
                                                                secondLinkTypeId);
        }

        String showType = config.getShowType();


        // 2. 处理数据
        List<JSONObject> data = new ArrayList<>();

        if ("SHOW_ALL".equals(showType)) {
            for (TrackerItemEntity mainTrackerItem : mainTrackerItems) {
                JSONObject row = new JSONObject();
                row.set("id", mainTrackerItem.getId());
                row.set("name", mainTrackerItem.getName());
                row.set("itemNo", prefix+mainTrackerItem.getItemNo());
                row.set("icon", mainTracker.getIcon());
                row.set("parentId", null);
                data.add(row);
            }
            linkedTrackerItems.forEach((key, value) -> {
                value.forEach(item -> {
                    JSONObject row = new JSONObject();
                    row.set("id", item.getId());
                    row.set("name", item.getName());
                    row.set("itemNo", prefix+item.getItemNo());
                    if (ObjectUtils.isValid(item.getTrackerId())) {
                        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
                        if (tracker!=null) {
                            row.set("icon", tracker.getIcon());
                        }
                    }
                    row.set("parentId", key);
                    data.add(row);
                });
            });
            secondLinkedTrackerItems.forEach((key, value) -> {
                value.forEach(item -> {
                    JSONObject row = new JSONObject();
                    row.set("id", item.getId());
                    row.set("name", item.getName());
                    row.set("itemNo", prefix+item.getItemNo());
                    if (ObjectUtils.isValid(item.getTrackerId())) {
                        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
                        if (tracker!=null) {
                            row.set("icon", tracker.getIcon());
                        }
                    }
                    row.set("parentId", key);
                    data.add(row);
                });
            });
        } else if ("SHOW_LINKS".equals(showType)) {
            for (TrackerItemEntity mainTrackerItem : mainTrackerItems) {
                if (linkedTrackerItems.containsKey(mainTrackerItem.getId())) {
                    JSONObject row = new JSONObject();
                    row.set("id", mainTrackerItem.getId());
                    row.set("name", mainTrackerItem.getName());
                    row.set("itemNo", prefix+mainTrackerItem.getItemNo());
                    row.set("icon", mainTracker.getIcon());
                    row.set("parentId", null);
                    data.add(row);
                }
            }
            linkedTrackerItems.forEach((key, value) -> {
                value.forEach(item -> {
                    JSONObject row = new JSONObject();
                    row.set("id", item.getId());
                    row.set("name", item.getName());
                    row.set("itemNo", prefix+item.getItemNo());
                    if (ObjectUtils.isValid(item.getTrackerId())) {
                        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
                        if (tracker!=null) {
                            row.set("icon", tracker.getIcon());
                        }
                    }
                    row.set("parentId", key);
                    data.add(row);
                });
            });
            secondLinkedTrackerItems.forEach((key, value) -> {
                value.forEach(item -> {
                    JSONObject row = new JSONObject();
                    row.set("id", item.getId());
                    row.set("name", item.getName());
                    row.set("itemNo", prefix+item.getItemNo());
                    if (ObjectUtils.isValid(item.getTrackerId())) {
                        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
                        if (tracker!=null) {
                            row.set("icon", tracker.getIcon());
                        }
                    }
                    row.set("parentId", key);
                    data.add(row);
                });
            });
        } else if ("SHOW_UNLINK".equals(showType)) {
            for (TrackerItemEntity mainTrackerItem : mainTrackerItems) {
                if (!linkedTrackerItems.containsKey(mainTrackerItem.getId())) {
                    JSONObject row = new JSONObject();
                    row.set("id", mainTrackerItem.getId());
                    row.set("name", mainTrackerItem.getName());
                    row.set("itemNo", prefix+mainTrackerItem.getItemNo());
                    row.set("icon", mainTracker.getIcon());
                    row.set("parentId", null);
                    data.add(row);
                }
            }
        } else if ("SHOW_PROBLEMS".equals(showType)) {

        }


        JSONObject result = new JSONObject();

        result.set("fields", new JSONArray());

        result.set("data", data);
        result.set("total", data.size());


        return JsonUtils.toJSONString(result);
    }

    private static String emptyData() {
        JSONObject result = new JSONObject();
        result.set("fields", new JSONArray());
        result.set("data", new JSONArray());
        result.set("total", 0);
        return JsonUtils.toJSONString(result);
    }
}
