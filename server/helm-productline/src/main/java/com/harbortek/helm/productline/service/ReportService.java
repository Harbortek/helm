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

package com.harbortek.helm.productline.service;

import com.harbortek.helm.productline.vo.ReportVo;

import java.util.Collection;

public interface ReportService {

    /**
     * 创建报告。
     *
     * @param report 报告值对象
     * @return 创建的报告值对象
     */
    ReportVo createReport(ReportVo report);

    /**
     * 更新报告。
     *
     * @param report 报告值对象
     * @return 更新的报告值对象
     */
    ReportVo updateReport(ReportVo report);

    /**
     * 删除报告。
     *
     * @param id 报告的 ID
     */
    void deleteReport(Long id);

    /**
     * 查找单个报告。
     *
     * @param id 报告的 ID
     * @return 报告值对象
     */
    ReportVo findOneReport(Long id);

    /**
     * 查找报告。
     *
     * @param productLineId 产品线的 ID
     * @param keyword 关键字
     * @return 报告值对象集合
     */
    Collection<ReportVo> findReports(Long productLineId, String keyword);
}