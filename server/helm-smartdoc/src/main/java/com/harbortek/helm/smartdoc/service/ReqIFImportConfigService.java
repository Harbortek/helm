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

import com.harbortek.helm.smartdoc.vo.ReqIFImportConfigVo;

import java.util.Collection;

public interface ReqIFImportConfigService {

    /**
     * 查找配置。
     *
     * @param projectId 项目的 ID
     * @param keyword 关键字
     * @return 配置值对象集合
     */
    Collection<ReqIFImportConfigVo> findConfigs(Long projectId, String keyword);

    /**
     * 查找单个配置。
     *
     * @param id 配置的 ID
     * @return 配置值对象
     */
    ReqIFImportConfigVo findOneConfig(Long id);

    /**
     * 创建配置。
     *
     * @param configVo 配置值对象
     * @return 创建的配置值对象
     */
    ReqIFImportConfigVo createConfig(ReqIFImportConfigVo configVo);

    /**
     * 更新配置。
     *
     * @param configVo 配置值对象
     * @return 更新的配置值对象
     */
    ReqIFImportConfigVo updateConfig(ReqIFImportConfigVo configVo);

    /**
     * 删除配置。
     *
     * @param id 配置的 ID
     */
    void deleteConfig(Long id);
}