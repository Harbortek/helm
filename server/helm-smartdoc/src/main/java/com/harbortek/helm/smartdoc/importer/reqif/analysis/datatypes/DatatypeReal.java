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
public class DatatypeReal extends Datatype {


	private Double max;
	private Double min;

	private int accuracy;



	public DatatypeReal(String id, String name, Date lastChange, String accuracy, String min, String max) {
		super(id, name,lastChange, ReqIFConst.REAL);
		this.accuracy =  Integer.parseInt(accuracy);
		this.min = Double.parseDouble(min);
		this.max = Double.parseDouble(max);
	}

}
