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

import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.Datatype;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFConst;
import com.harbortek.helm.smartdoc.utils.ReqIFUtils;
import com.harbortek.helm.util.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Date;
import java.util.Map;
@Data
@NoArgsConstructor
public class AttributeDefinition {
	
	
	private String id;
	private String name;
	private Date lastChange;
	private Datatype dataType;
	private String defaultValue;

	public AttributeDefinition(String id, String name, Datatype dataType) {
		this.id = id;
		this.name = name;
		this.dataType = dataType;
		this.lastChange = DateUtils.now();
	}

	public AttributeDefinition(Node attributeDefinition, Map<String, Datatype> dataTypes) {
		
		this.id = ReqIFUtils.getId(attributeDefinition);
		this.name =
				ReqIFUtils.getName(attributeDefinition);
		this.lastChange = ReqIFUtils.getLastChange(attributeDefinition);
		
		Element attDef = (Element) attributeDefinition;
		String typeID = attDef.getElementsByTagName(ReqIFConst.TYPE).item(0).getChildNodes().item(1).getTextContent();
		this.dataType = dataTypes.get(typeID);
		
		NodeList defVal = attDef.getElementsByTagName(ReqIFConst.DEFAULT_VALUE);
		if(defVal.getLength() > 0) {
			
			Node attDefVal = defVal.item(0).getChildNodes().item(1);
			if(attDefVal != null && attDefVal.hasAttributes() && attDefVal.getAttributes().getNamedItem(ReqIFConst.THE_VALUE) != null) {
				this.defaultValue = attDefVal.getAttributes().getNamedItem(ReqIFConst.THE_VALUE).getTextContent();
			}
		}
	}

}
