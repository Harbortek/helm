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

public class OperationType {
    public static final String INSERT_NODE = "insert_node";
    public static final String REMOVE_NODE = "remove_node";
    public static final String INSERT_TEXT = "insert_text";
    public static final String REMOVE_TEXT = "remove_text";
    public static final String SPLIT_NODE = "split_node";
    public static final String MERGE_NODE = "merge_node";
    public static final String MOVE_NODE = "move_node";
    public static final String SET_NODE = "set_node";
    public static final String SET_SELECTION = "set_selection";
}