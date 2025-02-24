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

package com.harbortek.helm.smartpage.api;

import com.harbortek.helm.smartpage.service.DatasetService;
import com.harbortek.helm.smartpage.vo.DatasetPreviewRequest;
import com.harbortek.helm.tracker.vo.smartpage.DatasetVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/smartPage/{pageId}/dataset")
public class DatasetApi {
    @Autowired
    DatasetService datasetService;

    @Parameter(name="查询预览数据")
    @PostMapping("/preview")
    public ResponseEntity<DatasetVo> getSQLPreview(@PathVariable Long pageId,
                                                   @RequestBody DatasetPreviewRequest request) {

        return ResponseEntity.ok(datasetService.preview(pageId, request));
    }


    @Parameter(name="查询数据集列表接口")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<DatasetVo>> findDatasets(@PathVariable Long pageId) {
        Collection<DatasetVo> DataSetVos = datasetService.findByPageId(pageId);
        return ResponseEntity.ok(DataSetVos);
    }

    @Parameter(name="查询一个数据集")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<DatasetVo> findOneDataset(@PathVariable Long pageId, @PathVariable Long id) {
        DatasetVo DataSetVo = datasetService.findById(pageId, id);
        return ResponseEntity.ok(DataSetVo);
    }

    @Parameter(name="创建一个数据集")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<DatasetVo> createDataset(@PathVariable Long pageId, @RequestBody DatasetPreviewRequest request) {
        DatasetVo result = datasetService.createDataset(pageId, request);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个数据集")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<DatasetVo> updateDataset(@PathVariable Long pageId, @RequestBody DatasetPreviewRequest request) {
        DatasetVo result = datasetService.updateDataset(pageId, request);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="重命名一个数据集")
    @RequestMapping(value = "/rename", method = RequestMethod.PUT)
    ResponseEntity<Void> renameDataset(@PathVariable Long pageId, @RequestBody DatasetVo dataset) {
        datasetService.renameDataset(pageId, dataset);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除一个数据集")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteDataset(@PathVariable Long pageId, @PathVariable Long id) {
        datasetService.deleteDataset(pageId, id);
        return ResponseEntity.ok().build();
    }


    @Parameter(name="获取数据集某个字段的枚举值")
    @RequestMapping(value = "/{id}/enum", method = RequestMethod.GET)
    ResponseEntity<List<String>> findEnumValues(@PathVariable Long pageId, @PathVariable Long id,@RequestParam String field) {
        List<String> enumValues = datasetService.findEnumValues(pageId, id, field);
        return ResponseEntity.ok(enumValues);
    }
}
