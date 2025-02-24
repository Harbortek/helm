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
public class BarGroupStack implements Component {

    @Autowired
    DatasetService datasetService;

    @Override
    public String getName() {
        return "bar-group-stack";
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
        List<DatasetField> stackFields = config.getExtStack();
        List<DatasetField> valueFields = config.getYaxis();
        List<DataFilter> filterFields = config.getCustomFilter();


        List<DatasetField> fields = new ArrayList<>();
        fields.addAll(dimensionFields);
        fields.addAll(subDimensionFields);
        fields.addAll(stackFields);
        fields.addAll(valueFields);

        List<DatasetField> groupByFields = new ArrayList<>();
        groupByFields.addAll(dimensionFields);
        groupByFields.addAll(subDimensionFields);
        groupByFields.addAll(stackFields);


        JSONObject result = new JSONObject();
        result.set("fields", fields);

        JSONArray data = null;
        if (dimensionFields.isEmpty() || subDimensionFields.isEmpty() || valueFields.isEmpty() || stackFields.isEmpty()) {
            //空数据
            data = new JSONArray();
        } else  {
            SqlRowSet rowSet = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields, valueFields,
                                                      filterFields);
            //多序列图，以堆叠维度为序列
            data = buildGroupSeriesData(rowSet, dimensionFields.get(0),
                                                 subDimensionFields.get(0), stackFields.get(0),
                                                 valueFields.get(0));
        }

        result.set("data", data);

        return JsonUtils.toJSONString(result);
    }

    private JSONArray buildGroupSeriesData(SqlRowSet rowSet, DatasetField dimensionField,
                                                    DatasetField subDimensionField, DatasetField stackField,
                                           DatasetField datasetField) {
        JSONArray data = new JSONArray();
        while (rowSet.next()) {
            //每个子维度单独一条记录
            JSONObject row = new JSONObject();
            Object xValue = rowSet.getObject(dimensionField.getName());
            row.set("name", xValue);
            row.set("field", xValue);

            Object gValue = rowSet.getObject(subDimensionField.getName());
            row.set("group", gValue);

            row.set("category", rowSet.getString(stackField.getName()));
            row.set("value", rowSet.getBigDecimal(datasetField.getName()));
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
