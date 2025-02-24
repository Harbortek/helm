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

//package com.harbortek.helm.common.config.calcite;
//
//import com.harbortek.helm.common.config.MongoConfig;
//import com.mongodb.client.MongoClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.mongo.MongoProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//
//import javax.sql.DataSource;
//
//@Configuration
//@ConditionalOnClass({MongoClient.class})
//@EnableConfigurationProperties({MongoProperties.class})
//@AutoConfigureAfter({MongoConfig.class})
//public class CalciteJdbcTemplate extends NamedParameterJdbcTemplate {
//
//    public CalciteJdbcTemplate(@Autowired DataSource dataSource) {
//        super(dataSource);
//
//    }
//
//}
