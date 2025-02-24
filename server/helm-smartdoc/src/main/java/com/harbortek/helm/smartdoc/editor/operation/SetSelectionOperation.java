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

import java.util.ArrayList;
import java.util.List;

public class SetSelectionOperation extends NodeOperation {
    // 假设 SetSelectionOperation 包含了需要设置的选择信息
    // 这里假设 SetSelectionOperation 有一个方法 applyToNodes 用于将选择信息应用到节点列表
    // private SelectionInfo selectionInfo;

    public SetSelectionOperation() {
        super(OperationType.SET_SELECTION);
        // 初始化 selectionInfo
    }


    // 假设 SelectionInfo 是一个包含选择信息的类
    // public SelectionInfo getSelectionInfo() {
    //     return selectionInfo;
    // }

    // public void setSelectionInfo(SelectionInfo selectionInfo) {
    //     this.selectionInfo = selectionInfo;
    // }

    // 假设 applyToNodes 方法用于将选择信息应用到节点列表
    // public void applyToNodes(List<SlateNode> nodes) {
    //     // 实现将选择信息应用到节点列表的逻辑
    // }
}