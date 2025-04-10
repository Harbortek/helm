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

package com.harbortek.helm.tracker.service;

import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.entity.block.DocBlockLink;
import com.harbortek.helm.tracker.entity.block.DocumentEntity;
import com.harbortek.helm.tracker.entity.block.TrackerItemBlockData;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;

import java.util.List;

public interface DocService {

    public DocBlock saveBlockAndTrackerItem(Long docId, DocBlock docBlock);

    DocumentEntity saveBlocksAndTrackerItems(Long projectId, Long pageId, List<SlateNode> elements);

    DocumentEntity saveBlocksAndTrackerItems(Long projectId, Long pageId, List<SlateNode> elements, List<DocBlockLink> docBlockLinks);

    DocVo findDocByPageId(Long pageId);

    List<TrackerItemVo> findTrackerItemByIds(List<Long> itemIds);

    List<DocVo> findDocByIds(List<Long> pageIds);

    TrackerItemBlockData.InnerTrackerItemVo fillTrackerItemVo(TrackerItemVo trackerItemVo);

    void createDoc(DocVo doc);

    DocumentEntity findOneDoc(Long docId);

    public DocumentEntity saveDoc(DocumentEntity documentEntity);

//    void saveBlockAndTrackerItemV2(Long projectId, DocEntity doc, List<DocBlock> toAdd, List<DocBlock> toUpdate, List<DocBlock> toDelete);
}
