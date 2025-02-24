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

package com.harbortek.helm.smartpage.components.quota;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.smartpage.components.Component;
import com.harbortek.helm.smartpage.service.DatasetService;
import com.harbortek.helm.smartpage.utils.ChartUtils;
import com.harbortek.helm.smartpage.vo.DataConfig;
import com.harbortek.helm.smartpage.vo.DataRequest;
import com.harbortek.helm.smartpage.vo.DataSeries;
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
public class Gauge implements Component {
    @Autowired
    DatasetService datasetService;
    @Override
    public String getName() {
        return "gauge";
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
        List<DatasetField> groupByFields = new ArrayList<>();
        List<DatasetField> fields = config.getYaxis();

        List<DataFilter> filterFields = config.getCustomFilter();


        JSONObject result = new JSONObject();
        SqlRowSet rowSet = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields, fields, filterFields);

        List<DataSeries> series = ChartUtils.toDataSeries(rowSet, fields);
        result.set("series", series);

        JSONObject customAttr = JsonUtils.parseObject(request.getCustomAttr());
        JSONObject size = customAttr.getJSONObject("size");
        if ("dynamic".equalsIgnoreCase(size.getStr("gaugeMinType"))){
            JSONObject gaugeMinField = size.getJSONObject("gaugeMinField");
            String fieldName = gaugeMinField.getStr("id");
            String summaryMethod = gaugeMinField.getStr("summary");
            DatasetField field = new DatasetField();
            field.setField(fieldName);
            field.setName(fieldName);
            field.setTitle(fieldName+"_MIN");
            field.setType("NUM");
            field.setSummary(summaryMethod);
            field.setSort("none");
            fields.add(field);

            SqlRowSet rowSet2 = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields,
                                                       List.of(field),
                                                       filterFields);
            List<DataSeries> series2 = ChartUtils.toDataSeries(rowSet2, List.of(field));
            series.addAll(series2);
            result.set("series", series);
        }

        if ("dynamic".equalsIgnoreCase(size.getStr("gaugeMaxType"))){
            JSONObject gaugeMaxField = size.getJSONObject("gaugeMaxField");
            String fieldName = gaugeMaxField.getStr("id");
            String summaryMethod = gaugeMaxField.getStr("summary");
            DatasetField field = new DatasetField();
            field.setField(fieldName);
            field.setName(fieldName);
            field.setTitle(fieldName+"_MAX");
            field.setType("NUM");
            field.setSummary(summaryMethod);
            field.setSort("none");
            fields.add(field);

            SqlRowSet rowSet3 = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields,
                                                       List.of(field),
                                                       filterFields);
            List<DataSeries> series3 = ChartUtils.toDataSeries(rowSet3, List.of(field));
            series.addAll(series3);
            result.set("series", series);
        }


        result.set("fields", fields);
        return JsonUtils.toJSONString(result);
    }

    private static String emptyData() {
        JSONObject result = new JSONObject();
        result.set("fields", new JSONArray());
        result.set("series", new JSONArray());
        return JsonUtils.toJSONString(result);
    }
}
