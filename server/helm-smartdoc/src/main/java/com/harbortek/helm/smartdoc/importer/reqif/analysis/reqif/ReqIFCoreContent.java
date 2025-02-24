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

package com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif;

import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.*;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.specification.*;
import com.harbortek.helm.smartdoc.utils.ReqIFUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public class ReqIFCoreContent {


    private Map<String, Datatype> dataTypes = new LinkedHashMap<String, Datatype>();
    private Map<String, SpecType> specTypes = new LinkedHashMap<String, SpecType>();
    private Map<String, SpecObject> specObjects = new LinkedHashMap<String, SpecObject>();
    private Map<String, Specification> specifications = new LinkedHashMap<String, Specification>();

    private Map<String, SpecRelation> specRelations = new LinkedHashMap<String, SpecRelation>();


    public Map<String, Datatype> getDatatypes() {
        return this.dataTypes;
    }

    public Datatype getDatatype(String id) {
        return this.dataTypes.get(id);
    }

    public Map<String, SpecType> getSpecTypes() {
        return this.specTypes;
    }

    public SpecType getSpecType(String id) {
        return this.specTypes.get(id);
    }

    public Map<String, SpecObject> getSpecObjects() {
        return this.specObjects;
    }

    public SpecObject getSpecObject(String id) {
        return this.specObjects.get(id);
    }

    public Map<String, Specification> getSpecifications() {
        return this.specifications;
    }

    public Specification getSpecification(String id) {
        return this.specifications.get(id);
    }

    public Map<String, SpecRelation> getSpecRelations() {
        return specRelations;
    }

    public List<Specification> getSpecificationsList() {
        List<Specification> specifications = new ArrayList<Specification>();
        for (Specification specification : this.specifications.values()) {
            specifications.add(specification);
        }

        return specifications;
    }

    public List<SpecHierarchy> getOrderedSpecHierarchyList() {

        List<SpecHierarchy> orderedSpecHierarchies = new ArrayList<SpecHierarchy>();
        for (Specification specification : this.specifications.values()) {
            orderedSpecHierarchies.addAll(specification.getAllSpecHierarchies());
        }
        return orderedSpecHierarchies;
    }


    public ReqIFCoreContent(Element coreContent) {


        if (coreContent.getElementsByTagName(ReqIFConst.DATATYPES).item(0).hasChildNodes()) {

            NodeList dataTypes = coreContent.getElementsByTagName(ReqIFConst.DATATYPES).item(0).getChildNodes();
            for (int datatype = 0; datatype < dataTypes.getLength(); datatype++) {

                Node dataType = dataTypes.item(datatype);
                String dataTypeNodeName = dataType.getNodeName();
                if (!dataTypeNodeName.equals(ReqIFConst._TEXT)) {

                    String dataTypeID = ReqIFUtils.getId(dataType);
                    String dataTypeName =
                            ReqIFUtils.getName(dataType);
                    Date lastChange = ReqIFUtils.getLastChange(dataType);

                    switch (dataTypeNodeName.substring(dataTypeNodeName.lastIndexOf("-") + 1)) {

                        case ReqIFConst.BOOLEAN:
                            this.dataTypes.put(dataTypeID, new DatatypeBoolean(dataTypeID, dataTypeName,lastChange));
                            break;

                        case ReqIFConst.INTEGER:
                            String min = dataType.getAttributes().getNamedItem(ReqIFConst.MIN).getTextContent();
                            String max = dataType.getAttributes().getNamedItem(ReqIFConst.MAX).getTextContent();
                            Datatype dataTypeObj = new DatatypeInteger(dataTypeID, dataTypeName,lastChange, min, max);
                            dataTypeObj.setLastChange(lastChange);
                            this.dataTypes.put(dataTypeID, dataTypeObj);
                            break;
                        case ReqIFConst.REAL:
                            String minV =
                                    dataType.getAttributes().getNamedItem(ReqIFConst.MIN).getTextContent();
                            String maxV = dataType.getAttributes().getNamedItem(ReqIFConst.MAX).getTextContent();
                            String accuracy =
                                    dataType.getAttributes().getNamedItem(ReqIFConst.ACCURACY).getTextContent();
                            this.dataTypes.put(dataTypeID, new DatatypeReal(dataTypeID, dataTypeName,lastChange, accuracy, minV,
                                                                            maxV));
                            break;
                        case ReqIFConst.DATE:
                            this.dataTypes.put(dataTypeID, new DatatypeDate(dataTypeID,
                                                                            dataTypeName,lastChange));
                            break;

                        case ReqIFConst.STRING:
                            String maxLength =
                                    dataType.getAttributes().getNamedItem(ReqIFConst.MAX_LENGTH).getTextContent();
                            this.dataTypes.put(dataTypeID, new DatatypeString(dataTypeID, dataTypeName,lastChange, maxLength));
                            break;

                        case ReqIFConst.ENUMERATION:
                            Node enumeration = dataType;
                            this.dataTypes.put(dataTypeID,
                                               new DatatypeEnumeration(dataTypeID, dataTypeName,lastChange, enumeration));
                            break;

                        case ReqIFConst.XHTML:
                            this.dataTypes.put(dataTypeID, new DatatypeXHTML(dataTypeID, dataTypeName,lastChange));
                            break;

                        default:
                            this.dataTypes.put(null, new Datatype(dataTypeID, dataTypeName,lastChange,
                                                                  ReqIFConst.UNDEFINED));
                            break;
                    }
                }
            }
        }


        if (coreContent.getElementsByTagName(ReqIFConst.SPEC_TYPES).item(0).hasChildNodes()) {

            NodeList specTypes = coreContent.getElementsByTagName(ReqIFConst.SPEC_TYPES).item(0).getChildNodes();
            for (int spectype = 0; spectype < specTypes.getLength(); spectype++) {

                Node specType = specTypes.item(spectype);
                String specTypeNodeName = specType.getNodeName();
                if (!specTypeNodeName.equals(ReqIFConst._TEXT)) {

                    String specTypeID =
                            ReqIFUtils.getId(specType);

                    switch (specTypeNodeName) {

                        case ReqIFConst.SPECIFICATION_TYPE:
                            this.specTypes.put(specTypeID, new SpecificationType(specType, this.dataTypes));
                            break;

                        case ReqIFConst.SPEC_OBJECT_TYPE:
                            this.specTypes.put(specTypeID, new SpecObjectType(specType, this.dataTypes));
                            break;

                        case ReqIFConst.SPEC_RELATION_TYPE:
                            this.specTypes.put(specTypeID, new SpecRelationType(specType, this.dataTypes));
                            break;

                        default:
                            this.specTypes.put(specTypeID, new SpecType(specType, this.dataTypes));
                            break;
                    }
                }
            }
        }


        if (coreContent.getElementsByTagName(ReqIFConst.SPEC_OBJECT).getLength() > 0) {

            NodeList specObjects = coreContent.getElementsByTagName(ReqIFConst.SPEC_OBJECT);
            for (int specobj = 0; specobj < specObjects.getLength(); specobj++) {

                Node specObj = specObjects.item(specobj);
                String specObjID =
                        ReqIFUtils.getId(specObj);
                String specObjTypeRef =
                        ((Element) specObj).getElementsByTagName(ReqIFConst.SPEC_OBJECT_TYPE_REF).item(0)
                                           .getTextContent();

                this.specObjects.put(specObjID, new SpecObject(specObj, this.specTypes.get(specObjTypeRef)));
            }
        }

        if (coreContent.getElementsByTagName(ReqIFConst.SPEC_RELATIONS).getLength() > 0) {
            NodeList specRelations = coreContent.getElementsByTagName(ReqIFConst.SPEC_RELATION);
            for (int specrelation = 0; specrelation < specRelations.getLength(); specrelation++) {

                Node specRelationNode = specRelations.item(specrelation);
                String specObjID = ReqIFUtils.getId(specRelationNode);

                Date lastChange =ReqIFUtils.getLastChange(specRelationNode);

                String sourceObjRef = StringUtils.trim(
                        ((Element) specRelationNode).getElementsByTagName(ReqIFConst.SOURCE).item(0)
                                                    .getTextContent());
                SpecObject sourceObj = this.specObjects.get(sourceObjRef);

                String targetObjRef =
                        StringUtils.trim(((Element) specRelationNode).getElementsByTagName(ReqIFConst.TARGET).item(0)
                                                                     .getTextContent());
                SpecObject targetObj = this.specObjects.get(targetObjRef);


                String typeRef =
                        StringUtils.trim(((Element) specRelationNode).getElementsByTagName(ReqIFConst.TYPE).item(0)
                                                                     .getTextContent());

                SpecRelationType relationType = (SpecRelationType) this.specTypes.get(typeRef);

                if (sourceObj!=null && targetObj!=null && relationType!=null){
                    this.specRelations.put(specObjID, new SpecRelation(specObjID, lastChange,sourceObj, targetObj,
                                                                       relationType));
                }
            }
        }


        if (coreContent.getElementsByTagName(ReqIFConst.SPECIFICATION).getLength() > 0) {

            NodeList specifications = coreContent.getElementsByTagName(ReqIFConst.SPECIFICATION);
            for (int spec = 0; spec < specifications.getLength(); spec++) {

                Node specification = specifications.item(spec);
                String specID =
                        ReqIFUtils.getId(specification);
                String specTypeRef = StringUtils.trim(
                        ((Element) specification).getElementsByTagName(ReqIFConst.SPECIFICATION_TYPE_REF).item(0)
                                                 .getTextContent());

                this.specifications.put(specID, new Specification(specification, this.specTypes.get(specTypeRef),
                                                                  this.specObjects));
            }
        }


    }

}
