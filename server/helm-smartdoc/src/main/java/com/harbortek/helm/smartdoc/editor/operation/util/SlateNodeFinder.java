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

package com.harbortek.helm.smartdoc.editor.operation.util;

import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;

import java.util.ArrayList;
import java.util.List;

public class SlateNodeFinder {

    public static SlateNode findNodeByPath(List<SlateNode> nodes, List<Integer> path) {
        SlateNode currentNode = null;
        for (Integer index : path) {
            if (currentNode == null) {
                currentNode = nodes.get(index);
            } else {
                if (currentNode instanceof SlateElement<?>) {
                    int len = ((SlateElement<?>) currentNode).getChildren().size();
                    currentNode = (SlateNode) ((SlateElement) currentNode).getChildren().get(index);
                }
            }
        }
        return currentNode;
    }
    public static void removeByPath(List<SlateNode> nodes, List<Integer> path){
        if (path == null || path.isEmpty()) {
            return;
        }
        if (path.size() == 1) {
            nodes.remove(path.get(0).intValue());
        } else {
            SlateElement parent = findParentNodeByPath(nodes, path);
            if (parent != null) {
                parent.getChildren().remove(path.get(path.size() - 1).intValue());
            }
        }
    }
    public static List<Integer> previousPath(List<Integer> path) {
        List<Integer> prevPath = new ArrayList<>(path);
        int lastIndex = prevPath.size() - 1;
        prevPath.set(lastIndex, prevPath.get(lastIndex) - 1);
        return prevPath;
    }

    public static SlateElement findParentNodeByPath(List<SlateNode> nodes, List<Integer> path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        SlateElement currentNode = null;
        for (int i = 0; i < path.size() - 1; i++) {
            int index = path.get(i);
            if (currentNode == null) {
                currentNode = (SlateElement) nodes.get(index);
            } else {
                currentNode = (SlateElement) currentNode.getChildren().get(index);
            }
        }
        return currentNode;
    }

}