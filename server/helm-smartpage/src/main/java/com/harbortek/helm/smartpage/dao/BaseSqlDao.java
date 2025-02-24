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

import com.harbortek.helm.tracker.constants.DatasetConstants;
import com.harbortek.helm.tracker.constants.FilterConstants;
import com.harbortek.helm.tracker.vo.smartpage.DatasetField;
import com.harbortek.helm.tracker.vo.smartpage.filter.DataFilter;
import com.harbortek.helm.tracker.vo.smartpage.filter.FilterCondition;
import com.harbortek.helm.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseSqlDao {
    public abstract NamedParameterJdbcTemplate getJdbcTemplate();

    public SqlRowSet plainSelect(String sql, List<DataFilter> queryParameters, List<DatasetField> fields,
                                 List<DataFilter> filterFields, int page,
                                 int pageSize) {

        String paginationSQL = SQLUtils.cleanSQL(buildPaginationSQL(sql, page, pageSize, fields, filterFields,
                                                                    queryParameters));

        return getJdbcTemplate().queryForRowSet(paginationSQL, new MapSqlParameterSource());
    }

    public SqlRowSet groupBy(String sql, List<DataFilter> parameters, List<DatasetField> groupByFields,
                             List<DatasetField> valueFields, List<DataFilter> filterFields) {

        String groupBySQL = buildGroupBySQL(sql, groupByFields, valueFields, filterFields, parameters);

        return getJdbcTemplate().queryForRowSet(SQLUtils.cleanSQL(groupBySQL), new MapSqlParameterSource());
    }

    private String buildGroupBySQL(String sql, List<DatasetField> groupByFields, List<DatasetField> valueFields,
                                   List<DataFilter> filterFields, List<DataFilter> queryParameters) {
        if (StringUtils.isEmpty(sql)) {
            return null;
        }
        List<DatasetField> fields = new ArrayList<>();
        fields.addAll(groupByFields);
        fields.addAll(valueFields);

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(getSelect(groupByFields, valueFields));


        sb.append(" FROM (").append("SELECT ").append(getInnerSelect(groupByFields, valueFields)).append(" FROM (")
          .append(sqlFix(sql)).append(") ").append("t1 ").append(buildWhereConditions(filterFields, queryParameters))
          .append(") T");

        String groupBy = getGroupBy(groupByFields);
        if (StringUtils.isNotEmpty(groupBy)) {
            sb.append(" GROUP BY ").append(groupBy).append(" ");
        }

        String orderBy = getOrderBy(fields);
        if (StringUtils.isNotEmpty(orderBy)) {
            sb.append(" ORDER BY ").append(orderBy).append(" ");
        }

        String originSQL = sb.toString();


        //如果存在自动填充字段，则构造自动填充的SQL
        if (hasAutoFillFields(groupByFields)) {
            originSQL = autoFill(originSQL, groupByFields, valueFields, filterFields, queryParameters);
        }

        //如果存在快速计算字段，则构造快速计算的SQL
        if (hasQuickCalculateFields(valueFields)) {
            originSQL = buildQuickCalculateSQL(originSQL, groupByFields, valueFields, filterFields, queryParameters);

        }

        return originSQL;
    }

    private boolean hasQuickCalculateFields(List<DatasetField> valueFields) {
        if (valueFields == null || valueFields.isEmpty()) {
            return false;
        }
        for (DatasetField valueField : valueFields) {
            if (StringUtils.isNotEmpty(valueField.getQuickCalc())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAutoFillFields(List<DatasetField> groupByFields) {
        if (groupByFields == null || groupByFields.isEmpty()) {
            return false;
        }
        for (DatasetField groupByField : groupByFields) {
            if (DatasetConstants.COLUMN_DATE.equals(groupByField.getType()) &&
                    "auto".equals(groupByField.getDateFill())) {
                return true;
            }
        }
        return false;
    }

    private String buildQuickCalculateSQL(String originSQL, List<DatasetField> groupByFields,
                                          List<DatasetField> valueFields, List<DataFilter> filterFields,
                                          List<DataFilter> queryParameters) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");

        for (DatasetField groupByField : groupByFields) {
            sb.append(SQLUtils.quote(groupByField.getName())).append(" AS ")
              .append(SQLUtils.quote(groupByField.getName()))
              .append(",");
        }
        for (DatasetField valueField : valueFields) {
            if (StringUtils.isNotEmpty(valueField.getQuickCalc())) {
                sb.append(getQuickCalcSegment(valueField, groupByFields)).append(" AS ")
                  .append(SQLUtils.quote(valueField.getName()))
                  .append(",");
                ;
            }
        }
        //删掉最后的逗号
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append(" FROM (").append(originSQL).append(") _QUICK_");

        return sb.toString();
    }

    private String getQuickCalcSegment(DatasetField valueField, List<DatasetField> groupByFields) {
        if ("accumulate".equals(valueField.getQuickCalc())) {
            StringBuilder sb = new StringBuilder();
            sb.append("SUM(").append(SQLUtils.quote(valueField.getName())).append(") over ( ");

            if (groupByFields != null && !groupByFields.isEmpty()) {
                if (groupByFields.size() > 1) {
                    sb.append("partition by ");
                    for (DatasetField groupByField : groupByFields) {
                        sb.append(SQLUtils.quote(groupByField.getName())).append(",");
                    }
                    //删掉最后的逗号
                    if (sb.charAt(sb.length() - 1) == ',') {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                }
                sb.append(" order by ");
                for (DatasetField groupByField : groupByFields) {
                    sb.append(SQLUtils.quote(groupByField.getName())).append("none".equals(groupByField.getSort()) ?
                                                                                     "asc" : groupByField.getSort())
                      .append(
                              ",");
                }
                if (sb.charAt(sb.length() - 1) == ',') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append(")");
                return sb.toString();
            } else {
                sb.append("order by ").append(SQLUtils.quote(valueField.getName())).append(")");
                return sb.toString();
            }
        }
        return SQLUtils.quote(valueField.getName());
    }

    private String autoFill(String originSQL, List<DatasetField> groupByFields, List<DatasetField> valueFields,
                            List<DataFilter> filterFields,
                            List<DataFilter> queryParameters) {
        boolean needAutoFill = false;
        DatasetField dateField = null;
        if (groupByFields != null) {
            for (DatasetField groupByField : groupByFields) {
                if (DatasetConstants.COLUMN_DATE.equals(groupByField.getType()) &&
                        "auto".equals(groupByField.getDateFill())) {
                    needAutoFill = true;
                    dateField = groupByField;
                    break;
                }
            }
        }
        if (needAutoFill) {
            if (queryParameters != null) {
                boolean hasDateRange = false;
                DataFilter dateFilter = null;
                for (DataFilter queryParameter : queryParameters) {
                    if (DatasetConstants.COLUMN_DATE.equals(queryParameter.getType()) &&
                            FilterConstants.FILTER_METHOD_DATE_RANGE.equals(queryParameter.getFilterMethod())) {
                        hasDateRange = true;
                        dateFilter = queryParameter;
                        break;
                    }
                }
                if (hasDateRange) {

                    String allDateSQL = """
                            SELECT DATE_FORMAT(date, '%Y-%m-%d') AS date
                                  FROM (
                                           SELECT '[RANGE_START_DATE]' + INTERVAL n DAY AS date
                                           FROM (
                                                    SELECT a.n + b.n * 10 + c.n * 100 AS n
                                                    FROM (SELECT 0 AS n
                                                          UNION
                                                          SELECT 1
                                                          UNION
                                                          SELECT 2
                                                          UNION
                                                          SELECT 3
                                                          UNION
                                                          SELECT 4
                                                          UNION
                                                          SELECT 5
                                                          UNION
                                                          SELECT 6
                                                          UNION
                                                          SELECT 7
                                                          UNION
                                                          SELECT 8
                                                          UNION
                                                          SELECT 9) a,
                                                         (SELECT 0 AS n
                                                          UNION
                                                          SELECT 1
                                                          UNION
                                                          SELECT 2
                                                          UNION
                                                          SELECT 3
                                                          UNION
                                                          SELECT 4
                                                          UNION
                                                          SELECT 5
                                                          UNION
                                                          SELECT 6
                                                          UNION
                                                          SELECT 7
                                                          UNION
                                                          SELECT 8
                                                          UNION
                                                          SELECT 9) b,
                                                         (SELECT 0 AS n
                                                          UNION
                                                          SELECT 1
                                                          UNION
                                                          SELECT 2
                                                          UNION
                                                          SELECT 3
                                                          UNION
                                                          SELECT 4
                                                          UNION
                                                          SELECT 5
                                                          UNION
                                                          SELECT 6
                                                          UNION
                                                          SELECT 7
                                                          UNION
                                                          SELECT 8
                                                          UNION
                                                          SELECT 9) c
                                                ) numbers
                                           WHERE '[RANGE_END_DATE]' >= '[RANGE_START_DATE]' + INTERVAL n DAY
                                       ) dates
                                  WHERE ((DATE_FORMAT(date, '%Y-%m-%d') > '[RANGE_START_DATE]') AND
                                                             (DATE_FORMAT(date, '%Y-%m-%d') < '[RANGE_END_DATE]'))
                                  order by date asc
                            """;
                    String rangeStartDate =
                            DateUtils.toString(DateUtils.toDate(dateFilter.getConditions().get(0).getMatchValue(),
                                                                DateUtils.DEFAULT_DATETIME_FORMAT),
                                               DateUtils.DEFAULT_DATE_FORMAT);
                    String rangeEndDate =
                            DateUtils.toString(DateUtils.toDate(dateFilter.getConditions().get(1).getMatchValue(),
                                                                DateUtils.DEFAULT_DATETIME_FORMAT),
                                               DateUtils.DEFAULT_DATE_FORMAT);

                    allDateSQL = StringUtils.replace(allDateSQL, "[RANGE_START_DATE]", rangeStartDate);
                    allDateSQL = StringUtils.replace(allDateSQL, "[RANGE_END_DATE]", rangeEndDate);

                    StringBuilder sql = new StringBuilder();
                    sql.append("SELECT ALL_DATES.date AS ").append(SQLUtils.quote(dateField.getName())).append(",");
                    for (DatasetField field : groupByFields) {
                        if (!field.getField().equals(dateField.getField())) {
                            sql.append(SQLUtils.quote(field.getField())).append(" AS ")
                               .append(SQLUtils.quote(field.getName())).append(
                                       ",");
                        }
                    }
                    for (DatasetField field : valueFields) {
                        sql.append("COALESCE(").append(SQLUtils.quote(field.getField())).append(",0) AS ")
                           .append(SQLUtils.quote(field.getName())).append(
                                   ",");
                    }
                    //删掉最后的逗号
                    if (sql.charAt(sql.length() - 1) == ',') {
                        sql.deleteCharAt(sql.length() - 1);
                    }
                    sql.append(" FROM (").append(allDateSQL).append(") ALL_DATES ");
                    sql.append("LEFT JOIN (").append(originSQL).append(") ORIGIN_DATA ");
                    sql.append("on ORIGIN_DATA.").append(SQLUtils.quote(dateField.getName())).append(
                            " = ALL_DATES.date ");
                    sql.append(" order by ALL_DATES.date  ");
                    if ("none".equals(dateField.getSort())) {
                        sql.append(" asc ");
                    } else {
                        sql.append(dateField.getSort());
                    }
                    return sql.toString();
                }
            }
        }
        return originSQL;
    }

    private String buildWhereConditions(List<DataFilter> filterFields, List<DataFilter> queryParameters) {
        if ((filterFields == null || filterFields.isEmpty()) &&
                (queryParameters == null || queryParameters.isEmpty())) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("WHERE 1=1 ");
        if (filterFields != null) {
            for (DataFilter filterField : filterFields) {
                String condition = buildWhereCondition(filterField);
                if (StringUtils.isNotEmpty(condition)) {
                    sb.append(" AND (").append(condition).append(")");
                }
            }
        }
        if (queryParameters != null) {
            for (DataFilter parameter : queryParameters) {
                String condition = buildWhereCondition(parameter);
                if (StringUtils.isNotEmpty(condition)) {
                    sb.append(" AND (").append(condition).append(")");
                }
            }
        }
        return sb.toString();
    }

    private String buildWhereCondition(DataFilter filterField) {
        if (ObjectUtils.isNotEmpty(filterField.getConditions())) {
            if (DatasetConstants.COLUMN_STRING.equals(filterField.getType())) {
                return buildTextCondition(filterField);
            } else if (DatasetConstants.COLUMN_DATE.equals(filterField.getType())) {
                return buildDateCondition(filterField);
            } else if (DatasetConstants.COLUMN_NUMBER.equals(filterField.getType())) {
                return buildNumberCondition(filterField);
            }
        }
        return "1=1";
    }

    private String buildNumberCondition(DataFilter filterField) {
        if (FilterConstants.CONDITION_METHOD_SINGLE.equals(filterField.getConditionMethod())) {
            if (!filterField.getConditions().isEmpty()) {
                FilterCondition condition = filterField.getConditions().get(0);
                return buildSingleNumberCondition(filterField.getField(), condition);
            }
        } else if (FilterConstants.CONDITION_METHOD_AND.equals(filterField.getConditionMethod())) {
            if (filterField.getConditions().size() == 2) {
                FilterCondition condition1 = filterField.getConditions().get(0);
                FilterCondition condition2 = filterField.getConditions().get(1);
                return "(" + buildSingleNumberCondition(filterField.getField(), condition1) + ") AND (" +
                        buildSingleNumberCondition(filterField.getField(), condition2) + ")";
            }
        } else {
            if (filterField.getConditions().size() == 2) {
                FilterCondition condition1 = filterField.getConditions().get(0);
                FilterCondition condition2 = filterField.getConditions().get(1);
                return "(" + buildSingleNumberCondition(filterField.getField(), condition1) + ") OR (" +
                        buildSingleNumberCondition(filterField.getField(), condition2) + ")";
            }
        }
        return null;
    }

    private String buildSingleNumberCondition(String field, FilterCondition condition) {
        return switch (condition.getMatchMethod()) {
            case FilterConstants.MATCH_METHOD_EQUAL -> field + " = " + condition.getMatchValue();
            case FilterConstants.MATCH_METHOD_NOT_EQUAL -> field + " != " + condition.getMatchValue();
            case FilterConstants.MATCH_METHOD_GREATER_THAN -> field + " > " + condition.getMatchValue();
            case FilterConstants.MATCH_METHOD_GREATER_THAN_OR_EQUAL -> field + " >= " + condition.getMatchValue();
            case FilterConstants.MATCH_METHOD_LESS_THAN -> field + " < " + condition.getMatchValue();
            case FilterConstants.MATCH_METHOD_LESS_THAN_OR_EQUAL -> field + " <= " + condition.getMatchValue();
            case FilterConstants.MATCH_METHOD_IS_NULL -> field + " is null ";
            case FilterConstants.MATCH_METHOD_IS_NOT_NULL -> field + " is not null ";
            default -> "1=1";
        };
    }


    private String buildDateCondition(DataFilter filterField) {
        if (FilterConstants.FILTER_METHOD_SINGLE_DATE.equals(filterField.getFilterMethod())) {
            if (!filterField.getConditions().isEmpty()) {
                FilterCondition condition = filterField.getConditions().get(0);
                condition.setMatchMethod(FilterConstants.MATCH_METHOD_EQUAL);
                return buildSingleDateCondition(filterField.getField(), condition);
            }
        } else {
            if (FilterConstants.RANGE_TYPE_START.equals(filterField.getRangeType())) {
                if (!filterField.getConditions().isEmpty()) {
                    FilterCondition condition = filterField.getConditions().get(0);
                    condition.setMatchMethod(FilterConstants.MATCH_METHOD_GREATER_THAN_OR_EQUAL);
                    return buildSingleDateCondition(filterField.getField(), condition);
                }
            } else if (FilterConstants.RANGE_TYPE_END.equals(filterField.getRangeType())) {
                if (!filterField.getConditions().isEmpty()) {
                    FilterCondition condition = filterField.getConditions().get(0);
                    condition.setMatchMethod(FilterConstants.MATCH_METHOD_LESS_THAN_OR_EQUAL);
                    return buildSingleDateCondition(filterField.getField(), condition);
                }
            } else if (filterField.getConditions().size() == 2) {
                FilterCondition condition1 = filterField.getConditions().get(0);
                FilterCondition condition2 = filterField.getConditions().get(1);
                condition1.setMatchMethod(FilterConstants.MATCH_METHOD_GREATER_THAN_OR_EQUAL);
                condition2.setMatchMethod(FilterConstants.MATCH_METHOD_LESS_THAN_OR_EQUAL);
                return "(" + buildSingleDateCondition(filterField.getField(), condition1) + ") AND (" +
                        buildSingleDateCondition(filterField.getField(), condition2) + ")";
            }
        }
        return null;
    }

    private String buildSingleDateCondition(String field, FilterCondition condition) {
        if (condition.getRelative()) {
            String value = "";
            if (FilterConstants.RELATIVE_UNIT_DAY.equals(condition.getRelativeUnit())) {
                if (FilterConstants.RELATIVE_DIRECTION_AFTER.equals(condition.getRelativeDirection())) {
                    value = "DATE_ADD(CURDATE(), INTERVAL " + condition.getRelativeValue() + " DAY)";
                } else {
                    value = "DATE_SUB(CURDATE(), INTERVAL " + condition.getRelativeValue() + " DAY)";
                }
            } else if (FilterConstants.RELATIVE_UNIT_MONTH.equals(condition.getRelativeUnit())) {
                if (FilterConstants.RELATIVE_DIRECTION_AFTER.equals(condition.getRelativeDirection())) {
                    value = "DATE_ADD(CURDATE(), INTERVAL " + condition.getRelativeValue() + " MONTH)";
                } else {
                    value = "DATE_SUB(CURDATE(), INTERVAL " + condition.getRelativeValue() + " MONTH)";
                }
            } else if (FilterConstants.RELATIVE_UNIT_YEAR.equals(condition.getRelativeUnit())) {
                if (FilterConstants.RELATIVE_DIRECTION_AFTER.equals(condition.getRelativeDirection())) {
                    value = "DATE_ADD(CURDATE(), INTERVAL " + condition.getRelativeValue() + " YEAR)";
                } else {
                    value = "DATE_SUB(CURDATE(), INTERVAL " + condition.getRelativeValue() + " YEAR)";
                }
            }

            if (FilterConstants.MATCH_METHOD_EQUAL.equals(condition.getMatchMethod())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'%Y-%m-%d')" + " = " + "DATE_FORMAT(" + value + "," +
                        "'%Y-%m" +
                        "-%d')";
            } else if (FilterConstants.MATCH_METHOD_GREATER_THAN_OR_EQUAL.equals(condition.getMatchMethod())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'%Y-%m-%d')" + " >= " + "DATE_FORMAT(" + value +
                        ",'%Y-%m-%d')";
            } else {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'%Y-%m-%d')" + " <= " + "DATE_FORMAT(" + value +
                        ",'%Y-%m-%d')";
            }
        } else {
            if (FilterConstants.MATCH_METHOD_EQUAL.equals(condition.getMatchMethod())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'%Y-%m-%d')" + " = " +
                        SQLUtils.wrapString(condition.getMatchValue());
            } else if (FilterConstants.MATCH_METHOD_GREATER_THAN_OR_EQUAL.equals(condition.getMatchMethod())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'%Y-%m-%d')" + " > " +
                        SQLUtils.wrapString(condition.getMatchValue());
            } else {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'%Y-%m-%d')" + " < " +
                        SQLUtils.wrapString(condition.getMatchValue());
            }
        }
    }

    private String buildTextCondition(DataFilter filterField) {
        if (FilterConstants.FILTER_METHOD_CONDITION.equals(filterField.getFilterMethod())) {
            if (FilterConstants.CONDITION_METHOD_SINGLE.equals(filterField.getConditionMethod())) {
                if (!filterField.getConditions().isEmpty()) {
                    FilterCondition condition = filterField.getConditions().get(0);
                    return buildSingleTextCondition(filterField.getField(), condition);
                }
            } else if (FilterConstants.CONDITION_METHOD_AND.equals(filterField.getConditionMethod())) {
                if (filterField.getConditions().size() == 2) {
                    FilterCondition condition1 = filterField.getConditions().get(0);
                    FilterCondition condition2 = filterField.getConditions().get(1);
                    return "(" + buildSingleTextCondition(filterField.getField(), condition1) + ") AND (" +
                            buildSingleTextCondition(filterField.getField(), condition2) + ")";
                }
            } else if (filterField.getConditions().size() == 2) {
                FilterCondition condition1 = filterField.getConditions().get(0);
                FilterCondition condition2 = filterField.getConditions().get(1);
                return "(" + buildSingleTextCondition(filterField.getField(), condition1) + ") OR (" +
                        buildSingleTextCondition(filterField.getField(), condition2) + ")";
            }
        } else {
            if (FilterConstants.QUERY_METHOD_SINGLE.equals(filterField.getQueryMethod())) {
                if (!filterField.getConditions().isEmpty()) {
                    FilterCondition condition = filterField.getConditions().get(0);
                    return filterField.getField() + " = (" + SQLUtils.wrapString(condition.getMatchValue()) + ")";
                }
            } else {
                FilterCondition condition = filterField.getConditions().get(0);
                if (ObjectUtils.isEmpty(condition.getMatchValue()) ||
                        "[]".equals(condition.getMatchValue())) {
                    return "1=1";
                }
                List<String> values = JsonUtils.toList(condition.getMatchValue(), String.class);
                StringBuilder sb = new StringBuilder();
                for (String value : values) {
                    sb.append(SQLUtils.wrapString(value)).append(",");
                }
                String valueArray = sb.toString();
                if (valueArray.endsWith(",")) {
                    valueArray = valueArray.substring(0, valueArray.length() - 1);
                }
                if (condition.getExclusive()) {
                    return filterField.getField() + " not in (" + valueArray + ")";
                } else {
                    return filterField.getField() + " in (" + valueArray + ")";
                }
            }
        }
        return null;
    }

    private String buildSingleTextCondition(String field, FilterCondition condition) {
        return switch (condition.getMatchMethod()) {
            case FilterConstants.MATCH_METHOD_STRICT_MATCH ->
                    SQLUtils.quote(field) + " = " + SQLUtils.wrapString(condition.getMatchValue());
            case FilterConstants.MATCH_METHOD_NOT_MATCH ->
                    SQLUtils.quote(field) + " != " + SQLUtils.wrapString(condition.getMatchValue());
            case FilterConstants.MATCH_METHOD_CONTAINS ->
                    SQLUtils.quote(field) + " LIKE " + SQLUtils.wrapString("%" + condition.getMatchValue() + "%");
            case FilterConstants.MATCH_METHOD_NOT_CONTAINS ->
                    SQLUtils.quote(field) + " NOT LIKE " + SQLUtils.wrapString("%" + condition.getMatchValue() + "%");
            case FilterConstants.MATCH_METHOD_START_WITH ->
                    SQLUtils.quote(field) + " like " + SQLUtils.wrapString(condition.getMatchValue() + "%");
            case FilterConstants.MATCH_METHOD_END_WITH ->
                    SQLUtils.quote(field) + " like " + SQLUtils.wrapString("%" + condition.getMatchValue());
            case FilterConstants.MATCH_METHOD_IS_NULL -> field + " is null ";
            case FilterConstants.MATCH_METHOD_IS_NOT_NULL -> field + " is not null ";
            case FilterConstants.MATCH_METHOD_IS_EMPTY -> field + " = '' ";
            case FilterConstants.MATCH_METHOD_IS_NOT_EMPTY -> field + " != '' ";
            default -> "1=1";
        };
    }


    public String getInnerSelect(List<DatasetField> groupByFields, List<DatasetField> valueFields) {
        StringBuffer sb = new StringBuffer();
        if (groupByFields != null && !groupByFields.isEmpty() || valueFields != null && !valueFields.isEmpty()) {
            if (groupByFields != null) {
                groupByFields.forEach(f -> {
                    sb.append(getFormatFunction(f)).append(",");
                });
            }
            if (valueFields != null) {
                valueFields.forEach(f -> {
                    sb.append(getFormatFunction(f)).append(",");
                });
            }
            //删掉最后的逗号
            if (sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
            }
        } else {
            sb.append("*");
        }
        return sb.toString();
    }

    public String getSelect(List<DatasetField> groupByFields, List<DatasetField> valueFields) {
        StringBuffer sb = new StringBuffer();
        if (groupByFields != null && !groupByFields.isEmpty() || valueFields != null && !valueFields.isEmpty()) {
            if (groupByFields != null) {
                groupByFields.forEach(f -> {
                    sb.append(SQLUtils.quote(f.getField())).append(" AS ").append(SQLUtils.quote(f.getName())).append(
                            ",");
                });
            }
            if (valueFields != null) {
                valueFields.forEach(f -> {
                    sb.append(getSummaryFunction(f)).append(",");
                });
            }
            //删掉最后的逗号
            if (sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
            }
        } else {
            sb.append("*");
        }
        return sb.toString();
    }

    public String getOrderBy(List<DatasetField> fields) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        fields.forEach(f -> {
            if (StringUtils.isNotEmpty(f.getSort())) {
                if ("ASC".equalsIgnoreCase(f.getSort())) {
                    sb.append(" ").append(SQLUtils.quote(f.getField())).append(" ASC,");
                } else if ("DESC".equalsIgnoreCase(f.getSort())) {
                    sb.append(" ").append(SQLUtils.quote(f.getField())).append(" DESC,");
                }
            }
        });
        //删掉最后的逗号
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getGroupBy(List<DatasetField> fields) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        fields.forEach(f -> {
            sb.append(SQLUtils.quote(f.getField())).append(",");
        });

        //删掉最后的逗号
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getFormatFunction(DatasetField f) {
        String field = f.getField();
        if (DatasetConstants.COLUMN_DATE.equals(f.getType())) {
            if ("y".equalsIgnoreCase(f.getDateStyle())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ", '" + datePattern(f, "%Y") + "') AS " +
                        SQLUtils.quote(field);
            } else if ("y_Q".equalsIgnoreCase(f.getDateStyle())) {
                return "concat( year(" + SQLUtils.quote(field) + ") , QUARTER(" + SQLUtils.quote(field) + "))  AS " +
                        SQLUtils.quote(field);
            } else if ("y_M".equalsIgnoreCase(f.getDateStyle())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'" + datePattern(f, "%Y-%m") + "') AS " +
                        SQLUtils.quote(field);
            } else if ("y_W".equalsIgnoreCase(f.getDateStyle())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'" + datePattern(f, "%Y-%u") + "') AS " +
                        SQLUtils.quote(field);
            } else if ("y_M_d".equalsIgnoreCase(f.getDateStyle())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'" + datePattern(f, "%Y-%m-%d") + "') AS " +
                        SQLUtils.quote(field);
            } else if ("M_m_s".equalsIgnoreCase(f.getDateStyle())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'" + datePattern(f, "%H:%i:%s") + "') AS " +
                        SQLUtils.quote(field);
            } else if ("y_M_d_H_m".equalsIgnoreCase(f.getDateStyle())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'" + datePattern(f, "%Y-%m-%d %H:%i") + "') AS " +
                        SQLUtils.quote(field);
            } else if ("y_M_d_H_m_s".equalsIgnoreCase(f.getDateStyle())) {
                return "DATE_FORMAT(" + SQLUtils.quote(field) + ",'" + datePattern(f, "%Y-%m-%d %H:%i:%s") + "') AS " +
                        SQLUtils.quote(field);
            }
        }
        return f.getName();
    }

    private String datePattern(DatasetField f, String format) {
        if ("sub".equalsIgnoreCase(f.getDatePattern())) {
            return format;
        } else {
            return StringUtils.replace(format, "-", "/");
        }
    }

    public String getSummaryFunction(DatasetField f) {
        if ("sum".equalsIgnoreCase(f.getSummary())) {
            return "SUM(" + SQLUtils.quote(f.getField()) + ") AS " + SQLUtils.quote(f.getName());
        } else if ("avg".equalsIgnoreCase(f.getSummary())) {
            return "AVG(" + SQLUtils.quote(f.getField()) + ") AS " + SQLUtils.quote(f.getName());
        } else if ("max".equalsIgnoreCase(f.getSummary())) {
            return "MAX(" + SQLUtils.quote(f.getField()) + ") AS " + SQLUtils.quote(f.getName());
        } else if ("min".equalsIgnoreCase(f.getSummary())) {
            return "MIN(" + SQLUtils.quote(f.getField()) + ") AS " + SQLUtils.quote(f.getName());
        } else if ("stddev_pop".equalsIgnoreCase(f.getSummary())) {
            return "STDDEV_POP(" + SQLUtils.quote(f.getField()) + ") AS " + SQLUtils.quote(f.getName());
        } else if ("var_pop".equalsIgnoreCase(f.getSummary())) {
            return "VAR_POP(" + SQLUtils.quote(f.getField()) + ") AS " + SQLUtils.quote(f.getName());
        } else if ("COUNT".equalsIgnoreCase(f.getSummary())) {
            return "COUNT(" + SQLUtils.quote(f.getField()) + ") AS " + SQLUtils.quote(f.getName());
        } else if ("count_distinct".equalsIgnoreCase(f.getSummary())) {
            return "COUNT( DISTINCT " + SQLUtils.quote(f.getField()) + ") AS " + SQLUtils.quote(f.getName());
        } else {
            return "SUM(" + SQLUtils.quote(f.getField()) + ") AS " + SQLUtils.quote(f.getName());
        }
    }


    public String buildPaginationSQL(String sql, int page, int pageSize,
                                     List<DatasetField> fields, List<DataFilter> filterFields,
                                     List<DataFilter> queryParameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(getSelect(fields, null));
        sb.append(" FROM (").append(sqlFix(sql)).append(") T ")
          .append(buildWhereConditions(filterFields, queryParameters));
        if (fields != null) {
            String orderBy = getOrderBy(fields);
            if (StringUtils.isNotEmpty(orderBy)) {
                sb.append(" ORDER BY ").append(orderBy);
            }
        }

        if (page == 0 && pageSize > 1000) {
            return sb.toString();
        }
        sb.append(" limit ").append((page - 1) * pageSize).append(",").append(pageSize);

        return sb.toString();
    }

    public String sqlFix(String sql) {
        sql = StringUtils.trim(sql);
        if (sql.lastIndexOf(";") == (sql.length() - 1)) {
            sql = sql.substring(0, sql.length() - 1);
        }
        return sql;
    }

    public Long count(String sql, List<DataFilter> queryParameters, List<DataFilter> filterFields) {

        String countSQL = buildCountSQL(sql, filterFields, queryParameters);

        return getJdbcTemplate().queryForObject(countSQL, new HashMap<>(), Long.class);
    }

    private String buildCountSQL(String sql, List<DataFilter> filterFields, List<DataFilter> queryParameters) {
        return "SELECT COUNT(1) FROM (" + sql + ") T " + buildWhereConditions(filterFields, queryParameters);
    }

    public SqlRowSet selectDistinct(String sql, String field) {
        if(sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        String sb = "SELECT DISTINCT(" + SQLUtils.quote(field) + ") FROM (" + sql + ") " +
                IDUtils.getShortId();
        return getJdbcTemplate().queryForRowSet(sb, new HashMap<>());
    }
}
