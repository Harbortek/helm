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

import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Set;

/**
 * Set aggregation rules when the parent field values of an item depend on child values.
 *
 * Aggregation rules can be set under the Aggregation rule column on the Field Configuration page.
 */
@Data
@NoArgsConstructor
public class AggregationRule {
	String id;
	String name;
	String description;
	Set<String> supportedTypes;

	public AggregationRule(String id, String name, String description, Set<String> supportedTypes) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.supportedTypes = supportedTypes;
	}


	public boolean isApplicable(TrackerField field) {
		return field != null ? this.supports(field.getInputType()) : false;
	}

	private boolean supports(String inputType) {
		return supportedTypes.contains(inputType);
	}

	public void aggregate(TrackerItemEntity parent, TrackerField field, Collection<TrackerItemEntity> children) {
		if (parent != null && field != null && field.getId() != null && this.isApplicable(field)) {
			//parent.setFieldValue(field, this.aggregate(field, children));
		}

	}

	protected Object aggregate(TrackerField field, Collection<TrackerItemEntity> children){
		return null;
	}
}
