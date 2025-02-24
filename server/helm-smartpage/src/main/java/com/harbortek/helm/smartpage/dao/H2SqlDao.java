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

package com.harbortek.helm.smartpage.dao;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.smartpage.utils.DataTypeUtils;
import com.harbortek.helm.tracker.vo.smartpage.DatasetField;
import com.harbortek.helm.tracker.vo.smartpage.DatasetVo;
import com.harbortek.helm.util.IDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

@Repository
public class H2SqlDao extends BaseSqlDao {
    private EmbeddedDatabase db;
    private NamedParameterJdbcTemplate jdbcTemplate;

    ThreadLocal<String> tableNames = new ThreadLocal<>();

    public synchronized void start() {
        if (db == null) {
            db = new EmbeddedDatabaseBuilder()
                    .generateUniqueName(true)
                    .setType(H2)
                    .setScriptEncoding("UTF-8")
                    .ignoreFailedDrops(true)
                    .build();
            jdbcTemplate = new NamedParameterJdbcTemplate(db);
        }
    }

    public synchronized void stop() {
        if (db != null) {
            db.shutdown();
        }
    }

    @Override
    public NamedParameterJdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            start();
        }
        return jdbcTemplate;
    }

    public void prepareData(DatasetVo dataset, JSONArray data) {
        getJdbcTemplate().getJdbcTemplate().execute(getDropTableSQL(dataset));

        getJdbcTemplate().getJdbcTemplate().execute(getCreateTableSQL(dataset));

        getJdbcTemplate().batchUpdate(getInsertSQL(dataset), getInsertData(dataset,data));
    }

    private String getDropTableSQL(DatasetVo dataset) {
        return "DROP TABLE IF EXISTS " + getTableName(dataset);
    }

    private SqlParameterSource[] getInsertData(DatasetVo dataset, JSONArray data){
       List<SqlParameterSource> sqlParameterSources = new ArrayList<>();
        if (data.size()==0){
            return sqlParameterSources.toArray(new SqlParameterSource[]{});
        }
        List<DatasetField> fields = dataset.getFields();
        for (int i = 0; i < data.size(); i++) {
            JSONObject row = data.getJSONObject(i);
            Map<String,Object> rowMap = row.getRaw();
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource(rowMap);
            sqlParameterSources.add(sqlParameterSource);
        }
        return sqlParameterSources.toArray(new SqlParameterSource[]{});
    }

    private String getInsertSQL(DatasetVo dataset) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(getTableName(dataset)).append(" (");
        List<DatasetField> fields = dataset.getFields();
        for (int i = 0; i < fields.size(); i++) {
            DatasetField field = fields.get(i);
            sb.append("`").append(field.getField()).append("`");
            if (i < fields.size() - 1){
                sb.append(",");
            }
        }
        sb.append(" ) VALUES ( ");
        for (int i = 0; i < fields.size(); i++) {
            DatasetField field = fields.get(i);
            sb.append(":").append(field.getField());
            if (i < fields.size() - 1){
                sb.append(",");
            }
        }
        sb.append(" )");

        return sb.toString();
    }

    private String getCreateTableSQL(DatasetVo dataset) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE MEMORY TEMPORARY TABLE ").append(getTableName(dataset)).append(" (");
        List<DatasetField> fields = dataset.getFields();
        for (int i = 0; i < fields.size(); i++) {
            DatasetField field = fields.get(i);
            sb.append("`").append(field.getField()).append("`").append(" ").append(DataTypeUtils.getJDBCDataType(field.getType()));
            if (i < fields.size() - 1){
                sb.append(",");
            }
        }
        sb.append(" )");
        return sb.toString();
    }

    public String getSelectSQL(DatasetVo dataset) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        List<DatasetField> fields = dataset.getFields();
        for (int i = 0; i < fields.size(); i++) {
            DatasetField field = fields.get(i);
            sb.append("`").append(field.getField()).append("`");
            if (i < fields.size() - 1){
                sb.append(",");
            }
        }
        sb.append(" FROM ").append(getTableName(dataset));
        return sb.toString();
    }

    private String getTableName(DatasetVo dataset){
        String tableName = tableNames.get();
        if (StringUtils.isEmpty(tableName)){
            tableName = "T_" + IDUtils.getId();
            tableNames.set(tableName);
        }
        return tableName;
    }
}
