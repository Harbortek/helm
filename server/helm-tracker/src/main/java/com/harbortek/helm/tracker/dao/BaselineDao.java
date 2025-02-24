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
import com.harbortek.helm.common.entity.HistoryBaseEntity;
import com.harbortek.helm.tracker.entity.baseline.BaselineEntity;
import com.harbortek.helm.tracker.entity.document.DocumentHistoryEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class BaselineDao extends BaseJdbcDao {
    public BaselineEntity createBaseline(BaselineEntity baseline) {
        return save(baseline);
    }

    public BaselineEntity updateBaseline(BaselineEntity baseline) {
        return save(baseline);
    }

    public void deleteBaseline(Long id) {
        markAsDeleted(id, BaselineEntity.class);
    }

    public BaselineEntity findOneBaseline(Long id) {
        return findById(id, BaselineEntity.class);
    }

    public List<BaselineEntity> findBaselinesByProjectId(Long projectId, String keyword) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaselineEntity.Fields.projectId).is(projectId));
        if (StringUtils.isNotEmpty(keyword)){
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%"+keyword+"%"));
        }
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.DESC,BaseEntity.Fields.createDate));
        return find(query, BaselineEntity.class);
    }

    public List<BaselineEntity> findBaselinesByDocumentId(Long projectId,Long documentId) {
        Result<Record1<Object>> historyIds =
                getDslContext().selectDistinct(getField(BaseEntity.Fields.id)).from(getTable(DocumentHistoryEntity.class))
                       .where(getField(DocumentHistoryEntity.Fields.projectId).eq(projectId))
                       .and(getField(HistoryBaseEntity.Fields.objectId).eq(documentId)).fetch();

        String sql = """
                select *
                from baselines
                where id in (
                              select id
                              from (select id,  cast(json_extract(document_history_ids, '$[0]') AS UNSIGNED) as history_id from baselines) t
                              where t.history_id in (:history_ids)
                          )
                       and project_id = :projectId
                       and deleted = 0
                """;

        Map<String,Object> params = new HashMap<>();
        params.put("projectId",projectId);
        params.put("history_ids",historyIds);
        return find(sql, params, BaselineEntity.class);
    }

    public void batchCreateBaselines(List<BaselineEntity> baselines) {
        saveAll(baselines);
    }
}
