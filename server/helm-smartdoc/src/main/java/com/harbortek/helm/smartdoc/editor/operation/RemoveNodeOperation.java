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

import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;

import java.util.List;

public class RemoveNodeOperation extends NodeOperation {
    private List<Integer> path;
    private SlateNode node;
    public RemoveNodeOperation(List<Integer> path,SlateNode node) {
        super(OperationType.REMOVE_NODE);
        this.path = path;
        this.node = node;
    }
    public RemoveNodeOperation(){}
    public List<Integer> getPath() {
        return path;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }

    public SlateNode getNode() {
        return node;
    }

    public void setNode(SlateNode node) {
        this.node = node;
    }
}