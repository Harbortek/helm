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

import com.harbortek.helm.productline.service.ReportGroupService;
import com.harbortek.helm.productline.service.ReportService;
import com.harbortek.helm.productline.vo.ReportGroupVo;
import com.harbortek.helm.productline.vo.ReportVo;
import com.harbortek.helm.tracker.constants.DatasetConstants;
import com.harbortek.helm.tracker.constants.SystemVariables;
import com.harbortek.helm.tracker.vo.smartpage.SystemVariableVo;
import com.harbortek.helm.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/product-line/{productLineId}/report")
public class ReportApi {

    @Autowired
    ReportGroupService reportGroupService;
    @Autowired
    ReportService reportService;

    @Parameter(name="查询报表列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<ReportGroupVo>> findReports(@PathVariable Long productLineId,
                                                       @RequestParam(required = false, defaultValue = "") String keyword) {

        Collection<ReportGroupVo> groups = reportGroupService.findReportGroups(productLineId);
        Map<Long, ReportGroupVo> groupVoMap =
                groups.stream().collect(Collectors.toMap(ReportGroupVo::getId, Function.identity()));

        Collection<ReportVo> reports = reportService.findReports(productLineId, keyword);
        reports.forEach(report -> {
            ReportGroupVo groupVo = groupVoMap.get(report.getGroupId());
            if (groupVo != null) {
                groupVo.getReports().add(report);
            }
        });

        return ResponseEntity.ok(groups);
    }

    @Parameter(name="查询一个报表")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<ReportVo> findOneReport(@PathVariable Long productLineId,@PathVariable Long id) {
        ReportVo report = reportService.findOneReport(id);
        report.setSystemVariables(Arrays.asList(
                SystemVariableVo.builder().id(SystemVariables.CURRENT_PRODUCT_LINE_ID).name("当前产品线ID").type(
                        DatasetConstants.COLUMN_NUMBER).value(String.valueOf(productLineId)).build(),
                SystemVariableVo.builder().id(SystemVariables.CURRENT_USER_ID).name("当前用户ID").type(
                        DatasetConstants.COLUMN_NUMBER).value(String.valueOf(SecurityUtils.getCurrentUser().getId())).build()
                     ));
        return ResponseEntity.ok(report);
    }

    @Parameter(name="创建一个报表")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<ReportVo> createReport(@PathVariable Long productLineId,@RequestBody ReportVo product) {
        product.setProductLineId(productLineId);
        ReportVo result = reportService.createReport(product);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个报表")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<ReportVo> updateReport(@PathVariable Long productLineId,@RequestBody ReportVo product) {
        product.setProductLineId(productLineId);
        ReportVo result = reportService.updateReport(product);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个报表")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteReport(@PathVariable Long productLineId,@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok().build();
    }
}
