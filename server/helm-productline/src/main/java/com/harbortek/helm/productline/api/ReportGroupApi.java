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

package com.harbortek.helm.productline.api;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.harbortek.helm.productline.service.ReportGroupService;
import com.harbortek.helm.productline.vo.ReportGroupVo;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping(value = "/product-line/{productLineId}/reportGroup")
public class ReportGroupApi {
    @Autowired
    ReportGroupService reportGroupService;

    @Parameter(name="查询报表分组列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<ReportGroupVo>> findReportGroups(@PathVariable Long productLineId) {
        Collection<ReportGroupVo> products = reportGroupService.findReportGroups(productLineId);
        return ResponseEntity.ok(products);
    }

    @Parameter(name="查询一个报表分组")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<ReportGroupVo> findOneReportGroup(@PathVariable Long productLineId,@PathVariable Long id) {
        ReportGroupVo product = reportGroupService.findOneReportGroup(id);
        return ResponseEntity.ok(product);
    }

    @Parameter(name="创建一个报表分组")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<ReportGroupVo> createReportGroup(@PathVariable Long productLineId,@RequestBody ReportGroupVo reportGroupVo) {
        reportGroupVo.setProductLineId(productLineId);
        ReportGroupVo result = reportGroupService.createReportGroup(reportGroupVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个报表分组")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<ReportGroupVo> updateReportGroup(@PathVariable Long productLineId,@RequestBody ReportGroupVo reportGroupVo) {
        reportGroupVo.setProductLineId(productLineId);
        ReportGroupVo result = reportGroupService.updateReportGroup(reportGroupVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个报表分组")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteReportGroup(@PathVariable Long productLineId,@PathVariable Long id) {
        reportGroupService.deleteReportGroup(id);
        return ResponseEntity.ok().build();
    }
}
