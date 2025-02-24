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
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;

import java.util.Collection;
import java.util.Collections;

@FieldRule(id= Rules.RULE_ID_CLOSE_UPWARDS,name=Rules.RULE_NAME_CLOSE_UPWARDS,description = "Close parent after closing last child")
public class CloseParentAfterLastChildCloseRule extends AggregationRule {
	public CloseParentAfterLastChildCloseRule(String id, String name, String description) {
		super(id, name, description, Collections.EMPTY_SET);
	}

	public boolean isApplicable(TrackerField field) {
		//TODO
		return field != null && field.getId() != null && field.getId() == 7;
	}

	@Override
	protected Object aggregate(TrackerField field, Collection<TrackerItemEntity> children) {
		return null;
	}

	public void aggregate(TrackerItemEntity parent, TrackerField field, Collection children) {


	}


}
