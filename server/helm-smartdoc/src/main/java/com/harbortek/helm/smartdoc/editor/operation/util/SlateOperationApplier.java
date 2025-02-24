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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.harbortek.helm.smartdoc.editor.operation.*;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.SlateElements;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.service.TrackerItemService;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SlateOperationApplier {
    private TrackerItemService trackerItemService;

    public SlateOperationApplier(TrackerItemService trackerItemService) {
        this.trackerItemService = trackerItemService;
    }

    private void applyInsertTextOperation(List<SlateNode> nodes, InsertTextOperation operation) {
        List<Integer> path = operation.getPath();
        int offset = operation.getOffset();
        String textToInsert = operation.getText();

        SlateText targetTextNode = (SlateText) SlateNodeFinder.findNodeByPath(nodes, path);
        if (targetTextNode != null && targetTextNode.getText() != null) {
            String originalText = targetTextNode.getText();
            String newText = originalText.substring(0, offset) + textToInsert + originalText.substring(offset);
            targetTextNode.setText(newText);
        }
        tryAndApplyTrackerItem(nodes, path.subList(0, 1), operation.getType());
    }

    private void applyRemoveNodeOperation(List<SlateNode> nodes, RemoveNodeOperation operation) {
        List<Integer> path = operation.getPath();
        SlateNodeFinder.removeByPath(nodes, path);
        if (path.size() == 1) {
            if (operation.getNode() instanceof TrackerItemSlateElement<?>) {
                tryAndApplyTrackerItem(nodes, path.subList(0, 1), operation.getType());
            }
        } else {
            tryAndApplyTrackerItem(nodes, path.subList(0, 1), OperationType.REMOVE_TEXT);
        }
    }

    private void applyRemoveTextOperation(List<SlateNode> nodes, RemoveTextOperation operation) {
        List<Integer> path = operation.getPath();
        int offset = operation.getOffset();
        int length = operation.getText().length();

        SlateText targetTextNode = (SlateText) SlateNodeFinder.findNodeByPath(nodes, path);
        if (targetTextNode != null && targetTextNode.getText() != null) {
            String originalText = targetTextNode.getText();
            String newText = originalText.substring(0, offset) + originalText.substring(offset + length);
            targetTextNode.setText(newText);
        }
        tryAndApplyTrackerItem(nodes, path.subList(0, 1), operation.getType());
    }

    private void applyInsertNodeOperation(List<SlateNode> nodes, InsertNodeOperation operation) throws IOException {
        List<Integer> path = operation.getPath();
        SlateNode node = operation.getNode();
        SlateElement parent = null;
        if (path.size() > 1) {
            parent = (SlateElement) SlateNodeFinder.findNodeByPath(nodes, path.subList(0, path.size() - 1));
        }
        if (parent != null) {
            parent.getChildren().add(path.get(path.size() - 1).intValue(), node);
        } else {
            nodes.add(path.get(path.size() - 1).intValue(), node);
        }
        tryAndApplyTrackerItem(nodes, path.subList(0, 1), operation.getType());
    }

    private void applySplitNodeOperation(List<SlateNode> nodes, SplitNodeOperation operation) throws IOException {
        List<Integer> path = operation.getPath();
        int position = operation.getPosition();
        NodeProperties properties = operation.getProperties();
        //文本节点截取文字
        SlateNode currentNode = SlateNodeFinder.findNodeByPath(nodes, path);
        if (currentNode instanceof SlateText) {
            SlateText targetTextNode = (SlateText) currentNode;
            String originalText = targetTextNode.getText();
            String firstPart = originalText.substring(0, position);
            String secondPart = originalText.substring(position);
            targetTextNode.setText(firstPart);
            //后一个为空，则不插入
            SlateText newTextNode = new SlateText();
            newTextNode.setText(secondPart);
            BeanUtil.fillBeanWithMap(properties, newTextNode, true);
            SlateNode parent = SlateNodeFinder.findParentNodeByPath(nodes, path);
            if (parent != null && parent instanceof SlateElement) {
                int index = ((SlateElement<SlateNode>) parent).getChildren().indexOf(targetTextNode);
                ((SlateElement<SlateNode>) parent).getChildren().add(index + 1, newTextNode);
            } else {
                int index = nodes.indexOf(targetTextNode);
                nodes.add(index + 1, newTextNode);
            }
            tryAndApplyTrackerItem(nodes, path.subList(0, 1), operation.getType());
            //非文本节点 在[...path,position]位置插入新节点
        } else {
            //new 节点
            SlateElement wrapperElem = (SlateElement) SlateParser.parseOne(JSONUtil.toJsonStr(properties));

            SlateElement prevElem = (SlateElement) SlateNodeFinder.findNodeByPath(nodes, path);
            if (prevElem != null) {
                int childrenCount = prevElem.getChildren().size();
                for (int i = position; i < childrenCount; i++) {
                    wrapperElem.getChildren().add(prevElem.getChildren().get(i));
                }
                prevElem.setChildren(new ArrayList(prevElem.getChildren().subList(0, position)));
            }
            SlateNode parent = SlateNodeFinder.findParentNodeByPath(nodes, path);
            if (parent != null && parent instanceof SlateElement) {
                int index = path.get(path.size() - 1) + position;
                ((SlateElement<SlateNode>) parent).getChildren().add(index, wrapperElem);
            } else {
                int index = path.get(0);
                nodes.add(index + 1, wrapperElem);
            }
        }
//        if (targetTextNode != null && targetTextNode.getText() != null) {
//            String originalText = targetTextNode.getText();
//            String firstPart = originalText.substring(0, offset);
//            String secondPart = originalText.substring(offset);
//
//            targetTextNode.setText(firstPart);
//
//            SlateText newTextNode = new SlateText();
//            newTextNode.setText(secondPart);
//
//            SlateNode parent = SlateNodeFinder.findParentNodeByPath(nodes, path);
//            if (parent != null && parent instanceof SlateElement) {
//                int index = ((SlateElement<SlateNode>) parent).getChildren().indexOf(targetTextNode);
//                ((SlateElement<SlateNode>) parent).getChildren().add(index + 1, newTextNode);
//            } else {
//                int index = nodes.indexOf(targetTextNode);
//                nodes.add(index + 1, newTextNode);
//            }
//        }
    }

    private void applyMoveNodeOperation(List<SlateNode> nodes, MoveNodeOperation operation) {
        List<Integer> sourcePath = operation.getPath();
        List<Integer> targetPath = operation.getNewPath();

        SlateNode nodeToMove = SlateNodeFinder.findNodeByPath(nodes, sourcePath);
        if (nodeToMove != null) {
            SlateNode sourceParent = SlateNodeFinder.findParentNodeByPath(nodes, sourcePath);
            if (sourceParent != null && sourceParent instanceof SlateElement) {
                ((SlateElement<SlateNode>) sourceParent).getChildren().remove(nodeToMove);
            } else {
                nodes.remove(nodeToMove);
            }

            SlateNode targetParent = SlateNodeFinder.findParentNodeByPath(nodes, targetPath);
            if (targetParent != null && targetParent instanceof SlateElement) {
                ((SlateElement<SlateNode>) targetParent).getChildren().add(nodeToMove);
            } else {
                if (targetParent == null && !targetPath.isEmpty()) {
                    int index = targetPath.get(0);
                    if (index >= 0 && index <= nodes.size()) {
                        nodes.add(index, nodeToMove);
                    } else {
                        nodes.add(nodeToMove);
                    }
                } else {
                    nodes.add(nodeToMove);
                }
            }
        }
    }

    // 新增 applyMergeNodeOperation 方法
    private void applyMergeNodeOperation(List<SlateNode> nodes, MergeNodeOperation operation) throws IOException {
        List<Integer> path = operation.getPath();
        int position = operation.getPosition();

        //文本节点截取文字
        SlateNode currentNode = SlateNodeFinder.findNodeByPath(nodes, path);
        List<Integer> previousPath = SlateNodeFinder.previousPath(path);
        SlateNode previousNode = (SlateNode) SlateNodeFinder.findNodeByPath(nodes, previousPath);
        if (currentNode instanceof SlateText) {
            SlateText previousTextNode = (SlateText) previousNode;
            String originalText = previousTextNode.getText().substring(0, position);
            String otherText = previousTextNode.getText().substring(position);
            originalText += ((SlateText) currentNode).getText();
            previousTextNode.setText(originalText + otherText);
            SlateNodeFinder.removeByPath(nodes, path);

        } else {
            SlateNodeFinder.removeByPath(nodes, path);
            if (previousNode != null && previousNode instanceof SlateElement) {
                SlateElement previousElem = (SlateElement) previousNode;
//                int index = previousElem.getChildren().size() - 1 + position;
                int index = position;
                SlateElement<SlateText> currentElem = (SlateElement) currentNode;
                String originalText = "";
                for (SlateText child : currentElem.getChildren()) {
                    originalText += child.getText();
                }
                SlateText newTextNode = new SlateText();
                newTextNode.setText(originalText);
                int diff = position - previousElem.getChildren().size();
                int initLen = previousElem.getChildren().size();
                for (int i = 0; i < diff; i++) {
                    previousElem.getChildren().add(initLen + i, new SlateText());

                }
                previousElem.getChildren().add(index, newTextNode);
            }
        }
        tryAndApplyTrackerItem(nodes, previousPath.subList(0, 1), operation.getType());
    }

    // 新增 applySetNodeOperation 方法
    private void applySetNodeOperation(List<SlateNode> nodes, SetNodeOperation operation) {
        List<Integer> path = operation.getPath();
        NodeProperties newProperties = operation.getNewProperties();
        NodeProperties properties = operation.getProperties();
        SlateNode targetNode = SlateNodeFinder.findNodeByPath(nodes, path);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();
            Object oldValue = entry.getValue();

            if (!newProperties.containsKey(key)) {
                if (oldValue instanceof Boolean) {
                    newProperties.put(key, oldValue);
                }
                if (oldValue instanceof String) {
                    newProperties.put(key, "");
                }

            }
        }

        if (targetNode != null) {
            BeanUtil.fillBeanWithMap(newProperties, targetNode, true);
            if (StringUtils.isEmpty(targetNode.getType())) {
                targetNode.setType(SlateElements.TEXT);
            }
        }
        tryAndApplyTrackerItem(nodes, path.subList(0, 1), operation.getType());

    }

    // 新增 applySetSelectionOperation 方法
    private void applySetSelectionOperation(List<SlateNode> nodes, SetSelectionOperation operation) {
        // 假设 SetSelectionOperation 包含了需要设置的选择信息
        // 这里假设 SetSelectionOperation 有一个方法 applyToNodes 用于将选择信息应用到节点列表
        // operation.applyToNodes(nodes);
    }

    public void applyOperation(List<SlateNode> nodes, Operation operation) throws IOException {
        String type = operation.getType();
        switch (type) {
            case OperationType.INSERT_TEXT:
                applyInsertTextOperation(nodes, (InsertTextOperation) operation);
                break;
            case OperationType.REMOVE_TEXT:
                applyRemoveTextOperation(nodes, (RemoveTextOperation) operation);
                break;
            case OperationType.REMOVE_NODE:
                RemoveNodeOperation removeNodeOperation = (RemoveNodeOperation) operation;
                applyRemoveNodeOperation(nodes, removeNodeOperation);
                break;
            case OperationType.INSERT_NODE:
                applyInsertNodeOperation(nodes, (InsertNodeOperation) operation);
                break;
            case OperationType.SPLIT_NODE:
                applySplitNodeOperation(nodes, (SplitNodeOperation) operation);
                break;
            case OperationType.MOVE_NODE:
                applyMoveNodeOperation(nodes, (MoveNodeOperation) operation);
                break;
            case OperationType.SET_NODE:
                applySetNodeOperation(nodes, (SetNodeOperation) operation);
                break;
            case OperationType.SET_SELECTION:
                applySetSelectionOperation(nodes, (SetSelectionOperation) operation);
                break;
            // 新增 MERGE_NODE 操作类型处理逻辑
            case OperationType.MERGE_NODE:
                applyMergeNodeOperation(nodes, (MergeNodeOperation) operation);
                break;
            default:
                throw new IllegalArgumentException("Unsupported operation type: " + type);
        }

    }

    /**
     * 尝试更新trackerItem
     *
     * @param nodes
     * @param fPath
     * @param opType
     */
    private void tryAndApplyTrackerItem(List<SlateNode> nodes, List<Integer> fPath, String opType) {
        SlateNode fNode = SlateNodeFinder.findNodeByPath(nodes, fPath);
        TrackerItemSlateElement trackerItemSlateElement = null;
        if (fNode != null && fNode instanceof TrackerItemSlateElement<?>) {
            trackerItemSlateElement = (TrackerItemSlateElement) fNode;
        } else {
            return;
        }
        TrackerItemVo trackerItem = this.trackerItemService.findOneTrackerItem(Long.parseLong(trackerItemSlateElement.getRef()));

        trackerItemSlateElement.getChildren().forEach(child -> {
            if (child instanceof TrackerItemSlateElement.TrackerItemTitleSlateElement) {
                TrackerItemSlateElement.TrackerItemTitleSlateElement title = ((TrackerItemSlateElement.TrackerItemTitleSlateElement) child);
                StringBuffer sb = new StringBuffer();
                title.getChildren().forEach(text -> {
                    sb.append(((SlateText) text).getText());
                });
                trackerItem.setName(sb.toString());
            }
            if (child instanceof TrackerItemSlateElement.TrackerItemDescriptionSlateElement) {
                TrackerItemSlateElement.TrackerItemDescriptionSlateElement desc = ((TrackerItemSlateElement.TrackerItemDescriptionSlateElement) child);
                trackerItem.setDescription(JsonUtils.toJSONString(desc.getChildren()));
            }
        });
        switch (opType) {
            case OperationType.INSERT_TEXT:
                trackerItemService.updateTrackerItem(trackerItem);
                break;
            case OperationType.REMOVE_TEXT:
                trackerItemService.updateTrackerItem(trackerItem);
                break;
            case OperationType.REMOVE_NODE:
                trackerItemService.deleteOneTrackerItem(trackerItem.getId());
                break;
            case OperationType.INSERT_NODE:
                trackerItemService.updateTrackerItem(trackerItem);
                break;
            case OperationType.SPLIT_NODE:
                trackerItemService.updateTrackerItem(trackerItem);
                break;
            case OperationType.MOVE_NODE:
                return;
            case OperationType.SET_NODE:
                trackerItemService.updateTrackerItem(trackerItem);
                break;
            case OperationType.SET_SELECTION:
                return;
            case OperationType.MERGE_NODE:
                trackerItemService.updateTrackerItem(trackerItem);
                break;
            default:
                break;
        }
    }

}
