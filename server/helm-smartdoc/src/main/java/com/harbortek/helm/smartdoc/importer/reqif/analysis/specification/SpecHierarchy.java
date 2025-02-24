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

import com.harbortek.helm.smartdoc.importer.reqif.analysis.attributes.AttributeValue;
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
public class SpecHierarchy {
    private String specHierarchyID;
    private Date lastChange;
    private int hierarchyLvl;
    private int section;
    private SpecObject specObject;
    private Map<String, SpecHierarchy> children = new LinkedHashMap<String, SpecHierarchy>();


    public String getSpecHierarchyID() {
        return this.specHierarchyID;
    }

    public String getSpecObjectID() {
        return this.specObject.getId();
    }

    public int getHierarchyLvl() {
        return this.hierarchyLvl;
    }

    public int getSection() {
        return this.section;
    }

    public SpecObject getSpecObject() {
        return this.specObject;
    }

    public Map<String, AttributeValue> getAttributes() {
        return this.specObject.getAttributes();
    }

    public Object getAttribute(String attributeName) {
        return (Object) this.specObject.getAttribute(attributeName);
    }

//    public AttributeValueXHTMLElementList getXHTMLContent() {
//        for (AttributeValue attributeValue : this.specObject.getAttributes().values()) {
//            if (attributeValue.getAttributeDefinition().getDataType().getType().equals(ReqIFConst.XHTML)) {
//                return (AttributeValueXHTMLElementList) attributeValue.getValue();
//            }
//        }
//        return null;
//    }

//    public List<XHTMLNode> getXHTMLDivContent() {
//        for (AttributeValue attributeValue : this.specObject.getAttributes().values()) {
//            if (attributeValue instanceof AttributeValueXHTML) {
//                XHTMLElementDiv value = ((AttributeValueXHTML) attributeValue).getDivValue();
//                if (value != null) {
//                    return value.getChildren();
//                }
//            }
//        }
//        return null;
//    }

    public String getSpecType() {
        return this.specObject.getSpecType().getType();
    }

    public String getSpecTypeName() {
        return this.specObject.getSpecTypeName();
    }

    public String getType() {
        return this.specObject.getType();
    }

    public boolean isReq() {
        if (this.specObject.isReq()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDef() {
        if (this.specObject.isReq() && this.section == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isHeadline() {
        if (this.specObject.isHeadline()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSubReq() {
        if (this.specObject.isSubReq()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isText() {
        if (this.specObject.isText()) {
            return true;
        } else {
            return false;
        }
    }

    public List<SpecHierarchy> getDirectChildren() {

        List<SpecHierarchy> directChildren = new ArrayList<SpecHierarchy>();
        for (SpecHierarchy specHierarchy : this.children.values()) {
            directChildren.add(specHierarchy);
        }
        return directChildren;
    }

    public List<SpecHierarchy> getAllChildren() {

        List<SpecHierarchy> allChildren = new ArrayList<SpecHierarchy>();
        for (SpecHierarchy specHierarchy : this.children.values()) {
            allChildren.add(specHierarchy);
            for (SpecHierarchy child : specHierarchy.getAllChildren()) {
                allChildren.add(child);
            }
        }

        return allChildren;
    }


    public SpecHierarchy(int hierarchyLvl, int section, Node specHierarchy, Map<String, SpecObject> specObjects) {

        this.hierarchyLvl = hierarchyLvl;
        this.section = section;
        this.specHierarchyID = ReqIFUtils.getId(specHierarchy);

        this.lastChange = ReqIFUtils.getLastChange(specHierarchy);

        for (int childnode = 0; childnode < specHierarchy.getChildNodes().getLength(); childnode++) {
            Node childNode = specHierarchy.getChildNodes().item(childnode);
            if (childNode.getNodeName().equals(ReqIFConst.OBJECT)) {
                String specObjectRef =
                        ((Element) childNode).getElementsByTagName(ReqIFConst.SPEC_OBJECT_REF).item(0).getTextContent();
                this.specObject = specObjects.get(specObjectRef);
            }
        }

        if (((Element) specHierarchy).getElementsByTagName(ReqIFConst.CHILDREN).getLength() > 0
                && ((Element) specHierarchy).getElementsByTagName(ReqIFConst.CHILDREN).item(0).getChildNodes()
                                            .getLength() > 0) {

            NodeList children =
                    ((Element) specHierarchy).getElementsByTagName(ReqIFConst.CHILDREN).item(0).getChildNodes();
            for (int child = 0; child < children.getLength(); child++) {

                Node newSpecHierarchy = children.item(child);
                if (!newSpecHierarchy.getNodeName().equals(ReqIFConst._TEXT)) {

                    String specHierarchyID =
                            ReqIFUtils.getId(newSpecHierarchy);

                    this.children.put(specHierarchyID,
                                      new SpecHierarchy(this.hierarchyLvl + 1, section, newSpecHierarchy, specObjects));
                }
            }
        }
    }

}
