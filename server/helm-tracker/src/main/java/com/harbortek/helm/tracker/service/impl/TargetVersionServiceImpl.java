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

package com.harbortek.helm.tracker.service.impl;

import com.harbortek.helm.tracker.dao.TargetVersionDao;
import com.harbortek.helm.tracker.entity.plan.TargetVersionEntity;
import com.harbortek.helm.tracker.service.TargetVersionService;
import com.harbortek.helm.tracker.vo.plan.TargetVersionVo;
import com.harbortek.helm.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service("targetVersionService")
@Slf4j
public class TargetVersionServiceImpl implements TargetVersionService {

    @Autowired
    TargetVersionDao versionDao;

    @Override
    public Collection<TargetVersionVo> findVersions(Long projectId) {
        return DataUtils.toVo(versionDao.findAll(projectId), TargetVersionVo.class);
    }

    @Override
    public TargetVersionVo findOneVersion(Long id) {
        return DataUtils.toVo(versionDao.findById(id, TargetVersionEntity.class), TargetVersionVo.class);
    }

    @Override
    public TargetVersionVo createVersion(TargetVersionVo versionVo) {
        TargetVersionEntity entity = DataUtils.toEntity(versionVo, TargetVersionEntity.class);
        entity = versionDao.createVersion(entity);
        return DataUtils.toVo(entity, TargetVersionVo.class);
    }

    @Override
    public TargetVersionVo updateVersion(TargetVersionVo versionVo) {
        TargetVersionEntity entity = DataUtils.toEntity(versionVo, TargetVersionEntity.class);
        entity = versionDao.updateVersion(entity);
        return DataUtils.toVo(entity, TargetVersionVo.class);
    }

    @Override
    public void deleteVersion(Long id) {
        versionDao.deleteVersion(id);
    }
}
