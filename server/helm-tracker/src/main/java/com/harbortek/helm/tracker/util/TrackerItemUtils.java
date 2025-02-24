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

package com.harbortek.helm.tracker.util;

import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.util.JsonUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrackerItemUtils {
	public static boolean listContainsAll(Collection<TrackerItemEntity> toTest, Collection<TrackerItemEntity> toContain) {
		return toContain.stream().allMatch((value) -> {
			return listContainsValue(toTest, value);
		});
	}

	public static boolean listContainsValue(Collection<TrackerItemEntity> toTest, TrackerItemEntity value) {
		return toTest.contains(value);
	}

	public static List<TrackerItemEntity> listRetainAll(Collection<TrackerItemEntity> base, Collection<TrackerItemEntity> toKeep) {
		return base.stream().filter((baseElement) -> {
			return listContainsValue(toKeep, baseElement);
		}).collect(Collectors.toList());
	}

    public static void setTableFieldValue(TrackerItemVo trackerItem, Long tableFieldId, List<Map<Long, String>> columnMapList) {
		trackerItem.getValues().put(tableFieldId, JsonUtils.toJSONString(columnMapList));
    }
}
