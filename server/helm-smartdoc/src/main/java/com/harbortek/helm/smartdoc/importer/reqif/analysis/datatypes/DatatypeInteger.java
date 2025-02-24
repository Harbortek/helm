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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class DatatypeInteger extends Datatype {
	
	
	private long max;
	private long min;
	

	public DatatypeInteger(String id, String name, Date lastChange, String min, String max) {
		super(id, name,lastChange, ReqIFConst.INTEGER);
		
		this.min = Long.parseLong(min);
		this.max = Long.parseLong(max);
	}

}
