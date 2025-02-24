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

package com.harbortek.helm.smartpage.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.smartpage.vo.ColumnGroup;
import com.harbortek.helm.smartpage.vo.DataSeries;
import com.harbortek.helm.smartpage.vo.TotalConfig;
import com.harbortek.helm.tracker.vo.smartpage.DatasetField;
import com.harbortek.helm.util.CompareUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ChartUtils {
    public static JSONArray toJsonArray(SqlRowSet rowSet, List<DatasetField> fields) {
        JSONArray data = new JSONArray();
        while (rowSet.next()) {
            JSONObject row = new JSONObject();
            for (DatasetField field : fields) {
                String key = field.getName();
                Object value = rowSet.getObject(key);
                row.set(key, value);
            }
            data.add(row);
        }
        return data;
    }

    public static JSONArray toPivotJsonArray(SqlRowSet rowSet, List<DatasetField> rowFields,
                                             List<DatasetField> columnFields, DatasetField valueField,
                                             List<ColumnGroup> columnGroups,
                                             TotalConfig totalConfig) {

        Map<Integer, JSONObject> data = new LinkedHashMap<>();
        //把结果集转换为数组
        while (rowSet.next()) {
            StringBuilder rowValue = new StringBuilder();
            for (DatasetField field : rowFields) {
                String key = field.getName();
                Object value = rowSet.getObject(key);
                rowValue.append(String.valueOf(value));
            }
            Integer rowKey = rowValue.toString().hashCode();
            JSONObject row = data.get(rowKey);
            if (row == null) {
                row = new JSONObject();
                for (DatasetField field : rowFields) {
                    String key = field.getName();
                    Object value = rowSet.getObject(key);
                    row.set(key, value);
                }
                data.put(rowKey, row);
            }


            String valueFieldName = buildColumnGroups(rowSet, columnFields.listIterator(), columnGroups, "");
            if (valueFieldName.endsWith(".")) {
                valueFieldName = StringUtils.removeEnd(valueFieldName, ".");
            }
            BigDecimal value = rowSet.getBigDecimal(valueField.getName());
            row.set(valueFieldName, value);

            data.put(rowKey, row);
        }

        Collection<JSONObject> rows = data.values();

        List<String> rowFieldNames = new ArrayList<>();
        rowFields.forEach(f -> {rowFieldNames.add(f.getField());});

        Set<String> valueNames = getValueColumns(columnGroups);
        //列合计由前端计算

        //列小计
        if (totalConfig.getCol().getShowSubTotals() && rowFields.size() > 1) {
            Map<String, List<JSONObject>> groupByRows =
                    rows.stream()
                        .collect(Collectors.groupingBy(row -> {return row.getStr(rowFields.get(0).getField());}));
            groupByRows.forEach((key, list) -> {
                JSONObject subTotalRow = new JSONObject();

                rowFieldNames.forEach(rowField -> {
                    subTotalRow.set(rowField, list.get(0).get(rowField));
                });
                subTotalRow.set(rowFieldNames.get(rowFieldNames.size() - 1), totalConfig.getCol().getSubLabel());


                List<BigDecimal> values = new ArrayList<>();
                for (String columnName : valueNames) {
                    //获得每一列的数组数组
                    list.forEach(item -> {
                        values.add(item.getBigDecimal(columnName));
                    });
                    String aggregationMethod = totalConfig.getCol().getCalcSubTotals().getAggregation();
                    BigDecimal subtotal = new BigDecimal(0);
                    if ("SUM".equalsIgnoreCase(aggregationMethod)) {
                        subtotal = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                    } else if ("AVG".equalsIgnoreCase(aggregationMethod)) {
                        subtotal =
                                values.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                                      .divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
                    } else if ("MAX".equalsIgnoreCase(aggregationMethod)) {
                        subtotal = values.stream().max(BigDecimal::compareTo).get();
                    } else if ("MIN".equalsIgnoreCase(aggregationMethod)) {
                        subtotal = values.stream().min(BigDecimal::compareTo).get();
                    }
                    //统计每个列的汇总值
                    subTotalRow.set(columnName, subtotal);
                }
                subTotalRow.set("_SUB_TOTAL_ROW", true);
                list.add(subTotalRow);
            });
            rows = groupByRows.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        }

        rows.forEach(row -> {
            //行合计
            if (totalConfig.getRow().getShowGrandTotals()) {
                List<BigDecimal> values = new ArrayList<>();
                for (Map.Entry<String, Object> stringObjectEntry : row.entrySet()) {
                    String key = stringObjectEntry.getKey();
                    if (rowFieldNames.contains(key) || "_SUB_TOTAL_ROW".equalsIgnoreCase(key)) {
                        continue;
                    }
                    BigDecimal v = new BigDecimal(String.valueOf(stringObjectEntry.getValue()));
                    values.add(v);
                }
                String aggregationMethod = totalConfig.getRow().getCalcTotals().getAggregation();
                BigDecimal total = new BigDecimal(0);
                if ("SUM".equalsIgnoreCase(aggregationMethod)) {
                    total = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                } else if ("AVG".equalsIgnoreCase(aggregationMethod)) {
                    total =
                            values.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                                  .divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
                } else if ("MAX".equalsIgnoreCase(aggregationMethod)) {
                    total = values.stream().max(BigDecimal::compareTo).get();
                } else if ("MIN".equalsIgnoreCase(aggregationMethod)) {
                    total = values.stream().min(BigDecimal::compareTo).get();
                }
                row.set("_ROW_TOTAL", total);
            }
            //行小计
            if (totalConfig.getRow().getShowSubTotals()) {
                String aggregationMethod = totalConfig.getRow().getCalcSubTotals().getAggregation();
                Map<String, BigDecimal> subTotal = new HashMap<>();
                for (ColumnGroup cg : columnGroups) {
                    if (cg.getChildren().size() == 0) {
                        continue;
                    }
                    String prefix = cg.getField();
                    List<BigDecimal> subValues = new ArrayList<>();
                    for (Map.Entry<String, Object> stringObjectEntry : row.entrySet()) {
                        String key = stringObjectEntry.getKey();
                        if (key.startsWith(prefix)) {
                            BigDecimal v = new BigDecimal(String.valueOf(stringObjectEntry.getValue()));
                            subValues.add(v);
                        }
                    }

                    BigDecimal total = new BigDecimal(0);
                    if ("SUM".equalsIgnoreCase(aggregationMethod)) {
                        total = subValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                    } else if ("AVG".equalsIgnoreCase(aggregationMethod)) {
                        total =
                                subValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                                         .divide(BigDecimal.valueOf(subValues.size()), 2, RoundingMode.HALF_UP);
                    } else if ("MAX".equalsIgnoreCase(aggregationMethod)) {
                        total = subValues.stream().max(BigDecimal::compareTo).get();
                    } else if ("MIN".equalsIgnoreCase(aggregationMethod)) {
                        total = subValues.stream().min(BigDecimal::compareTo).get();
                    }
                    subTotal.put(cg.getField() + ".summary", total);
                }

                subTotal.keySet().forEach(key -> {
                    row.set(key, subTotal.get(key));
                });
            }
        });

        //行合计
        if (totalConfig.getRow().getShowGrandTotals()) {
            ColumnGroup g = new ColumnGroup();
            g.setField("_ROW_TOTAL");
            g.setType("NUM");
            g.setTitle(totalConfig.getRow().getLabel());
            columnGroups.add(g);
        }

        //行小计
        if (totalConfig.getRow().getShowSubTotals()) {
            for (ColumnGroup cg : columnGroups) {
                if (cg.getChildren().size() > 0) {
                    ColumnGroup g = new ColumnGroup();
                    g.setField(cg.getField() + ".summary");
                    g.setType("NUM");
                    g.setTitle(totalConfig.getRow().getSubLabel());
                    cg.getChildren().add(g);
                }
            }
        }


        JSONArray result = new JSONArray();
        result.addAll(rows);
        return result;
    }

    private static String buildColumnGroups(SqlRowSet rowSet, Iterator<DatasetField> columnFieldsIterator,
                                            List<ColumnGroup> columnGroups, String prefix) {

        if (columnFieldsIterator.hasNext()) {
            DatasetField columnField = columnFieldsIterator.next();
            String column = rowSet.getString(columnField.getName());

            Optional<ColumnGroup> cg = columnGroups.stream().filter(c -> {
                return CompareUtils.compare(c.getTitle(),
                                            column) == 0;
            }).findFirst();
            if (!cg.isPresent()) {
                ColumnGroup g = new ColumnGroup();
                g.setTitle(column);
                g.setType("NUM");
                g.setField(prefix + columnField.getField() + "-" + (columnGroups.size() + 1));
                columnGroups.add(g);
                return buildColumnGroups(rowSet, columnFieldsIterator, g.getChildren(),
                                         g.getField() + ".");
            } else {
                ColumnGroup g = cg.get();
                return buildColumnGroups(rowSet, columnFieldsIterator, g.getChildren(),
                                         g.getField() + ".");
            }
        }
        return prefix;
    }

    private static HashSet<String> getValueColumns(List<ColumnGroup> columnGroups) {
        HashSet<String> names = new LinkedHashSet<>();
        for (ColumnGroup cg : columnGroups) {
            if (cg.getChildren().size() == 0) {
                names.add(cg.getField());
            } else {
                names.addAll(getValueColumns(cg.getChildren()));
            }
        }
        return names;
    }


    public static List<DataSeries> toDataSeries(SqlRowSet rowSet, List<DatasetField> fields) {
        List<DataSeries> series = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            DatasetField field = fields.get(i);
            DataSeries dataSeries = new DataSeries();
            dataSeries.setName(field.getName());
            dataSeries.setType(field.getType());
            series.add(dataSeries);
        }

        while (rowSet.next()) {
            for (int i = 0; i < fields.size(); i++) {
                DatasetField field = fields.get(i);
                String key = field.getName();
                Object value = rowSet.getObject(key);
                series.get(i).getData().add(value);
            }
        }
        return series;
    }
}
