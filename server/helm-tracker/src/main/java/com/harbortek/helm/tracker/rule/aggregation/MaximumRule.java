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

@FieldRule(id= Rules.RULE_ID_MAXIMUM,name=Rules.RULE_NAME_MAXIMUM,description = "Set parent value to largest child value")
public class MaximumRule extends AggregationRule {
	public MaximumRule(String id, String name, String description) {
		super(id, name, description, FieldTypes.ORDERED_TYPES);
	}

	protected Object aggregate(TrackerField field, Collection children) {
		Object maxValue = null;
		boolean first = true;
		Iterator iterator = children.iterator();

		while(iterator.hasNext()) {
			TrackerItemEntity child = (TrackerItemEntity)iterator.next();
			if (child != null) {
				Object childValue = child.getFieldValue(field);
				if (first) {
					maxValue = childValue;
					first = false;
				} else if (CompareUtils.compare(childValue, maxValue) > 0) {
					maxValue = childValue;
				}
			}
		}

		return maxValue;
	}
}
