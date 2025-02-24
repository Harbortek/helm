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

package com.harbortek.helm.system.service;

import com.harbortek.helm.system.vo.EnumCategoryVo;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.ScriptExecuteResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnumService {
	Page<EnumCategoryVo> findEnumCategories(String keyword, Long projectId, Pageable pageable);

	void createEnumCategory(EnumCategoryVo categoryVo);

	void updateEnumCategory(EnumCategoryVo categoryVo);

	void deleteEnumCategory(Long categoryId);

	Boolean existsCategoryCode(String code);

	Boolean existsCategoryName(Long projectId, String name);


	List<EnumItemVo> findEnumItems(Long categoryId, Long projectId);

	void updateEnumItems(Long categoryId, Long projectId, List<EnumItemVo> enums);

	void batchCreateEnumItems(List<EnumItemVo> list);

	List<EnumItemVo> findEnumItemsByCode(Long currentProjectId, String code);

    EnumItemVo findOneEnumItemById(Long enumId);

    EnumItemVo findOneEnumItemByCode(Long projectId, String categoryCode, String itemCode);

	ScriptExecuteResult testScript(Long categoryId, Long projectId, String script);

    Long findDefaultEnumItemByCode(Long projectId, String categoryCode);
}
