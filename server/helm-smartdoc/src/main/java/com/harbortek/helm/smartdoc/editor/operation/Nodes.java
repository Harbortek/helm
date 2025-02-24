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

package com.harbortek.helm.smartdoc.editor.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.util.JsonUtils;

public class Nodes {
    // Common properties for all nodes
    public static SlateNode parse(String nodeMap) throws JsonProcessingException {
//        Object obj = nodeMap.get("type");
//        String type = null;
//        if (obj != null) {
//            type = obj.toString();
//        } else {
//            type = "text";
//        }
//        SlateNode node = null;
        SlateNode node = JsonUtils.getObjectMapper().readValue(nodeMap, SlateNode.class);
//        switch (type) {
//            case "tracker-item":
//                node = JSONUtil.toBean(nodeMap, new TypeReference<TrackerItemSlateElement>() {
//                }, true);
//            case "image":
//                node = JSONUtil.toBean(nodeMap, new TypeReference<ImageSlateElement>() {
//                }, true);
//                break;
//            default:
//                node = JSONUtil.toBean(nodeMap, new TypeReference<TrackerItemSlateElement>() {
//                }, true);
//        }
        return node;
    }
}