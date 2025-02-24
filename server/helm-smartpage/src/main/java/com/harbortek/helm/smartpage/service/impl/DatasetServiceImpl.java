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

package com.harbortek.helm.smartpage.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.smartpage.dao.BaseSqlDao;
import com.harbortek.helm.smartpage.dao.H2SqlDao;
import com.harbortek.helm.smartpage.dao.MySqlDao;
import com.harbortek.helm.smartpage.service.DatasetService;
import com.harbortek.helm.smartpage.utils.DataTypeUtils;
import com.harbortek.helm.smartpage.utils.ExpressionUtils;
import com.harbortek.helm.smartpage.vo.DataRequest;
import com.harbortek.helm.smartpage.vo.DatasetPreviewRequest;
import com.harbortek.helm.tracker.constants.FieldTypes;
import com.harbortek.helm.tracker.dao.SmartPageDao;
import com.harbortek.helm.tracker.service.TrackerService;
import com.harbortek.helm.tracker.vo.smartpage.DatasetField;
import com.harbortek.helm.tracker.vo.smartpage.DatasetVo;
import com.harbortek.helm.tracker.vo.smartpage.PageDefinitionVo;
import com.harbortek.helm.tracker.vo.smartpage.filter.DataFilter;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DatasetServiceImpl implements DatasetService {
    @Autowired
    SmartPageDao smartPageDao;

    @Autowired
    MySqlDao sqlDao;

    @Autowired
    H2SqlDao h2SqlDao;

    @Autowired
    TrackerService trackerService;

    @Override
    public DatasetVo preview(Long pageId, DatasetPreviewRequest request) {
        DatasetVo dataset = request.getDataset();
        DataRequest dataRequest = request.getDataRequest();

        if (!ObjectUtils.isValid(dataset.getId())) {
            dataset.setId(IDUtils.getId());
        }
        List<DataFilter> filterFields = new ArrayList<>();
        if ("SQL".equals(dataset.getType())) {

        } else if ("JavaScript".equals(dataset.getType())) {
            Object result = ExpressionUtils.execute(dataset.getSql());
            if (result instanceof JSONArray data) {
                buildFieldsByJSON(data, dataset);
                h2SqlDao.prepareData(dataset, data);
            }
        } else if ("JSON".equals(dataset.getType())) {
            JSONArray data = JsonUtils.parseArray(dataset.getSql());
            buildFieldsByJSON(data, dataset);
            h2SqlDao.prepareData(dataset, data);
        }
        PageDefinitionVo pageDefinitionVo = smartPageDao.findPageDefinition(pageId);
        List<DataFilter> parameters = pageDefinitionVo.getPageParameters();

        SqlRowSet rowSet = null;
        if ("SQL".equals(dataset.getType())) {
            String sql = dataset.getSql();
            sql = translateVirtualTables(sql);
            rowSet = sqlDao.plainSelect(sql, null, null, filterFields, 1,
                                        100);
            rowSetToDataset(rowSet, dataset);
        } else {
            rowSet = h2SqlDao.plainSelect(h2SqlDao.getSelectSQL(dataset), parameters, null, filterFields, 1,
                                          100);
            rowSetToDataset(rowSet, dataset);
        }
        return dataset;
    }

    @Override
    public SqlRowSet plainSelect(Long pageId, DatasetVo dataset, List<DataFilter> parameters,
                                 List<DatasetField> fields,
                                 List<DataFilter> filterFields, int page, int pageSize) {
        if ("SQL".equals(dataset.getType())) {
            String sql = dataset.getSql();
            sql = translateVirtualTables(sql);
            return sqlDao.plainSelect(sql, parameters, fields, filterFields,  page, pageSize);

        } else if ("JavaScript".equals(dataset.getType())) {
            Object result = ExpressionUtils.execute(dataset.getSql());
            if (result instanceof JSONArray data) {
                h2SqlDao.prepareData(dataset, data);
                return h2SqlDao.plainSelect(h2SqlDao.getSelectSQL(dataset), parameters, fields, filterFields, page,
                                            pageSize);
            }
        } else if ("JSON".equals(dataset.getType())) {
            JSONArray data = JsonUtils.parseArray(dataset.getSql());
            h2SqlDao.prepareData(dataset, data);
            return h2SqlDao.plainSelect(h2SqlDao.getSelectSQL(dataset), parameters, fields, filterFields, page,
                                        pageSize);
        }
        return null;
    }

    @Override
    public Long count(Long pageId, DatasetVo dataset, List<DataFilter> filterFields, List<DataFilter> queryParameters) {
        if ("SQL".equals(dataset.getType())) {
            String sql = dataset.getSql();
            sql = translateVirtualTables(sql);
            return sqlDao.count(sql, queryParameters, filterFields);
        } else if ("JavaScript".equals(dataset.getType())) {
            Object result = ExpressionUtils.execute(dataset.getSql());
            if (result instanceof JSONArray data) {
                h2SqlDao.prepareData(dataset, data);
                return h2SqlDao.count(h2SqlDao.getSelectSQL(dataset), queryParameters, filterFields);
            }
        } else if ("JSON".equals(dataset.getType())) {
            JSONArray data = JsonUtils.parseArray(dataset.getSql());
            h2SqlDao.prepareData(dataset, data);
            return h2SqlDao.count(h2SqlDao.getSelectSQL(dataset), queryParameters, filterFields);
        }
        return null;
    }

    @Override
    public SqlRowSet groupBy(Long pageId, DatasetVo dataset, List<DataFilter> queryParameters,
                             List<DatasetField> groupByFields,
                             List<DatasetField> valueFields, List<DataFilter> filterFields) {
        if ("SQL".equals(dataset.getType())) {
            String sql = dataset.getSql();
            sql = translateVirtualTables(sql);
            return sqlDao.groupBy(sql, queryParameters, groupByFields, valueFields, filterFields);
        } else if ("JavaScript".equals(dataset.getType())) {
            Object result = ExpressionUtils.execute(dataset.getSql());
            if (result instanceof JSONArray data) {
                h2SqlDao.prepareData(dataset, data);
                return h2SqlDao.groupBy(h2SqlDao.getSelectSQL(dataset), queryParameters, groupByFields, valueFields,
                                        filterFields);
            }
        } else if ("JSON".equals(dataset.getType())) {
            JSONArray data = JsonUtils.parseArray(dataset.getSql());
            h2SqlDao.prepareData(dataset, data);
            return h2SqlDao.groupBy(h2SqlDao.getSelectSQL(dataset), queryParameters, groupByFields, valueFields,
                                    filterFields);
        }
        return null;
    }

    @Override
    public List<DatasetVo> findByPageId(Long pageId) {
        return smartPageDao.findDatasets(pageId);
    }

    @Override
    public DatasetVo findById(Long pageId, Long datasetId) {
        return smartPageDao.findOneDataset(pageId, datasetId);
    }

    @Override
    public DatasetVo createDataset(Long pageId, DatasetPreviewRequest request) {
        DatasetVo dataset = preview(pageId, request);
        return smartPageDao.createDataSet(pageId, dataset);
    }

    @Override
    public DatasetVo updateDataset(Long pageId, DatasetPreviewRequest request) {
        DatasetVo dataset = preview(pageId, request);
        smartPageDao.updateDataSet(pageId, dataset);
        return dataset;
    }

    @Override
    public void renameDataset(Long pageId, DatasetVo dataSetVo) {
        smartPageDao.updateDataSet(pageId, dataSetVo);
    }

    @Override
    public void deleteDataset(Long pageId, Long datasetId) {
        smartPageDao.deleteDataSet(pageId, datasetId);
    }

    @Override
    public List<String> findEnumValues(Long pageId, Long datasetId, String field) {
        DatasetVo dataset =  findById(pageId,datasetId);
        if ("SQL".equals(dataset.getType())) {
            String sql = dataset.getSql();
            sql = translateVirtualTables(sql);
            SqlRowSet rowSet = sqlDao.selectDistinct(sql,field);
            List<String> result = new ArrayList<>();
            while (rowSet.next()){
                result.add(rowSet.getString(field));
            }
            return result;
        } else if ("JavaScript".equals(dataset.getType())) {
        } else if ("JSON".equals(dataset.getType())) {
        }

        return new ArrayList<>();
    }

    private void rowSetToDataset(SqlRowSet rowSet, DatasetVo dataset) {
        SqlRowSetMetaData metaData = rowSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<DatasetField> originFields = dataset.getFields();
        Map<String, DatasetField> fieldMap = new HashMap<>();
        originFields.forEach(f -> {fieldMap.put(f.getName(), f);});

        List<DatasetField> fieldList = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            String fieldName = metaData.getColumnName(i + 1);

            DatasetField field = fieldMap.get(fieldName);
            if (field == null) {
                field = new DatasetField();
                field.setName(fieldName);
                field.setField(fieldName);
                field.setTitle(fieldName);
                field.setType(DataTypeUtils.getDataTypeFromJDBC(metaData.getColumnType(i + 1)));
            }

            if (!"_class".equals(field.getName())) {
                fieldList.add(field);
            }
        }
        dataset.setFields(fieldList);

        JSONArray data = new JSONArray();
        while (rowSet.next()) {
            JSONObject row = new JSONObject();
            for (DatasetField field : fieldList) {
                String key = field.getName();
                Object value = rowSet.getObject(key);
                row.set(key, value);
            }
            data.add(row);
        }
        dataset.setData(data);
    }

    private void buildFieldsByJSON(JSONArray jsonArray, DatasetVo dataset) {
        if (jsonArray.isEmpty()) {
            return;
        }
        JSONObject row = jsonArray.getJSONObject(0);
        List<DatasetField> fields = new ArrayList<>();
        row.keySet().forEach(key -> {
            DatasetField field = new DatasetField();
            field.setName(key);
            field.setTitle(key);
            field.setField(key);
            field.setType(DataTypeUtils.getDataType(row.get(key)));
            if (!"_class".equals(field.getName())) {
                fields.add(field);
            }
        });
        dataset.setFields(fields);
    }

    private List<String> tokenize(String inputText) {
        if (!inputText.contains("[[") && !inputText.contains("]]")) {
            return new ArrayList<>();
        }

        List<String> tokens = new ArrayList<>();
        int pos = 0;
        int length = inputText.length();
        boolean inBracket = false;
        int endBracketCount = 0;
        StringBuilder currentToken = new StringBuilder();
        while (pos < length) {
            char currentChar = inputText.charAt(pos);
            if (!inBracket && '[' == currentChar && pos + 1 < length && inputText.charAt(pos + 1) == '[') {
                currentToken.append(currentChar);
                inBracket = true;
                endBracketCount = 0;
                pos++;
            } else if (inBracket) {
                currentToken.append(currentChar);
                pos++;
                if (']' == currentChar) {
                    endBracketCount++;
                }
                if (endBracketCount == 2) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                    inBracket = false;
                    endBracketCount = 0;
                }
            } else {
                pos++;
            }
        }
        String possibleLastToken = currentToken.toString();
        if (!possibleLastToken.isEmpty()) {
            tokens.add(possibleLastToken);
        }
        return tokens;
    }

    public String translateVirtualTables(String sql) {
        //从SQL中识别出类似[[PROJECT|TRACKER|PLAN|VERSION|SPRING|USER][表名]]这样的字符串
        List<String> tokens = tokenize(sql);
        for (String token : tokens) {
            String type = extractTypeWithinBrackets(token);
            String resourceName = extractNameWithinBrackets(token);
            String[] elementNames = StringUtils.split(resourceName, "|");
            //根据表名查询表的定义
            if ("PROJECT".equals(type)) {
                //根据项目ID查询项目的定义
                String newToken = translateVirtualProjectTable(elementNames);
                sql = StringUtils.replaceOnce(sql, token, newToken);
            } else if ("TRACKER".equals(type)) {
                //根据跟踪器ID查询跟踪器的定义
                String newToken = translateVirtualTrackerTable(elementNames);
                sql = StringUtils.replaceOnce(sql, token, newToken);
            } else if ("PLAN".equals(type)) {
                //根据计划ID查询计划的定义
                String newToken = translateVirtualPlanTable(elementNames);
                sql = StringUtils.replaceOnce(sql, token, newToken);
            } else if ("VERSION".equals(type)) {
                //根据版本ID查询版本的定义
                String newToken = translateVirtualVersionTable(elementNames);
                sql = StringUtils.replaceOnce(sql, token, newToken);
            } else if ("SPRINT".equals(type)) {
                //根据Spring Bean的ID查询Spring Bean的定义
                String newToken = translateVirtualSprintTable(elementNames);
                sql = StringUtils.replaceOnce(sql, token, newToken);
            } else if ("CURRENT_USER_NAME".equals(type)) {
                //根据用户ID查询用户的定义
                String newToken = translateVirtualUserVariable();
                sql = StringUtils.replaceOnce(sql, token, newToken);
            }
            //根据表的定义创建临时表
        }
        return sql;
    }

    private String translateVirtualUserVariable() {
        return SQLUtils.wrapString(SecurityUtils.getCurrentUser().getName());
    }


    private String translateVirtualSprintTable(String[] elementNames) {
        StringBuilder sb = new StringBuilder();
        String sql = """
                select p.item_no            as '编号',
                       p.name               as '名称',
                       p.description        as '描述',
                       status.name          as '状态',
                       p.progress           as '进度',
                       p.duration           as '时长',
                       p.meaning            as '通用状态',
                       u4.name              as '所有这',
                       p.plan_start_date    as '计划开始日期',
                       p.plan_end_date      as '计划完成日期',
                       p.real_start_date    as '实际开始日期',
                       p.real_end_date      as '实际完成日期',
                       tv.name              as '目标版本',
                       u1.name              as '创建者',
                       p.create_date        as '创建日期',
                       u2.name              as '更新者',
                       p.last_modified_date as '更新日期'
                from sprints p
                         left join enum_items status on (p.status_id = status.id)
                         left join users u1 on p.create_by = u1.id
                         left join users u2 on p.last_modified_by = u2.id
                         left join users u4 on p.owner_id = u4.id
                         left join target_versions tv on p.target_version_id = tv.id
                """;
        sb.append(sql);
        sb.append(" WHERE p.deleted =0");
        Long currentProjectId = (Long) SecurityUtils.get(SecurityUtils.PROJECT_ID);
        sb.append(" AND  p.project_id = ").append(currentProjectId);
        if (elementNames.length > 0 && !"*".equals(elementNames[0])) {
            String names = Arrays.stream(elementNames).map(SQLUtils::wrapString).collect(Collectors.joining(","));
            sb.append(" AND  p.name in (").append(names).append(")");
        }
        return "(" + sb + ")";
    }

    private String translateVirtualVersionTable(String[] elementNames) {
        StringBuilder sb = new StringBuilder();
        String sql = """
                select tv.name               as '目标版本',
                       tv.description           '版本描述',
                       u1.name               as '创建者',
                       tv.create_date        as '创建日期',
                       u2.name               as '更新者',
                       tv.last_modified_date as '更新日期'
                from target_versions tv
                         left join users u1 on tv.create_by = u1.id
                         left join users u2 on tv.last_modified_by = u2.id
                """;
        sb.append(sql);
        sb.append(" WHERE tv.deleted =0");
        Long currentProjectId = (Long) SecurityUtils.get(SecurityUtils.PROJECT_ID);
        sb.append(" AND  tv.project_id = ").append(currentProjectId);
        if (elementNames.length > 0 && !"*".equals(elementNames[0])) {
            String names = Arrays.stream(elementNames).map(SQLUtils::wrapString).collect(Collectors.joining(","));
            sb.append(" AND  tv.name in (").append(names).append(")");
        }
        return "(" + sb + ")";
    }

    private String translateVirtualPlanTable(String[] elementNames) {
        StringBuilder sb = new StringBuilder();
        String sql = """
                select p.seq_number           as '序号',
                       case p.type
                           when 'GROUP' then '组'
                           when 'TASK' then '任务'
                           when 'MILE_STONE' then '里程碑'
                           end                as '类型',
                       p.name                 as '任务名称',
                       p.description          as '任务描述',
                       p.duration             as '工期',
                       p.finished             as '已完成',
                       u3.name                as '负责人',
                       p.plan_start_date      as '计划开始日期',
                       p.plan_end_date        as '计划完成日期',
                       p.progress             as '进度',
                       p.real_start_date      as '实际开始日期',
                       p.real_end_date        as '实际完成日期',
                       u1.name                as '创建者',
                       p.create_date          as '创建日期',
                       u2.name                as '更新者',
                       p.last_modified_date   as '更新日期'
                from plans p
                         left join users u1 on p.create_by = u1.id
                         left join users u2 on p.last_modified_by = u2.id
                         left join users u3 on p.owner_id = u3.id
                """;
        sb.append(sql);
        sb.append(" WHERE p.deleted =0");
        Long currentProjectId = (Long) SecurityUtils.get(SecurityUtils.PROJECT_ID);
        sb.append(" AND  p.project_id = ").append(currentProjectId);
        if (elementNames.length > 0 && !"*".equals(elementNames[0])) {
            String names = Arrays.stream(elementNames).map(SQLUtils::wrapString).collect(Collectors.joining(","));
            sb.append(" AND  p.name in (").append(names).append(")");
        }
        return "(" + sb + ")";
    }

    private String translateVirtualTrackerTable(String[] trackerNames) {
        StringBuilder sb = new StringBuilder();
        if (trackerNames.length > 1 || "*".equals(trackerNames[0])) {
            String sql = """
                        select upper(concat(concat(p.key_name, '-'), items.item_no)) as `工作项编号`,
                               case items.tracker_id
                                   when -1 then '章节'
                                   when -2 then '标题'
                                   when -3 then '段落'
                                   else tracker.name
                                   end                                               as '工作项类型',
                               items.name                                            AS '标题',
                               items.description                                     as '描述',
                               status.name                                           as '状态',
                               status.meaning                                        as '通用状态',
                               priority_enum.name                                    as '优先级',
                               severity.name                                         as '严重程度',
                               sprint.name                                           as '迭代',
                               items.assigned_date                                   as '分配日期',
                               u3.name                                               as '当前处理人',
                               items.close_date                                      as '关闭日期',
                               u4.name                                               as '负责人',
                               items.parent_id                                       as '父工作项ID',
                               items.plan_end_date                                   as '计划完成日期',
                               items.plan_start_date                                 as '计划开始日期',
                               items.real_end_date                                   as '实际完成日期',
                               items.real_start_date                                 as '实际开始日期',
                               items.progress                                        as '进度',
                               items.estimate_working_hours                          as '预期工作时长',
                               items.registered_working_hours                        as '已登记工作时长',
                               items.remaining_working_hours                         as '剩余工作时长',
                               p.name                                                as '项目名称',
                               u1.name                                               as '创建者',
                               items.create_date                                     as '创建日期',
                               u2.name                                               as '更新者',
                               items.last_modified_date                              as '更新日期'
                        from tracker_items items
                                 left join projects p on (items.project_id = p.id)
                                 left join trackers tracker on (items.tracker_id = tracker.id)
                                 left join enum_items priority_enum on (items.priority_id = priority_enum.id)
                                 left join enum_items meaning on (items.meaning_id = meaning.id)
                                 left join enum_items severity on (items.severity_id = severity.id)
                                 left join json_table(tracker.tracker_statuses,'$[*]' COLUMNS (id bigint path '$.id', name varchar(200) path '$.name',meaning varchar(200) path '$.meaning.name')) status on (items.status_id = status.id)
                                 left join sprints sprint on items.sprint_id = sprint.id
                                 left join users u1 on items.create_by = u1.id
                                 left join users u2 on items.last_modified_by = u2.id
                                 left join users u3 on items.assigned_to_id = u3.id
                                 left join users u4 on items.owner_id = u4.id
                        where items.deleted = 0
                    """;
            sb.append(sql);
            Long currentProjectId = (Long) SecurityUtils.get(SecurityUtils.PROJECT_ID);
            sb.append(" AND items.project_id = ").append(currentProjectId);
            if (trackerNames.length > 1) {
                String names = Arrays.stream(trackerNames).map(SQLUtils::wrapString).collect(Collectors.joining(","));
                sb.append(" AND  items.tracker_id in (select id from trackers tracker where tracker.deleted = 0 and " +
                                  "tracker.name in " +
                                  "(").append(names).append("))");
            }
        } else {
            String select = """
                    select upper(concat(concat(p.key_name, '-'), items.item_no)) as '工作项编号',
                           case items.tracker_id
                               when -1 then '章节'
                               when -2 then '标题'
                               when -3 then '段落'
                               else tracker.name
                               end                                               as '工作项类型',
                           items.name                                            AS '标题',
                           items.description                                     as '描述',
                           status.name                                           as '状态',
                           status.meaning                                        as '通用状态',
                           priority_enum.name                                    as '优先级',
                           severity.name                                         as '严重程度',
                           sprint.name                                           as '迭代',
                           items.assigned_date                                   as '分配日期',
                           u3.name                                               as '当前处理人',
                           items.close_date                                      as '关闭日期',
                           u4.name                                               as '负责人',
                           items.parent_id                                       as '父工作项ID',
                           items.plan_end_date                                   as '计划完成日期',
                           items.plan_start_date                                 as '计划开始日期',
                           items.real_end_date                                   as '实际完成日期',
                           items.real_start_date                                 as '实际开始日期',
                           items.progress                                        as '进度',
                           items.estimate_working_hours                          as '预期工作时长',
                           items.registered_working_hours                        as '已登记工作时长',
                           items.remaining_working_hours                         as '剩余工作时长',
                           p.name                                                as '项目名称',
                           u1.name                                               as '创建者',
                           items.create_date                                     as '创建日期',
                           u2.name                                               as '更新者',
                           items.last_modified_date                              as '更新日期'
                    """;
            String where = """
                        from tracker_items items
                                 left join projects p on (items.project_id = p.id)
                                 left join trackers tracker on (items.tracker_id = tracker.id)
                                 left join enum_items priority_enum on (items.priority_id = priority_enum.id)
                                 left join enum_items meaning on (items.meaning_id = meaning.id)
                                 left join enum_items severity on (items.severity_id = severity.id)
                                 left join json_table(tracker.tracker_statuses,'$[*]' COLUMNS (id bigint path '$.id', name varchar(200) path '$.name',meaning varchar(200) path '$.meaning.name')) status on (items.status_id = status.id)
                                 left join sprints sprint on items.sprint_id = sprint.id
                                 left join users u1 on items.create_by = u1.id
                                 left join users u2 on items.last_modified_by = u2.id
                                 left join users u3 on items.assigned_to_id = u3.id
                                 left join users u4 on items.owner_id = u4.id
                        where items.deleted = 0
                          and items.tracker_id =
                    """;
            sb.append(select);

            Long currentProjectId = (Long) SecurityUtils.get(SecurityUtils.PROJECT_ID);
            TrackerVo tracker = trackerService.findOneTrackerByName(currentProjectId, trackerNames[0]);
            List<TrackerField> trackerFields = tracker.getTrackerFields();
            for (TrackerField trackerField : trackerFields) {
                if (!trackerField.isSystem()) {
                    if (!Objects.equals(trackerField.getInputType(), FieldTypes.TABLE) &&
                            !Objects.equals(trackerField.getInputType(), FieldTypes.TEST_STEP)) {
                        sb.append(",\n");
                        sb.append("json_extract(items.values,'$.\"").append(trackerField.getId()).append("\"') as '")
                          .append(trackerField.getName()).append("'");
                    }
                }
            }
            sb.append(where);
            sb.append(tracker.getId());
        }

        return "(" + sb + ")";
    }


    private String translateVirtualProjectTable(String[] elementNames) {
        StringBuilder sb = new StringBuilder();
        String sql = """
                select p.key_name           as '代码',
                       p.name               as '名称',
                       p.description        as '描述',
                       category.name        as '项目类别',
                       u4.name              as '负责人',
                       p.progress           as '进度',
                       p.start_date         as '开始日期',
                       p.end_date           as '结束日期',
                       status.name          as '状态',
                       u1.name              as '创建者',
                       p.create_date        as '创建日期',
                       u2.name              as '更新者',
                       p.last_modified_date as '更新日期'
                from projects p
                         left join enum_items status on (p.status_id = status.id)
                         left join enum_items category on (p.category_id = category.id)
                         left join users u1 on p.create_by = u1.id
                         left join users u2 on p.last_modified_by = u2.id
                         left join users u4 on p.owner_id = u4.id
                """;
        sb.append(sql);
        sb.append(" WHERE p.deleted =0 AND  p.name in (");
        for (int i = 0; i < elementNames.length; i++) {
            sb.append(SQLUtils.wrapString(elementNames[i]));
            if (i < elementNames.length - 1) {
                sb.append(",");
            }
        }
        sb.append(")");

        return "(" + sb + ")";
    }

    private String extractNameWithinBrackets(String token) {
        return StringUtils.substring(token, token.indexOf("]") + 1, token.lastIndexOf("]"));
    }

    private String extractTypeWithinBrackets(String token) {
        return StringUtils.substring(token, 2, token.indexOf("]"));
    }

}
