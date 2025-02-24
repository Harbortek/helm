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

import com.harbortek.helm.productline.service.ProductService;
import com.harbortek.helm.productline.vo.ProductVo;
import com.harbortek.helm.tracker.vo.ProjectVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/product-line/{productLineId}/product")
public class ProductApi {
    @Autowired
    ProductService productService;



    @Parameter(name="查询产品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<ProductVo>> findProducts(@PathVariable Long productLineId,
                                                       @RequestParam(required = false, defaultValue = "") String keyword) {
        Collection<ProductVo> products = productService.findProducts(productLineId, keyword);
        return ResponseEntity.ok(products);
    }

    @Parameter(name="查询一个产品")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<ProductVo> findOneProduct(@PathVariable Long productLineId,@PathVariable Long id) {
        ProductVo product = productService.findOneProduct(id);
        return ResponseEntity.ok(product);
    }

    @Parameter(name="创建一个产品")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<ProductVo> createProduct(@PathVariable Long productLineId,@RequestBody ProductVo product) {
        product.setProductLineId(productLineId);
        ProductVo result = productService.createProduct(product);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个产品")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<ProductVo> updateProduct(@PathVariable Long productLineId,@RequestBody ProductVo product) {
        product.setProductLineId(productLineId);
        ProductVo result = productService.updateProduct(product);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个产品")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteProduct(@PathVariable Long productLineId,@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询项目列表")
    @RequestMapping(value = "/{id}/project/list", method = RequestMethod.GET)
    ResponseEntity<List<ProjectVo>> findProjects(@PathVariable Long productLineId,@PathVariable Long id) {
        List<ProjectVo> projectVos = productService.findAvailableProjects(productLineId,id);
        return ResponseEntity.ok(projectVos);
    }
}
