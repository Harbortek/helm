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

package com.harbortek.helm.tracker.template.builder;

import com.fasterxml.jackson.databind.node.ValueNode;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.InternalTrackers;
import com.harbortek.helm.tracker.constants.ObjectTypes;
import com.harbortek.helm.tracker.constants.SystemFields;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.util.ResourceUtils;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.DateUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.Cleanup;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TrackerItemXmlReader {
    private final Logger logger = LoggerFactory.getLogger(TrackerItemXmlReader.class);
    EntityResolver entityResolver;

    public TrackerItemXmlReader(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public TrackerItemVo build(Resource parent) {
        Resource resource = ResourceUtils.getResource(parent,"work-item.xml");


        SAXReader saxReader = new SAXReader();
        try {
            @Cleanup InputStream is = resource.getInputStream();
            Document document = saxReader.read(is);
            Node root = document.selectSingleNode("work-item");
            TrackerItemVo trackerItemVo = readFromXml(root);
            trackerItemVo.setItemNo(parent.getFile().getName());
            return trackerItemVo;
        } catch (IOException | DocumentException e) {
            logger.error("无法加载项目模版文件,{}", ResourceUtils.getPath(resource));
        }
        return null;
    }

    private TrackerItemVo readFromXml(Node parent){
        TrackerItemVo item = new TrackerItemVo();
        List<Node> nodes = parent.selectNodes("field");
        for(Node node : nodes){
            String fieldName = node.valueOf("@name");
            if(TrackerItemEntity.Fields.values.equals(fieldName)){
                List<Node> valueNodes= node.selectNodes("value");
                valueNodes.forEach(valueNode -> {
                    item.getValues().put((long) valueNodes.indexOf(valueNode),valueNode.valueOf("@key")+"_"+valueNode.valueOf("@value"));
                });
            }else{
                String value = node.getStringValue();
                setFieldValue(item,fieldName,value);
            }
        }
        return item;
    }
    private void setFieldValue(TrackerItemVo item, String fieldName, String fieldValue){
        if (SystemFields.STATUS.equals(fieldName)){
            if(ObjectUtils.isNotEmpty(item.getTracker())&&ObjectUtils.isNotEmpty(item.getTracker().getReferTo())){
                for (TrackerStatus trackerStatus : item.getTracker().getReferTo().getTrackerStatuses()) {
                    if(trackerStatus.getName().equals(fieldValue)){
                        item.setStatus(trackerStatus);
                        item.setStatusId(trackerStatus.getId());
                        break;
                    }
                }
            }
//            item.setStatus(entityResolver.findByName(fieldValue, TrackerStatus.class));
        }else if (SystemFields.PRIORITY.equals(fieldName)){
            item.setPriority(entityResolver.findByName(fieldValue, ObjectTypes.TRACKER_PRIORITY, EnumItemVo.class));
        }else if (SystemFields.SEVERITY.equals(fieldName)){
            item.setPriority(entityResolver.findByName(fieldValue, ObjectTypes.TRACKER_SEVERITY, EnumItemVo.class));
        }else if (SystemFields.STATUS_TYPE.equals(fieldName)){
            item.setMeaning(entityResolver.findByName(fieldValue, ObjectTypes.TRACKER_STATUS_MEANING, EnumItemVo.class));
        }else if (SystemFields.DESCRIPTION.equals(fieldName)){
            item.setDescription(fieldValue);
        }else if (SystemFields.ITEM_NO.equals(fieldName)){
            item.setItemNo(fieldValue);
        }else if (SystemFields.NAME.equals(fieldName)){
            item.setName(fieldValue);
        }else if (SystemFields.ASSIGNED_DATE.equals(fieldName)){
            item.setAssignedDate(DateUtils.toDate(fieldValue));
        }else if (SystemFields.ASSIGNED_TO.equals(fieldName)){

        }else if (SystemFields.CREATE_BY.equals(fieldName)){

        }else if (SystemFields.CREATE_DATE.equals(fieldName)){
            item.setCreateDate(DateUtils.toDate(fieldValue));
        }else if (SystemFields.LAST_MODIFIED_BY.equals(fieldName)){

        }else if (SystemFields.LAST_MODIFIED_DATE.equals(fieldName)){
            item.setLastModifiedDate(DateUtils.toDate(fieldValue));
        }else if (SystemFields.OWNER.equals(fieldName)){
            item.setOwner(new IdNameReference<UserVo>(entityResolver.findByName(fieldValue,ObjectTypes.USER,
                                                                           UserVo.class)));
        }else if (SystemFields.PLAN_START_DATE.equals(fieldName)){
            item.setPlanStartDate(DateUtils.toDate(fieldValue));
        }else if (SystemFields.PLAN_END_DATE.equals(fieldName)){
            item.setPlanEndDate(DateUtils.toDate(fieldValue));
        }else if (SystemFields.CLOSE_DATE.equals(fieldName)){
            item.setCloseDate(DateUtils.toDate(fieldValue));
        }else if (SystemFields.REAL_START_DATE.equals(fieldName)){
            item.setRealStartDate(DateUtils.toDate(fieldValue));
        }else if (SystemFields.REAL_END_DATE.equals(fieldName)){
            item.setRealEndDate(DateUtils.toDate(fieldValue));
        }else if (SystemFields.ESTIMATE_WORKING_HOURS.equals(fieldName)){
            item.setEstimateWorkingHours(Double.valueOf(fieldValue));
        }else if (SystemFields.REGISTERED_WORKING_HOURS.equals(fieldName)){
            item.setRegisteredWorkingHours(Double.valueOf(fieldValue));
        }else if (SystemFields.REMAINING_WORKING_HOURS.equals(fieldName)){
            item.setRemainingWorkingHours(Double.valueOf(fieldValue));
        }else if (SystemFields.TRACKER.equals(fieldName)){
            if(InternalTrackers.HEADING.getId().toString().equals(fieldValue)){
                item.setTracker(new IdNameReference<>(InternalTrackers.HEADING));
            }else if(InternalTrackers.PARAGRAPH.getId().toString().equals(fieldValue)) {
                item.setTracker(new IdNameReference<>(InternalTrackers.PARAGRAPH));
            }else if(InternalTrackers.TITLE.getId().toString().equals(fieldValue)) {
                item.setTracker(new IdNameReference<>(InternalTrackers.TITLE));
            }else {
                TrackerVo tracker = entityResolver.findByName(fieldValue,ObjectTypes.TRACKER,  TrackerVo.class);
                item.setTracker(new IdNameReference<>(tracker));
            }
        }else {
            TrackerField trackerField = entityResolver.findByName(fieldValue,ObjectTypes.TRACKER_FIELD,
                                                                  TrackerField.class);
            item.setCustomerFieldValue(trackerField,fieldValue);
        }
    }
}
