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

package com.harbortek.helm.smartpage.service;

import com.harbortek.helm.smartpage.vo.DatasetPreviewRequest;
import com.harbortek.helm.tracker.vo.smartpage.filter.DataFilter;
import com.harbortek.helm.tracker.vo.smartpage.DatasetField;
import com.harbortek.helm.tracker.vo.smartpage.DatasetVo;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.Collection;
import java.util.List;

public interface DatasetService {
    DatasetVo preview(Long pageId, DatasetPreviewRequest request);

    SqlRowSet plainSelect(Long pageId, DatasetVo dataset, List<DataFilter> parameters, List<DatasetField> fields,
                          List<DataFilter> filterFields, int page,
                          int pageSize);

    Long count(Long pageId, DatasetVo dataset, List<DataFilter> filterFields, List<DataFilter> parameters);
    SqlRowSet groupBy(Long pageId, DatasetVo dataset, List<DataFilter> parameters, List<DatasetField> groupByFields,
                      List<DatasetField> valueFields, List<DataFilter> filterFields);



    Collection<DatasetVo> findByPageId(Long pageId);

    DatasetVo findById(Long pageId,Long datasetId);

    DatasetVo createDataset(Long pageId,DatasetPreviewRequest request);

    DatasetVo updateDataset(Long pageId,DatasetPreviewRequest request);

    void renameDataset(Long pageId,DatasetVo dataSetVo);

    void deleteDataset(Long pageId,Long datasetId);


    List<String> findEnumValues(Long pageId, Long datasetId, String field);
}
