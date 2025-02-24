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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.harbortek.helm.system.dao.SystemDao;
import com.harbortek.helm.system.entity.SystemEntity;
import com.harbortek.helm.system.service.SystemService;
import com.harbortek.helm.system.vo.SystemVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SystemServiceImpl implements SystemService {
	@Autowired
	private SystemDao systemDao;

	
	public SystemVo createSystem(SystemVo system) {
		system.setId(IDUtils.getId());
		SystemEntity systemEntity = DataUtils.toEntity(system, SystemEntity.class);

		SystemEntity entity = systemDao.create(systemEntity);
		LogUtils.log("系统管理", "系统设置", "系统 {0} 创建成功",system.getName());
		return DataUtils.toVo(entity,SystemVo.class);
	}

	
	public SystemVo updateSystem(SystemVo system) {
		SystemEntity oldSystem = systemDao.findById(system.getId());
		BeanUtil.copyProperties(system, oldSystem,
				CopyOptions.create().ignoreNullValue());
		SystemEntity entity = systemDao.update(oldSystem);
		LogUtils.log("系统管理", "系统设置", "系统 {0} 更新成功",system.getName());
		return DataUtils.toVo(entity,SystemVo.class);
	}

	
	public void deleteSystem(Long systemId) {
		SystemEntity system = systemDao.findById(systemId);
		systemDao.delete(systemId);
		LogUtils.log("系统管理", "系统设置", "系统 {0} 删除成功",system.getName());
	}

	public SystemVo findOneSystem(Long id) {
		return DataUtils.toVo( systemDao.findById(id),SystemVo.class);
	}

	public Page<SystemVo> findSystems(Pageable pageable) {
		return DataUtils.toVo( systemDao.findSystems(pageable), SystemVo.class);
	}

	@Override
	public List<SystemVo> findAllSystems(String keyword) {
		return DataUtils.toVo( systemDao.findAllSystems(keyword), SystemVo.class);
	}


	public Boolean checkExistsByName(String name) {
		return systemDao.checkExistsByName(name);
	}


}
