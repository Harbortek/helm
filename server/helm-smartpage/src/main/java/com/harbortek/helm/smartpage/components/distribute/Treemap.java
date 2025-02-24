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

package com.harbortek.helm.smartpage.components.distribute;

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
public class Treemap implements Component {
    @Autowired
    DatasetService datasetService;
    @Override
    public String getName() {
        return "treemap";
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

        List<DatasetField> valueFields = config.getYaxis();
        List<DataFilter> filterFields = config.getCustomFilter();

        List<DatasetField> fields = new ArrayList<>();
        fields.addAll(dimensionFields);
        fields.addAll(valueFields);

        List<DatasetField> groupByFields = new ArrayList<>();
        groupByFields.addAll(dimensionFields);


        JSONObject result = new JSONObject();
        result.set("fields", fields);

        JSONArray data = new JSONArray();;
        if (!dimensionFields.isEmpty() && !valueFields.isEmpty()) {
            SqlRowSet rowSet = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields, valueFields,
                                                      filterFields);
            //单线图
            data = buildSingleSeriesData(rowSet, dimensionFields, valueFields.get(0));
        }

        result.set("data", data);

        return JsonUtils.toJSONString(result);
    }

    private JSONArray buildSingleSeriesData(SqlRowSet rowSet, List<DatasetField> dimensionFields,
                                            DatasetField valueField) {
        JSONArray data = new JSONArray();
        while (rowSet.next()) {
            JSONObject row = new JSONObject();

            StringBuilder sb = new StringBuilder();
            for(DatasetField dimensionField:dimensionFields){
                Object xValue = rowSet.getObject(dimensionField.getName());
                if (sb.length()>0){
                    sb.append("\n");
                }
                sb.append(xValue);
            }
            row.set("name", sb.toString());
            row.set("field", sb.toString());

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
