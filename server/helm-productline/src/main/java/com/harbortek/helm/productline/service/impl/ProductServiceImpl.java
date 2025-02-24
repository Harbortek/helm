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

package com.harbortek.helm.productline.service.impl;

import com.harbortek.helm.productline.dao.ProductDao;
import com.harbortek.helm.productline.entity.ProductEntity;
import com.harbortek.helm.productline.service.ProductService;
import com.harbortek.helm.productline.vo.ProductVo;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.service.ProjectService;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductDao productDao;

    @Autowired
    UserService userService;

    @Autowired
    ProjectService projectService;

    @Override
    public ProductVo createProduct(ProductVo productVo) {
        productVo.setId(IDUtils.getId());
        ProductEntity entity = DataUtils.toEntity(productVo, ProductEntity.class);

        entity = productDao.createProduct(entity);
        return DataUtils.toVo(entity, ProductVo.class);
    }

    @Override
    public ProductVo updateProduct(ProductVo productVo) {
        ProductEntity entity = DataUtils.toEntity(productVo, ProductEntity.class);
        productDao.updateProduct(entity);
        return productVo;
    }

    @Override
    public void deleteProduct(Long id) {
        productDao.deleteProduct(id);
    }

    @Override
    public ProductVo findOneProduct(Long id) {
        ProductEntity entity = productDao.findOneProduct(id);
        return DataUtils.toVo(entity, ProductVo.class);
    }

    @Override
    public Collection<ProductVo> findProducts(Long productLineId, String keyword) {
        List<ProductEntity> entities = productDao.findProducts(productLineId,keyword);
        List<ProductVo> productVos = DataUtils.toVo(entities, ProductVo.class);
        productVos.forEach(productVo -> {
            if (productVo.getProject()!=null){
                productVo.setProject( projectService.findOneProject(productVo.getProject().getId()));
            }
        });

        return productVos;
    }

    @Override
    public List<ProjectVo> findAvailableProjects(Long productLineId,Long id) {
        List<ProjectEntity> projectEntities = productDao.findAvailableProjects(productLineId,id);
        return DataUtils.toVo(projectEntities, ProjectVo.class);
    }
}
