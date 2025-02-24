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

package com.harbortek.helm.util;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import org.h2.util.StringUtils;
import org.reflections.Reflections;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SQLUtils {

    public static String underScoreTpCamelCase(String input){
        //将下划线命名法转换为驼峰命名法
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split("_");
        StringBuilder camelCaseString = new StringBuilder(words[0].toLowerCase());

        for (int i = 1; i < words.length; i++) {
            camelCaseString.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1).toLowerCase());
        }

        return camelCaseString.toString();
    }
    public static String camelCaseToUnderScore(String name){
        //将驼峰命名法转换为下划线命名法
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if ('_' ==c ){
                continue;
            }
            else if (Character.isAlphabetic(c) && Character.isUpperCase(c)) {
                if (i != 0) {
                    sb.append("_");
                }
                sb.append(Character.toLowerCase(c));
            }else{
                sb.append(Character.toLowerCase(c));
            }
        }
        return '`'+sb.toString()+'`';
    }



    public static String extractTableNameFromSQL(String sql) {
        Assert.notNull(sql, "sql must not be null");
        String tableName = null;
        SQLStatement stmt = com.alibaba.druid.sql.SQLUtils.parseSingleMysqlStatement(sql);
        if (stmt instanceof SQLUpdateStatement){
            tableName = ((SQLUpdateStatement)stmt).getTableName().getSimpleName();
        }else if (stmt instanceof SQLDeleteStatement){
            tableName = ((SQLDeleteStatement)stmt).getTableName().getSimpleName();
        }

        if (tableName==null){
            return null;
        }
        if (tableName.contains("`")){
            tableName = tableName.substring(1,tableName.length()-1);
        }

        return tableName;
    }



    static Map<String, Class<?>> classMap = new HashMap<>();

    public static void loadEntityTables(){
        if (classMap.isEmpty()){
            Reflections reflections = new Reflections("com.harbortek.helm");
            Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(Table.class);
            classSet.forEach(clazz -> {
                Table table = clazz.getAnnotation(Table.class);
                classMap.put(table.value(), clazz);
            });
        }
    }

    public static Class<?> getJavaTypeForTableName(String tableName) {
        loadEntityTables();
        return classMap.get(tableName);
    }


    public static String wrapString(String field){
        return "'"+field+"'";
    }

    public static String quote(String resourceName) {
        return "`"+resourceName+"`";
    }

    public static String cleanSQL(String sql){
        return StringUtils.replaceAll(sql,"\n", " ");
    }



}
