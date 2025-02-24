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

package com.harbortek.helm.util.tree;

import com.harbortek.helm.util.DataUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TreeUtils {
    public static <T> List<TreeNode<T>> listToTree(List<T> rows, String id, String parentId){
        Map<Object, TreeNode<T>> map = new LinkedHashMap<>();
        List<TreeNode<T>> rootNodes = new ArrayList<>();
        rows.forEach(row->{
            Object objectId =  DataUtils.getProperty(row, id);
            TreeNode<T> node = new TreeNode<>();
            node.setObject(row);
            map.put(objectId,node);

            Object objectParentId = DataUtils.getProperty(row,parentId);
            if (objectParentId==null){
                rootNodes.add(node);
            }else{
                TreeNode<T> parent = map.get(objectParentId);
                if (parent!=null){
                    parent.getChildren().add(node);
                }
            }
        });

        return rootNodes;
    }

    public static <T> void traversTree(TreeNode<T> parent, List<TreeNode<T>> rootNodes, TreeNodeAction<T> action){
        int index = 0;
        for(TreeNode<T> node: rootNodes){
            action.doAction(parent, index++, node);

            if (!node.getChildren().isEmpty()){
                traversTree(node, node.getChildren(),action);
            }
        }
    }

    public static <T> List<T> treeToList(List<TreeNode<T>> rootNodes) {
        List<T> result = new ArrayList<>();
        for(TreeNode<T> node: rootNodes){
            result.add(node.getObject());
            if (!node.getChildren().isEmpty()){
                result.addAll(treeToList(node.getChildren()));
            }
        }
        return result;
    }
}
