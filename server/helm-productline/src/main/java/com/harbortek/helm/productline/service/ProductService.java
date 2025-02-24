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

import com.harbortek.helm.productline.vo.ProductVo;
import com.harbortek.helm.tracker.vo.ProjectVo;

import java.util.Collection;
import java.util.List;

public interface ProductService {

    /**
     * 创建产品。
     *
     * @param product 产品值对象
     * @return 创建的产品值对象
     */
    ProductVo createProduct(ProductVo product);

    /**
     * 更新产品。
     *
     * @param product 产品值对象
     * @return 更新的产品值对象
     */
    ProductVo updateProduct(ProductVo product);

    /**
     * 删除产品。
     *
     * @param id 产品的 ID
     */
    void deleteProduct(Long id);

    /**
     * 查找单个产品。
     *
     * @param id 产品的 ID
     * @return 产品值对象
     */
    ProductVo findOneProduct(Long id);

    /**
     * 查找产品。
     *
     * @param productLineId 产品线的 ID
     * @param keyword 关键字
     * @return 产品值对象集合
     */
    Collection<ProductVo> findProducts(Long productLineId, String keyword);

    /**
     * 查找可用项目。
     *
     * @param productLineId 产品线的 ID
     * @param id 产品的 ID
     * @return 项目值对象列表
     */
    List<ProjectVo> findAvailableProjects(Long productLineId, Long id);
}