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
import com.harbortek.helm.tracker.entity.log.ChangeLogEntity;
import com.harbortek.helm.tracker.entity.log.HyperlinkEntity;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
public class HyperlinkDao extends BaseJdbcDao {
    public HyperlinkEntity createHyperlink(HyperlinkEntity hyperlink) {
        if (hyperlink.getId() == null) {
            hyperlink.setId(IDUtils.getId());
        }
        return save(hyperlink);
    }

    public HyperlinkEntity updateHyperlink(HyperlinkEntity hyperlink) {
        if (hyperlink.getId() == null) {
            hyperlink.setId(IDUtils.getId());
        }
        return save(hyperlink);
    }

    public List<HyperlinkEntity> findHyperlinkByObjectId(Long objectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ChangeLogEntity.Fields.objectId).is(objectId));
        Query query = Query.query(criteria);
        return find(query, HyperlinkEntity.class);
    }
    public List<HyperlinkEntity> findByObjectIds(Collection<Long> odlItemIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ChangeLogEntity.Fields.objectId).in(odlItemIds));
        Query query = Query.query(criteria);
        return find(query, HyperlinkEntity.class);
    }

    public void deleteHyperlink(Long id) {
        markAsDeleted(id, HyperlinkEntity.class);
    }

    public HyperlinkEntity findById(Long id) {
        return findById(id,HyperlinkEntity.class);
    }

    public void batchCreateHyperlink(List<HyperlinkEntity> hyperlinks) {
        saveAll(hyperlinks);
    }

}
