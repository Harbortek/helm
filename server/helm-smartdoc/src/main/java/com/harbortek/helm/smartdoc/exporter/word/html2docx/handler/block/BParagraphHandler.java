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

package com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.block;

import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.ConverterUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.docx4j.wml.SdtBlock;
import org.docx4j.wml.SdtContentBlock;
import org.docx4j.wml.SdtPr;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public class BParagraphHandler extends BlockHandler {

    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {

        SdtBlock sdtBlock = factory.createSdtBlock();
        SdtContentBlock sdtContentBlock = factory.createSdtContentBlock();
        sdtBlock.setSdtContent(sdtContentBlock);
        SdtPr sdtPr = factory.createSdtPr();
        // 设置 sdtPr 属性
        SdtPr.Alias alias = factory.createSdtPrAlias();
        alias.setVal("工作项");
        sdtPr.getRPrOrAliasOrLock().add(alias);
        // 将 sdtPr 和 sdtContent 添加到 sdtBlock 中
        sdtBlock.setSdtPr(sdtPr);

        wordMLPackage.getMainDocumentPart().getContent().add(sdtBlock);

        P p = factory.createP();
        sdtContentBlock.getContent().add(p);

        List<Element> blockElements = new ArrayList<>();
        List<Element> otherElements = new ArrayList<>();
        ((Element) node).children().forEach(childNode -> {
            if ("b-span-id".equals(childNode.tagName()) || "b-span-code".equals(childNode.tagName())
                    || "b-span-title".equals(childNode.tagName())) {
                blockElements.add(childNode);
            } else {
                otherElements.add(childNode);
            }
        });
        this.processChild(blockElements, wordMLPackage, p.getContent());
        this.processChild(otherElements, wordMLPackage, sdtContentBlock.getContent());
    }

    private void processChild(List<Element> children, WordprocessingMLPackage wordMLPackage, List<Object> content) {
        children.forEach(childNode -> {
            List<Object> tempContent = new ArrayList<>(wordMLPackage.getMainDocumentPart().getContent());
            wordMLPackage.getMainDocumentPart().getContent().clear();
            converter.convert(childNode.outerHtml(), wordMLPackage);
            ConverterUtils.addListContent(content, wordMLPackage.getMainDocumentPart().getContent());
            ConverterUtils.replaceListContent(wordMLPackage.getMainDocumentPart().getContent(), tempContent);
        });
    }


}
