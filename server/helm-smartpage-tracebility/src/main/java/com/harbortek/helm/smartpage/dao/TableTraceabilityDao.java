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

package com.harbortek.helm.smartpage.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.smartpage.vo.ItemVo;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class TableTraceabilityDao extends BaseJdbcDao {
    public List<ItemVo> findLinkedTrackerItems(List<ItemVo> trackerItems, Long linkTypeId, Integer linkDirection){

        Collection<Long> trackerItemIds = ObjectUtils.ids(trackerItems);
        Map<Long, ItemVo> mainTrackerItemMap = trackerItems.stream()
                .collect(Collectors.toMap(
                        ItemVo::getId,
                        item -> item,
                        (existing, replacement) -> existing // 处理重复key的情况
                ));
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        if (linkDirection == 0){
            criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.targetItemId).in(trackerItemIds));
        }else{
            criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.sourceItemId).in(trackerItemIds));
        }
        criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.linkTypeId).is(linkTypeId));
        Query query = Query.query(criteria);
        List<TrackerLinkEntity> linkEntities =  find(query, TrackerLinkEntity.class);
        if (linkDirection == 0){
            Collection<Long> linkedItemIds = ObjectUtils.ids(linkEntities, TrackerLinkEntity.Fields.sourceItemId);

            List<ItemVo> linkedTrackerItems = DataUtils.toVo(findByIds(linkedItemIds, TrackerItemEntity.class), ItemVo.class);
            Map<Long, ItemVo> linkedTrackerItemMap = linkedTrackerItems.stream().collect(Collectors.toMap(
                    ItemVo::getId,
                    item -> item,
                    (existing, replacement) -> existing
            ));

            for (TrackerLinkEntity firstLink : linkEntities) {
                ItemVo mainTrackerItem = mainTrackerItemMap.get(firstLink.getTargetItemId());
                if (mainTrackerItem != null) {
                    ItemVo linkedTrackerItem = linkedTrackerItemMap.get(firstLink.getSourceItemId());
                    if (linkedTrackerItem != null) {
                        mainTrackerItem.getChildren().add(linkedTrackerItem);
                    }
                }
            }
            return linkedTrackerItems;
        }else{
            Collection<Long> linkedItemIds = ObjectUtils.ids(linkEntities, TrackerLinkEntity.Fields.targetItemId);
            List<ItemVo> linkedTrackerItems = DataUtils.toVo(findByIds(linkedItemIds, TrackerItemEntity.class), ItemVo.class);
            Map<Long, ItemVo> linkedTrackerItemMap = linkedTrackerItems.stream().collect(Collectors.toMap(
                    ItemVo::getId,
                    item -> item,
                    (existing, replacement) -> existing
            ));

            for (TrackerLinkEntity firstLink : linkEntities) {
                ItemVo mainTrackerItem = mainTrackerItemMap.get(firstLink.getSourceItemId());
                if (mainTrackerItem != null) {
                    ItemVo linkedTrackerItem = linkedTrackerItemMap.get(firstLink.getTargetItemId());
                    if (linkedTrackerItem != null) {
                        mainTrackerItem.getChildren().add(linkedTrackerItem);
                    }
                }
            }
            return linkedTrackerItems;
        }
    }
}
