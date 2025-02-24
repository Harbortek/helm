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

package com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes;

import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFConst;
import com.harbortek.helm.smartdoc.utils.ReqIFUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DatatypeEnumeration extends Datatype {
	
	
	private Map<String, DatatypeEnumerationValue> enumValues = new LinkedHashMap<String, DatatypeEnumerationValue>();

	public void setEnumValues(List<DatatypeEnumerationValue> enumValues){
		this.enumValues.clear();
		for (DatatypeEnumerationValue enumValue : enumValues) {
            this.enumValues.put(enumValue.getKey(), enumValue);
        }
	}
	
	public List<DatatypeEnumerationValue> getEnumValues(){
		return new ArrayList<>(this.enumValues.values());
	}
	
	public String getEnumValueName(String id) {
		if (this.enumValues.containsKey(id)) {
			return this.enumValues.get(id).getName();
		}
		return null;
	}
	
	public String getEnumValueKey(String id) {
		if (this.enumValues.containsKey(id)) {
			return this.enumValues.get(id).getKey();
		}
		return null;

	}
	
	public String getEnumValueOtherContent(String id) {
		if (this.enumValues.containsKey(id)) {
			return this.enumValues.get(id).getOtherContent();
		}
		return null;
	}
	
	
	

	public DatatypeEnumeration(String id, String name, Date lastChange, Node enumeration) {
		super(id, name,lastChange, ReqIFConst.ENUMERATION);
		
		if (enumeration.getChildNodes().getLength() == 0){
			return;
		}

		NodeList values = enumeration.getChildNodes().item(1).getChildNodes();
		for(int value = 0; value < values.getLength(); value++) {
			
			if(!values.item(value).getNodeName().equals(ReqIFConst._TEXT)) {
				
				String identifier =
						ReqIFUtils.getId(values.item(value));
				String longName =
						ReqIFUtils.getName(values.item(value));
				Date lastChange1 = ReqIFUtils.getLastChange(values.item(value));

				String key = values.item(value).getChildNodes().item(1).getChildNodes().item(1).getAttributes().getNamedItem(ReqIFConst.KEY).getTextContent();
				String otherContent;
				if (values.item(value).getChildNodes().item(1).getChildNodes().item(1).getAttributes().getNamedItem(ReqIFConst.OTHER_CONTENT) != null) {
					otherContent = values.item(value).getChildNodes().item(1).getChildNodes().item(1).getAttributes().getNamedItem(ReqIFConst.OTHER_CONTENT).getTextContent();
				}else{
					otherContent = "";
				}
				
				enumValues.put(identifier, new DatatypeEnumerationValue(identifier, longName,lastChange1, key,
																		otherContent));
			}
		}
	}

}
