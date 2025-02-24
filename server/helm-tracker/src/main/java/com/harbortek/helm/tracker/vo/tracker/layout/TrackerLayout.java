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

package com.harbortek.helm.tracker.vo.tracker.layout;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.util.JsonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
@NoArgsConstructor
public class TrackerLayout extends BaseVo {
    String layout;

    List<IdNameReference> fields = new ArrayList<>();

    List<String> sections = new ArrayList<>();

    List<IdNameReference> keyFields = new ArrayList<>();

    public void updateField(TrackerField trackerField) {
        if (StringUtils.isEmpty(layout)) {
            return;
        }

        JSONObject jsonObject = JsonUtils.parseObject(layout);
        JSONArray fields = jsonObject.getJSONArray("fields");
        updateRecursively(fields,trackerField);
        this.layout = JsonUtils.toJSONString(jsonObject);
    }

    private void updateRecursively(JSONArray fields, TrackerField trackerField){
        for (int i = 0; i < fields.size(); i++) {
            JSONObject field = fields.getJSONObject(i);
            Long id = field.getLong("fieldId");
            JSONObject config = field.getJSONObject("__config__");
            if (id.equals(trackerField.getId())){
                config.set("label",trackerField.getName());
                JSONObject meta = field.getJSONObject("meta");
                if (meta!=null){
                    meta = JsonUtils.parseObject(JsonUtils.toJSONString(trackerField));
                    field.set("meta",meta);
                }
            }else {
                JSONArray children = config.getJSONArray("children");
                if (children!=null && children.size()>0){
                    updateRecursively(children,trackerField);
                }
            }
        }
    }


    public void deleteField(TrackerField trackerField) {
        if (StringUtils.isEmpty(layout)) {
            return;
        }

        JSONObject jsonObject = JsonUtils.parseObject(layout);
        JSONArray fields = jsonObject.getJSONArray("fields");
        deleteRecursively(fields,trackerField);
        this.layout = JsonUtils.toJSONString(jsonObject);
    }

    private void deleteRecursively(JSONArray fields, TrackerField trackerField){
        Iterator iterator = fields.iterator();
        while (iterator.hasNext()) {
            JSONObject field = (JSONObject) iterator.next();
            Long id = field.getLong("fieldId");
            JSONObject config = field.getJSONObject("__config__");
            if (id.equals(trackerField.getId())){
                fields.remove(field);
            }else {
                JSONArray children = config.getJSONArray("children");
                if (children!=null && children.size()>0){
                    deleteRecursively(children,trackerField);
                }
            }
        }
    }



}
