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

import org.springframework.data.convert.CustomConversions;
import org.springframework.data.jdbc.core.convert.JdbcTypeFactory;
import org.springframework.data.jdbc.core.convert.MappingJdbcConverter;
import org.springframework.data.jdbc.core.convert.RelationResolver;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;

public class StrictJdbcConverter extends MappingJdbcConverter {
    public StrictJdbcConverter(RelationalMappingContext context,
                               RelationResolver relationResolver) {
        super(context, relationResolver);
    }

    public StrictJdbcConverter(RelationalMappingContext context, RelationResolver relationResolver,
                               CustomConversions conversions,
                               JdbcTypeFactory typeFactory) {
        super(context, relationResolver, conversions, typeFactory);
    }


}
