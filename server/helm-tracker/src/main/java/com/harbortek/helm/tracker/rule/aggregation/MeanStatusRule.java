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
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;

import java.util.Collection;
import java.util.Set;

@FieldRule(id= Rules.RULE_ID_MEAN_STATUS,name=Rules.RULE_NAME_MEAN_STATUS,description = "Set parent status to mean children status")
public class MeanStatusRule extends MinimumRule {
	public MeanStatusRule(String id, String name, String description) {
		super(id, name, description, (Set)null);
	}

	public Set getSupportedTypes() {
		return super.getSupportedTypes();
	}

	public boolean isApplicable(TrackerField field) {
		return field != null && field.getId() != null && field.getId() == 7;
	}

	protected Object aggregate(TrackerField field, Collection children) {
		Object minStatus = super.aggregate(field, children);
		return minStatus;
	}
}
