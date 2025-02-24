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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Data
@NoArgsConstructor
public class SpecObject {
	
	
	private String id;
	private Date lastChange;
	private SpecType specType;
	private String type;
	private Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
	
	


	public String getSpecTypeName() {
		return this.specType.getName();
	}
	
	public Map<String, AttributeValue> getAttributes() {
		return this.attributeValues;
	}
	
	public Object getAttribute(String attributeName) {
		return this.attributeValues.get(attributeName).getValue();
	}
	
	public boolean isReq() {
		
		if(this.type.equals(ReqIFConst.REQ)) {
			
			for(AttributeValue attValue: this.attributeValues.values()) {
				
				if(attValue.getAttributeDefinition().getDataType().getType().equals(ReqIFConst.BOOLEAN) && attValue.getName().toLowerCase().contains(ReqIFConst.REQ.toLowerCase())) {
					
					if((Boolean)attValue.getValue()) {
						return true;
						
					}else{
						return false;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isHeadline() {
		
		if(this.type.equals(ReqIFConst.HEADLINE)) {
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isSubReq() {
		
		if(this.type.equals(ReqIFConst.SUB_REQ)) {
			
			for(AttributeValue attValue: this.attributeValues.values()) {
				
				if(attValue.getAttributeDefinition().getDataType().getType().equals(ReqIFConst.BOOLEAN) && attValue.getName().toLowerCase().contains(ReqIFConst.SUB.toLowerCase()) && attValue.getName().toLowerCase().contains(ReqIFConst.REQ.toLowerCase())) {
					
					if((Boolean)attValue.getValue()) {
						return true;
						
					}else{
						return false;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isText() {
		
		if(!this.isReq() && !this.isHeadline() && !this.isSubReq()) {
			return true;
		}else{
			return false;
		}
	}
	
	
	
	
	public SpecObject(Node specObject, SpecType specType) {
		
		this.id = ReqIFUtils.getId(specObject);
		this.lastChange = ReqIFUtils.getLastChange(specObject);
		this.specType = specType;
		
		if(this.specType.getName().toLowerCase().contains(ReqIFConst.REQ.toLowerCase())) {
			
			if(this.specType.getName().toLowerCase().contains(ReqIFConst.SUB.toLowerCase())) {
				this.type = ReqIFConst.SUB_REQ;
			
			}else{
				this.type = ReqIFConst.REQ;
			}
		}else if(this.specType.getName().toLowerCase().contains(ReqIFConst.HEADLINE.toLowerCase())) {
			this.type = ReqIFConst.HEADLINE;
		
		}else{
			this.type = ReqIFConst.TEXT;
		}
		
		if(			((Element)specObject).getElementsByTagName(ReqIFConst.VALUES).getLength() > 0
				&&	((Element)specObject).getElementsByTagName(ReqIFConst.VALUES).item(0).hasChildNodes()		) {
			
			NodeList attributeValues = ((Element)specObject).getElementsByTagName(ReqIFConst.VALUES).item(0).getChildNodes();
			for(int attval = 0; attval < attributeValues.getLength(); attval++) {
				
				Node attribute = attributeValues.item(attval);
				String attValNodeName = attribute.getNodeName();
				if(!attValNodeName.equals(ReqIFConst._TEXT)) {
					
					String attributeDefinitionRef = ((Element)attribute).getElementsByTagName(ReqIFConst.DEFINITION).item(0).getChildNodes().item(1).getTextContent();
					String attributeDefinitionName = specType.getAttributeDefinitions().get(attributeDefinitionRef).getName();
					String attributeValue;
					AttributeDefinition attributeDefinition = specType.getAttributeDefinition(attributeDefinitionRef);
					
					switch(attValNodeName.substring(attValNodeName.lastIndexOf("-")+1)) {
					
						case ReqIFConst.BOOLEAN:		if(attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE) !=null) {
															attributeValue = attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE).getTextContent();
														}else{
															attributeValue = "";
														}
														this.attributeValues.put(attributeDefinitionName, new AttributeValueBoolean(attributeValue, attributeDefinition));
														break;
							
						case ReqIFConst.INTEGER:		if(attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE) !=null) {
															attributeValue = attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE).getTextContent();
														}else{
															attributeValue = "";
														}
														this.attributeValues.put(attributeDefinitionName, new AttributeValueInteger(attributeValue, attributeDefinition));
														break;
							
						case ReqIFConst.STRING:			if(attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE) !=null) {
															attributeValue = attribute.getAttributes().getNamedItem(ReqIFConst.THE_VALUE).getTextContent();
														}else{
															attributeValue = "";
														}
														this.attributeValues.put(attributeDefinitionName, new AttributeValueString(attributeValue, attributeDefinition));
														break;
							
						case ReqIFConst.ENUMERATION:	String enumValueRef = ((Element)attribute).getElementsByTagName(ReqIFConst.VALUES).item(0).getChildNodes().item(1).getTextContent(); 
														attributeValue = specType.getEnumValueName(enumValueRef);
														this.attributeValues.put(attributeDefinitionName, new AttributeValueEnumeration(attributeValue, attributeDefinition));
														break;
							
						case ReqIFConst.XHTML:			this.attributeValues.put(attributeDefinitionName, new AttributeValueXHTML(attribute, attributeDefinition));
														break;
												
						default:						break;
					}
				}
			}
		}
		
		if(this.attributeValues.size() < specType.getAttributeDefinitions().size()) {
			
			for(AttributeDefinition attributeDefinition: specType.getAttributeDefinitions().values()) {
				
				if(!this.attributeValues.containsKey(attributeDefinition.getName())) { 
					
					switch(attributeDefinition.getDataType().getType()) {
					
						case ReqIFConst.BOOLEAN:		this.attributeValues.put(attributeDefinition.getName(), new AttributeValueBoolean(attributeDefinition.getDefaultValue(), attributeDefinition));
														break;
						
						case ReqIFConst.INTEGER:		this.attributeValues.put(attributeDefinition.getName(), new AttributeValueInteger(attributeDefinition.getDefaultValue(), attributeDefinition));
														break;
						
						case ReqIFConst.STRING:			this.attributeValues.put(attributeDefinition.getName(), new AttributeValueString(attributeDefinition.getDefaultValue(), attributeDefinition));
														break;
						
						case ReqIFConst.ENUMERATION:	this.attributeValues.put(attributeDefinition.getName(), new AttributeValueEnumeration(attributeDefinition.getDefaultValue(), attributeDefinition));
														break;
						
						case ReqIFConst.XHTML:			this.attributeValues.put(attributeDefinition.getName(), new AttributeValueXHTML(attributeDefinition.getDefaultValue(), attributeDefinition));
														break;
											
						default:						break;
					}
				}
			}
		}
		
	}

}
