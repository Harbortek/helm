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

package com.harbortek.helm.smartdoc.api;

import cn.hutool.json.JSONArray;
import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.smartdoc.editor.operation.util.SlateParser;
import com.harbortek.helm.smartdoc.exporter.word.Block2Docx;
import com.harbortek.helm.smartdoc.vo.PageSettingTrackerVo;
import com.harbortek.helm.smartdoc.vo.ProjectPage4BlockVo;
import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.entity.block.DocEntity;
import com.harbortek.helm.tracker.entity.block.TrackerItemBlockData;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.block.BlockParser;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.title.TitleSlateElement;
import com.harbortek.helm.tracker.entity.project.PageSettingTracker;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.service.*;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.util.BeanCopyUtils;
import com.harbortek.helm.util.JsonUtils;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/smart-doc/{projectId}/{pageId}/doc")
public class DocApi {
    @Autowired
    private DocService docService;
    @Autowired
    private TrackerItemService trackerItemService;
    @Autowired
    private TrackerService trackerService;
    @Autowired
    private ProjectPageService projectPageService;

    @Autowired
    private ProjectService projectService;


    @Parameter(name = "通过pageId查询blocks")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<DocVo> findByPageId(@PathVariable Long pageId) throws Exception {
        DocVo docVo = docService.findDocByPageId(pageId);
        if (docVo.getElements() == null || docVo.getElements().isEmpty()) {
            docVo.setElements(new ArrayList<SlateNode>());
            for (DocBlock docBlock : docVo.getBlocks()) {
                docVo.getElements().add(BlockParser.parse(docBlock));
            }
        }
        if (!docVo.getElements().isEmpty()) {
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
        //默认添加一个
//        if(docVo.getElements().isEmpty()){
//            docVo.getElements().add(new TitleSlateElement<>());
//        }
        docVo.setBlocks(new ArrayList<>());
        return ResponseEntity.ok(docVo);
    }

    @Parameter(name = "通过pageId查询blocks")
    @RequestMapping(value = "/list-preview", method = RequestMethod.GET)
    ResponseEntity<DocVo> findByPageId4Preview(@PathVariable Long pageId) {
        DocVo docVo = docService.findDocByPageId(pageId);
        return ResponseEntity.ok(docVo);
    }

    @Parameter(name = "查询 Page 工作项")
    @RequestMapping(value = "/trackerItemsWithoutInternalTrackers", method = RequestMethod.GET)
    ResponseEntity<Page<TrackerItemVo>> findTrackerItemsByPageIdWithoutInternalTrackers(@PathVariable Long pageId, Pageable pageable) {
        List<Long> refIds = new ArrayList<>();
        List<DocBlock> withoutInternalBlocks = new ArrayList<>();
        DocVo docVo = docService.findDocByPageId(pageId);
        for (int i = 0; i < docVo.getBlocks().size(); i++) {
            DocBlock docBlock = docVo.getBlocks().get(i);
            if (docBlock.getData() instanceof TrackerItemBlockData) {
                withoutInternalBlocks.add(docBlock);
            }
        }
        for (int i = 0; i < withoutInternalBlocks.size(); i++) {
            DocBlock docBlock = withoutInternalBlocks.get(i);
            if (i >= pageable.getPageNumber() * pageable.getPageSize() && i < (pageable.getPageNumber() + 1) * pageable.getPageSize()) {
                refIds.add(docBlock.getData().getRefId());
            }
        }
        Page<TrackerItemVo> page = new PageImpl(trackerItemService.findTrackerItemByIds(refIds), pageable, withoutInternalBlocks.size());

        return ResponseEntity.ok(page);
    }

    @Parameter(name = "通过pageId查询blocks并按sortNo排序")
    @RequestMapping(value = "/findOneProjectPage4Block", method = RequestMethod.GET)
    ResponseEntity<ProjectPage4BlockVo> findOneProjectPage4Block(@PathVariable Long pageId) {
        ProjectPageVo pageVo = projectPageService.findOneProjectPage(pageId);
        ProjectVo projectVo = projectService.findOneProject(pageVo.getProjectId());
        List<TrackerVo> trackers = trackerService.findTrackerByIds(pageVo.getPageSettingTrackers().stream().map(t -> t.getId()).collect(Collectors.toList()));

        ProjectPage4BlockVo projectPage4BlockVo = new ProjectPage4BlockVo();
        BeanCopyUtils.copyWithoutNullProperties(pageVo, projectPage4BlockVo);

        trackers.forEach(t -> {
            for (PageSettingTracker pageSettingTracker : pageVo.getPageSettingTrackers()) {
                if (pageSettingTracker.getId().equals(t.getId())) {
                    PageSettingTrackerVo pageSettingTrackerVo = PageSettingTrackerVo.builder().name(t.getName()).id(t.getId()).content(pageSettingTracker.getContent()).fields(new ArrayList<>()).build();

                    if (pageSettingTracker.getFieldIds() != null) {
                        for (Long fieldId : pageSettingTracker.getFieldIds()) {
                            t.getTrackerFields().stream().filter(f -> f.getId().equals(fieldId))
                                    .findFirst().ifPresent(pageSettingTrackerVo.getFields()::add);
                        }
                    }
                    trackerService.fillTrackerFields(pageSettingTrackerVo.getFields(), t.getTrackerStatuses(), projectVo.getId());
                    projectPage4BlockVo.getPageSettingTrackers().add(pageSettingTrackerVo);
                    return;
                }
            }

        });
        projectPage4BlockVo.setProjectkeyName(projectVo.getKeyName());
        return ResponseEntity.ok(projectPage4BlockVo);
    }


    @Parameter(name = "保存Blocks")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<DocEntity> save(@PathVariable Long projectId, @PathVariable Long pageId, @RequestBody String newBlocks) {
        try {
            newBlocks = URLDecoder.decode(newBlocks, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("格式错误！");
        }
        //1. 解析block，并保存为bean
        List<DocBlock> docBlocks = this.parseBlocks(newBlocks);
        //2. 遍历bclok bean，批量更新工作项（保存新的，更新已存在的）
        //2.1 比较是否更改
        // to-do
        //2.2 保存变动
        DocEntity entity = docService.saveBlocksAndTrackerItems(projectId, pageId, docBlocks, null);
        //3.比较需要删除的工作项
        //to-do
        //4.返回更新后的blocks
        return ResponseEntity.ok(entity);

    }

    @Parameter(name = "DOC 导出 WORD")
    @RequestMapping(value = "/doc2word", method = RequestMethod.GET)
    ResponseEntity exportDoc2Word(@RequestParam Long pageId, HttpServletResponse response) throws Exception {
        DocVo docVo = docService.findDocByPageId(pageId);

        String fileName = docVo.getName() + ".docx";
        String contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''"
                + URLEncoder.encode(fileName);

        response.setHeader("Content-disposition", contentDisposition);
        response.setHeader(HttpHeaders.CONTENT_TYPE,
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        Block2Docx.convert(docVo.getBlocks(), response.getOutputStream());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

//    //fix po-tl 对sdt p 节点下sdt 自动增加换行的问题
//    private void fixSdtBr(XWPFDocument xwpfDocument) {
//        SAXReader reader = new SAXReader();
//        Document doc = null;
//        try {
//            doc = reader.read(new ByteArrayInputStream(xwpfDocument.getDocument().xmlText().getBytes("UTF-8")));
//        } catch (Exception e) {
//            throw new RuntimeException("导出word,ooml 解析错误!");
//        }
//        List students = doc.selectNodes("//w:p/w:r/w:sdt");
//        for (int i = 0; i < students.size(); i++) {
//            Element item = (Element) students.get(i);
//            Element itemCopy = item.createCopy();
//            Element body = item.getParent().getParent().getParent();
//            Element pWithSdt = item.getParent().getParent();
//
//            int idx = 0;
//            for (Object element : body.elements()) {
//                if (element == pWithSdt) {
//                    body.remove(pWithSdt);
//                    body.elements().add(idx, itemCopy);
//                }
//                idx++;
//            }
//        }
//        org.w3c.dom.Document nSdtDocument = null;
//        try {
//            nSdtDocument = DocumentHelper.readDocument(new InputSource(new StringReader(doc.asXML())));
//            XmlObject newDoc = XmlObject.Factory.parse(nSdtDocument.getDocumentElement(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
//            xwpfDocument.getDocument().set(newDoc);
//        } catch (Exception e) {
//            throw new RuntimeException("导出word,ooml 修复sdt错误!");
//        }
//    }
//    @Parameter(name="DOC 导出 PDF")
//    @RequestMapping(value = "/doc2pdf", method = RequestMethod.GET)
//    ResponseEntity exportDoc2Pdf(@RequestParam Long pageId, HttpServletResponse response) throws Exception {
//        DocVo docVo = docService.findDocByPageId(pageId);
//
//        String fileName = docVo.getName() +".pdf";
//        String contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''"
//                + URLEncoder.encode(fileName);
//
//        StringBuffer sb= new StringBuffer();
//        for (DocBlock block : docVo.getBlocks()){
//            sb.append(Block2Md.convert(block)).append("  ");
//        }
//        System.out.println(sb);
//        response.setHeader("Content-disposition", contentDisposition);
//        response.setHeader(HttpHeaders.CONTENT_TYPE,
//                "application/pdf");
//        Markdown2PdfConverter
//                .newConverter()
//                .readFrom(() -> sb.toString())
//                .writeTo(out -> {
//                    try {
//                        response.getOutputStream().write(out);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .doIt();
//        return new ResponseEntity<Void>(HttpStatus.OK);
//    }

//    private Map blockWrapper(DocVo docVo) {
//        Map data = new HashMap();
//        List<Map> blocks = new ArrayList<>();
//        data.put("blocks", blocks);
//        List<DocBlock> docBlocks = docVo.getBlocks();
//
//        for (DocBlock block : docBlocks) {
//            Map item = new HashMap();
//            item.put("isTitle", false);
//            item.put("isH1", false);
//            item.put("isH2", false);
//            item.put("isH3", false);
//            item.put("isH4", false);
//            item.put("isH5", false);
//            item.put("isH6", false);
//            item.put("isParagraph", false);
//            item.put("isTrackerItem", false);
//
//            if (BlockTypes.TITLE.equals(block.getType())) {
//                item.put("text", block.getData().getText());
//                item.put("isTitle", true);
//            } else if (BlockTypes.HEADING.equals(block.getType())) {
//                item.put("text", block.getData().getText());
//                item.put("isH" + ((HeaderBlockData) block.getData()).getLevel(), true);
//            } else if (BlockTypes.PARAGRAPH.equals(block.getType())) {
//                item.put("isParagraph", true);
//                item.put("richText", MdUtils.html2Md(block));
//            } else {
//                item.put("isTrackerItem", true);
//                item.put("trackerItem", new TrackerItemRenderData(((TrackerItemBlockData) (block.getData())).getTrackerItem()));
//            }
//            blocks.add(item);
//        }
//        return data;
//    }

    private List<DocBlock> parseBlocks(String newBlocks) {
        List<DocBlock> docBlocks = new ArrayList<>();
        JSONArray blockArrays = null;
        try {
            blockArrays = new JSONArray(newBlocks);
            for (int i = 0; i < blockArrays.size(); i++) {
                String blockStr = blockArrays.getStr(i);
                DocBlock docBlock = DocBlock.newBlock(blockStr);
                docBlocks.add(docBlock);
            }
        } catch (Exception e) {
            throw new ServiceException("文档格式错误，请检查文档格式!");
        }
        return docBlocks;
    }
}
