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

import com.harbortek.helm.smartdoc.vo.ReqIFExportJobVo;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface ReqIFExportJobService {

    /**
     * 查找 ReqIF 文件。
     *
     * @param filePath 文件路径
     * @return 文件名列表
     */
    List<String> findReqIFFiles(String filePath);

    /**
     * 导出 ReqIF 文件。
     *
     * @param pageId 页面的 ID
     * @param job 导出作业值对象
     * @param response HTTP 响应
     */
    void export(Long pageId, ReqIFExportJobVo job, HttpServletResponse response);

    /**
     * 加载导出作业。
     *
     * @param pageId 页面的 ID
     * @return 导出作业值对象
     */
    ReqIFExportJobVo loadExportJob(Long pageId);

    /**
     * 保存导出作业。
     *
     * @param pageId 页面的 ID
     * @param job 导出作业值对象
     */
    void saveExportJob(Long pageId, ReqIFExportJobVo job);
}