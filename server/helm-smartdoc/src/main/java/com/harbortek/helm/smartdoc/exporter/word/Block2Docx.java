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

package com.harbortek.helm.smartdoc.exporter.word;

import com.harbortek.helm.smartdoc.exporter.word.html2docx.converter.HtmlToOpenXMLConverter;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import com.harbortek.helm.tracker.constants.BlockTypes;
import com.harbortek.helm.tracker.entity.block.*;
import com.harbortek.helm.tracker.util.ResourceUtils;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.toc.TocGenerator;
import org.docx4j.wml.*;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;

public class Block2Docx {
    private static Logger logger = Logger.getLogger(Block2Docx.class.getName());

    private static final String DOCX_TPL = "/tpl/docx/smartdoc.docx";

    private static final String TITLE_TPL = "<b-title data-_type={0}>{1}</b-title>";
    private static final String HEADING_TPL = "<h{1} data-_type={0}>{2}</h{1}>";
    private static final String TRACKER_ITEM_TPL = "<b-p data-_type={0}>" +
            "<b-span-id>{1}</b-span-id>" +
            "<b-span-code>{2}</b-span-code>" +
            "<b-span-title>{3}</b-span-title>" +
            "{4}" +
            "</b-p>";
    private static final String PARAGRAPH_TPL = "<div data-_type={0}>{1}</div>";

    private static String convert2Html(DocBlock docBlock) throws IOException {
        DocBlockData docBlockData = docBlock.getData();
        String html = StringUtils.EMPTY;
        if (docBlockData instanceof TitleBlockData) {
            html = MessageFormat.format(TITLE_TPL,
                    BlockTypes.TITLE, docBlock.getData().getText());
        } else if (docBlockData instanceof HeaderBlockData) {
            HeaderBlockData headerBlockData = (HeaderBlockData) docBlockData;
            html = MessageFormat.format(HEADING_TPL,
                    BlockTypes.HEADING, headerBlockData.getLevel(), docBlock.getData().getText());
        } else if (docBlockData instanceof ParagraphBlockData) {
            html = MessageFormat.format(PARAGRAPH_TPL,
                    BlockTypes.PARAGRAPH, docBlock.getData().getText());
        } else if (docBlockData instanceof TrackerItemBlockData) {
            TrackerItemBlockData.InnerTrackerItemVo trackerItemVo = ((TrackerItemBlockData) docBlockData).getTrackerItem();
            html = MessageFormat.format(TRACKER_ITEM_TPL,
                    BlockTypes.TRACKER_ITEM, trackerItemVo.getId() + "",
                    trackerItemVo.getItemNo(), StringUtils.trimToEmpty(trackerItemVo.getName()), trackerItemVo.getDescription());
        }
        return html;
    }

    private static void convert2Docx(String html, OutputStream outputStream) throws Exception {
        Resource tpl = ResourceUtils.getResource("classpath:" + DOCX_TPL);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(tpl.getInputStream());
        wordMLPackage = new HtmlToOpenXMLConverter().convert(
                html,
                clearDocumentContent(wordMLPackage)
                                                            );
        updateToc(wordMLPackage);
//        addTOC(wordMLPackage);
//        addPageBreak(wordMLPackage);

        wordMLPackage.save(outputStream);
    }

    private static void updateToc(WordprocessingMLPackage wordMLPackage) throws Exception {
        TocGenerator tocGenerator = new TocGenerator(wordMLPackage);
        tocGenerator.updateToc(true);
    }

    //生成目录
    private static void addTOC(WordprocessingMLPackage wordMLPackage) throws Exception {

        ObjectFactory factory = RunUtils.getObjectFactory();// 创建新的section properties
        SectPr sectPr = factory.createSectPr();
        // 创建新的段落
        P paragraph = factory.createP();
        // 创建新的运行
        R run = factory.createR();
        // 创建新的文本
        Text text = factory.createText();
        text.setValue("This is a new section.");

        // 将文本添加到运行中
        run.getContent().add(text);

        // 将运行添加到段落中
        paragraph.getContent().add(run);

        // 将段落添加到新的section中
        wordMLPackage.getMainDocumentPart().addObject(paragraph);

        // 将新的section properties设置到新的section中
        wordMLPackage.getMainDocumentPart().addObject(sectPr);


        TocGenerator tocGenerator = new TocGenerator(wordMLPackage);
        SdtBlock sdtBlock = tocGenerator.generateToc(wordMLPackage.getMainDocumentPart().getContent().size(),
                " TOC \\o \"1-3\" \\h \\u ", true);
        wordMLPackage.getMainDocumentPart().getContent().add(0, sdtBlock);


    }

    private static void addPageBreak(WordprocessingMLPackage wordMLPackage) {
        // 创建分页符元素
        Br pageBreak = new Br();
        pageBreak.setType(STBrType.PAGE);

        // 创建段落和运行元素，并将分页符添加到运行元素
        P paragraph = new P();
        R run = new R();
        run.getContent().add(pageBreak);

        // 将运行元素添加到段落
        paragraph.getContent().add(run);


        wordMLPackage.getMainDocumentPart().getContent().add(1, paragraph);
    }

    /**
     * 清除模板中多余的段落
     *
     * @param wordMLPackage
     * @return
     */
    private static WordprocessingMLPackage clearDocumentContent(WordprocessingMLPackage wordMLPackage) {
//        if (wordMLPackage != null) {
//            MainDocumentPart mainPart = wordMLPackage.getMainDocumentPart();
//            // 获取文档中的所有段落
//            List<Object> contentList = mainPart.getContent();
//            ((Body) ((P) contentList.get(0)).getParent()).getContent().remove(0);
//
//        }
        return wordMLPackage;
    }

    public static void convert(List<DocBlock> docBlocks, OutputStream outputStream) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (DocBlock docBlock : docBlocks) {
            sb.append(convert2Html(docBlock));
        }
        String html = sb.toString();
        logger.info("block2html :" + html);
        convert2Docx(html, outputStream);
    }


}