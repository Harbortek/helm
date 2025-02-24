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
import com.harbortek.helm.tracker.constants.Rules;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.util.TrackerItemUtils;
import com.harbortek.helm.tracker.vo.tracker.fields.ChoiceField;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.util.CompareUtils;

import java.util.*;

@FieldRule(id= Rules.RULE_ID_INTERSECTION,name=Rules.RULE_NAME_INTERSECTION,description = "Set parent value to the intersection of child values")
public class IntersectionRule extends AggregationRule {
	public IntersectionRule(String id, String name, String description) {
		super(id, name, description, Collections.EMPTY_SET);
	}

	public boolean isApplicable(TrackerField field) {
		return field != null && field.isChoiceField() && ((ChoiceField)field).getMultiple();
	}

	protected List toList(Object value) {
		if (value instanceof Collection) {
			return (List)value;
		} else {
			return  Collections.singletonList(value);
		}
	}

	protected Object aggregate(TrackerField field, Collection<TrackerItemEntity> children) {
		List intersection = null;
		Iterator iterator = children.iterator();

		while(iterator.hasNext()) {
			TrackerItemEntity child = (TrackerItemEntity)iterator.next();
			if (child != null) {
				Collection childValues = this.toList(child.getFieldValue(field));
				if (childValues != null && !childValues.isEmpty()) {
					if (intersection == null) {
						intersection = new ArrayList(childValues);
					} else {
						intersection = TrackerItemUtils.listRetainAll((Collection)intersection, childValues);
					}
				}

				if (intersection == null || ((List)intersection).isEmpty()) {
					break;
				}
			}
		}

		if (intersection != null && ((List)intersection).size() > 1) {
			CompareUtils.sort((List)intersection);
		}

		return intersection;
	}
}

