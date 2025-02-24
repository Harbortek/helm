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
import com.harbortek.helm.common.entity.IdName;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.tracker.entity.log.ChangeLogEntity;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ChangeLogDao extends BaseJdbcDao {

    public void createChangeLog(Long objectId, String type, String message, IdName target, Object oldValue,
                                Object newObject) {
        if(oldValue == null){
            oldValue="";
        }
        if(newObject==null){
            newObject="";
        }
        ChangeLogEntity changeLog =
                ChangeLogEntity.builder().id(IDUtils.getId()).objectId(objectId)
                               .messageType(type).message(message).target(new IdNameReference<>(target))
                               .oldValue(JsonUtils.toJSONString(oldValue)).newValue(JsonUtils.toJSONString(newObject)).build();
        save(changeLog);
    }

    public List<ChangeLogEntity> findByObjectId(Long objectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ChangeLogEntity.Fields.objectId).is(objectId));
        Query query = Query.query(criteria);
        return find(query, ChangeLogEntity.class);
    }

    public List<ChangeLogEntity> findByObjectIds(List<Long> oldItemIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ChangeLogEntity.Fields.objectId).in(oldItemIds));
        Query query = Query.query(criteria);
        return find(query, ChangeLogEntity.class);
    }

    public void batchCreateChangeLog(List<ChangeLogEntity> changeLogList) {
        saveAll(changeLogList);
    }
}
