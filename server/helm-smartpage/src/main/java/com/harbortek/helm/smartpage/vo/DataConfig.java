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

package com.harbortek.helm.smartpage.vo;

import com.harbortek.helm.tracker.vo.smartpage.filter.DataFilter;
import com.harbortek.helm.tracker.vo.smartpage.DatasetField;
import lombok.Data;

import java.util.List;

@Data
public class DataConfig {
    String type;

    Long datasetId;

    List<DatasetField> xaxis;

    List<DatasetField> xaxisExt;

    List<DatasetField> yaxis;

    List<DatasetField> yaxisExt;

    List<DatasetField> extStack;

    List<DatasetField> extBubble;

    List<DataFilter> customFilter;

    List<DatasetField> drillFields;

    int page;
    int pageSize = 20;

    TotalConfig totalConfig;
}
