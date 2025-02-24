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

package com.harbortek.helm.smartdoc.exporter.reqif.writer;

import com.harbortek.helm.smartdoc.importer.reqif.analysis.attributes.AttributeDefinition;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.attributes.AttributeValue;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.attributes.AttributeValueXHTML;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.*;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFConst;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFDocument;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.specification.*;
import com.harbortek.helm.tracker.util.ResourceUtils;
import com.harbortek.helm.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.util.*;

@Slf4j
public class ReqIFDocumentWriter {
    ReqIFDocument reqIFDocument;

    public ReqIFDocumentWriter(ReqIFDocument reqIFDocument) {
        this.reqIFDocument = reqIFDocument;
    }

    public void write(File outputFile) {
        Document document = DocumentHelper.createDocument();
        Element rootNode = document.addElement(ReqIFConst.REQ_IF);
        writeHeader(rootNode);
        writeCoreContent(rootNode);
        writeToolExtensions(rootNode);
        ResourceUtils.writeXml(outputFile, document);
    }


    private void writeHeader(Element rootNode) {
        Element header =
                rootNode.addElement("THE-HEADER").addElement(ReqIFConst.REQ_IF_HEADER)
                        .addAttribute(ReqIFConst.IDENTIFIER, reqIFDocument.getHeader().getId());
        header.addElement(ReqIFConst.REQ_IF_TOOL_ID).addText(reqIFDocument.getHeader().getToolID());
        header.addElement(ReqIFConst.REQ_IF_VERSION).addText(reqIFDocument.getHeader().getReqifVersion());
        header.addElement(ReqIFConst.SOURCE_TOOL_ID).addText(reqIFDocument.getHeader().getSourceToolID());
        header.addElement(ReqIFConst.TITLE).addText(reqIFDocument.getHeader().getTitle());
        header.addElement(ReqIFConst.CREATION_TIME)
              .addText(DateUtils.toISODate(reqIFDocument.getHeader().getCreationDate()));
    }

    private void writeCoreContent(Element rootNode) {
        Element coreContent = rootNode.addElement(ReqIFConst.CORE_CONTENT);
        Element content = coreContent.addElement(ReqIFConst.REQ_IF_CONTENT);

        writeDataTypes(content);
        writeSpecTypes(content);
        writeSpecObjects(content);
        writeSpecRelations(content);
        writeSpecifications(content);
    }

    private void writeDataTypes(Element content) {
        Element dataTypesNode = content.addElement(ReqIFConst.DATATYPES);
        for (Datatype dataType : reqIFDocument.getCoreContent().getDatatypes().values()) {

            Element dataTypeNode = dataTypesNode.addElement("DATATYPE-DEFINITION-" + dataType.getType());
            dataTypeNode.addAttribute(ReqIFConst.IDENTIFIER, dataType.getId());
            dataTypeNode.addAttribute(ReqIFConst.LONG_NAME, dataType.getName());
            dataTypeNode.addAttribute(ReqIFConst.LAST_CHANGE, DateUtils.toISODate(dataType.getLastChange()));

            if (dataType instanceof DatatypeString){
                dataTypeNode.addAttribute(ReqIFConst.MAX_LENGTH,
                                          String.valueOf(((DatatypeString)dataType).getMaxLength()));
            }else if (dataType instanceof DatatypeInteger){
                dataTypeNode.addAttribute(ReqIFConst.MIN,
                                          String.valueOf(((DatatypeInteger)dataType).getMin()));
                dataTypeNode.addAttribute(ReqIFConst.MAX,
                                          String.valueOf(((DatatypeInteger)dataType).getMax()));
            }else if (dataType instanceof DatatypeReal){
                dataTypeNode.addAttribute(ReqIFConst.MIN,
                                          String.valueOf(((DatatypeReal)dataType).getMin()));
                dataTypeNode.addAttribute(ReqIFConst.MAX,
                                          String.valueOf(((DatatypeReal)dataType).getMax()));
                dataTypeNode.addAttribute(ReqIFConst.ACCURACY,
                                          String.valueOf(((DatatypeReal)dataType).getAccuracy()));
            }else  if (dataType instanceof DatatypeEnumeration) {
                Element valuesNode = dataTypeNode.addElement("SPECIFIED-VALUES");
                DatatypeEnumeration enumeration = (DatatypeEnumeration) dataType;
                for (DatatypeEnumerationValue value : enumeration.getEnumValues()) {
                    Element valueNode = valuesNode.addElement("ENUM-VALUE");
                    valueNode.addAttribute(ReqIFConst.IDENTIFIER, value.getId());
                    valueNode.addAttribute(ReqIFConst.LONG_NAME, value.getName());
                    valueNode.addAttribute(ReqIFConst.LAST_CHANGE, DateUtils.toISODate(value.getLastChange()));
                    Element embeddedValueNode = valueNode.addElement("PROPERTIES").addElement("EMBEDDED-VALUE");
                    embeddedValueNode.addAttribute(ReqIFConst.KEY, value.getKey());
                    embeddedValueNode.addAttribute(ReqIFConst.OTHER_CONTENT, value.getOtherContent());
                }
            }
        }

    }

    private void writeSpecTypes(Element content) {
        Element specTypesNode = content.addElement(ReqIFConst.SPEC_TYPES);
        List<SpecType> specTypeList = new ArrayList<>(reqIFDocument.getCoreContent().getSpecTypes().values());
        specTypeList.sort(new Comparator<SpecType>() {
            @Override
            public int compare(SpecType o1, SpecType o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });

        for (SpecType specType : specTypeList) {
            if (specType instanceof SpecificationType) {
                specTypesNode.addElement(ReqIFConst.SPECIFICATION_TYPE).addAttribute(ReqIFConst.IDENTIFIER,
                                                                                     specType.getId())
                             .addAttribute(ReqIFConst.LONG_NAME,
                                           specType.getName());
            } else if (specType instanceof SpecObjectType) {
                SpecObjectType specObjectType = (SpecObjectType) specType;
                Element specObjectTypeNode = specTypesNode.addElement(ReqIFConst.SPEC_OBJECT_TYPE);
                specObjectTypeNode.addAttribute(ReqIFConst.IDENTIFIER, specObjectType.getId());
                specObjectTypeNode.addAttribute(ReqIFConst.LONG_NAME, specObjectType.getName());
                specObjectTypeNode.addAttribute(ReqIFConst.LAST_CHANGE,
                                                DateUtils.toISODate(specObjectType.getLastChange()));

                Element attributesNode = specObjectTypeNode.addElement("SPEC-ATTRIBUTES");
                for (AttributeDefinition attribute : specObjectType.getAttributeDefinitions().values()) {
                    Element attributeNode =
                            attributesNode.addElement("ATTRIBUTE-DEFINITION-" + attribute.getDataType().getType());
                    attributeNode.addAttribute(ReqIFConst.IDENTIFIER, attribute.getId());
                    attributeNode.addAttribute(ReqIFConst.LONG_NAME, attribute.getName());
                    attributeNode.addAttribute(ReqIFConst.LAST_CHANGE, DateUtils.toISODate(attribute.getLastChange()));
                    attributeNode.addAttribute(ReqIFConst.IS_EDITABLE, "true");

                    attributeNode.addElement("TYPE")
                                 .addElement("DATATYPE-DEFINITION-" + attribute.getDataType().getType() + "-REF")
                                 .addText(attribute.getDataType().getId());
                }
            } else if (specType instanceof SpecRelationType) {
                SpecRelationType specRelationType = (SpecRelationType) specType;
                specTypesNode.addElement(ReqIFConst.SPEC_RELATION_TYPE).addAttribute(ReqIFConst.IDENTIFIER,
                                                                                     specType.getId())
                             .addAttribute(ReqIFConst.LONG_NAME,
                                           specType.getName()).addAttribute(ReqIFConst.LAST_CHANGE,
                                                                            DateUtils.toISODate(
                                                                                    specRelationType.getLastChange()));
            }
        }
    }

    private void writeSpecObjects(Element rootNode) {
        Element specObjectsNode = rootNode.addElement(ReqIFConst.SPEC_OBJECTS);
        for (SpecObject specObject : reqIFDocument.getCoreContent().getSpecObjects().values()) {

            Element specObjectNode = specObjectsNode.addElement(ReqIFConst.SPEC_OBJECT);
            specObjectNode.addAttribute(ReqIFConst.IDENTIFIER, specObject.getId());
            specObjectNode.addAttribute(ReqIFConst.LAST_CHANGE, DateUtils.toISODate(specObject.getLastChange()));

            Element valuesNode = specObjectNode.addElement("VALUES");
            for (AttributeValue attributeValue : specObject.getAttributes().values()) {
                Element valueNode =
                        valuesNode.addElement(
                                "ATTRIBUTE-VALUE-" + attributeValue.getAttributeDefinition().getDataType().getType());
                if (attributeValue.getAttributeDefinition().getDataType() instanceof DatatypeEnumeration) {
                    valueNode.addElement("VALUES").addElement("ENUM-VALUE-REF").addText(attributeValue.getValue().toString());
                } else if (attributeValue.getAttributeDefinition().getDataType() instanceof DatatypeXHTML) {
                    String xhtml =
                            (String) ((AttributeValueXHTML) attributeValue).getValue();
                    valueNode.addElement(ReqIFConst.THE_VALUE).addText(xhtml);
                } else if (attributeValue.getAttributeDefinition().getDataType() instanceof DatatypeDate) {
                    String isoDate = DateUtils.toISODate((Date) attributeValue.getValue());
                    valueNode.addAttribute(ReqIFConst.THE_VALUE, isoDate);
                } else {
                    valueNode.addAttribute(ReqIFConst.THE_VALUE,
                                           StringUtils.trim(String.valueOf(attributeValue.getValue())));
                }

                valueNode.addElement("DEFINITION").addElement(
                        "ATTRIBUTE-DEFINITION-" + attributeValue.getAttributeDefinition().getDataType().getType() +
                                "-REF").addText(attributeValue.getAttributeDefinition().getId());
            }

            Element typeNode = specObjectNode.addElement(ReqIFConst.TYPE);
            typeNode.addElement("SPEC-OBJECT-TYPE-REF").addText(specObject.getSpecType().getId());
        }
    }

    private void writeSpecRelations(Element content) {
        Element specRelationsNode = content.addElement(ReqIFConst.SPEC_RELATIONS);
        for (SpecRelation specRelation : reqIFDocument.getCoreContent().getSpecRelations().values()) {
            Element specRelationNode = specRelationsNode.addElement(ReqIFConst.SPEC_RELATION);
            specRelationNode.addAttribute(ReqIFConst.IDENTIFIER, specRelation.getId());
            specRelationNode.addAttribute(ReqIFConst.LAST_CHANGE,DateUtils.toISODate(specRelation.getLastChange()));

            specRelationNode.addElement(ReqIFConst.SOURCE).addElement("SPEC-OBJECT-REF")
                            .addText(specRelation.getSource().getId());
            specRelationNode.addElement(ReqIFConst.TARGET).addElement("SPEC-OBJECT-REF")
                            .addText(specRelation.getTarget().getId());
            specRelationNode.addElement(ReqIFConst.TYPE).addElement("SPEC-RELATION-TYPE-REF")
                            .addText(specRelation.getType().getType());
        }
    }

    private void writeSpecifications(Element content) {
        Element specificationsNode = content.addElement(ReqIFConst.SPECIFICATIONS);
        for (Specification specification : reqIFDocument.getCoreContent().getSpecifications().values()) {

            Element specificationNode =
                    specificationsNode.addElement(ReqIFConst.SPECIFICATION).addAttribute(ReqIFConst.IDENTIFIER,
                                                                                         specification.getId());
            specificationNode.addAttribute(ReqIFConst.LAST_CHANGE,DateUtils.toISODate(specification.getLastChange()));

            specificationNode.addElement(ReqIFConst.TYPE).addElement("SPECIFICATION-TYPE-REF")
                             .addText(specification.getSpecType().getId());
            Element childrenNode = specificationNode.addElement(ReqIFConst.CHILDREN);

            for (SpecHierarchy specHierarchy : specification.getLvlOneSpecHierarchies()) {
                writeSpecHierarchy(childrenNode, specHierarchy);
            }
        }
    }

    private void writeSpecHierarchy(Element childrenNode, SpecHierarchy specHierarchy) {
        Element specHierarchyNode = childrenNode.addElement(ReqIFConst.SPEC_HIERARCHY);
        specHierarchyNode.addAttribute(ReqIFConst.IDENTIFIER, specHierarchy.getSpecHierarchyID());
        specHierarchyNode.addAttribute(ReqIFConst.LAST_CHANGE,DateUtils.toISODate(specHierarchy.getLastChange()));
        specHierarchyNode.addElement(ReqIFConst.OBJECT).addElement("SPEC-OBJECT-REF")
                         .addText(specHierarchy.getSpecObject().getId());
        if (!specHierarchy.getDirectChildren().isEmpty()) {
            Element grandsonNode = specHierarchyNode.addElement(ReqIFConst.CHILDREN);
            for (SpecHierarchy child : specHierarchy.getDirectChildren()) {
                writeSpecHierarchy(grandsonNode, child);
            }
        }
    }

    private void writeToolExtensions(Element rootNode) {
        Element toolExtensionsNode = rootNode.addElement("TOOL_EXTENSIONS");
        Element reqIfToolExtensionNode =
                toolExtensionsNode.addElement("REQ-IF-TOOL-EXTENSION", "http://www.ibm.com/rdm/doors/REQIF/xmlns/1.0");
        Element moduleNode = reqIfToolExtensionNode.addElement("MODULE-DEFINITION");
        moduleNode.addElement("DDC-MOD").addText("DDC_FULL_MODULE");
        Optional<Specification> specification =
                reqIFDocument.getCoreContent().getSpecifications().values().stream().findFirst();
        specification.ifPresent(
                value -> moduleNode.addElement("SPECIFICATION-REF", "http://www.omg.org/spec/ReqIF/20110401/reqif.xsd")
                                   .addText(value.getId()));

        Element attributesNode = reqIfToolExtensionNode.addElement("READONLY-ATTRIBUTES", "http://www.ibm" +
                ".com/rdm/doors/REQIF/xmlns/1.0");


        for (SpecType specType : reqIFDocument.getCoreContent().getSpecTypes().values()) {
            if (specType instanceof SpecObjectType) {
                SpecObjectType specObjectType = (SpecObjectType) specType;

                for (AttributeDefinition attribute : specObjectType.getAttributeDefinitions().values()) {
                    Element attributeNode =
                            attributesNode.addElement("ATTRIBUTE-DEFINITION-" + attribute.getDataType().getType() +
                                                              "-REF",
                                                      "http://www.omg.org/spec/ReqIF/20110401/reqif.xsd");
                    attributeNode.addText(attribute.getId());
                }
            }
        }
    }
}
