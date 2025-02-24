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

package com.harbortek.helm.smartdoc.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.harbortek.helm.smartdoc.editor.operation.util.SlateParser;
import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.entity.block.DocEntity;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.block.BlockParser;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.service.DocService;
import com.harbortek.helm.tracker.service.TrackerItemService;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 被操作的整体数据对象
 */
@Component
@Data
@Log
public class Operator {

    @Autowired
    private DocService docService;
    @Autowired
    private TrackerItemService trackerItemService;

    private Map<Long, DocVo> docVoMap = new HashMap<>();
    private Map<Long, DocVoStatus> docVoStatusMap = new HashMap<>();

    public DocVo get(Long docId) {
        DocVo docVo = docVoMap.get(docId);
        if (docVo == null) {
            DocEntity docEntity = docService.findOneDoc(docId);
            docEntity.setBlocks(new ArrayList<>(docEntity.getBlocks()));
            docVo = DataUtils.toVo(docEntity, DocVo.class);
            //element 不存在时，从Block中解析
            if (docVo.getElements() == null || docVo.getElements().isEmpty()) {
                docVo.setElements(new ArrayList<SlateNode>());
                for (DocBlock docBlock : docVo.getBlocks()) {
                    docVo.getElements().add(BlockParser.parse(docBlock));
                }
            } else {
                List<Long> itemIds = docVo.getElements().stream()
                        .filter(slateNode -> slateNode instanceof TrackerItemSlateElement<?>)
                        .map(slateNode -> Long.parseLong(((TrackerItemSlateElement<?>) slateNode).getRef())).toList();
                Map<String, TrackerItemVo> trackerItemMap =
                        trackerItemService.findTrackerItemByIds(itemIds)
                                .stream()
                                .collect(Collectors.toMap(t -> t.getId().toString(), t -> t));
                List<SlateNode> list = docVo.getElements().stream().filter(slateNode -> slateNode instanceof TrackerItemSlateElement<?>).toList();
                for (SlateNode slateNode : list) {
                    TrackerItemSlateElement<?> trackerItemSlateElement = (TrackerItemSlateElement<?>) slateNode;
                    TrackerItemVo item = trackerItemMap.get(trackerItemSlateElement.getRef());
                    for (Object child : trackerItemSlateElement.getChildren()) {
                        if (child instanceof TrackerItemSlateElement.TrackerItemTitleSlateElement) {
                            TrackerItemSlateElement.TrackerItemTitleSlateElement title = ((TrackerItemSlateElement.TrackerItemTitleSlateElement) child);
                            List<SlateText> titleChildren = new ArrayList<>();
                            titleChildren.add(new SlateText(item.getName()));
                            title.setChildren(titleChildren);
                        }
                        if (child instanceof TrackerItemSlateElement.TrackerItemDescriptionSlateElement) {
                            TrackerItemSlateElement.TrackerItemDescriptionSlateElement desc = ((TrackerItemSlateElement.TrackerItemDescriptionSlateElement) child);
                            List<SlateNode> descChildren = new ArrayList<>();
                            try {
                                List<SlateNode> descSlateNodeChildren = SlateParser.parse(new JSONArray(item.getDescription()));
                                if (!descSlateNodeChildren.isEmpty()) {
                                    for (SlateNode slateNodeChild : descSlateNodeChildren) {
                                        if (slateNodeChild instanceof SlateText) {
                                            //paragraph 包裹
                                            slateNodeChild = new ParagraphSlateElement<>();
                                            ((ParagraphSlateElement<?>) slateNodeChild).getChildren().add(slateNodeChild);
                                        }
                                    }
                                } else {
                                    ParagraphSlateElement pe = new ParagraphSlateElement<>();
                                    pe.getChildren().add(new SlateText());
                                    descSlateNodeChildren.add(pe);
                                }

                                descChildren.addAll(descSlateNodeChildren);
                            } catch (Exception e) {
                            }
                            desc.setChildren(descChildren);
                        }
                    }
                    trackerItemSlateElement.setTrackerItem(item);
                }
            }
        }
        docVoMap.put(docId, docVo);
        docVoStatusMap.put(docId, DocVoStatus.IN_USE);
        return docVo;
    }

    public void updateStatus(Long docId, DocVoStatus status) {
        docVoStatusMap.put(docId, status);
    }

    public void tryToSave() {
        docVoMap.forEach((k, v) -> {
            if (docVoStatusMap.get(k) == null || DocVoStatus.UNUSED.equals(docVoStatusMap.get(k))) {
                DocEntity newDocEntity = DataUtils.toEntity(v, DocEntity.class);
                docService.saveDoc(newDocEntity);
                DocVo newDocVo = new DocVo();
                BeanUtil.copyProperties(v, newDocVo, true);
                log.info(() -> {
                    return "save doc " + newDocVo.getId() + " successed";
                });
            }
        });
    }

    public enum DocVoStatus {
        UNUSED,
        IN_USE
    }
}
