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

package com.harbortek.helm.smartdoc.importer.reqif.analysis.attributes;

import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFConst;
import com.harbortek.helm.smartdoc.utils.ReqIFUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AttributeValueXHTML extends AttributeValue {

    public AttributeValueXHTML(Node xhtmlContent, AttributeDefinition type) {
        super(type);

        this.value =
                ReqIFUtils.w3cNode2String(
                        ((Element) xhtmlContent).getElementsByTagName(ReqIFConst.THE_VALUE).item(0).getChildNodes()
                                                .item(0));

    }

    public AttributeValueXHTML(String value, AttributeDefinition type) {
        super(type);
		this.value = value;
    }

//
//    private AttributeValueXHTMLElementList deconstructXHTML(Node xhtmlContent) {
//
//        AttributeValueXHTMLElementList xhtmlElementList = new AttributeValueXHTMLElementList();
//
//        Node div = ((Element) xhtmlContent).getElementsByTagName("xhtml:div").item(0);
//
//        for (int e = 0; e < div.getChildNodes().getLength(); e++) {
//            Node xhtmlElement = div.getChildNodes().item(e);
//            String elementName = div.getChildNodes().item(e).getNodeName();
//            if (elementName.contains("xhtml:")) {
//                if (elementName.endsWith("p")) {
//                    xhtmlElementList.add("P", decostructXHTMLElement("P", xhtmlElement));
//                } else if (elementName.endsWith("table")) {
//                    xhtmlElementList.add("TBL", decostructXHTMLElement("TBL", xhtmlElement));
//                } else if (elementName.endsWith(":ul")) {
//                    xhtmlElementList.add("L", decostructXHTMLElement("L", xhtmlElement));
//                } else if (elementName.contains(":h")) {
//                    xhtmlElementList.add("H", decostructXHTMLElement("H", xhtmlElement));
//                } else if (elementName.contains("object")) {
//                    xhtmlElementList.add("OBJ", decostructXHTMLElement("OBJ", xhtmlElement));
//                }
//            }
//        }
//        return xhtmlElementList;
//    }
//
//    private List<String> decostructXHTMLElement(String elementType, Node xhtmlElement) {
//
//        List<String> content = new ArrayList<String>();
//
//        if (elementType.equals("P")) {
//            for (int childNode = 0; childNode < xhtmlElement.getChildNodes().getLength(); childNode++) {
//                Node child = xhtmlElement.getChildNodes().item(childNode);
//                if (child.getNodeName().contains("span")) {
//                    content.add("TXT");
//                    content.add(child.getTextContent().trim());
//
//                } else if (child.getNodeName().equals(ReqIFConst._TEXT) && !child.getTextContent().isEmpty()) {
//                    content.add("TXT");
//                    content.add(child.getTextContent().trim());
//
//                } else if (child.getNodeName().contains("var")) {        //		TODO
//                    //System.out.println("VAR:\n" + child.getTextContent().trim());
//                    content.add("VAR");
//                    if (child.getTextContent().trim().equals("")) {
//                        content.add("VARIABLE_NAME_MISSING");
//
//                    } else {
//                        content.add(child.getTextContent().trim());
//                        if (child.getAttributes().getNamedItem("GUID") != null) {
//                            content.add(child.getAttributes().getNamedItem("GUID").getTextContent());
//                        }
//                    }
//
//                } else if (child.getNodeName().contains("br")) {
//                    //System.out.println();
//                    content.add("BR");
//
//                }
//            }
//        } else if (elementType.equals("TBL")) {
//
//            content.addAll(decontructTable(xhtmlElement));
//
//        } else if (elementType.equals("L")) {        //		TODO: nested lists
//
//            content.addAll(list(xhtmlElement));
//
//        } else if (elementType.equals("H")) {
//            //System.out.println("\n"+xhtmlElement.getTextContent().trim());
//            //content.add(xhtmlElement.getTextContent().trim());
//            //																							TODO
//            for (int childNode = 0; childNode < xhtmlElement.getChildNodes().getLength(); childNode++) {
//                Node child = xhtmlElement.getChildNodes().item(childNode);
//
//                if (child.getNodeName().contains("span")) {
//                    content.add("TXT");
//                    content.add(child.getTextContent().trim());
//
//                } else if (child.getNodeName().equals(ReqIFConst._TEXT) && !child.getTextContent().isEmpty()) {
//                    content.add("TXT");
//                    content.add(child.getTextContent().trim());
//
//                } else if (child.getNodeName().contains("var")) {
//                    content.add("VAR");
//                    if (child.getTextContent().trim().equals("")) {
//                        content.add("VARIABLE_NAME_MISSING");
//                    } else {
//                        content.add(child.getTextContent().trim());
//                        if (child.getAttributes().getNamedItem("GUID") != null) {
//                            content.add(child.getAttributes().getNamedItem("GUID").getTextContent());
//                        }
//                    }
//                }
//            }
//
//        } else if (elementType.equals("OBJ")) {
//            //System.out.println("\n"+xhtmlElement.getAttributes().getNamedItem("data").getTextContent());
//            String path = xhtmlElement.getAttributes().getNamedItem("data").getTextContent().trim();
//            content.add(path.replace("/", System.getProperty("file.separator")));
//        }
//
//		/*//
//		for(int i=0; i<content.size(); i++) {
//			System.out.println(content.get(i));
//		}
//		//*/
//
//        return content;
//    }
//
//    private List<String> list(Node listNode) {
//
//        List<String> list = new ArrayList<String>();
//
//        ///
//        for (int childNode = 0; childNode < listNode.getChildNodes().getLength(); childNode++) {
//            Node child = listNode.getChildNodes().item(childNode);
//
//            if (child.getNodeName().endsWith(":li")) {
//                list.add("LE");
//
//                for (int listElement = 0; listElement < child.getChildNodes().getLength(); listElement++) {
//                    Node listChild = child.getChildNodes().item(listElement);
//                    String leName = listChild.getNodeName();
//
//                    if (leName.endsWith("span")) {
//                        list.add("TXT");
//                        list.add(listChild.getTextContent().trim());
//
//                    } else if (leName.equals(ReqIFConst._TEXT) && !listChild.getTextContent().trim().isEmpty()) {
//                        list.add("TXT");
//                        list.add(listChild.getTextContent().trim());
//
//                    } else if (leName.endsWith("var")) {
//                        list.add("VAR");
//                        if (child.getTextContent().trim().equals("")) {
//                            list.add("VARIABLE_NAME_MISSING");
//                        } else {
//                            list.add(child.getTextContent().trim());
//                        }
//
//                        ///
//                    } else if (leName.endsWith("ul")) {
//                        list.add("L");
//                        list.addAll(list(listChild));
//                        //list.add("_L");
//                        //*/
//
//                    } else if (leName.endsWith("table")) {
//                        list.add("TBL");
//                        list.addAll(decontructTable(listChild));
//
//                    }
//                }
//            }
//        }
//        //*/
//
//        list.add("/L");
//
//        return list;
//    }
//
//    private List<String> decontructTable(Node tableNode) {
//
//        List<String> tblContent = new ArrayList<String>();
//
//        for (int tbl = 0; tbl < tableNode.getChildNodes().getLength(); tbl++) {
//            Node tbody = tableNode.getChildNodes().item(tbl);
//
//            if (!tbody.getNodeName().equals(ReqIFConst._TEXT)) {
//
//                for (int tr = 0; tr < tbody.getChildNodes().getLength(); tr++) {
//                    Node trow = tbody.getChildNodes().item(tr);
//
//                    if (!trow.getNodeName().equals(ReqIFConst._TEXT)) {
//                        //System.out.println("\nTR");
//                        tblContent.add("TR");
//
//                        for (int tc = 0; tc < trow.getChildNodes().getLength(); tc++) {
//                            Node tcoloumn = trow.getChildNodes().item(tc);
//
//                            if (!tcoloumn.getNodeName().equals(ReqIFConst._TEXT)) {
//                                //System.out.println("TC");
//                                tblContent.add("TC");
//                                if (tcoloumn.getChildNodes().getLength() <= 1) {
//                                    //System.out.println(tcoloumn.getTextContent());
//                                    tblContent.add(tcoloumn.getTextContent().trim());
//                                } else {
//                                    tblContent.add(tcoloumn.getChildNodes().item(1).getTextContent().trim());
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return tblContent;
//    }

}
