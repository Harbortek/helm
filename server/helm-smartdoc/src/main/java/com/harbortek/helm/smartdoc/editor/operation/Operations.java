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

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;

import java.util.ArrayList;
import java.util.List;

public class Operations {

    public static List<Operation> parse(String operationsStr) throws JsonProcessingException {
        List<Operation> operations = new ArrayList<>();
        //1、解析字符串至json数组
        JSONArray jsonArray = new JSONArray(operationsStr);
        //2、遍历数组，判断元素类型，转换为不同的operation对象
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String type = obj.getStr("type");
            switch (type) {
                case OperationType.INSERT_TEXT:
                    operations.add(obj.toBean(InsertTextOperation.class));
                    break;
                case OperationType.REMOVE_TEXT:
                    operations.add(obj.toBean(RemoveTextOperation.class));
                    break;

                case OperationType.INSERT_NODE: {
                    String nodeObj = wrapperText(obj.getJSONObject("node"));
                    SlateNode slateNode = Nodes.parse(nodeObj);
                    obj.set("node", null);
                    InsertNodeOperation bean = obj.toBean(InsertNodeOperation.class);
                    bean.setNode(slateNode);
                    operations.add(bean);
                    break;
                }
                case OperationType.REMOVE_NODE: {
                    String nodeObj = wrapperText(obj.getJSONObject("node"));
                    SlateNode slateNode = Nodes.parse(nodeObj);
                    obj.set("node", null);
                    RemoveNodeOperation bean = obj.toBean(RemoveNodeOperation.class);
                    bean.setNode(slateNode);
                    operations.add(bean);
                    break;
                }
                case OperationType.SPLIT_NODE:
                    operations.add(obj.toBean(SplitNodeOperation.class));
                    break;
                case OperationType.MERGE_NODE:
                    operations.add(obj.toBean(MergeNodeOperation.class));
                    break;
                case OperationType.MOVE_NODE:
                    operations.add(obj.toBean(MoveNodeOperation.class));
                    break;
                case OperationType.SET_NODE:
                    operations.add(obj.toBean(SetNodeOperation.class));
                    break;
                case OperationType.SET_SELECTION:
                    operations.add(obj.toBean(SetSelectionOperation.class));
                    break;
                default:
                    throw new RuntimeException("不支持的操作类型:" + type);
            }

        }


        return operations;
    }

    public static String wrapperText(JSONObject nodeMap) throws JsonProcessingException {
        if (nodeMap.getStr("type") == null) {
            nodeMap.set("type", "text");
        }
        JSONArray children = nodeMap.getJSONArray("children");
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                JSONObject child = (JSONObject) children.get(i);
                wrapperText(child);
            }
        }
        return nodeMap.toString();
    }
}