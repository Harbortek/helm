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

import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.Datatype;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFConst;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.w3c.dom.Node;

import java.util.Map;
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SpecObjectType extends SpecType {
	
	
	public SpecObjectType(Node specType, Map<String, Datatype> dataTypes) {
		super(specType, dataTypes);
		
		this.type = ReqIFConst.SPEC_OBJECT_TYPE;
	}

}
