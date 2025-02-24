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


import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.SqlBuilder;
import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.entity.IdName;
import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.entity.block.DocEntity;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class DocDao extends BaseJdbcDao {
    private Table mTable;

    public DocEntity saveDoc(DocEntity doc) {
        if (doc.getId() == null) {
            doc.setId(IDUtils.getId());
        }
        return save(doc);
    }

    public void batchSave(Collection<DocEntity> entities) {
        if (entities.size() == 0) {
            return;
        }

        saveAll(entities);
    }

    public void deleteDocBlock(Long id) {
        markAsDeleted(id, DocEntity.class);
    }


    public DocEntity findOneDoc(Long id) {
        return findById(id, DocEntity.class);
    }

    public DocEntity findDocByPageId(Long pageId) {
        SqlBuilder sqlBuilder = SqlBuilder.create()
                .select(getTableDotAllName(DocEntity.class))
                .from(getTable(DocEntity.class).getName())
                .join(getTable(ProjectPageEntity.class).getName(), SqlBuilder.Join.INNER)
                .on(
                        "%s = %s ".formatted(
                                getTableDotFieldName(ProjectPageEntity.class, ProjectPageEntity.Fields.smartDocId),
                                getTableDotFieldName(DocEntity.class, BaseEntity.Fields.id)
                        )
                ).where(
                        "%s = %s ".formatted(
                                getTableDotFieldName(ProjectPageEntity.class, BaseEntity.Fields.id),
                                pageId
                        )
                );
        String sql = sqlBuilder.build();
        List<DocEntity> docs = this.jdbcTemplate.query(sql, mapRow(DocEntity.class));
        return docs == null ? null : docs.get(0);
    }

    public List<DocEntity> findByIds(List<Long> docIds) {
        return findByIds(docIds, DocEntity.class);
    }

    public void incVersion(List<Long> pageIds) {
        List<ProjectPageEntity> pageEntities = findByIds(pageIds, ProjectPageEntity.class);
        List<Long> docIds = pageEntities.stream().map(ProjectPageEntity::getSmartDocId).toList();

        List<DocEntity> docEntities = findByIds(docIds, DocEntity.class);
        for (DocEntity docEntity : docEntities) {
            docEntity.setVersion(docEntity.getVersion() + 1);
            save(docEntity);
        }
    }
}
