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

import java.util.Collection;

@FieldRule(id= Rules.RULE_ID_AVERAGE,name=Rules.RULE_NAME_AVERAGE,description = "Set parent value to average of child values")
public class AverageRule extends SummationRule {
	public AverageRule(String id, String name, String description) {
		super(id, name, description);
	}

	protected Object aggregate(TrackerField field, Collection<TrackerItemEntity> children) {
		boolean isInt = field.getInputType() == FieldTypes.INTEGER;
		boolean isLong = field.getInputType() == FieldTypes.DECIMAL;
		Number total = (Number)super.aggregate(field, children);
		if (total != null && children.size() > 1) {
			if (isInt) {
				return total.intValue() / children.size();
			} else {
				return isLong ? total.longValue() / (long)children.size() : total.doubleValue() / (double)children.size();
			}
		} else {
			return total;
		}
	}
}
