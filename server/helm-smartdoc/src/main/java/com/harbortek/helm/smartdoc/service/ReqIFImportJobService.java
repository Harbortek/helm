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

package com.harbortek.helm.smartdoc.service;

import com.harbortek.helm.smartdoc.importer.reqif.analysis.ReqIFDocumentProperties;
import com.harbortek.helm.smartdoc.vo.ReqIFImportJobVo;

public interface ReqIFImportJobService {

    /**
     * 分析 ReqIF 导入作业。
     *
     * @param job ReqIF 导入作业值对象
     * @return ReqIF 文档属性
     */
    ReqIFDocumentProperties analysis(ReqIFImportJobVo job);

    /**
     * 查找已存在的作业。
     *
     * @param projectId 项目的 ID
     * @param pageId 页面的 ID
     * @return 已存在的 ReqIF 导入作业值对象
     */
    ReqIFImportJobVo findExistedJob(Long projectId, Long pageId);

    /**
     * 创建新的作业。
     *
     * @param job ReqIF 导入作业值对象
     * @return 创建的 ReqIF 导入作业值对象
     */
    ReqIFImportJobVo createJob(ReqIFImportJobVo job);

    /**
     * 更新作业。
     *
     * @param job ReqIF 导入作业值对象
     * @return 更新的 ReqIF 导入作业值对象
     */
    ReqIFImportJobVo updateJob(ReqIFImportJobVo job);

    /**
     * 完成作业。
     *
     * @param job ReqIF 导入作业值对象
     */
    void completeJob(ReqIFImportJobVo job);

    /**
     * 删除作业。
     *
     * @param id 作业的 ID
     */
    void deleteJob(Long id);

    /**
     * 撤回作业。
     *
     * @param id 作业的 ID
     * @return 撤回的 ReqIF 导入作业值对象
     */
    ReqIFImportJobVo withdrawJob(Long id);
}