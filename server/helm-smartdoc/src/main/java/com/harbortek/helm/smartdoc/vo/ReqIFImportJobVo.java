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

package com.harbortek.helm.smartdoc.vo;

import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.ReqIFDocumentProperties;
import com.harbortek.helm.smartdoc.importer.reqif.rules.ReqIFRule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
public class ReqIFImportJobVo extends BaseVo {
    Long revision;

    Long pageId;
    Long projectId;
    String filePath;

    boolean autoNumber;

    String blocksJSON;

    ReqIFDocumentProperties docProps;

    List<ReqIFRule> rules = new ArrayList<>();
}
