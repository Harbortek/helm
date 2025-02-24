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

package com.harbortek.helm.smartpage.components.relation;

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
public class Scatter implements Component {

    @Autowired
    DatasetService datasetService;
    @Override
    public String getName() {
        return "scatter";
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
        List<DataFilter> filterFields = config.getCustomFilter();
        List<DatasetField> quotaFields = config.getYaxis();
        List<DatasetField> extBubble = config.getExtBubble();


        List<DatasetField> fields = new ArrayList<>();
        fields.addAll(dimensionFields);
        fields.addAll(quotaFields);
        fields.addAll(extBubble);

        List<DatasetField> groupByFields = new ArrayList<>();
        groupByFields.addAll(dimensionFields);

        List<DatasetField> valueList = new ArrayList<>();
        valueList.addAll(quotaFields);
        valueList.addAll(extBubble);


        JSONObject result = new JSONObject();
        result.set("fields", fields);

        JSONArray data = new JSONArray();;
        if (dimensionFields.size() == 1 && quotaFields.size() == 1 && extBubble.size()==1) {
            SqlRowSet rowSet = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields, valueList,
                                                      filterFields);
            //单线图
            data = buildSingleSeriesData(rowSet, dimensionFields.get(0), quotaFields.get(0),extBubble.get(0));
        }else if (dimensionFields.size() == 1 && quotaFields.size() > 1 && extBubble.size()==1){
            SqlRowSet rowSet = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields, valueList,
                                                      filterFields);
            //单线图
            data = buildMultiValueSeriesData(rowSet, dimensionFields.get(0), quotaFields,extBubble.get(0));
        }

        result.set("data", data);

        return JsonUtils.toJSONString(result);
    }

    private JSONArray buildSingleSeriesData(SqlRowSet rowSet, DatasetField dimensionField, DatasetField valueField,
                                            DatasetField bubbleField) {
        JSONArray data = new JSONArray();
        while (rowSet.next()) {
            JSONObject row = new JSONObject();
            Object xValue = rowSet.getObject(dimensionField.getName());
            row.set("name", xValue);
            row.set("field", xValue);

            row.set("category",valueField.getTitle());
            row.set("value", rowSet.getBigDecimal(valueField.getName()));
            row.set("popSize", rowSet.getBigDecimal(bubbleField.getName()).intValue());
            data.add(row);
        }
        return data;
    }

    private JSONArray buildMultiValueSeriesData(SqlRowSet rowSet, DatasetField dimensionField,
                                                List<DatasetField> valueFields,DatasetField bubbleField) {
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
                row.set("popSize", rowSet.getBigDecimal(bubbleField.getName()).intValue());
                data.add(row);
            }
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
