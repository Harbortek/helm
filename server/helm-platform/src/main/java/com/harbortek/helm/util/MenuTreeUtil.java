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

package com.harbortek.helm.util;

import com.harbortek.helm.system.vo.OrgVo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuTreeUtil {
	public static Map<String, Object> mapArray = new LinkedHashMap<String, Object>();


	public static List orgList(List<OrgVo> orgList) {
		ArrayList<Map> root = new ArrayList<>();
		for (OrgVo x : orgList) {
			Map<String, Object> mapArr = new LinkedHashMap<String, Object>();
			if (x.getParentId() != null) {
				if (x.getParentId() == 0L) {
					mapArr.put("key", x.getId());
					mapArr.put("title", x.getName());
					List<?> children = menuChild(orgList, x.getId());
					mapArr.put("children", children);
					mapArr.put("group", !children.isEmpty());

					root.add(mapArr);
				}
			}
		}
		return root;
	}

	public static List<?> menuChild(List<OrgVo> orgList, Long parentId) {
		List<Object> lists = new ArrayList<Object>();
		for (OrgVo item : orgList) {
			Map<String, Object> child = new LinkedHashMap<String, Object>();
			if (item.getParentId().equals(parentId)) {
				child.put("key", item.getId());
				child.put("title", item.getName());
				List<?> children = menuChild(orgList, item.getId());
				child.put("children", children);
				child.put("group", !children.isEmpty());

				lists.add(child);
			}
		}
		return lists;
	}

	/**
	 * 根据父部门id，返回所有子部门部门id
	 * @param orgList
	 * @param parentId
	 * @return
	 */
	public static List<Long> ChildOrdId(List<OrgVo> orgList, Long parentId) {
		List<Long> lists = new ArrayList<Long>();
		lists.add(parentId);
		for (OrgVo item : orgList) {
			if (item.getParentId().equals(parentId)) {
				List<Long> children = ChildOrdId(orgList, item.getId());
				lists.addAll(children);
			}
		}
		return lists;
	}


}
