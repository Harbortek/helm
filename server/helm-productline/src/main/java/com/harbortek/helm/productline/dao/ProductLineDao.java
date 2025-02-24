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
import com.harbortek.helm.productline.entity.ProductLineEntity;
import lombok.extern.slf4j.Slf4j;
import org.parboiled.common.StringUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ProductLineDao extends BaseJdbcDao {
    public ProductLineEntity createProductLine(ProductLineEntity productLine) {
        return save(productLine);
    }

    public ProductLineEntity updateProductLine(ProductLineEntity productLine) {
        return save(productLine);
    }

    public void deleteProductLine(Long id) {
        markAsDeleted(id, ProductLineEntity.class);
    }

    public ProductLineEntity findOneProductLine(Long id) {
        return findById(id, ProductLineEntity.class);
    }

    public List<ProductLineEntity> findProductLines(String keyword) {

        Criteria criteria = Criteria.empty();
        if (StringUtils.isNotEmpty(keyword)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%"+keyword+"%"));
        }
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        Query query = Query.query(criteria);
        return find(query, ProductLineEntity.class);
    }
}
