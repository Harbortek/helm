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

package com.harbortek.helm.common.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.entity.IdName;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.SQLUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.EntityRowMapper;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.core.mapping.JdbcValue;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.jooq.impl.DSL.*;

@Slf4j
public abstract class BaseJdbcDao {
    @Autowired
    public NamedParameterJdbcTemplate jdbcTemplate;


    @Autowired
    JdbcAggregateTemplate operations;

    @Autowired
    @Getter
    DSLContext dslContext;

    @Autowired
    JdbcConverter jdbcConverter;

    @Autowired
    JdbcMappingContext jdbcMappingContext;


    private NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    private JdbcAggregateTemplate getOperations() {
        return operations;
    }


    public <T extends BaseEntity> T findById(Long id, Class<T> clazz) {
        T result = CacheUtils.get(id, clazz);
        if (result != null) {
            return result;
        }
        result = getOperations().findById(id, clazz);
        if (result != null) {
            CacheUtils.put(result);
        }
        return result;
    }


    public <T extends BaseEntity> List<T> findByIds(Collection<Long> ids, Class<T> clazz) {
        return findByIds(ids, clazz, false);
    }

    public <T extends BaseEntity> List<T> findByIds(Collection<Long> ids, Class<T> clazz, boolean useCache) {
        List<T> existed = new ArrayList<>();
        if (useCache) {
            for (Long id : ids) {
                T object = CacheUtils.get(id, clazz);
                if (object != null) {
                    existed.add(object);
                }
            }
        }
        Collection<Long> needToLoadIds = CollectionUtil.disjunction(ids,
                ObjectUtils.ids(existed));

        List<T> results = new ArrayList<>();
        if (!needToLoadIds.isEmpty()) {
            results = IterableUtils.toList(getOperations().findAllById(needToLoadIds, clazz));
            if (useCache) {
                CacheUtils.put(results);
            }
        }

        results.addAll(existed);
        return results;
    }

    public <T extends BaseEntity> T findOne(Query query, Class<T> clazz) {
        return getOperations().findOne(query, clazz).orElse(null);
    }

    public <T extends BaseEntity> List<T> find(Query query, Class<T> clazz) {
        Iterable<T> entities = getOperations().findAll(query, clazz);
        List<T> result = new ArrayList<>();
        entities.forEach(result::add);
        return result;
    }

    public <T extends BaseEntity> Page<T> find(Query query, Pageable pageable, Class<T> clazz) {
        Iterable<T> entities = getOperations().findAll(query, clazz, pageable);
        List<T> result = new ArrayList<>();
        entities.forEach(result::add);
        return new PageImpl<>(result, pageable, count(query.offset(0).limit(0), clazz));
    }

    public <T> List<T> find(String sql, Map<String, Object> params, Class<T> clazz) {
        return jdbcTemplate.query(sql, params, mapRow(clazz));
    }

    public List<Long> findIds(String sql, Map<String, Object> params) {
        return jdbcTemplate.queryForList(sql, params, Long.class);
    }

    public <T> T findFirst(String sql, Map<String, Object> params, Class<T> clazz) {
        List<T> rows = jdbcTemplate.query(sql, params, mapRow(clazz));
        if (!rows.isEmpty()) {
            return rows.get(0);
        }
        return null;
    }


    public <T extends BaseEntity> Long count(Query query, Class<T> clazz) {
        return getOperations().count(query, clazz);
    }

    public Long count(String sql, Map<String, Object> params) {
        return jdbcTemplate.queryForObject(sql, params, Long.class);
    }

    public <T> T getValue(String sql, Map<String, Object> params, Class<T> clazz) {
        return jdbcTemplate.queryForObject(sql, params, clazz);
    }

    public <T extends BaseEntity> T insert(T entity) {
        T result = getOperations().insert(entity);
        CacheUtils.put(result);
        return result;
    }

    public <T extends BaseEntity> List<T> insertAll(Collection<T> entities) {
        List<T> results = IterableUtils.toList(getOperations().insertAll(entities));
        CacheUtils.put(results);
        return results;
    }

    public <T extends BaseEntity> T save(T entity) {
        T result = getOperations().save(entity);
        CacheUtils.put(result);
        return result;
    }

    public <T extends BaseEntity> List<T> saveAll(Collection<T> entities) {
        List<T> results = IterableUtils.toList(getOperations().saveAll(entities));
        CacheUtils.put(results);
        return results;
    }

    public <T extends BaseEntity> T update(T entity) {
        T result = getOperations().update(entity);
        CacheUtils.put(result);
        return result;
    }

    public <T extends BaseEntity> Iterable<T> updateAll(Collection<T> entities) {
        Iterable<T> result = getOperations().updateAll(entities);
        result.forEach(CacheUtils::put);
        return result;
    }

    public <T extends BaseEntity> void updateById(Long id, Update update, Class<T> clazz) {
        Map<Field<Object>, Object> map = new HashMap<>();
        update.getAssignments().forEach((k, v) -> {
            String property = k.getReference();
            Class<?> sourceType = DataUtils.getPropertyType(clazz, property);
            Class<?> targetType = guessTargetFieldType(sourceType);
            Object value = jdbcConverter.getConversionService().convert(v, targetType);
            if (value instanceof JdbcValue) {
                map.put(getField(k.getReference()), ((JdbcValue) value).getValue());
            } else {
                map.put(getField(k.getReference()), value);
            }
        });
        getDslContext().update(getTable(clazz)).set(map).where(getField(BaseEntity.Fields.id).eq(id)).execute();
        CacheUtils.evict(id, clazz);
    }

    public void update(String sql, Map<String, Object> params) {
        jdbcTemplate.update(sql, params);
    }

    public <T extends BaseEntity> void update(String sql, Map<String, Object> params, Class<T> clazz) {
        jdbcTemplate.update(sql, params);
        CacheUtils.evictAll(clazz);
    }

    public <T extends BaseEntity> void update(Condition condition, Map<String, Object> update, Class<T> clazz) {
        Map<Field<Object>, Object> map = new HashMap<>();
        update.forEach((property, originValue) -> {
            Class<?> sourceType = DataUtils.getPropertyType(clazz, property);
            Class<?> targetType = guessTargetFieldType(sourceType);
            Object value = jdbcConverter.getConversionService().convert(originValue, targetType);
            if (value instanceof JdbcValue) {
                map.put(getField(property), ((JdbcValue) value).getValue());
            } else {
                map.put(getField(property), value);
            }
        });
        getDslContext().update(getTable(clazz)).set(map).where(condition).execute();
        CacheUtils.evictAll(clazz);
    }

    public <T extends BaseEntity> void batchUpdate(String sql, Map<String, Object>[] params, Class<T> clazz) {
        jdbcTemplate.batchUpdate(sql, params);
        CacheUtils.evictAll(clazz);
    }

    public <T extends BaseEntity> void batchUpdate(String sql, SqlParameterSource[] params, Class<T> clazz) {
        jdbcTemplate.batchUpdate(sql, params);
        CacheUtils.evictAll(clazz);
    }

    public <T extends BaseEntity> void markAsDeleted(Long id, Class<T> clazz) {
        getDslContext().update(getTable(clazz)).set(getField(BaseEntity.Fields.deleted), true)
                .where(getField(BaseEntity.Fields.id
                ).eq(id)).execute();
        CacheUtils.evict(id, clazz);
    }

    public <T extends BaseEntity> void markAdDeleted(List<Long> ids, Class<T> clazz) {
        getDslContext().update(getTable(clazz)).set(getField(BaseEntity.Fields.deleted), true)
                .where(getField(BaseEntity.Fields.id
                ).in(ids)).execute();
        CacheUtils.evict(ids, clazz);
    }

    public <T extends BaseEntity> void delete(Long id, Class<T> clazz) {
        getOperations().deleteById(id, clazz);
        CacheUtils.evict(id, clazz);
    }

    public <T extends BaseEntity> void delete(Query query, Class<T> clazz) {
        List<T> results = IterableUtils.toList(getOperations().findAll(query, clazz));
        results.forEach(entity -> getOperations().delete(entity));
        CacheUtils.evict(ObjectUtils.ids(results, "id"), clazz);
    }

    public <T extends BaseEntity> void delete(T entity) {
        getOperations().delete(entity);
        Long id = entity.getId();
        CacheUtils.evict(id, entity.getClass());
    }

    public <T extends BaseEntity> void deleteAll(Class<T> clazz) {
        getOperations().deleteAll(clazz);
        CacheUtils.evictAll(clazz);
    }

    public <T extends BaseEntity> void delete(String sql, Map<String, Object> params) {
        jdbcTemplate.update(sql, params);
    }

    public <T extends BaseEntity> void delete(String sql, Map<String, Object> params, Class<T> clazz) {
        jdbcTemplate.update(sql, params);
        CacheUtils.evictAll(clazz);
    }

    public <T extends BaseEntity> boolean exists(Query query, Class<T> clazz) {
        return getOperations().exists(query, clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> RowMapper<T> mapRow(Class<T> clazz) {
        if (clazz.getAnnotation(org.springframework.data.relational.core.mapping.Table.class) != null) {
            RelationalPersistentEntity<T> persistentEntity =
                    (RelationalPersistentEntity<T>) jdbcMappingContext.getRequiredPersistentEntity(clazz);
            return new EntityRowMapper<T>(persistentEntity, jdbcConverter);
        }
        return new BeanPropertyRowMapper<>(clazz);
    }

    public <T extends IdName> Table<?> getTable(Class<T> clazz) {
        org.springframework.data.relational.core.mapping.Table annotation =
                clazz.getAnnotation(org.springframework.data.relational.core.mapping.Table.class);
        if (annotation == null) {
            throw new RuntimeException("table not found");
        }
        return table(annotation.value());
    }

    public Field<Object> getField(String fieldName) {
        return field(SQLUtils.camelCaseToUnderScore(fieldName));
    }

    private Class<?> guessTargetFieldType(Class<?> sourceType) {
        if (SimpleTypeHolder.DEFAULT.isSimpleType(sourceType)) {
            return sourceType;
        }
        return JdbcValue.class;
    }

    public <T> String getTableDotFieldName(Class<T> clazz, String fieldName) {
        org.springframework.data.relational.core.mapping.Table annotation =
                clazz.getAnnotation(org.springframework.data.relational.core.mapping.Table.class);
        if (annotation == null) {
            throw new RuntimeException("table not found");
        }
        Table table = table(annotation.value());
        Field field = getField(fieldName);
        return table.getName() + "." + field.getName();
    }

    public <T> String getTableDotAllName(Class<T> clazz) {
        org.springframework.data.relational.core.mapping.Table annotation =
                clazz.getAnnotation(org.springframework.data.relational.core.mapping.Table.class);
        if (annotation == null) {
            throw new RuntimeException("table not found");
        }
        Table table = table(annotation.value());
        return table.getName() + ".*";
    }
}
