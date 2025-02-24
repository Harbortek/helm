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
 * Deny closing a parent, as long as all child values are open
 */
@Data
@FieldRule(id= Rules.RULE_ID_CLOSE_RESTRICTED,name=Rules.RULE_NAME_CLOSE_RESTRICTED,description = "On parent close, all children must be closed")
public class CloseOnlyIfAllChildrenClosedRule extends DistributionRule{
	public CloseOnlyIfAllChildrenClosedRule(String id, String name, String description) {
		super(id, name, description);
	}
}
