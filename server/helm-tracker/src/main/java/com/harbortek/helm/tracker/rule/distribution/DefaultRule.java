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

package com.harbortek.helm.tracker.rule.distribution;

import com.harbortek.helm.tracker.anotation.FieldRule;
import com.harbortek.helm.tracker.constants.Rules;
import lombok.Data;

/**
 * Set child field value to parent field value, but only if the child field is empty
 */
@Data
@FieldRule(id= Rules.RULE_ID_DEFAULT,name=Rules.RULE_NAME_DEFAULT,description = "Use parent value as default child value")
public class DefaultRule extends DistributionRule{
	public DefaultRule(String id, String name, String description) {
		super(id, name, description);
	}
}
