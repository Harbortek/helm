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

package com.harbortek.helm.tracker.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.tracker.entity.view.ViewEntity;
import com.harbortek.helm.tracker.vo.view.ViewVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ViewDao extends BaseJdbcDao {

    public ViewEntity createView(ViewEntity viewEntity) {
        return save(viewEntity);
    }

    public ViewEntity updateView(ViewEntity viewEntity) {
        return save(viewEntity);
    }

    public void deleteView(Long id) {
        markAsDeleted(id, ViewEntity.class);
    }


    public ViewEntity findOneView(Long id) {
        return findById(id, ViewEntity.class);
    }

    public List<ViewEntity> findByObjectId(Long objectId,Long userId,Boolean display) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ViewEntity.Fields.objectId).is(objectId));
        if(display){
            criteria = criteria.and(Criteria.where(ViewEntity.Fields.display).is(Boolean.TRUE));
        }

        //所有公共视图，或当前用户私有视图
        criteria = criteria.and(Criteria.where(ViewEntity.Fields.viewType).is("PRIVATE").and(Criteria.where(BaseEntity.Fields.createBy).is(userId))
                .or(Criteria.where(ViewEntity.Fields.viewType).is("PUBLIC")));
        Query query = Query.query(criteria);
        query.columns(BaseEntity.Fields.id,BaseEntity.Fields.name,ViewEntity.Fields.viewType,
                      ViewEntity.Fields.display,ViewEntity.Fields.ordinary,ViewEntity.Fields.system);
        query.sort(Sort.by(Sort.Order.asc(ViewEntity.Fields.ordinary)));
        return find(query, ViewEntity.class);
    }

    public void createViews(List<ViewEntity> views) {
        saveAll(views);
    }

    public List<ViewEntity> findByObjectId(Long objectId,Boolean isAllField) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ViewEntity.Fields.objectId).is(objectId));
        Query query = Query.query(criteria);
        if(!isAllField){
            query.columns(BaseEntity.Fields.id,BaseEntity.Fields.name,ViewEntity.Fields.viewType,
                          ViewEntity.Fields.display,ViewEntity.Fields.ordinary,ViewEntity.Fields.system);
        }
        query.sort(Sort.by(Sort.Order.asc(ViewEntity.Fields.ordinary)));
        return find(query, ViewEntity.class);
    }

    public void batchUpdateOrdinary(List<ViewVo> viewVos) {


        viewVos.forEach(d -> {
            Update update = Update.update(ViewEntity.Fields.ordinary, d.getOrdinary());
            updateById(d.getId(), update, ViewEntity.class);
        });
    }

    public List<ViewEntity> findByObjectIds(List<Long> objectIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ViewEntity.Fields.objectId).in(objectIds));
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Order.asc(ViewEntity.Fields.ordinary)));
        return find(query, ViewEntity.class);
    }
}
