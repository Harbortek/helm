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

import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.smartdoc.dao.ReqIFImportJobDao;
import com.harbortek.helm.smartdoc.entity.ReqIFImportJobEntity;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.ReqIFDocumentProperties;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.attributes.*;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.DatatypeEnumeration;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.DatatypeEnumerationValue;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.*;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.specification.*;
import com.harbortek.helm.smartdoc.importer.reqif.rules.EnumerationValueMapping;
import com.harbortek.helm.smartdoc.importer.reqif.rules.ReqIFFieldMapping;
import com.harbortek.helm.smartdoc.importer.reqif.rules.ReqIFRule;
import com.harbortek.helm.smartdoc.service.ReqIFImportJobService;
import com.harbortek.helm.smartdoc.utils.ReqIFUtils;
import com.harbortek.helm.smartdoc.vo.ReqIFImportJobVo;
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.Associations;
import com.harbortek.helm.tracker.constants.BlockTypes;
import com.harbortek.helm.tracker.constants.InternalTrackers;
import com.harbortek.helm.tracker.constants.SystemFields;
import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.entity.block.DocBlockLink;
import com.harbortek.helm.tracker.entity.block.HeaderBlockData;
import com.harbortek.helm.tracker.entity.block.TrackerItemBlockData;
import com.harbortek.helm.tracker.service.*;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.plan.SprintVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReqIFImportServiceImpl implements ReqIFImportJobService {

    @Autowired
    private ReqIFImportJobDao reqIFImportJobDao;
    @Autowired
    private DocService docService;
    @Autowired
    FileService fileService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectPageService projectPageService;

    @Autowired
    EnumService enumService;

    @Autowired
    TrackerService trackerService;

    @Autowired
    TrackerItemService trackerItemService;

    @Autowired
    TrackerLinkTypeService trackerLinkTypeService;

    @Autowired
    UserService userService;

    private ReqIFFile getReqIFFile(ReqIFImportJobVo job) {
        ReqIFFile reqIFFile = null;
        try {
            File tempFile = File.createTempFile("req-", FilenameUtils.getExtension(job.getFilePath()));
            InputStream inputStream = fileService.download(job.getFilePath());
            IOUtils.copy(inputStream, new FileOutputStream(tempFile, false));
            IOUtils.closeQuietly(inputStream);


            if (job.getFilePath().endsWith(".reqifz")) {
                reqIFFile = new ReqIFz(tempFile.getAbsolutePath());

            } else if (job.getFilePath().endsWith("reqif")) {
                reqIFFile = new ReqIF(tempFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return reqIFFile;
    }

    @Override
    public ReqIFDocumentProperties analysis(ReqIFImportJobVo job) {
        ReqIFDocumentProperties docProp = new ReqIFDocumentProperties();
        ReqIFFile reqIFFile = getReqIFFile(job);
        if (reqIFFile != null) {
            for (ReqIFDocument document : reqIFFile.getReqIFDocuments().values()) {
                ReqIFCoreContent content = document.getCoreContent();

                docProp.getSpecTypes().addAll(new ArrayList<>(content.getSpecTypes().values()));
            }
        }
        return docProp;
    }

    @Override
    public ReqIFImportJobVo findExistedJob(Long projectId, Long pageId) {
        List<ReqIFImportJobEntity> jobs = reqIFImportJobDao.findJobs(projectId, pageId);
        if (jobs.isEmpty()) {
            return null;
        }
        ReqIFImportJobVo job = DataUtils.toVo(jobs.stream().findFirst().get(), ReqIFImportJobVo.class);
        ReqIFDocumentProperties docProp = analysis(job);
        List<ReqIFRule> rules = new ArrayList<>();
        for (SpecType specType : docProp.getSpecTypes()) {
            if (ReqIFConst.SPEC_OBJECT_TYPE.equals(specType.getType())) {
                ReqIFRule rule = new ReqIFRule();
                rule.setSpecTypeId(specType.getId());
                rule.setSpecTypeName(specType.getName());
                for (AttributeDefinition attributeDefinition : specType.getAttributeDefinitions().values()) {
                    ReqIFFieldMapping fieldMapping = new ReqIFFieldMapping();
                    fieldMapping.setReqIFAttributeId(attributeDefinition.getId());
                    fieldMapping.setReqIFAttributeName(attributeDefinition.getName());
                    fieldMapping.setType(attributeDefinition.getDataType().getType());
                    if (attributeDefinition.getDataType() instanceof DatatypeEnumeration) {
                        DatatypeEnumeration dataType = (DatatypeEnumeration) attributeDefinition.getDataType();
                        List<DatatypeEnumerationValue> values = dataType.getEnumValues();
                        fieldMapping.setEnumMapping(values.stream().map(value -> {
                            return EnumerationValueMapping.builder().reqIFValueId(value.getId())
                                                          .reqIFValueKey(value.getKey())
                                                          .reqIFValue(value.getName() != null ? value.getName() :
                                                                              value.getOtherContent())
                                                          .build();
                        }).collect(Collectors.toList()));
                    }
                    rule.getFieldMappings().add(fieldMapping);
                }
                rule.getFieldMappings().sort(Comparator.comparing(ReqIFFieldMapping::getReqIFAttributeName));
                rules.add(rule);
            }

        }
        job.setRules(rules);
        job.setDocProps(docProp);

        return job;
    }

    @Override
    public ReqIFImportJobVo createJob(ReqIFImportJobVo job) {
        reqIFImportJobDao.deleteJobs(job.getProjectId(), job.getPageId());

        ReqIFImportJobEntity jobEntity = DataUtils.toEntity(job, ReqIFImportJobEntity.class);
        jobEntity.setBlocksJSON(JsonUtils.toJSONString(new ArrayList<>()));
        jobEntity.setId(IDUtils.getId());
        reqIFImportJobDao.createJob(jobEntity);

        job = DataUtils.toVo(jobEntity, ReqIFImportJobVo.class);

        return job;
    }

    @Override
    public ReqIFImportJobVo updateJob(ReqIFImportJobVo job) {

        Long projectId = job.getProjectId();
        ProjectVo project = projectService.findOneProject(projectId);

        ReqIFFile reqIFFile = getReqIFFile(job);

        List<DocBlock> newBlocks = new ArrayList<>();

        List<ReqIFRule> rules = job.getRules();
        Map<String, ReqIFRule> ruleMap =
                rules.stream().collect(Collectors.toMap(ReqIFRule::getSpecTypeId, Function.identity()));
        Map<String, String> specRelations = new HashMap<>();

        for (ReqIFDocument document : reqIFFile.getReqIFDocuments().values()) {
            ReqIFCoreContent content = document.getCoreContent();
            for (Specification specification : content.getSpecificationsList()) {
                List<SpecHierarchy> specHierarchies = specification.getLvlOneSpecHierarchies();
                for (SpecHierarchy specHierarchy : specHierarchies) {
                    newBlocks.addAll(processHierarchy(reqIFFile, document, null, specHierarchy, ruleMap, project,
                                                      specRelations));
                }
            }
        }

        job.setBlocksJSON(JsonUtils.toJSONString(newBlocks));
        ReqIFImportJobEntity jobEntity = DataUtils.toEntity(job, ReqIFImportJobEntity.class);
        reqIFImportJobDao.updateJob(jobEntity);

        job = DataUtils.toVo(jobEntity, ReqIFImportJobVo.class);
        return job;
    }

    private List<DocBlock> processHierarchy(ReqIFFile reqIFFile, ReqIFDocument reqIFDocument, SpecObject parent,
                                            SpecHierarchy specHierarchy,
                                            Map<String, ReqIFRule> ruleMap,
                                            ProjectVo project, Map<String, String> specRelations) {
        List<DocBlock> newBlocks = new ArrayList<>();
        SpecObject specObject = specHierarchy.getSpecObject();
        int level = specHierarchy.getHierarchyLvl();


        ReqIFRule rule = ruleMap.get(specObject.getSpecType().getId());
        if (rule != null) {
            if (InternalTrackers.HEADING.getId().equals(rule.getTarget())) {
                DocBlock block = createHeadingBlock(reqIFFile, parent, specObject, rule, level);
                if (parent != null) {
                    specRelations.put(parent.getId(), specObject.getId());
                }
                newBlocks.add(block);
            } else if (ObjectUtils.isValid(rule.getTarget())) {
                DocBlock block = createTrackerBlock(reqIFFile, reqIFDocument, parent, specObject, rule, project);
                if (parent != null) {
                    specRelations.put(parent.getId(), specObject.getId());
                }
                newBlocks.add(block);
            }


            for (SpecHierarchy child : specHierarchy.getDirectChildren()) {
                newBlocks.addAll(
                        processHierarchy(reqIFFile, reqIFDocument, specObject, child, ruleMap, project, specRelations));
            }
        }
        return newBlocks;
    }


    private DocBlock createTrackerBlock(ReqIFFile reqIFFile, ReqIFDocument reqIFDocument, SpecObject parent,
                                        SpecObject specObject, ReqIFRule rule,
                                        ProjectVo project) {
        TrackerItemVo trackerItem = createWorkItem(reqIFFile, reqIFDocument, parent, specObject, rule, project);
        TrackerItemBlockData itemBlockData = new TrackerItemBlockData();
        itemBlockData.setTrackerItem(docService.fillTrackerItemVo(trackerItem));
        itemBlockData.setName(trackerItem.getName());
        itemBlockData.setText(trackerItem.getDescription());
        return new DocBlock(specObject.getId(), String.valueOf(trackerItem.getTracker().getId()),
                            itemBlockData);
    }

    private TrackerItemVo createWorkItem(ReqIFFile reqIFFile, ReqIFDocument reqIFDocument, SpecObject parent,
                                         SpecObject specObject, ReqIFRule rule, ProjectVo project
                                        ) {
        TrackerVo tracker = trackerService.findOneTracker(rule.getTarget());
        TrackerItemVo trackerItem = new TrackerItemVo();
        trackerItem.setProject(new IdNameReference<>(project));
        trackerItem.setTracker(new IdNameReference<>(tracker));
        trackerItem.setItemNo("000");


        List<TrackerField> trackerFields = tracker.getTrackerFields();
        Map<Long, TrackerField> trackerFieldMap =
                trackerFields.stream().collect(Collectors.toMap(TrackerField::getId, Function.identity()));

        for (ReqIFFieldMapping fieldRule : rule.getFieldMappings()) {
            TrackerField trackerField = trackerFieldMap.get(fieldRule.getTrackerFieldId());
            if (trackerField == null) {
                continue;
            }
            String attrName = fieldRule.getReqIFAttributeName();
            AttributeValue attrValue = specObject.getAttributes().get(attrName);

            if (attrValue instanceof AttributeValueXHTML) {
                String html = ReqIFUtils.xhtml2HTML(reqIFFile, reqIFDocument, (String) attrValue.getValue());
                setFieldValue(trackerItem, trackerField, html, rule);
            } else if (attrValue instanceof AttributeValueString) {
                setFieldValue(trackerItem, trackerField, (String) attrValue.getValue(), rule);
            } else if (attrValue instanceof AttributeValueBoolean) {
                setFieldValue(trackerItem, trackerField, (boolean) attrValue.getValue() ? "true" : "false", rule);
            } else if (attrValue instanceof AttributeValueEnumeration) {
                AttributeValueEnumeration value = (AttributeValueEnumeration) attrValue;
                List<EnumerationValueMapping> enumMappings = fieldRule.getEnumMapping();
                for (EnumerationValueMapping enumMapping : enumMappings) {
                    if (value.getValue() != null && value.getValue().equals(enumMapping.getReqIFValue()) &&
                            ObjectUtils.isValid(enumMapping.getTrackerEnumValueId())) {
                        setFieldValue(trackerItem, trackerField, String.valueOf(enumMapping.getTrackerEnumValueId()),
                                      rule);
                        break;
                    }
                }
            } else if (attrValue instanceof AttributeValueInteger) {
                setFieldValue(trackerItem, trackerField, String.valueOf(attrValue.getValue()), rule);
            } else if (attrValue instanceof AttributeValueReal) {
                setFieldValue(trackerItem, trackerField, (String) attrValue.getValue(), rule);
            } else if (attrValue instanceof AttributeValueDate) {
                LocalDateTime localDateTime = (LocalDateTime) attrValue.getValue();
                Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                setFieldValue(trackerItem, trackerField, DateUtils.toDefDatetimeString(date), rule);
            }
        }

        //TODO 对于工作项的未导入的属性设置默认值
        if (trackerItem.getOwner() == null) {
            trackerItem.setOwner(trackerItem.getCreateBy());
        }

        return trackerItem;
    }

    private DocBlock createHeadingBlock(ReqIFFile reqIFFile, SpecObject parent, SpecObject specObject, ReqIFRule rule,
                                        int level) {
        HeaderBlockData data = new HeaderBlockData();
        List<TrackerField> trackerFields = InternalTrackers.HEADING.getTrackerFields();
        Map<Long, TrackerField> trackerFieldMap =
                trackerFields.stream().collect(Collectors.toMap(TrackerField::getId, Function.identity()));
        data.setLevel(Long.valueOf(String.valueOf(level)));
        for (ReqIFFieldMapping fieldRule : rule.getFieldMappings()) {
            TrackerField trackerField = trackerFieldMap.get(fieldRule.getTrackerFieldId());
            if (trackerField == null) {
                continue;
            }
            String attrName = fieldRule.getReqIFAttributeName();
            AttributeValue attrValue = specObject.getAttributes().get(attrName);
            if (trackerField.getSystemProperty().equals(SystemFields.NAME)) {
                if (attrValue instanceof AttributeValueXHTML) {
                    String text = ReqIFUtils.plainText((AttributeValueXHTML) attrValue);
                    data.setText(text);
                } else if (attrValue instanceof AttributeValueString) {
                    data.setText(String.valueOf(attrValue.getValue()));
                }
            } else if (trackerField.getSystemProperty().equals(SystemFields.DESCRIPTION)) {
                if (attrValue instanceof AttributeValueXHTML) {
                } else if (attrValue instanceof AttributeValueString) {
                }
            } else if (trackerField.getSystemProperty().equals(SystemFields.CREATE_BY)) {
                if (attrValue instanceof AttributeValueString) {
                    String userName = String.valueOf(attrValue.getValue());
                    if (StringUtils.isNotEmpty(userName)) {
                    }
                }
            } else if (trackerField.getSystemProperty().equals(SystemFields.CREATE_DATE)) {

            } else if (trackerField.getSystemProperty().equals(SystemFields.ITEM_NO)) {

            }
        }
        DocBlock block = new DocBlock(specObject.getId(), BlockTypes.HEADING, data);
        return block;
    }

    @Override
    public void completeJob(ReqIFImportJobVo job) {
        Long projectId = job.getProjectId();
        Long pageId = job.getPageId();
        ProjectVo project = projectService.findOneProject(projectId);

        ReqIFFile reqIFFile = getReqIFFile(job);
        List<DocBlock> blocks = new ArrayList<>();

        List<ReqIFRule> rules = job.getRules();
        Map<String, ReqIFRule> ruleMap =
                rules.stream().collect(Collectors.toMap(ReqIFRule::getSpecTypeId, Function.identity()));
        Map<String, String> specParentChildRelations = new HashMap<>();

        List<SpecRelation> specRelations = new ArrayList<>();

        for (ReqIFDocument document : reqIFFile.getReqIFDocuments().values()) {
            ReqIFCoreContent content = document.getCoreContent();
            for (Specification specification : content.getSpecificationsList()) {
                List<SpecHierarchy> specHierarchies = specification.getLvlOneSpecHierarchies();
                for (SpecHierarchy specHierarchy : specHierarchies) {
                    blocks.addAll(processHierarchy(reqIFFile, document, null, specHierarchy, ruleMap, project,
                                                   specParentChildRelations));
                }
            }
            specRelations.addAll(content.getSpecRelations().values());
        }


        Map<String, DocBlock> blockMap =
                blocks.stream().collect(Collectors.toMap(DocBlock::getId, Function.identity()));

        //构建工作项之间的关系
        List<DocBlockLink> links = new ArrayList<>();
        specParentChildRelations.forEach(
                (key, value) -> links.add(DocBlockLink.builder().sourceBlockId(value).targetBlockId(key).linkCode(
                        Associations.PARENT.getId()).build()));
        specRelations.forEach(specRelation -> {
            String code = ReqIFUtils.mapRelationNameToCode(reqIFFile, specRelation.getType().getName());
            links.add(DocBlockLink.builder().sourceBlockId(specRelation.getSource().getId())
                                  .targetBlockId(specRelation.getTarget().getId()).linkCode(
                            code).build());
        });


        docService.saveBlocksAndTrackerItems(projectId, pageId, blocks, links);

        reqIFImportJobDao.deleteJob(job.getId());
    }

    @Override
    public void deleteJob(Long id) {
        reqIFImportJobDao.deleteJob(id);
    }

    @Override
    public ReqIFImportJobVo withdrawJob(Long id) {
        ReqIFImportJobVo job = null;
        reqIFImportJobDao.withdraw(id);

        job = DataUtils.toVo(reqIFImportJobDao.findOneJob(id), ReqIFImportJobVo.class);
        job.setDocProps(analysis(job));

        return job;
    }

    private void setFieldValue(TrackerItemVo trackerItem, TrackerField trackerField, String value, ReqIFRule rule) {
        log.debug("setFieldValue: {} = {}", trackerField.getSystemProperty(), value);
        String fieldName = trackerField.getSystemProperty();
        if (SystemFields.NAME.equals(fieldName)) {
            trackerItem.setName(value);
        } else if (SystemFields.ITEM_NO.equals(fieldName)) {
//            if (StringUtils.isNotEmpty(rule.getItemNoPrefix())) {
//                trackerItem.setItemNo(StringUtils.removeStart(value, rule.getItemNoPrefix()));
//            } else {
//                trackerItem.setItemNo(value);
//            }
        } else if (SystemFields.DESCRIPTION.equals(fieldName)) {
            trackerItem.setDescription(value);
        } else if (SystemFields.OWNER.equals(fieldName)) {
            UserVo user = userService.findOneUserByLoginName(value);
            if (user != null) {
                trackerItem.setOwner(new IdNameReference<>(user));
            }
        } else if (SystemFields.SPRINT.equals(fieldName)) {
            trackerItem.setSprint(new IdNameReference<>(SprintVo.builder().id(Long.valueOf(value)).build()));
        } else if (SystemFields.CREATE_BY.equals(fieldName)) {
            UserVo user = userService.findOneUserByLoginName(value);
            if (user != null) {
                trackerItem.setCreateBy(new IdNameReference<>(user));
            }
        } else if (SystemFields.CREATE_DATE.equals(fieldName)) {
            trackerItem.setCreateDate(DateUtils.strToDate(value));
        } else if (SystemFields.LAST_MODIFIED_BY.equals(fieldName)) {
            UserVo user = userService.findOneUserByLoginName(value);
            if (user != null) {
                trackerItem.setLastModifiedBy(
                        new IdNameReference<>(user));
            }
        } else if (SystemFields.LAST_MODIFIED_DATE.equals(fieldName)) {
            trackerItem.setLastModifiedDate(DateUtils.strToDate(value));
        } else if (SystemFields.STATUS.equals(fieldName)) {
            trackerItem.setStatusId(Long.valueOf(value));
            for (TrackerStatus status : trackerItem.getTracker().getReferTo().getTrackerStatuses()) {
                if (status.getId().equals(trackerItem.getStatusId())) {
                    trackerItem.setStatus(status);
                    break;
                }
            }
        } else if (SystemFields.ASSIGNED_TO.equals(fieldName)) {
            UserVo user = userService.findOneUserByLoginName(value);
            if (user != null) {
                trackerItem.setAssignedTo(new IdNameReference<>(user));
            }
        } else if (SystemFields.ASSIGNED_DATE.equals(fieldName)) {
            trackerItem.setAssignedDate(DateUtils.strToDate(value));
        } else if (SystemFields.PRIORITY.equals(fieldName)) {
            EnumItemVo enumItemVo = enumService.findOneEnumItemById(Long.valueOf(value));
            trackerItem.setPriority(enumItemVo);
        } else if (SystemFields.SEVERITY.equals(fieldName)) {
            EnumItemVo enumItemVo = enumService.findOneEnumItemById(Long.valueOf(value));
            trackerItem.setSeverity(enumItemVo);
        } else if (SystemFields.PLAN_START_DATE.equals(fieldName)) {
            trackerItem.setPlanStartDate(DateUtils.strToDate(value));
        } else if (SystemFields.PLAN_END_DATE.equals(fieldName)) {
            trackerItem.setPlanEndDate(DateUtils.strToDate(value));
        } else if (SystemFields.REAL_START_DATE.equals(fieldName)) {
            trackerItem.setRealStartDate(DateUtils.strToDate(value));
        } else if (SystemFields.REAL_END_DATE.equals(fieldName)) {
            trackerItem.setRealEndDate(DateUtils.strToDate(value));
        } else if (SystemFields.PROGRESS.equals(fieldName)) {
            trackerItem.setProgress(Integer.valueOf(value));
        } else if (SystemFields.CLOSE_DATE.equals(fieldName)) {
            trackerItem.setCloseDate(DateUtils.strToDate(value));
        } else if (SystemFields.ESTIMATE_WORKING_HOURS.equals(fieldName)) {
            trackerItem.setEstimateWorkingHours(Double.valueOf(value));
        } else if (SystemFields.REGISTERED_WORKING_HOURS.equals(fieldName)) {
            trackerItem.setRegisteredWorkingHours(Double.valueOf(value));
        } else if (SystemFields.REMAINING_WORKING_HOURS.equals(fieldName)) {
            trackerItem.setRemainingWorkingHours(Double.valueOf(value));
//        } else if (SystemFields.TEST_CASE_TYPE.equals(fieldName)) {
//            EnumItemVo enumItemVo = enumService.findOneEnumItemById(Long.valueOf(value));
//            trackerItem.setTestCaseType(enumItemVo);
        } else {
            List<TrackerField> fields = trackerItem.getTracker().getReferTo().getTrackerFields();
            for (TrackerField customerField : fields) {
                if (!customerField.isSystem() && customerField.getName().equals(fieldName)) {
                    trackerItem.setCustomerFieldValue(customerField, value);
                    break;
                }
            }
        }
    }
}
