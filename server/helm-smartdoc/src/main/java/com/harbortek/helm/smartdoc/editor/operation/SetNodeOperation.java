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

public class SetNodeOperation extends NodeOperation {
    private List<Integer> path;
    private NodeProperties properties;
    private NodeProperties newProperties;
    public SetNodeOperation(List<Integer> path, NodeProperties properties,NodeProperties newProperties) {
        super(OperationType.SET_NODE);
        this.path = path;
        this.properties = properties;
        this.newProperties = newProperties;
    }

    public List<Integer> getPath() {
        return path;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }

    public NodeProperties getProperties() {
        return properties;
    }

    public void setProperties(NodeProperties properties) {
        this.properties = properties;
    }

    public NodeProperties getNewProperties() {
        return newProperties;
    }

    public void setNewProperties(NodeProperties newProperties) {
        this.newProperties = newProperties;
    }
}