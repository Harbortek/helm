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

package com.harbortek.helm.common.config;

import cn.hutool.core.util.ReflectUtil;
import com.harbortek.helm.common.dao.jdbc.StrictJdbcConverter;
import com.harbortek.helm.common.dao.jdbc.StrictJdbcMappingContext;
import com.harbortek.helm.common.entity.IdName;
import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.JsonUtils;
import com.harbortek.helm.util.SQLUtils;
import com.harbortek.helm.util.SecurityUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteType;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.ExceptionTranslatorExecuteListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.*;
import org.springframework.data.jdbc.core.dialect.JdbcDialect;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.core.mapping.JdbcValue;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.support.JdbcUtil;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.RelationalManagedTypes;
import org.springframework.data.relational.core.conversion.MutableAggregateChange;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.DefaultNamingStrategy;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.event.BeforeSaveCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Autowired
    DataSource dataSource;

    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider
                (new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public DefaultDSLContext dslContext() {
        return new DefaultDSLContext(configuration());
    }

    public DefaultConfiguration configuration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(connectionProvider());
        jooqConfiguration.set(new DefaultExecuteListenerProvider(ExceptionTranslatorExecuteListener.DEFAULT),
                              new DefaultExecuteListenerProvider(new ExecuteListener() {
                                  @Override
                                  public void end(ExecuteContext ctx) {
                                      String sql = ctx.sql();
                                      if (ctx.type().equals(ExecuteType.WRITE)  && StringUtils.isNotEmpty(sql)) {
                                          String tableName = SQLUtils.extractTableNameFromSQL(sql);
                                          Class<?> clazz = SQLUtils.getJavaTypeForTableName(tableName);
                                          if (clazz != null) {
                                              CacheUtils.evictAll(clazz);
                                          }
                                      }
                                  }
                              }));
        return jooqConfiguration;
    }


    @NotNull
    @Override
    protected Collection<String> getMappingBasePackages() {
        return List.of("com.harbortek.helm");
    }



    @NotNull
    @Bean
    public JdbcMappingContext jdbcMappingContext(Optional<NamingStrategy> namingStrategy,
                                                 JdbcCustomConversions customConversions,
                                                 RelationalManagedTypes jdbcManagedTypes) {


        StrictJdbcMappingContext mappingContext =
                new StrictJdbcMappingContext(namingStrategy.orElse(DefaultNamingStrategy.INSTANCE));
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        mappingContext.setManagedTypes(jdbcManagedTypes);
        mappingContext.setStrict(true);

        return mappingContext;
    }

    @NotNull
    @Bean
    public JdbcConverter jdbcConverter(@NotNull JdbcMappingContext mappingContext, NamedParameterJdbcOperations operations,
                                       @NotNull @Lazy RelationResolver relationResolver, @NotNull JdbcCustomConversions conversions, @NotNull Dialect dialect) {

        JdbcArrayColumns arrayColumns = dialect instanceof JdbcDialect ? ((JdbcDialect) dialect).getArraySupport()
                : JdbcArrayColumns.DefaultSupport.INSTANCE;
        DefaultJdbcTypeFactory jdbcTypeFactory = new DefaultJdbcTypeFactory(operations.getJdbcOperations(), arrayColumns);

        return new StrictJdbcConverter(mappingContext, relationResolver, conversions, jdbcTypeFactory);
    }

    @NotNull
    @Bean
    public JdbcAggregateTemplate jdbcAggregateTemplate(@NotNull ApplicationContext applicationContext,
                                                       @NotNull JdbcMappingContext mappingContext,
                                                       @NotNull JdbcConverter converter,
                                                       @NotNull DataAccessStrategy dataAccessStrategy) {


        JdbcAggregateTemplate template = new JdbcAggregateTemplate(applicationContext, mappingContext, converter,
                                                                   dataAccessStrategy);

        template.setEntityCallbacks(EntityCallbacks.create(new BeforeSaveCallback<Object>() {

            @NotNull
            @Override
            public Object onBeforeSave(@NotNull Object aggregate,
                                       @NotNull MutableAggregateChange<Object> aggregateChange) {
                Field[] objFields = ReflectUtil.getFields(aggregate.getClass());
                try {
                    for (Field field : objFields) {
                        if (field.getAnnotation(LastModifiedBy.class) != null) {
                            if (SecurityUtils.getCurrentUser() != null) {
                                BeanUtils.setProperty(aggregate, field.getName(),
                                                      SecurityUtils.getCurrentUser().getId());
                            }
                            continue;
                        }
                        if (field.getAnnotation(LastModifiedDate.class) != null) {
                            BeanUtils.setProperty(aggregate, field.getName(), new Date());
                            continue;
                        }
                        if (field.getAnnotation(CreatedBy.class) != null) {
                            if (SecurityUtils.getCurrentUser() != null) {
                                if (StringUtils.isEmpty(BeanUtils.getProperty(aggregate, field.getName()))) {
                                    BeanUtils.setProperty(aggregate, field.getName(),
                                                          SecurityUtils.getCurrentUser().getId());
                                }
                            }
                            continue;
                        }
                        if (field.getAnnotation(CreatedDate.class) != null) {
                            if (StringUtils.isEmpty(BeanUtils.getProperty(aggregate, field.getName()))) {
                                BeanUtils.setProperty(aggregate, field.getName(), new Date());
                            }
                            continue;
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                return aggregate;
            }
        }));
        return template;
    }

    @ReadingConverter
    public static class GenericJsonStringToObjectConverter implements ConditionalGenericConverter {

        @Override
        public boolean matches(@NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
            // yes
            return IdName.class.isAssignableFrom(targetType.getType()) || targetType.isMap() || targetType.isCollection();
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Set.of(new ConvertiblePair(String.class, Collection.class),
                          new ConvertiblePair(String.class, Map.class),
                          new ConvertiblePair(String.class, IdName.class));
        }

        @Override
        public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
            String content = (String) source;
            if (StringUtils.isEmpty(content)) {
                if (List.class.isAssignableFrom(targetType.getType())) {
                    return new ArrayList<>();
                } else if (Map.class.isAssignableFrom(targetType.getType())) {
                    return new HashMap<>();
                } else {
                    return null;
                }
            }
            if (StringUtils.equalsIgnoreCase(content, "[]")) {
                return new ArrayList<>();
            }

            if (List.class.isAssignableFrom(targetType.getType())) {
                return JsonUtils.toList((String) source, Objects.requireNonNull(
                        targetType.getElementTypeDescriptor()).getType());
            } else if (Map.class.isAssignableFrom(targetType.getType())) {
                return JsonUtils.toMap((String) source,
                                       Objects.requireNonNull(targetType.getMapKeyTypeDescriptor()).getType(),
                                       Objects.requireNonNull(targetType.getMapValueTypeDescriptor()).getType());
            } else if (IdName.class.isAssignableFrom(targetType.getType())) {
                return JsonUtils.toObject((String) source, targetType.getType());
            }

            throw new RuntimeException(MessageFormat.format("not support convert form {0} to {1}", sourceType.getName(),
                                                            targetType.getName()));
        }
    }


    @WritingConverter
    public static class GenericObjectToJsonStringConverter implements ConditionalGenericConverter {

        @Override
        public boolean matches(@NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
            // yes
            return IdName.class.isAssignableFrom(sourceType.getType()) ||
                    BaseVo.class.isAssignableFrom(sourceType.getType()) ||
                    IdNameReference.class.isAssignableFrom(sourceType.getType()) ||
                    (sourceType.isArray() || sourceType.isMap() || sourceType.isCollection());
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Set.of(new ConvertiblePair(Collection.class, JdbcValue.class),
                          new ConvertiblePair(Map.class, JdbcValue.class),
                          new ConvertiblePair(IdName.class, JdbcValue.class),
                          new ConvertiblePair(BaseVo.class, JdbcValue.class),
                          new ConvertiblePair(IdNameReference.class, JdbcValue.class));
        }

        @Override
        public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
            if (source == null) {
                return null;
            } else {
                return JdbcValue.of(JsonUtils.toJSONString(source), JdbcUtil.targetSqlTypeFor(String.class));
            }
        }
    }

    @NotNull
    @Override
    protected List<?> userConverters() {
        return List.of(new GenericJsonStringToObjectConverter(),
                       new GenericObjectToJsonStringConverter());
    }


}
