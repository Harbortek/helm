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

import com.harbortek.helm.tracker.dao.TrackerCommitDao;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.entity.code.TrackerCommitEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.service.TrackerCommitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("trackerCommitService")
@Slf4j
public class TrackerCommitServiceImpl implements TrackerCommitService {

    @Autowired
    private TrackerCommitDao trackerCommitDao;

    @Autowired
    private TrackerItemDao trackerItemDao;

    @Override
    public void createCommits(List<TrackerCommitEntity> commitEntities) {
        trackerCommitDao.createCommits(commitEntities);
    }

    @Override
    public List<TrackerCommitEntity> findByItemId(Long itemId) {
        return trackerCommitDao.findByItemId(itemId);
    }

    @Override
    public List<TrackerCommitEntity> findBySprintId(Long sprintId) {
        if(sprintId == null){
            return null;
        }
        List<TrackerItemEntity> trackerItemEntities = trackerItemDao.findBySprintIds(Arrays.asList(sprintId));
        List<Long> itemIds = trackerItemEntities.stream().map(item -> item.getId()).collect(Collectors.toList());
        return trackerCommitDao.findByItemIds(itemIds);
    }

}
