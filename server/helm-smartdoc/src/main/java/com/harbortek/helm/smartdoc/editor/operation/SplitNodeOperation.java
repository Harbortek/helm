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

import java.util.List;

public class SplitNodeOperation extends NodeOperation {
    private List<Integer> path;
    private int position;
    private NodeProperties properties;

    public SplitNodeOperation(List<Integer> path, int position, NodeProperties properties) {
        super(OperationType.SPLIT_NODE);
        this.path = path;
        this.position = position;
        this.properties = properties;
    }

    public List<Integer> getPath() {
        return path;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public NodeProperties getProperties() {
        return properties;
    }

    public void setProperties(NodeProperties properties) {
        this.properties = properties;
    }
}