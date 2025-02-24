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

package com.harbortek.helm.tracker.vo.tracker.fields;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.tracker.constants.FieldTypes;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
@JsonTypeName(value = FieldTypes.SINGLE_OPTIONS)
public class OptionsField extends TrackerField{
	@Builder.Default
	Boolean multiple = false;

	ChoiceField dependsOn;
	Boolean union;

	Long enumId;

	String enumName;

	List<OptionItem> items = new ArrayList<>();
	Map<String,Collection<OptionItem>> allowedValues;
	Map<String,OptionItem> defaultValues;

	@Data
	@AllArgsConstructor
	@SuperBuilder
	@NoArgsConstructor
	public static class OptionItem implements Serializable {
		Long id;
		String name;
		String description;
		String color = "#000000";;

		String backgroundColor = "#FFFFFF";
		String meaning;
	}

}
