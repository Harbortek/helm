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

package com.harbortek.helm.common.dao.jdbc;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.util.TypeInformation;

import java.lang.annotation.Annotation;

public class StrictJdbcMappingContext extends JdbcMappingContext {

    public StrictJdbcMappingContext(NamingStrategy namingStrategy) {
        super(namingStrategy);
    }

    @NotNull
    @Override
    protected RelationalPersistentProperty createPersistentProperty(@NotNull Property property,
                                                                    @NotNull RelationalPersistentEntity<?> owner,
                                                                    @NotNull SimpleTypeHolder simpleTypeHolder) {
        StrictJdbcPersistentProperty
                persistentProperty = new StrictJdbcPersistentProperty(property, owner, simpleTypeHolder,
                                                                     this.getNamingStrategy());
        applyDefaults(persistentProperty);
        return persistentProperty;
    }

    @Override
    protected boolean shouldCreatePersistentEntityFor(@NotNull TypeInformation<?> typeInformation) {
        if (typeInformation.isMap() || typeInformation.isCollectionLike() ){
            return false;
        }
        Annotation annotation = typeInformation.getType().getAnnotation(Table.class);
        return annotation != null;
//        return super.shouldCreateProperties(typeInformation);
    }
}
