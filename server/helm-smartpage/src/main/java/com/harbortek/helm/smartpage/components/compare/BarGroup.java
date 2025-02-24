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

package com.harbortek.helm.smartpage.components.compare;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.smartpage.components.Component;
import com.harbortek.helm.smartpage.service.DatasetService;
import com.harbortek.helm.smartpage.vo.DataConfig;
import com.harbortek.helm.smartpage.vo.DataRequest;
import com.harbortek.helm.tracker.vo.smartpage.filter.DataFilter;
import com.harbortek.helm.tracker.vo.smartpage.DatasetField;
import com.harbortek.helm.tracker.vo.smartpage.DatasetVo;
import com.harbortek.helm.util.JsonUtils;
import com.harbortek.helm.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BarGroup implements Component {

    @Autowired
    DatasetService datasetService;

    @Override
    public String getName() {
        return "bar-group";
    }

    @Override
    public String getData(Long pageId, DataRequest request) {
        DataConfig config = JsonUtils.toObject(request.getConfig(), DataConfig.class);
        assert config != null;
        Long datasetId = config.getDatasetId();
        if (!ObjectUtils.isValid(datasetId)){
            return emptyData();
        }

        DatasetVo dataset = datasetService.findById(pageId, datasetId);
        if (StringUtils.isEmpty(dataset.getSql())){
            return emptyData();
        }
        List<DatasetField> dimensionFields = config.getXaxis();
        List<DatasetField> subDimensionFields = config.getXaxisExt();
        List<DatasetField> valueFields = config.getYaxis();
        List<DataFilter> filterFields = config.getCustomFilter();


        List<DatasetField> fields = new ArrayList<>();
        fields.addAll(dimensionFields);
        fields.addAll(subDimensionFields);
        fields.addAll(valueFields);

        List<DatasetField> groupByFields = new ArrayList<>();
        groupByFields.addAll(dimensionFields);
        groupByFields.addAll(subDimensionFields);


        JSONObject result = new JSONObject();
        result.set("fields", fields);

        JSONArray data = null;
        if (dimensionFields.isEmpty() || valueFields.isEmpty()) {
            //空数据
            data = new JSONArray();
        } else if (subDimensionFields.isEmpty() && dimensionFields.size() == 1 && valueFields.size() == 1) {
            SqlRowSet rowSet = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields, valueFields,
                                                      filterFields);
            //单线图
            data = buildSingleSeriesData(rowSet, dimensionFields.get(0), valueFields.get(0));
        } else if (subDimensionFields.size() == 1 && valueFields.size() == 1) {
            SqlRowSet rowSet = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields, valueFields,
                                                      filterFields);
            //多序列图，以子维度为序列
            data = buildMultiDimensionSeriesData(rowSet, dimensionFields.get(0), subDimensionFields.get(0),
                                                 valueFields.get(0));
        } else if (subDimensionFields.isEmpty() && valueFields.size() > 1) {
            SqlRowSet rowSet = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields, valueFields,
                                                      filterFields);
            //多序列图,以值为序列
            data = buildMultiValueSeriesData(rowSet, dimensionFields.get(0), valueFields);
        }

        result.set("data", data);

        return JsonUtils.toJSONString(result);
    }

    private JSONArray buildMultiValueSeriesData(SqlRowSet rowSet, DatasetField dimensionField,
                                                List<DatasetField> valueFields) {
        JSONArray data = new JSONArray();
        while (rowSet.next()) {
            //每个指标单独一条记录
            for (DatasetField categoryField : valueFields) {
                JSONObject row = new JSONObject();
                Object xValue = rowSet.getObject(dimensionField.getName());
                row.set("name", xValue);
                row.set("field", xValue);

                String key = categoryField.getName();
                Object value = rowSet.getBigDecimal(key);
                row.set("category", categoryField.getTitle());
                row.set("value", value);
                data.add(row);
            }
        }
        return data;
    }

    private JSONArray buildMultiDimensionSeriesData(SqlRowSet rowSet, DatasetField dimensionField,
                                                    DatasetField subDimensionField, DatasetField datasetField) {
        JSONArray data = new JSONArray();
        while (rowSet.next()) {
            //每个子维度单独一条记录
            JSONObject row = new JSONObject();
            Object xValue = rowSet.getObject(dimensionField.getName());
            row.set("name", xValue);
            row.set("field", xValue);

            row.set("category", rowSet.getString(subDimensionField.getName()));
            row.set("value", rowSet.getBigDecimal(datasetField.getName()));
            data.add(row);
        }
        return data;

    }

    private JSONArray buildSingleSeriesData(SqlRowSet rowSet, DatasetField dimensionField, DatasetField valueField) {
        JSONArray data = new JSONArray();
        while (rowSet.next()) {
            JSONObject row = new JSONObject();
            Object xValue = rowSet.getObject(dimensionField.getName());
            row.set("name", xValue);
            row.set("field", xValue);

            Object yValue = rowSet.getBigDecimal(valueField.getName());
            row.set("category", valueField.getTitle());
            row.set("value", yValue);
            data.add(row);
        }
        return data;
    }

    private static String emptyData() {
        JSONObject result = new JSONObject();
        result.set("fields", new JSONArray());
        result.set("data", new JSONArray());
        return JsonUtils.toJSONString(result);
    }

}
