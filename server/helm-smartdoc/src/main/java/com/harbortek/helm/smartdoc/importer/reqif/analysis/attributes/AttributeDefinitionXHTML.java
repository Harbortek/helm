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
import org.w3c.dom.Node;

import java.util.Map;

public class AttributeDefinitionXHTML extends AttributeDefinition {
	public AttributeDefinitionXHTML(String id, String name, Datatype dataType) {
		super(id, name, dataType);
	}

	public AttributeDefinitionXHTML(Node attributeDefinition, Map<String, Datatype> dataTypes) {
		super(attributeDefinition, dataTypes);
		
		
	}

}
