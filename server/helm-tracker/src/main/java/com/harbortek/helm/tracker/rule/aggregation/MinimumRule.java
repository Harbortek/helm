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

package com.harbortek.helm.tracker.rule.aggregation;

import com.harbortek.helm.tracker.anotation.FieldRule;
import com.harbortek.helm.tracker.constants.FieldTypes;
import com.harbortek.helm.tracker.constants.Rules;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.util.CompareUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@FieldRule(id= Rules.RULE_ID_MINIMUM,name=Rules.RULE_NAME_MINIMUM,description = "Set parent value to smallest child value")
public class MinimumRule extends AggregationRule {
	protected MinimumRule(String id, String name, String description, Set supportedTypes) {
		super(id, name, description, supportedTypes);
	}

	public MinimumRule(String id, String name, String description) {
		this(id, name, description, FieldTypes.ORDERED_TYPES);
	}

	protected Object aggregate(TrackerField field, Collection<TrackerItemEntity> children) {
		Object minValue = null;
		boolean first = true;
		Iterator<TrackerItemEntity> iterator = children.iterator();

		while(iterator.hasNext()) {
			TrackerItemEntity child = iterator.next();
			if (child != null) {
				Object childValue = child.getFieldValue(field);
				if (first) {
					minValue = childValue;
					first = false;
				} else if ( CompareUtils.compare(childValue, minValue)<0) {
					minValue = childValue;
				}
			}
		}

		return minValue;
	}
}
