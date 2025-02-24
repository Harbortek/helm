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

package com.harbortek.helm.system.service.impl;

import com.harbortek.helm.system.dao.EnumCategoryDao;
import com.harbortek.helm.system.dao.EnumDao;
import com.harbortek.helm.system.entity.EnumCategoryEntity;
import com.harbortek.helm.system.entity.EnumItemEntity;
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.vo.EnumCategoryVo;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.ScriptExecuteResult;
import com.harbortek.helm.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.script.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@Service
@Slf4j
public class EnumServiceImpl implements EnumService {
	@Autowired
	private EnumDao enumDao;

	@Autowired
	private EnumCategoryDao enumCategoryDao;




	@Override
	public Page<EnumCategoryVo> findEnumCategories(String keyword, Long projectId, Pageable pageable) {
		Page<EnumCategoryEntity>  entities = enumCategoryDao.findEnumCategories(keyword, projectId, pageable);
		return DataUtils.toVo(entities, EnumCategoryVo.class);
	}

	@Override
	public void createEnumCategory(EnumCategoryVo categoryVo) {
		EnumCategoryEntity categoryEntity = DataUtils.toEntity(categoryVo, EnumCategoryEntity.class);
		if (categoryEntity.getId() == null){
			categoryEntity.setId(IDUtils.getId());
		}

		enumCategoryDao.createEnumCategory(categoryEntity);
		LogUtils.log("系统管理", "枚举值管理", "枚举值分类 {0} 增加成功",categoryEntity.getName());
	}

	@Override
	public void updateEnumCategory(EnumCategoryVo categoryVo) {
		EnumCategoryEntity categoryEntity = DataUtils.toEntity(categoryVo, EnumCategoryEntity.class);
		EnumCategoryEntity oldEnum = enumCategoryDao.findById(categoryEntity.getId());
		BeanCopyUtils.copyWithoutNullProperties(categoryEntity, oldEnum);
		enumCategoryDao.updateEnumCategory(oldEnum);
		LogUtils.log("系统管理", "枚举值管理", "枚举值分类 {0} 修改成功",categoryEntity.getName());
	}

	@Override
	public void deleteEnumCategory(Long categoryId) {
		enumDao.deleteByCategoryId(categoryId);
		enumCategoryDao.deleteEnumCategory(categoryId);
	}

	@Override
	public Boolean existsCategoryCode(String code) {
		return enumCategoryDao.existCategory(code);
	}

	@Override
	public Boolean existsCategoryName(Long projectId, String name) {
		return enumCategoryDao.checkExistsByName(projectId,name);
	}

	@Override
	public List<EnumItemVo> findEnumItems(Long categoryId, Long projectId) {
		EnumCategoryEntity enumCategoryEntity = enumCategoryDao.findById(categoryId);
		List<EnumItemEntity> enums = new ArrayList<>();
		if (enumCategoryEntity.getDynamic() && StringUtils.isNotEmpty(enumCategoryEntity.getScript())){
			enums = getDynamicEnumItems(projectId, enumCategoryEntity.getScript());
		}else {
			enums = enumDao.findEnums(categoryId, projectId);
		}

		return DataUtils.toVo(enums, EnumItemVo.class);
	}

	@Override
	public void updateEnumItems(Long categoryId, Long projectId, List<EnumItemVo> enumVos) {
		Collection<EnumItemEntity> entities = DataUtils.toEntity(enumVos, EnumItemEntity.class);
		enumDao.updateEnums(categoryId,projectId,entities);
	}

	@Override
	public void batchCreateEnumItems(List<EnumItemVo> enumVos) {
		Collection<EnumItemEntity> entities = DataUtils.toEntity(enumVos, EnumItemEntity.class);
		enumDao.batchCreateEnums(entities);
	}

	@Override
	public List<EnumItemVo> findEnumItemsByCode(Long projectId, String code) {
		EnumCategoryEntity category = enumCategoryDao.findByCode(projectId, code);
		if (category == null){
			return new ArrayList<>();
		}
		return DataUtils.toVo(enumDao.findEnums(category.getId(), projectId), EnumItemVo.class);
	}

	@Override
	public EnumItemVo findOneEnumItemById(Long enumId) {
		return DataUtils.toVo(enumDao.findById(enumId), EnumItemVo.class);
	}

	@Override
	public EnumItemVo findOneEnumItemByCode(Long projectId, String categoryCode, String itemCode) {
		EnumCategoryEntity category = enumCategoryDao.findByCode(projectId,categoryCode);
		return DataUtils.toVo(enumDao.findByCode( category.getId(),itemCode), EnumItemVo.class);
	}

	@Override
	public ScriptExecuteResult testScript(Long categoryId, Long projectId, String script) {
		ScriptExecuteResult scriptExecuteResult = new ScriptExecuteResult();
		scriptExecuteResult.setData(new ArrayList<>());
		ScriptEngine jsEngine =createScriptEngine();

		jsEngine.put("context", SpringContextUtil.getApplicationContext());
		jsEngine.put("projectId", projectId);
		try {
			jsEngine.eval(script);
			Object result = jsEngine.eval("values()");
			if (result instanceof List){
				List list = (List) result;
				scriptExecuteResult.setData( DataUtils.toVo(list, EnumItemVo.class) );
			}
		} catch (ScriptException e) {
            log.error("脚本执行失败",e);
            scriptExecuteResult.setErrorMessage(e.getMessage());
		}
		return scriptExecuteResult;
	}

	@Override
	public Long findDefaultEnumItemByCode(Long projectId, String categoryCode) {
		EnumCategoryEntity category = enumCategoryDao.findByCode(projectId, categoryCode);
		List<EnumItemEntity> items = enumDao.findEnums(category.getId(), projectId);
		for (EnumItemEntity item : items) {
			//TODO 判断是否是默认值
            if (true){
				return item.getId();
			}
        }
		return null;
	}

	private List<EnumItemEntity> getDynamicEnumItems(Long projectId,String script){
		ScriptEngine jsEngine =createScriptEngine();

		jsEngine.put("context", SpringContextUtil.getApplicationContext());
		jsEngine.put("projectId", projectId);
		try {
			jsEngine.eval(script);
			Object result = jsEngine.eval("values()");
			if (result instanceof List){
				List list = (List) result;
				return  DataUtils.toVo(list, EnumItemEntity.class);
			}
		} catch (ScriptException e) {
			log.error("脚本执行失败",e);
		}
		return  new ArrayList<>();
	}
	private ScriptEngine createScriptEngine() {

		ScriptEngineManager engineManager = new ScriptEngineManager();
		ScriptEngine jsEngine = engineManager.getEngineByName("graal.js");

		Bindings bindings = jsEngine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("polyglot.js.allowHostAccess", true);
		bindings.put("polyglot.js.allowNativeAccess", false);
		bindings.put("polyglot.js.allowCreateThread", false);
		bindings.put("polyglot.js.allowIO", true);
		bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
		bindings.put("polyglot.js.allowHostClassLoading", true);
		bindings.put("polyglot.js.allowAllAccess", true);
		bindings.put("polyglot.js.ecmascript-version", "2022");
		bindings.put("polyglot.engine.WarnInterpreterOnly",false);
		return jsEngine;
	}

}
