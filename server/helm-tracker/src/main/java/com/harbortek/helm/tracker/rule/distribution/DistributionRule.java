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

/*
 * 项目名称:浩博ELM协同平台
 * Copyright 南京浩博科技有限公司 2023 版权所有
 *
 */

package com.harbortek.helm.tracker.rule.distribution;

import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * For users with parent/child hierarchies of configuration and work items,
 * define dependencies between parent and child field values.
 *
 * To ensure that parent items are not closed until all child items are closed,
 * use distribution rules under the Distribution rule column on the Field Configuration page.
 */
@Data
@NoArgsConstructor
public class DistributionRule {
	String id;
	String name;
	String description;
	Set<String> supportedTypes;

	public DistributionRule(String id, String name, String description, Set<String> supportedTypes){
		this.id = id;
		this.name = name;
		this.description = description;
		this.supportedTypes = supportedTypes;
	}

	public DistributionRule(String id, String name, String description){
		this.id = id;
		this.name = name;
		this.description = description;
		this.supportedTypes = Collections.EMPTY_SET;
	}


	public boolean isApplicable(TrackerField field) {
		return field != null ? this.supports(field.getInputType()) : false;
	}

	private boolean supports(String inputType) {
		return supportedTypes.contains(inputType);
	}


	public void distribute(TrackerItemEntity parent, TrackerField field, Collection children) {
		if (parent != null && field != null && field.getId() != null && this.isApplicable(field) && children != null && !children.isEmpty()) {
			this.distribute(field, parent.getFieldValue(field), children);
		}

	}

	protected void distribute(TrackerField field, Object value, Collection children) {
		Iterator iterator = children.iterator();

		while(iterator.hasNext()) {
			TrackerItemEntity child = (TrackerItemEntity)iterator.next();
			if ((child = (TrackerItemEntity)child) != null) {
				child.setFieldValue(field, value);
			}
		}

	}
}
