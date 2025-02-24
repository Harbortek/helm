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
//import org.apache.calcite.jdbc.CalciteConnection;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.*;
//import java.util.StringJoiner;
//
//public class MongoCalciteConnectionManager {
//    private static final Logger logger = LoggerFactory.getLogger(MongoCalciteConnectionManager.class.getName());
//
//    public static void main(String[] args) throws Exception {
//        String modelPath = MongoCalciteConnectionManager.class.getResource("/mongo-model.json").getPath();
//        logger.info("Model Path = {}", modelPath);
//        Class.forName("org.apache.calcite.jdbc.Driver");
//        Connection connection = DriverManager.getConnection("jdbc:calcite:lex=MYSQL;schemaType=JDBC;caseSensitive=false;model=" + modelPath);
//        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
//        String query = "select * from TRACKER_ITEMS where projectId = ?";
//
//        PreparedStatement prepareStatement = calciteConnection.prepareStatement(query);
//
//        prepareStatement.setLong(1, 1L);
//
//        ResultSet result = null;prepareStatement.execute(query);
//        ResultSetMetaData resultSetMetaData = result.getMetaData();
//        int columnCount = resultSetMetaData.getColumnCount();
//        long startTime = System.currentTimeMillis();
//
//        int count = 1;
//        while (result.next()) {
//            if (count == 1) {
//                StringJoiner joiner = new StringJoiner(", ");
//                for (int i = 1; i <= columnCount; i++) {
//                    joiner.add(resultSetMetaData.getColumnName(i));
//                }
//                System.out.println(joiner);
//
//            }
//            StringJoiner joiner = new StringJoiner(", ");
//            for (int i = 1; i <= columnCount; i++) {
//                joiner.add(result.getString(i));
//            }
//            System.out.println(joiner);
//            count++;
//        }
//        logger.info("Total time took {} ms", System.currentTimeMillis() - startTime);
//        logger.info("Total of {} records read", count);
//    }
//}