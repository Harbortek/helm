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

package com.harbortek.helm.smartdoc.importer.word.extractors;

import com.harbortek.helm.smartdoc.constants.ValueExtractorTypes;
import com.harbortek.helm.smartdoc.importer.word.rules.FieldExtractAction;
import com.harbortek.helm.tracker.util.TrackerItemUtils;
import com.harbortek.helm.tracker.util.TrackerUtils;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ValueExtractor(id = ValueExtractorTypes.TABLE_COLUMN_MAP,hasArgument = true)
public class TableExtractor implements IValueExtractor{
    @Override
    public Object extractValue(Element table, String argument) {
        HashMap columnMap = JsonUtils.toObject(argument,HashMap.class);
        if (columnMap == null) {
            return null;
        }

        Element headerRow = table.selectFirst("tr");
        if (headerRow == null) {
            return null;
        }

        List<Element> rows = new ArrayList<>(table.select("tr"));
        if (rows.size() > 1) {
            rows.remove(0);
        }
        List<Element> headers = headerRow.select("td");
        Map<Long, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            Element header = headers.get(i);
            String key = header.text();
            Object value = columnMap.get(key);
            if (value instanceof String) {
                Long fieldId = StringUtils.isNotEmpty((String)value) ? Long.valueOf((String)value) : 0L;
                if (fieldId > 0) {
                    indexMap.put(fieldId, i);
                }
            }
        }
        List<Map<Long, String>> columnMapList = new ArrayList<>();
        for (Element row : rows) {
            Map<Long, String> rowColumnMap = new HashMap<>();
            Elements columns = row.select("td");
            for (Long field : indexMap.keySet()) {
                int index = indexMap.get(field);
                if (index < columns.size()) {
                    Element column = columns.get(index);
                    String value = column.text();
                    if (StringUtils.isNotEmpty(value)) {
                        rowColumnMap.put(field, value);
                    }
                }
            }
            columnMapList.add(rowColumnMap);
        }

        return columnMapList;
    }
}
