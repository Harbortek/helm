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

import com.harbortek.helm.system.dao.LogDao;
import com.harbortek.helm.system.entity.LogEntity;
import com.harbortek.helm.system.service.LogService;
import com.harbortek.helm.system.vo.LogVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LogServiceImpl implements LogService {
	@Autowired
	private LogDao logDao;

	public LogVo createLog(LogVo log) {
		log.setId(log.getId() == null ? IDUtils.getId() : log.getId());
		LogEntity logEntity = DataUtils.toEntity(log, LogEntity.class);
		return DataUtils.toVo(logDao.create(logEntity),LogVo.class) ;
	}

	public LogVo updateLog(LogVo log) {
		LogEntity logEntity = DataUtils.toEntity(log, LogEntity.class);
		return DataUtils.toVo(logDao.update(logEntity),LogVo.class);
	}

	public void deleteLog(LogVo log) {
		logDao.delete(log.getId());
	}

	public LogVo findOneLog(Long logId) {
		return DataUtils.toVo(logDao.findById(logId),LogVo.class);
	}

	public Page<LogVo> findLogs(String keyword, Pageable pageable) {

		return DataUtils.toVo( logDao.findLogs(keyword, pageable),LogVo.class);
	}
}
