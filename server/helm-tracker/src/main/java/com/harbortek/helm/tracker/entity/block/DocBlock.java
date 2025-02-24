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

package com.harbortek.helm.tracker.entity.block;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.tracker.constants.BlockTypes;
import com.harbortek.helm.util.JsonUtils;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

@SuperBuilder
@Data
@NoArgsConstructor
@FieldNameConstants
public class DocBlock implements Serializable {

    private String id;
    private String type;
    private DocBlockData data;
    @Transient
    @Builder.Default
    private DocBlockConfig config = new DocBlockConfig();
    public DocBlock(String id, String type, DocBlockData data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public static DocBlock newBlock(String blockStr) {
        try {
            ObjectMapper objectMapper = JsonUtils.getObjectMapper();


            JSONObject blockJSON = new JSONObject(blockStr);
            JSONObject dataJson = blockJSON.getJSONObject(Fields.data);
            DocBlock newBlock = DocBlock.builder().id(blockJSON.getStr(Fields.id)).type(blockJSON.getStr(Fields.type)).build();
            if(BlockTypes.ALL_BLOCK_TYPES.contains(newBlock.getType())){
                dataJson.put("type", newBlock.getType());
            }else{
                dataJson.put("type", BlockTypes.TRACKER_ITEM);
            }
            String data = JsonUtils.toJSONString(dataJson);

            DocBlockData docBlockData = null;
            if (BlockTypes.HEADING.equals(newBlock.getType())) {
                docBlockData = objectMapper.readValue(data , HeaderBlockData.class);
            } else if (BlockTypes.PARAGRAPH.equals(newBlock.getType())) {
                docBlockData = objectMapper.readValue(data, ParagraphBlockData.class);
            } else if (BlockTypes.TITLE.equals(newBlock.getType())) {

                docBlockData = objectMapper.readValue(data, TitleBlockData.class);
            } else {
                //BlockTypes.TrackerItem
                docBlockData = objectMapper.readValue(data, TrackerItemBlockData.class);
            }
            newBlock.setData(docBlockData);
            return newBlock;
        } catch (Exception e) {
            throw new ServiceException("Block格式有误，请检查！" + blockStr);
        }

    }
}
