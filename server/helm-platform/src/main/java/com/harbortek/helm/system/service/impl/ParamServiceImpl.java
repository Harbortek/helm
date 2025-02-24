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

import com.harbortek.helm.system.dao.ParamDao;
import com.harbortek.helm.system.entity.ParamEntity;
import com.harbortek.helm.system.service.ParamService;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ParamServiceImpl implements ParamService {
	private final Logger logger = LoggerFactory.getLogger(ParamServiceImpl.class);
	@Autowired
	private ParamDao paramDao;

	@Override
	public ParamEntity createParam(ParamEntity param) {
		param.setId(IDUtils.getId());

		LogUtils.log("系统管理", "参数管理", "参数 {0} 创建成功",param.getName());
		return paramDao.create(param);
	}

	@Override
	public ParamEntity updateParam(ParamEntity param) {
		LogUtils.log("系统管理", "参数管理", "参数 {0} 更新成功",param.getName());
		return paramDao.update(param);
	}

	@Override
	public void deleteParamById(Long paramId) {
		ParamEntity p = paramDao.findById(paramId);
		p.setId(paramId);
		LogUtils.log("系统管理", "参数管理", "参数 {0} 删除成功",p.getName());
		paramDao.delete(p);
	}

	@Override
	public void batchDelete(List<Long> paramIds) {
		LogUtils.log("系统管理", "参数管理", "参数 {0} 批量删除",paramIds);
		paramDao.batchDelete(paramIds);
	}

	@Override
	public ParamEntity findOneParam(Long paramId) {
		return paramDao.findById(paramId);
	}

	@Override
	public Page<ParamEntity> findParams(String keyword, Pageable pageable) {
		return paramDao.findParams(keyword, pageable);
	}

	@Override
	public Boolean checkExistsByName(String name) {
		return paramDao.checkExistsByName(name);
	}

	@Override
	public Boolean checkExistsByCode(String code) {
		return paramDao.checkExistsByCode(code);
	}

}
