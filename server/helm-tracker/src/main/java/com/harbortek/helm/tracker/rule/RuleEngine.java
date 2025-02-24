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

package com.harbortek.helm.tracker.rule;

import com.harbortek.helm.tracker.anotation.FieldRule;
import com.harbortek.helm.tracker.rule.aggregation.AggregationRule;
import com.harbortek.helm.tracker.rule.distribution.DistributionRule;
import lombok.Synchronized;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class RuleEngine{
	private static Map<String, AggregationRule> aggregationRuleMap =  new HashMap<>();

	private static Map<String, DistributionRule> distributionRuleMap =  new HashMap<>();

	private static boolean initialized = false;

	@Synchronized
	public static void init() {
		// init modules
		if (initialized){
			return;
		}

		Reflections reflections = new Reflections("com.harbortek.helm.tracker.rule");

		Set<Class<?>> types = reflections
				.getTypesAnnotatedWith(FieldRule.class);
		types.stream()
				.forEach(clazz -> {
					String name = clazz.getAnnotation(FieldRule.class).name();
					String id = clazz.getAnnotation(FieldRule.class).id();
					String description = clazz.getAnnotation(FieldRule.class).description();
					if (!aggregationRuleMap.containsKey(name) || !distributionRuleMap.containsKey(name)){
						try {
							Constructor constructor = clazz.getConstructor(String.class, String.class, String.class);
							Object r = constructor.newInstance(id,name,description);
							if (r instanceof AggregationRule){
								AggregationRule rule = (AggregationRule)r;
								aggregationRuleMap.put(rule.getName(), rule );
							}else if (r instanceof DistributionRule){
								DistributionRule rule = (DistributionRule)r;
								distributionRuleMap.put(rule.getName(),rule);
							}
						} catch (InstantiationException | NoSuchMethodException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				});
		initialized = true;
	}

	public static AggregationRule getAggregationRule(String ruleName){
		init();
		return aggregationRuleMap.get(ruleName);
	}

	public static DistributionRule getDistributionRule(String ruleName){
		init();
		return distributionRuleMap.get(ruleName);
	}
}
