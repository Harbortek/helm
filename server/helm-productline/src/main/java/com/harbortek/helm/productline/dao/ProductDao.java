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

package com.harbortek.helm.productline.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.productline.entity.ProductEntity;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.parboiled.common.StringUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
@Repository
@Slf4j
public class ProductDao extends BaseJdbcDao {
    public ProductEntity createProduct(ProductEntity product) {
        return save(product);
    }

    public ProductEntity updateProduct(ProductEntity product) {
        return save(product);
    }

    public void deleteProduct(Long productId) {
        markAsDeleted(productId, ProductEntity.class);
    }

    public ProductEntity findOneProduct(Long productId) {
        return findById(productId, ProductEntity.class);
    }

    public List<ProductEntity> findProducts(Long productLineId, String keyword) {
        Criteria criteria = Criteria.empty();
        if (StringUtils.isNotEmpty(keyword)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%"+keyword+"%"));
        }
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProductEntity.Fields.productLineId).is(productLineId));
        Query query = Query.query(criteria);
        return find(query, ProductEntity.class);
    }

    public Collection<Long> findUsesProjectIds(Long productLineId, Long productId){
        Criteria criteria = Criteria.empty();

        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProductEntity.Fields.projectId).greaterThan(0L));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).not(productId));
        Query query = Query.query(criteria);
        query.columns(ProductEntity.Fields.projectId);
        return ObjectUtils.ids(find(query, ProductEntity.class),"projectId");
    }

    public List<ProjectEntity> findAvailableProjects(Long productLineId,Long productId) {
        Collection<Long> projectIds = findUsesProjectIds(productLineId, productId);
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).notIn(projectIds));
        Query query = Query.query(criteria);
        return find(query, ProjectEntity.class);
    }

    public void deleteByProductLineId(Long productLineId) {
        Condition condition = DSL.noCondition();
        condition = condition.and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE));
        condition = condition.and(getField(ProductEntity.Fields.productLineId).eq(productLineId));
        getDslContext().update(getTable(ProductEntity.class)).set(getField(BaseEntity.Fields.deleted), Boolean.TRUE).where(condition).execute();

    }
}
