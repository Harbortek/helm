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

/**
 * 指标卡
 */
@Service
public class IndicatorCard implements Component {
    @Autowired
    DatasetService datasetService;

    @Override
    public String getName() {
        return "indicator-card";
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
        result.set("fields", fields);
        SqlRowSet rowSet = datasetService.groupBy(pageId, dataset, request.getParams(), groupByFields, fields, filterFields);

        List<DataSeries> series = ChartUtils.toDataSeries(rowSet, fields);
        result.set("series", series);

        return JsonUtils.toJSONString(result);
    }

    private static String emptyData() {
        JSONObject result = new JSONObject();
        result.set("fields", new JSONArray());
        result.set("series", new JSONArray());
        return JsonUtils.toJSONString(result);
    }
}
