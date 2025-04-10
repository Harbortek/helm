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


import cn.hutool.db.sql.SqlBuilder;
import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.tracker.entity.block.DocumentEntity;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
public class DocDao extends BaseJdbcDao {
    private Table mTable;

    public DocumentEntity saveDoc(DocumentEntity doc) {
        if (doc.getId() == null) {
            doc.setId(IDUtils.getId());
        }
        return save(doc);
    }

    public void batchSave(Collection<DocumentEntity> entities) {
        if (entities.size() == 0) {
            return;
        }

        saveAll(entities);
    }

    public void deleteDocBlock(Long id) {
        markAsDeleted(id, DocumentEntity.class);
    }


    public DocumentEntity findOneDoc(Long id) {
        return findById(id, DocumentEntity.class);
    }

    public DocumentEntity findDocByPageId(Long pageId) {
        SqlBuilder sqlBuilder = SqlBuilder.create()
                .select(getTableDotAllName(DocumentEntity.class))
                .from(getTable(DocumentEntity.class).getName())
                .join(getTable(ProjectPageEntity.class).getName(), SqlBuilder.Join.INNER)
                .on(
                        "%s = %s ".formatted(
                                getTableDotFieldName(ProjectPageEntity.class, ProjectPageEntity.Fields.smartDocId),
                                getTableDotFieldName(DocumentEntity.class, BaseEntity.Fields.id)
                        )
                ).where(
                        "%s = %s ".formatted(
                                getTableDotFieldName(ProjectPageEntity.class, BaseEntity.Fields.id),
                                pageId
                        )
                );
        String sql = sqlBuilder.build();
        List<DocumentEntity> docs = this.jdbcTemplate.query(sql, mapRow(DocumentEntity.class));
        return docs == null ? null : docs.get(0);
    }

    public List<DocumentEntity> findByIds(List<Long> docIds) {
        return findByIds(docIds, DocumentEntity.class);
    }

    public void incVersion(List<Long> pageIds) {
        List<ProjectPageEntity> pageEntities = findByIds(pageIds, ProjectPageEntity.class);
        List<Long> docIds = pageEntities.stream().map(ProjectPageEntity::getSmartDocId).toList();

        List<DocumentEntity> docEntities = findByIds(docIds, DocumentEntity.class);
        for (DocumentEntity documentEntity : docEntities) {
            documentEntity.setRevision(documentEntity.getRevision() + 1);
            save(documentEntity);
        }
    }
}
