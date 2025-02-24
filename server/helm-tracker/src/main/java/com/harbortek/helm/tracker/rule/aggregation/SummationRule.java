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
import java.util.Iterator;

@FieldRule(id= Rules.RULE_ID_SUM_TOTAL,name=Rules.RULE_NAME_SUM_TOTAL,description = "Set parent value to sum/total of child values")
public class SummationRule extends AggregationRule {
	public SummationRule(String id, String name, String description) {
		super(id, name, description, FieldTypes.NUMERIC_TYPES);
	}

	public void aggregate(TrackerItemEntity parent, TrackerField field, Collection<TrackerItemEntity> children) {
		if (parent != null && field != null && field.getId() != null && this.isApplicable(field)) {
			Object total = this.aggregate(field, children);
			//parent.setFieldValue(field, total);
			if (children != null && children.size() > 0 && total != null && (field.getId() == 10 || field.getId() == 11 || field.getId() == 19)) {
				Iterator iterator = children.iterator();

				while(iterator.hasNext()) {
					TrackerItemEntity child = (TrackerItemEntity)iterator.next();
//					if ((child = (TrackerItemEntity)child) != null &&  child.getTrackerId() == parent.getTrackerId() && child.getFieldValue(field) != null) {
//						break;
//					}
				}

//				if (field.getId() == 11) {
//					long totalAccruedMillis = 0L;
//					Iterator itemIterator = children.iterator();
//
//					while(itemIterator.hasNext()) {
//						TrackerItemEntity child = (TrackerItemEntity)itemIterator.next();
//						if ((child = (TrackerItemEntity)child) != null && TrackerTypeDto.WORKLOG.isInstance(child.getTracker()) && child.getSpentMillis() != null) {
//							totalAccruedMillis += child.getSpentMillis();
//						}
//					}
//
//					parent.setAccruedMillis(totalAccruedMillis > 0L ? totalAccruedMillis : null);
//				}
			}
		}

	}

	protected Object aggregate(TrackerField field, Collection<TrackerItemEntity> children) {
		boolean isInt = field.getInputType() == FieldTypes.INTEGER;
		boolean isLong = field.getInputType() == FieldTypes.DECIMAL;
		boolean hasVal = false;
		int intSum = 0;
		long lngSum = 0L;
		double dblSum = 0.0D;
		Number result = null;
		Iterator var12 = children.iterator();

		while(var12.hasNext()) {
			TrackerItemEntity child = (TrackerItemEntity)var12.next();
			if ((child = (TrackerItemEntity)child) != null) {
				Object childValue = child.getFieldValue(field);
				if (childValue instanceof Number) {
					Number number = (Number)childValue;
					if (isInt) {
						intSum += number.intValue();
					} else if (isLong) {
						lngSum += number.longValue();
					} else {
						dblSum += number.doubleValue();
					}

					hasVal = true;
				}
			}
		}

		if (hasVal) {
			if (isInt) {
				result = intSum;
			} else if (isLong) {
				result = lngSum;
			} else {
				result = dblSum;
			}
		}

		return result;
	}
}
