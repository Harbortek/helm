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
import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.Datatype;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.DatatypeEnumeration;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFConst;
import com.harbortek.helm.smartdoc.utils.ReqIFUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class SpecType {


    private Map<String, AttributeDefinition> attributeDefinitions = new LinkedHashMap<String, AttributeDefinition>();
    private String id;
    protected String name;
    protected String type;
    private Date lastChange;


    public Map<String, AttributeDefinition> getAttributeDefinitions() {
        return this.attributeDefinitions;
    }

    public AttributeDefinition getAttributeDefinition(String id) {
        return this.attributeDefinitions.get(id);
    }

    public String getEnumValueName(String id) {

        for (AttributeDefinition attributeDefinition : this.attributeDefinitions.values()) {

            if (attributeDefinition.getDataType().getClass().equals(DatatypeEnumeration.class)) {

                if (((DatatypeEnumeration) attributeDefinition.getDataType()).getEnumValueName(id) != null) {

                    return ((DatatypeEnumeration) attributeDefinition.getDataType()).getEnumValueName(id);
                }
            }
        }
        return "";
    }

    public String getEnumValueKey(String id) {

        for (AttributeDefinition attributeDefinition : this.attributeDefinitions.values()) {

            if (attributeDefinition.getDataType().getClass().equals(DatatypeEnumeration.class)) {

                if (((DatatypeEnumeration) attributeDefinition.getDataType()).getEnumValueName(id) != null) {

                    return ((DatatypeEnumeration) attributeDefinition.getDataType()).getEnumValueKey(id);
                }
            }
        }
        return "";
    }

    public String getEnumValueOtherContent(String id) {

        for (AttributeDefinition attributeDefinition : this.attributeDefinitions.values()) {

            if (attributeDefinition.getDataType().getClass().equals(DatatypeEnumeration.class)) {

                if (((DatatypeEnumeration) attributeDefinition.getDataType()).getEnumValueName(id) != null) {

                    return ((DatatypeEnumeration) attributeDefinition.getDataType()).getEnumValueOtherContent(id);
                }
            }
        }
        return "";
    }


    public SpecType(Node specType, Map<String, Datatype> dataTypes) {

        this.id = ReqIFUtils.getId(specType);
        this.name = ReqIFUtils.getName(specType);
        this.lastChange = ReqIFUtils.getLastChange(specType);
        this.type = ReqIFConst.UNDEFINED;

        if (specType.getChildNodes().getLength() > 0) {
            NodeList attributeDefinitions = specType.getChildNodes().item(1).getChildNodes();

            for (int specatt = 0; specatt < attributeDefinitions.getLength(); specatt++) {

                Node attributeDefinition = attributeDefinitions.item(specatt);
                String attDefNodeName = attributeDefinition.getNodeName();
                if (!attDefNodeName.equals(ReqIFConst._TEXT)) {

                    String attDefID = ReqIFUtils.getId(attributeDefinition);

                    switch (attDefNodeName.substring(attDefNodeName.lastIndexOf("-") + 1)) {

                        case ReqIFConst.BOOLEAN:
                            this.attributeDefinitions.put(attDefID, new AttributeDefinitionBoolean(attributeDefinition,
                                                                                                   dataTypes));
                            break;

                        case ReqIFConst.INTEGER:
                            this.attributeDefinitions.put(attDefID, new AttributeDefinitionInteger(attributeDefinition,
                                                                                                   dataTypes));
                            break;

                        case ReqIFConst.FLOAT:
                            this.attributeDefinitions.put(attDefID,
                                                          new AttributeDefinitionFloat(attributeDefinition, dataTypes));
                            break;
                        case ReqIFConst.REAL:
                            this.attributeDefinitions.put(attDefID,
                                                          new AttributeDefinitionReal(attributeDefinition, dataTypes));
                            break;
                        case ReqIFConst.DATE:
                            this.attributeDefinitions.put(attDefID,
                                                          new AttributeDefinitionDate(attributeDefinition, dataTypes));
                            break;

                        case ReqIFConst.STRING:
                            this.attributeDefinitions.put(attDefID, new AttributeDefinitionString(attributeDefinition,
                                                                                                  dataTypes));
                            break;

                        case ReqIFConst.ENUMERATION:
                            this.attributeDefinitions.put(attDefID,
                                                          new AttributeDefinitionEnumeration(attributeDefinition,
                                                                                             dataTypes));
                            break;

                        case ReqIFConst.XHTML:
                            this.attributeDefinitions.put(attDefID,
                                                          new AttributeDefinitionXHTML(attributeDefinition, dataTypes));
                            break;

                        default:
                            this.attributeDefinitions.put(attDefID,
                                                          new AttributeDefinition(attributeDefinition, dataTypes));
                            break;
                    }
                }
            }
        }
    }

}
