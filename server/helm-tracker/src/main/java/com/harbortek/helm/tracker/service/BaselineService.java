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

package com.harbortek.helm.tracker.service;


import com.harbortek.helm.tracker.entity.document.DocumentHistoryEntity;
import com.harbortek.helm.tracker.vo.baseline.BaselineCompare;
import com.harbortek.helm.tracker.vo.baseline.BaselineVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemHistoryVo;

import java.util.List;

public interface BaselineService {
     BaselineVo createBaseline(BaselineVo baseline);

    void createDocumentVersion(Long documentId, Long projectId);

    BaselineVo updateBaseline(BaselineVo baseline);

     void deleteBaseline(Long id);

     BaselineVo findOneBaseline(Long id);

     List<BaselineVo> findBaselinesByProjectId(Long projectId, String keyword);

     List<BaselineVo> findBaselinesByDocumentId(Long projectId,Long documentId);

    List<BaselineCompare> compareBaseline(Long projectId,Long leftId, Long rightId);

     List<TrackerItemHistoryVo> findItemsHistory(List<Long> historyIds);

    List<DocumentHistoryEntity> findDocumentHistory(List<Long> historyIds);
}
