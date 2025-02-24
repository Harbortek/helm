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

import org.springframework.data.jdbc.core.mapping.BasicJdbcPersistentProperty;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.mapping.Table;

public class StrictJdbcPersistentProperty extends BasicJdbcPersistentProperty {
    /**
     * Creates a new {@link BasicJdbcPersistentProperty}.
     *
     * @param property         must not be {@literal null}.
     * @param owner            must not be {@literal null}.
     * @param simpleTypeHolder must not be {@literal null}.
     * @param namingStrategy   must not be {@literal null}
     * @since 2.0
     */
    public StrictJdbcPersistentProperty(Property property,
                                        PersistentEntity<?, RelationalPersistentProperty> owner,
                                        SimpleTypeHolder simpleTypeHolder,
                                        NamingStrategy namingStrategy) {
        super(property, owner, simpleTypeHolder, namingStrategy);
    }

    @Override
    public boolean isEntity() {
        return super.isEntity() && getType().isAnnotationPresent(Table.class);
    }
}
