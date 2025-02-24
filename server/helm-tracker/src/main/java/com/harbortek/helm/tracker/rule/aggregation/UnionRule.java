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

import java.util.Collections;

@FieldRule(id= Rules.RULE_ID_UNION,name=Rules.RULE_NAME_UNION,description = "Set parent value to the union of child values")
public class UnionRule extends AggregationRule {

	public UnionRule(String id, String name, String description) {
		super(id, name, description, Collections.EMPTY_SET);
	}
}
