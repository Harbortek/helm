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

import com.harbortek.helm.productline.vo.IdentityByGroupVo;
import com.harbortek.helm.productline.vo.ProductLineVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;

import java.util.Collection;
import java.util.List;

public interface ProductLineService {

    /**
     * 创建产品线。
     *
     * @param baseline 产品线值对象
     * @return 创建的产品线值对象
     */
    ProductLineVo createProductLine(ProductLineVo baseline);

    /**
     * 更新产品线。
     *
     * @param baseline 产品线值对象
     * @return 更新的产品线值对象
     */
    ProductLineVo updateProductLine(ProductLineVo baseline);

    /**
     * 删除产品线。
     *
     * @param id 产品线的 ID
     */
    void deleteProductLine(Long id);

    /**
     * 查找单个产品线。
     *
     * @param id 产品线的 ID
     * @return 产品线值对象
     */
    ProductLineVo findOneProductLine(Long id);

    /**
     * 查找产品线。
     *
     * @param keyword 关键字
     * @return 产品线值对象集合
     */
    Collection<ProductLineVo> findProductLines(String keyword);

    /**
     * 查找权限授予。
     *
     * @param productLineId 产品线的 ID
     * @return 权限授予值对象列表
     */
    List<PermissionGrantVo> findPermissionGrants(Long productLineId);

    /**
     * 查找角色和成员。
     *
     * @param productLineId 产品线的 ID
     * @param scope 范围
     * @return 角色和成员值对象
     */
    IdentityByGroupVo findRolesAndMembers(Long productLineId, String scope);
}