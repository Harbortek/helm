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

package com.harbortek.helm.smartdoc.exporter.reqif.hanlder;

import com.harbortek.helm.smartdoc.constants.ReqIFDataTypes;
import com.harbortek.helm.smartdoc.exporter.reqif.mapping.ReqIFTrackerMapping;
import com.harbortek.helm.smartdoc.exporter.reqif.writer.ReqIFDocumentWriter;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.attributes.*;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.Datatype;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.DatatypeEnumeration;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.DatatypeEnumerationValue;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFConst;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFDocument;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFHeader;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.specification.*;
import com.harbortek.helm.smartdoc.importer.reqif.rules.EnumerationValueMapping;
import com.harbortek.helm.smartdoc.importer.reqif.rules.ReqIFFieldMapping;
import com.harbortek.helm.smartdoc.utils.ImageSaver;
import com.harbortek.helm.smartdoc.utils.ReqIFUtils;
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.tracker.constants.Associations;
import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.entity.block.DocBlockData;
import com.harbortek.helm.tracker.entity.block.HeaderBlockData;
import com.harbortek.helm.tracker.entity.block.TrackerItemBlockData;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkTypeVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.util.DateUtils;
import com.harbortek.helm.util.IDUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class ReqIFDocumentHandler {
    FileService fileService;

    String documentName;
    ReqIFDocument document;

    File directory;
    File reqifFile;

    Map<Long, TrackerVo> trackerMap = new HashMap<>();
    Map<Long, TrackerItemVo> trackerItemMap = new HashMap<>();
    List<TrackerLinkTypeVo> trackerLinkTypes = new ArrayList<>();

    List<TrackerLinkVo> trackerLinks = new ArrayList<>();

    ProjectVo project;

    public ReqIFDocumentHandler(File directory, File reqifFile, ProjectVo project, Map<Long, TrackerVo> trackerMap,
                                Map<Long, TrackerItemVo> trackerItemMap,
                                List<TrackerLinkTypeVo> linkTypes,
                                List<TrackerLinkVo> links) {
        this.directory = directory;
        this.reqifFile = reqifFile;
        this.project = project;
        this.trackerMap = trackerMap;
        this.trackerItemMap = trackerItemMap;
        this.trackerLinkTypes = linkTypes;
        this.trackerLinks = links;
    }

    public void createDocument(String documentName) {
        this.documentName = documentName;
        Resource tempFile = new ClassPathResource("/template/template.reqif");
        if (tempFile.exists()) {
            try (InputStream is = tempFile.getInputStream()) {
                this.document = new ReqIFDocument(is, "", "");
            } catch (IOException ex) {
            }
            ReqIFHeader header = document.getHeader();
            header.setId(ReqIFUtils.systemIdToUUID(IDUtils.getId()));
            header.setTitle(documentName);
            header.setCreationDate(DateUtils.now());
        }
    }

    @SneakyThrows
    public void loadDocument() {
        this.document = new ReqIFDocument(this.reqifFile.getAbsolutePath());
    }

    public void updateDocumentFromBlocks(List<DocBlock> blocks, List<ReqIFTrackerMapping> mappings) {
        updateHeader(blocks, mappings);
        updateDataTypes(blocks, mappings);
        updateSpecTypes(blocks, mappings);
        updateSpecObjects(blocks, mappings);
        updateSpecRelations(blocks, mappings);
        updateSpecifications(blocks, mappings);
        updateToolExtensions(blocks, mappings);
    }

    private void updateHeader(List<DocBlock> blocks, List<ReqIFTrackerMapping> mappings) {
        ReqIFHeader header = document.getHeader();
        header.setId(ReqIFUtils.systemIdToUUID(IDUtils.getId()));
        header.setCreationDate(DateUtils.now());
    }

    private void updateDataTypes(List<DocBlock> blocks, List<ReqIFTrackerMapping> mappings) {
        Map<String, Datatype> datatypeMap = this.document.getCoreContent().getDatatypes();
        for (ReqIFTrackerMapping mapping : mappings) {
            List<ReqIFFieldMapping> fieldMappings = mapping.getFieldMappings();
            for (ReqIFFieldMapping fieldMapping : fieldMappings) {
                String reqIFAttributeName = fieldMapping.getReqIFAttributeName();
                String type = fieldMapping.getType();
                if (ReqIFDataTypes.ENUMERATION.equals(type)) {
                    String typeName = "Enumeration for " + reqIFAttributeName;
                    if (!datatypeMap.containsKey(typeName)) {
                        DatatypeEnumeration datatype = new DatatypeEnumeration();
                        datatype.setId(ReqIFUtils.systemIdToUUID(IDUtils.getId()));
                        datatype.setName(typeName);
                        datatype.setType(ReqIFConst.ENUMERATION);
                        datatype.setLastChange((DateUtils.now()));
                        List<DatatypeEnumerationValue> enumValues = new ArrayList<>();

                        int index = 1;
                        for (EnumerationValueMapping valueMapping : fieldMapping.getEnumMapping()) {
                            DatatypeEnumerationValue value = new DatatypeEnumerationValue();
                            value.setId(ReqIFUtils.systemIdToUUID(IDUtils.getId()));
                            value.setName(valueMapping.getReqIFValue());
                            value.setKey(String.valueOf(index++));
                            value.setOtherContent(valueMapping.getReqIFValue());
                            value.setLastChange(DateUtils.now());
                            enumValues.add(value);
                            valueMapping.setReqIFValueId(value.getId());
                        }

                        datatype.setEnumValues(enumValues);
                        datatypeMap.put(datatype.getId(), datatype);
                    }
                }
            }
        }
    }

    private void updateSpecTypes(List<DocBlock> blocks, List<ReqIFTrackerMapping> mappings) {
        updateSpecObjectTypes(blocks, mappings);
        updateSpecRelationTypes();
    }

    private void updateSpecObjectTypes(List<DocBlock> blocks, List<ReqIFTrackerMapping> mappings) {
        Map<String, SpecType> specTypeMap =
                this.document.getCoreContent().getSpecTypes().values().stream()
                             .filter(specType -> ReqIFConst.SPEC_OBJECT_TYPE.equals(specType.getType())).collect(
                            Collectors.toMap(SpecType::getName, Function.identity()));

        for (ReqIFTrackerMapping mapping : mappings) {
            SpecObjectType specType = (SpecObjectType)specTypeMap.get(mapping.getSpecTypeName());
            if (specType == null) {
                specType = new SpecObjectType();
                specType.setId(ReqIFUtils.systemIdToUUID(IDUtils.getId()));
                specType.setName(mapping.getSpecTypeName());
                TrackerVo tracker = trackerMap.get(mapping.getTarget());
                if (tracker != null) {
                    specType.setLastChange(tracker.getLastModifiedDate());
                }
                specType.setType(ReqIFConst.SPEC_OBJECT_TYPE);

                List<AttributeDefinition> attributes = new ArrayList<>();
                List<ReqIFFieldMapping> fieldMappings = mapping.getFieldMappings();
                for (ReqIFFieldMapping fieldMapping : fieldMappings) {
                    AttributeDefinition attribute = null;
                    switch (fieldMapping.getType()) {

                        case ReqIFConst.BOOLEAN:
                            attribute =
                                    new AttributeDefinitionBoolean(
                                            ReqIFUtils.systemIdToUUID(fieldMapping.getTrackerFieldId()),
                                            fieldMapping.getReqIFAttributeName(),
                                            ReqIFUtils.findDataType(document.getCoreContent().getDatatypes().values(),
                                                                    fieldMapping));
                            break;
                        case ReqIFConst.INTEGER:
                            attribute =
                                    new AttributeDefinitionInteger(
                                            ReqIFUtils.systemIdToUUID(fieldMapping.getTrackerFieldId()),
                                            fieldMapping.getReqIFAttributeName(),
                                            ReqIFUtils.findDataType(document.getCoreContent().getDatatypes().values(),
                                                                    fieldMapping));
                            break;
                        case ReqIFConst.DATE:
                            attribute =
                                    new AttributeDefinitionDate(
                                            ReqIFUtils.systemIdToUUID(fieldMapping.getTrackerFieldId()),
                                            fieldMapping.getReqIFAttributeName(),
                                            ReqIFUtils.findDataType(document.getCoreContent().getDatatypes().values(),
                                                                    fieldMapping));
                            break;
                        case ReqIFConst.REAL:
                            attribute =
                                    new AttributeDefinitionReal(
                                            ReqIFUtils.systemIdToUUID(fieldMapping.getTrackerFieldId()),
                                            fieldMapping.getReqIFAttributeName(),
                                            ReqIFUtils.findDataType(document.getCoreContent().getDatatypes().values(),
                                                                    fieldMapping));
                            break;
                        case ReqIFConst.FLOAT:
                            attribute =
                                    new AttributeDefinitionFloat(
                                            ReqIFUtils.systemIdToUUID(fieldMapping.getTrackerFieldId()),
                                            fieldMapping.getReqIFAttributeName(),
                                            ReqIFUtils.findDataType(document.getCoreContent().getDatatypes().values(),
                                                                    fieldMapping));
                            break;
                        case ReqIFConst.XHTML:
                            attribute =
                                    new AttributeDefinitionXHTML(
                                            ReqIFUtils.systemIdToUUID(fieldMapping.getTrackerFieldId()),
                                            fieldMapping.getReqIFAttributeName(),
                                            ReqIFUtils.findDataType(document.getCoreContent().getDatatypes().values(),
                                                                    fieldMapping));
                            break;
                        case ReqIFConst.ENUMERATION:
                            attribute =
                                    new AttributeDefinitionEnumeration(
                                            ReqIFUtils.systemIdToUUID(fieldMapping.getTrackerFieldId()),
                                            fieldMapping.getReqIFAttributeName(),
                                            ReqIFUtils.findDataType(document.getCoreContent().getDatatypes().values(),
                                                                    fieldMapping));
                            break;
                        default:
                            attribute =
                                    new AttributeDefinitionString(
                                            ReqIFUtils.systemIdToUUID(fieldMapping.getTrackerFieldId()),
                                            fieldMapping.getReqIFAttributeName(),
                                            ReqIFUtils.findDataType(document.getCoreContent().getDatatypes().values(),
                                                                    fieldMapping));
                            break;
                    }
                    attributes.add(attribute);
                }
                Map<String, AttributeDefinition> attributeDefinitionMap =
                        attributes.stream().collect(Collectors.toMap(AttributeDefinition::getId, Function.identity()));

                specType.setAttributeDefinitions(attributeDefinitionMap);

                specTypeMap.put(specType.getName(), specType);
                document.getCoreContent().getSpecTypes().putIfAbsent(specType.getId(), specType);
            }
        }
        document.getCoreContent().getSpecTypes().remove("TEMPLATE");

    }

    private void updateSpecRelationTypes() {
        Map<String, SpecType> specTypeMap =
                this.document.getCoreContent().getSpecTypes().values().stream()
                             .filter(specType -> ReqIFConst.SPEC_RELATION_TYPE.equals(specType.getType())).collect(
                            Collectors.toMap(SpecType::getName, Function.identity()));
        for (TrackerLinkTypeVo linkType : trackerLinkTypes) {
            String objectId = ReqIFUtils.systemIdToUUID(linkType.getId());
            if (!specTypeMap.containsKey(objectId)) {
                SpecRelationType specType = new SpecRelationType();
                specType.setId(objectId);
                specType.setName(linkType.getName());
                specType.setType(ReqIFConst.SPEC_RELATION_TYPE);
                specTypeMap.put(specType.getName(), specType);
                document.getCoreContent().getSpecTypes().putIfAbsent(specType.getId(), specType);
            }
        }
    }

    private void updateSpecObjects(List<DocBlock> blocks, List<ReqIFTrackerMapping> mappings) {
        Map<String, SpecObject> specObjectMap = document.getCoreContent().getSpecObjects();
        for (DocBlock block : blocks) {
            DocBlockData blockData = block.getData();
            if (blockData instanceof HeaderBlockData || blockData instanceof TrackerItemBlockData) {
                Long trackerItemId = blockData.getRefId();
                String objectId = ReqIFUtils.systemIdToUUID(trackerItemId);
                TrackerItemVo trackerItem = trackerItemMap.get(trackerItemId);
                SpecObject specObject = specObjectMap.get(objectId);
                if (specObject == null) {
                    specObject = new SpecObject();
                    specObject.setId(objectId);
                    specObject.setType(ReqIFConst.SPEC_OBJECT_TYPE);
                    specObject.setLastChange(trackerItem.getLastModifiedDate());
                    Optional<ReqIFTrackerMapping> trackerMapping =
                            mappings.stream()
                                    .filter(mapping -> trackerItem.getTracker().getId().equals(mapping.getTarget()))
                                    .findFirst();
                    if (trackerMapping.isPresent()) {
                        ReqIFTrackerMapping mapping = trackerMapping.get();
                        SpecType specType = ReqIFUtils.findSpecType(document.getCoreContent().getSpecTypes().values(),
                                                                    mapping.getSpecTypeName());
                        specObject.setSpecType(specType);
                        Map<String, AttributeValue> attributeValues = convertTrackerItemFields(trackerItem, mapping);
                        specObject.setAttributeValues(attributeValues);
                        document.getCoreContent().getSpecObjects().putIfAbsent(specObject.getId(), specObject);
                    }
                }

            }
        }
    }

    private Map<String, AttributeValue> convertTrackerItemFields(TrackerItemVo trackerItem,
                                                                 ReqIFTrackerMapping mapping) {
        Map<String, AttributeValue> values = new HashMap<>();
        for (ReqIFFieldMapping fieldMapping : mapping.getFieldMappings()) {
            SpecType specType = ReqIFUtils.findSpecType(document.getCoreContent().getSpecTypes().values(),
                                                        mapping.getSpecTypeName());
            if (specType == null) {
                continue;
            }
            AttributeDefinition attributeDefinition =
                    specType.getAttributeDefinition(ReqIFUtils.systemIdToUUID(fieldMapping.getTrackerFieldId()));
            if (attributeDefinition == null) {
                continue;
            }
            String value = ReqIFUtils.getValue(trackerItem, fieldMapping);
            if (fieldMapping.getReqIFAttributeName().equals("ReqIF.ForeignID")) {
                value = project.getKeyName() + "-" + value;
            }

            AttributeValue attributeValue = null;
            switch (fieldMapping.getType()) {

                case ReqIFConst.BOOLEAN:
                    attributeValue = new AttributeValueBoolean(value, attributeDefinition);
                    break;
                case ReqIFConst.INTEGER:
                    attributeValue = new AttributeValueInteger(value, attributeDefinition);
                    break;
                case ReqIFConst.DATE:
                    attributeValue = new AttributeValueDate(value, attributeDefinition);
                    break;
                case ReqIFConst.REAL:
                    attributeValue = new AttributeValueReal(value, attributeDefinition);
                    break;
                case ReqIFConst.FLOAT:
                    attributeValue = new AttributeValueFloat(value, attributeDefinition);
                    break;
                case ReqIFConst.XHTML:
                    ImageSaver imageSaver = new ImageSaver() {
                        @Override
                        public void saveImage(String fileName, String data, boolean isBase64) {
                            if (isBase64) {
                                String targetFile = new File(directory, fileName).getAbsolutePath();
                                //将BASE解码后保存为图片
                                ReqIFUtils.convertBase64StrToImage(data, targetFile);
                            } else {
                                if (fileService != null) {
                                    InputStream is = null;
                                    try {
                                        is = fileService.download(data);
                                        File imageFile = new File(directory, fileName);
                                        IOUtils.copy(is, new FileOutputStream(imageFile));
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    } finally {
                                        IOUtils.closeQuietly(is);
                                    }

                                }
                            }
                        }
                    };
                    attributeValue =
                            new AttributeValueXHTML(ReqIFUtils.html2XHTML(value, imageSaver), attributeDefinition);
                    break;
                case ReqIFConst.ENUMERATION:
                    attributeValue = new AttributeValueEnumeration(value, attributeDefinition);
                    break;
                default:
                    attributeValue = new AttributeValueString(value, attributeDefinition);
                    break;
            }

            values.putIfAbsent(attributeValue.getName(), attributeValue);
        }
        return values;
    }

    private void updateSpecRelations(List<DocBlock> blocks, List<ReqIFTrackerMapping> mappings) {
        document.getCoreContent().getSpecRelations().clear();
        for (TrackerLinkVo link : trackerLinks) {
            if (link.getLinkType() != null && Associations.PARENT.getId().equals(link.getLinkType().getCode())) {
                continue;
            }
            SpecRelation relation = new SpecRelation();
            relation.setId(ReqIFUtils.systemIdToUUID(link.getId()));
            relation.setSource(
                    document.getCoreContent().getSpecObject(ReqIFUtils.systemIdToUUID(link.getSourceItem().getId())));
            relation.setTarget(
                    document.getCoreContent().getSpecObject(ReqIFUtils.systemIdToUUID(link.getTargetItem().getId())));
            document.getCoreContent().getSpecRelations().putIfAbsent(relation.getId(), relation);
        }
    }

    private void updateSpecifications(List<DocBlock> blocks, List<ReqIFTrackerMapping> mappings) {
        document.getCoreContent().getSpecifications().clear();
        Specification specification = new Specification();
        specification.setId((ReqIFUtils.systemIdToUUID(IDUtils.getId())));
        specification.setName(documentName);
        for (SpecType specType : document.getCoreContent().getSpecTypes().values()) {
            if (specType.getType().equals(ReqIFConst.SPECIFICATION_TYPE)) {
                specification.setSpecType(specType);
                break;
            }
        }

        document.getCoreContent().getSpecifications().putIfAbsent(specification.getId(), specification);
        Map<String, SpecObject> specObjectMap = document.getCoreContent().getSpecObjects();
        Map<String, SpecHierarchy> hierarchyMap = new LinkedHashMap<>();

        Map<Long, Long> childParentMap = new HashMap<>();
        for (TrackerLinkVo link : trackerLinks) {
            if (link.getLinkType() != null && Associations.PARENT.getId().equals(link.getLinkType().getCode())) {
                childParentMap.putIfAbsent(link.getSourceItem().getId(), link.getTargetItem().getId());
            }
        }

        for (DocBlock block : blocks) {
            DocBlockData blockData = block.getData();
            if (blockData instanceof HeaderBlockData || blockData instanceof TrackerItemBlockData) {
                Long trackerItemId = blockData.getRefId();
                String objectId = ReqIFUtils.systemIdToUUID(trackerItemId);
                SpecObject specObject = specObjectMap.get(objectId);
                SpecHierarchy childSpecHierarchy = new SpecHierarchy();
                childSpecHierarchy.setHierarchyLvl(0);
                childSpecHierarchy.setLastChange(DateUtils.now());
                childSpecHierarchy.setSpecObject(specObject);
                childSpecHierarchy.setSpecHierarchyID(ReqIFUtils.systemIdToUUID(IDUtils.getId()));
                childSpecHierarchy.setSection(0);
                hierarchyMap.putIfAbsent(objectId, childSpecHierarchy);
            }
        }

        for (DocBlock block : blocks) {
            DocBlockData blockData = block.getData();
            if (blockData instanceof HeaderBlockData || blockData instanceof TrackerItemBlockData) {
                Long trackerItemId = blockData.getRefId();
                Long parentTrackerItemId = childParentMap.get(trackerItemId);
                if (parentTrackerItemId != null) {
                    String objectId = ReqIFUtils.systemIdToUUID(trackerItemId);
                    String parentObjectId = ReqIFUtils.systemIdToUUID(parentTrackerItemId);
                    SpecHierarchy childSpecHierarchy = hierarchyMap.get(objectId);
                    SpecHierarchy parentSpecHierarchy = hierarchyMap.get(parentObjectId);
                    if (parentSpecHierarchy != null) {
                        //儿子的级别加1
                        childSpecHierarchy.setHierarchyLvl(childSpecHierarchy.getHierarchyLvl() + 1);
                        //儿子的所有后代级别加1
                        for (SpecHierarchy grandson : childSpecHierarchy.getAllChildren()) {
                            grandson.setHierarchyLvl(grandson.getHierarchyLvl() + 1);
                        }
                        //加入到父亲的儿子中
                        parentSpecHierarchy.getChildren().putIfAbsent(childSpecHierarchy.getSpecHierarchyID(),
                                                                      childSpecHierarchy);
                    }
                }
            }
        }

        for (SpecHierarchy specHierarchy : hierarchyMap.values()) {
            if (specHierarchy.getHierarchyLvl() == 0) {
                specification.getChildren().put(specHierarchy.getSpecHierarchyID(), specHierarchy);
            }
        }
    }

    private void updateToolExtensions(List<DocBlock> blocks, List<ReqIFTrackerMapping> mappings) {

    }


    public void saveDocument() {
        new ReqIFDocumentWriter(this.document).write(this.reqifFile);
    }


}
