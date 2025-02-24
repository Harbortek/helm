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

import com.harbortek.helm.smartdoc.importer.word.analysis.WordDocumentProperties;
import com.harbortek.helm.smartdoc.vo.WordImportJobVo;

public interface WordImportJobService {

    /**
     * 分析 Word 导入作业。
     *
     * @param job Word 导入作业值对象
     * @return Word 文档属性
     */
    WordDocumentProperties analysis(WordImportJobVo job);

    /**
     * 查找已存在的作业。
     *
     * @param projectId 项目的 ID
     * @param pageId 页面的 ID
     * @return 已存在的 Word 导入作业值对象
     */
    WordImportJobVo findExistedJob(Long projectId, Long pageId);

    /**
     * 创建新的作业。
     *
     * @param job Word 导入作业值对象
     * @return 创建的 Word 导入作业值对象
     */
    WordImportJobVo createJob(WordImportJobVo job);

    /**
     * 更新作业。
     *
     * @param job Word 导入作业值对象
     * @return 更新的 Word 导入作业值对象
     */
    WordImportJobVo updateJob(WordImportJobVo job);

    /**
     * 完成作业。
     *
     * @param job Word 导入作业值对象
     */
    void completeJob(WordImportJobVo job);

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
     * @return 撤回的 Word 导入作业值对象
     */
    WordImportJobVo withdrawJob(Long id);
}