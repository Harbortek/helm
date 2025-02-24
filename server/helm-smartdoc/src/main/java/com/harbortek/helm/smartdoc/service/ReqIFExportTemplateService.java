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

import com.harbortek.helm.smartdoc.vo.ReqIFExportTemplateVo;

import java.util.Collection;

public interface ReqIFExportTemplateService {

    /**
     * 查找模板。
     *
     * @param projectId 项目的 ID
     * @param keyword 关键字
     * @return 模板值对象集合
     */
    Collection<ReqIFExportTemplateVo> findTemplates(Long projectId, String keyword);

    /**
     * 查找单个模板。
     *
     * @param id 模板的 ID
     * @return 模板值对象
     */
    ReqIFExportTemplateVo findOneTemplate(Long id);

    /**
     * 创建模板。
     *
     * @param configVo 模板值对象
     * @return 创建的模板值对象
     */
    ReqIFExportTemplateVo createTemplate(ReqIFExportTemplateVo configVo);

    /**
     * 更新模板。
     *
     * @param configVo 模板值对象
     * @return 更新的模板值对象
     */
    ReqIFExportTemplateVo updateTemplate(ReqIFExportTemplateVo configVo);

    /**
     * 删除模板。
     *
     * @param id 模板的 ID
     */
    void deleteTemplate(Long id);
}