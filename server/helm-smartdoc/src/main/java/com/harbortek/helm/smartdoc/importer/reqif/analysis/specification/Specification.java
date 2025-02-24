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

package com.harbortek.helm.smartdoc.importer.reqif.analysis.specification;

import com.harbortek.helm.smartdoc.importer.reqif.analysis.attributes.*;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFConst;
import com.harbortek.helm.smartdoc.utils.ReqIFUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
@Data
@NoArgsConstructor
public class Specification {


    private String id;
    private String name;
    private Date lastChange;
    private SpecType specType;
    private int numberOfSpecObjects;
    private int sectionCounter = 0;
    private Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
    private Map<String, SpecHierarchy> children = new LinkedHashMap<String, SpecHierarchy>();
    private List<SpecHierarchy> allSpecHierarchies = new ArrayList<SpecHierarchy>();
    //private Map<Integer, List<SpecObject>> allSpecObjects = new HashMap<Integer, List<SpecObject>>();		//		TODO
    ///
    public String getDescription() {
        return (String) this.attributeValues.get("Description").getValue();
    }
    //*/

    public String getSpecTypeName() {
        return this.specType.getName();
    }

    public Map<String, AttributeValue> getAttributes() {
        return this.attributeValues;
    }

    public Object getAttribute(String attributeName) {
        return this.attributeValues.get(attributeName).getValue();
    }

    public int getNumberOfSpecObjects() {
        return this.numberOfSpecObjects;
    }

    public int getNumberOfSections() {
        return this.sectionCounter;
    }


    public List<SpecHierarchy> getLvlOneSpecHierarchies() {

        return new ArrayList<>(this.children.values());
    }

    public List<SpecHierarchy> getAllSpecHierarchies() {
        return this.allSpecHierarchies;
    }


    public Specification(Node specification, SpecType specType, Map<String, SpecObject> specObjects) {

        this.id = ReqIFUtils.getId(specification);
        this.name = ReqIFUtils.getName(specification);
        this.lastChange = ReqIFUtils.getLastChange(specification);
        this.specType = specType;

        if (((Element) specification).getElementsByTagName(ReqIFConst.VALUES).getLength() > 0
                &&
                ((Element) specification).getElementsByTagName(ReqIFConst.VALUES).item(0).getChildNodes().getLength() >
                        0) {

            NodeList attributeValues =
                    ((Element) specification).getElementsByTagName(ReqIFConst.VALUES).item(0).getChildNodes();
            for (int attval = 0; attval < attributeValues.getLength(); attval++) {

                Node attribute = attributeValues.item(attval);
                String attValNodeName = attribute.getNodeName();
                if (!attValNodeName.equals(ReqIFConst._TEXT)) {

                    String attributeDefinitionRef =
                            ((Element) attribute).getElementsByTagName(ReqIFConst.DEFINITION).item(0).getChildNodes()
                                                 .item(1).getTextContent();
                    String attributeDefinitionName =
                            specType.getAttributeDefinitions().get(attributeDefinitionRef).getName();
                    String attributeValue;
                    AttributeDefinition attributeDefinition = specType.getAttributeDefinition(attributeDefinitionRef);

                    switch (attValNodeName.substring(attValNodeName.lastIndexOf("-") + 1)) {

                        case ReqIFConst.BOOLEAN:
                            if (attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE) != null) {
                                attributeValue =
                                        attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE).getTextContent();
                            } else {
                                attributeValue = "";
                            }
                            this.attributeValues.put(attributeDefinitionName,
                                                     new AttributeValueBoolean(attributeValue, attributeDefinition));
                            break;

                        case ReqIFConst.INTEGER:
                            if (attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE) != null) {
                                attributeValue =
                                        attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE).getTextContent();
                            } else {
                                attributeValue = "";
                            }
                            this.attributeValues.put(attributeDefinitionName,
                                                     new AttributeValueInteger(attributeValue, attributeDefinition));
                            break;

                        case ReqIFConst.STRING:
                            if (attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE) != null) {
                                attributeValue =
                                        attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE).getTextContent();
                            } else {
                                attributeValue = "";
                            }
                            this.attributeValues.put(attributeDefinitionName,
                                                     new AttributeValueString(attributeValue, attributeDefinition));
                            break;

                        case ReqIFConst.ENUMERATION:
                            String enumValueRef = ((Element) attribute).getElementsByTagName(ReqIFConst.VALUES).item(0)
                                                                       .getChildNodes().item(1).getTextContent();
                            attributeValue = specType.getEnumValueName(enumValueRef);
                            this.attributeValues.put(attributeDefinitionName,
                                                     new AttributeValueEnumeration(attributeValue,
                                                                                   attributeDefinition));
                            break;

                        case ReqIFConst.XHTML:
                            this.attributeValues.put(attributeDefinitionName,
                                                     new AttributeValueXHTML(attribute, attributeDefinition));
                            break;

                        case ReqIFConst.DATE:
                            if (attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE) != null) {
                                attributeValue =
                                        attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE).getTextContent();
                            } else {
                                attributeValue = "";
                            }
                            this.attributeValues.put(attributeDefinitionName,
                                                     new AttributeValueDate(attributeValue,
                                                                            attributeDefinition));
                            break;
                        case ReqIFConst.REAL:
                            if (attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE) != null) {
                                attributeValue =
                                        attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE).getTextContent();
                            } else {
                                attributeValue = "";
                            }
                            this.attributeValues.put(attributeDefinitionName,
                                                     new AttributeValueReal(attributeValue,
                                                                            attributeDefinition));
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        if (this.attributeValues.size() < specType.getAttributeDefinitions().size()) {

            for (AttributeDefinition attributeDefinition : specType.getAttributeDefinitions().values()) {

                if (!this.attributeValues.containsKey(attributeDefinition.getName())) {

                    switch (attributeDefinition.getDataType().getType()) {

                        case ReqIFConst.BOOLEAN:
                            this.attributeValues.put(attributeDefinition.getName(),
                                                     new AttributeValueBoolean(attributeDefinition.getDefaultValue(),
                                                                               attributeDefinition));
                            break;

                        case ReqIFConst.INTEGER:
                            this.attributeValues.put(attributeDefinition.getName(),
                                                     new AttributeValueInteger(attributeDefinition.getDefaultValue(),
                                                                               attributeDefinition));
                            break;

                        case ReqIFConst.STRING:
                            this.attributeValues.put(attributeDefinition.getName(),
                                                     new AttributeValueString(attributeDefinition.getDefaultValue(),
                                                                              attributeDefinition));
                            break;

                        case ReqIFConst.ENUMERATION:
                            this.attributeValues.put(attributeDefinition.getName(), new AttributeValueEnumeration(
                                    attributeDefinition.getDefaultValue(), attributeDefinition));
                            break;

                        case ReqIFConst.XHTML:
                            this.attributeValues.put(attributeDefinition.getName(),
                                                     new AttributeValueXHTML(attributeDefinition.getDefaultValue(),
                                                                             attributeDefinition));
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        if (((Element) specification).getElementsByTagName(ReqIFConst.CHILDREN).getLength() > 0
                && ((Element) specification).getElementsByTagName(ReqIFConst.CHILDREN).item(0).getChildNodes()
                                            .getLength() > 0) {

            NodeList children =
                    ((Element) specification).getElementsByTagName(ReqIFConst.CHILDREN).item(0).getChildNodes();
            for (int child = 0; child < children.getLength(); child++) {

                Node specHierarchy = children.item(child);
                if (!specHierarchy.getNodeName().equals(ReqIFConst._TEXT)) {

                    String specHierarchyID = ReqIFUtils.getId(specHierarchy);

                    this.children.put(specHierarchyID,
                                      new SpecHierarchy(1, ++this.sectionCounter, specHierarchy, specObjects));
                }
            }
        }

        List<SpecHierarchy> allChildren = new ArrayList<SpecHierarchy>();
        for (SpecHierarchy specHierarchy : this.children.values()) {
            allChildren.add(specHierarchy);
            for (SpecHierarchy child : specHierarchy.getAllChildren()) {
                allChildren.add(child);
            }
        }
        this.allSpecHierarchies.addAll(allChildren);
        this.numberOfSpecObjects = allChildren.size();
    }

}
