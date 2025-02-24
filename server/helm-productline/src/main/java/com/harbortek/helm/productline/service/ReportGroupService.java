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

import com.harbortek.helm.productline.vo.ReportGroupVo;

import java.util.Collection;

public interface ReportGroupService {

    /**
     * 创建报告组。
     *
     * @param reportGroupVo 报告组值对象
     * @return 创建的报告组值对象
     */
    ReportGroupVo createReportGroup(ReportGroupVo reportGroupVo);

    /**
     * 更新报告组。
     *
     * @param reportGroupVo 报告组值对象
     * @return 更新的报告组值对象
     */
    ReportGroupVo updateReportGroup(ReportGroupVo reportGroupVo);

    /**
     * 删除报告组。
     *
     * @param id 报告组的 ID
     */
    void deleteReportGroup(Long id);

    /**
     * 查找单个报告组。
     *
     * @param id 报告组的 ID
     * @return 报告组值对象
     */
    ReportGroupVo findOneReportGroup(Long id);

    /**
     * 查找报告组。
     *
     * @param productLineId 产品线的 ID
     * @return 报告组值对象集合
     */
    Collection<ReportGroupVo> findReportGroups(Long productLineId);
}