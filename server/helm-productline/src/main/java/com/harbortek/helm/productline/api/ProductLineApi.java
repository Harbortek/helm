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

import com.harbortek.helm.productline.service.ProductLineService;
import com.harbortek.helm.productline.vo.ProductLineVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/product-line")
public class ProductLineApi {
    @Autowired
    ProductLineService productLineService;

    @Parameter(name="查询产品线列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<ProductLineVo>> findProductLines(
            @RequestParam(required = false,defaultValue = "") String keyword) {
        Collection<ProductLineVo> baselines = productLineService.findProductLines(keyword);
        return ResponseEntity.ok(baselines);
    }

    @Parameter(name="查询一个产品线")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<ProductLineVo> findOneProductLine(@PathVariable Long id) {
        ProductLineVo baseline = productLineService.findOneProductLine(id);
        return ResponseEntity.ok(baseline);
    }

    @Parameter(name="创建一个产品线")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<ProductLineVo> createProductLine(@RequestBody ProductLineVo baselineVo) {
        ProductLineVo result = productLineService.createProductLine(baselineVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个产品线")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<ProductLineVo> updateProductLine(@RequestBody ProductLineVo baselineVo) {
        ProductLineVo result = productLineService.updateProductLine(baselineVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个产品线")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteProductLine(@PathVariable Long id) {
        productLineService.deleteProductLine(id);
        return ResponseEntity.ok().build();
    }
}
