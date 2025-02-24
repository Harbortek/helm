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

import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * The SpanHandler class is an implementation of the TagHandler interface. It is responsible for handling
 * span nodes in an HTML document, specifically dealing with background colors and adding them as highlights
 * in a WordprocessingMLPackage.
 */
public class BSpanTitleHandler extends BlockHandler {
    protected ObjectFactory factory = RunUtils.getObjectFactory();
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {

        SdtBlock sdtBlock = factory.createSdtBlock();
        SdtContentBlock sdtContentBlock = factory.createSdtContentBlock();
        sdtBlock.setSdtContent(sdtContentBlock);
        SdtPr sdtPr = factory.createSdtPr();
        // 设置 sdtPr 属性
        SdtPr.Alias alias = factory.createSdtPrAlias();
        alias.setVal("标题");
        sdtPr.getRPrOrAliasOrLock().add(alias);
        Tag tag = factory.createTag();
        tag.setVal("title");
        sdtPr.setTag(tag);
        sdtBlock.setSdtPr(sdtPr);
        RPr rPr = factory.createRPr();
        rPr.setB(factory.createBooleanDefaultTrue());

        Text text = RunUtils.getObjectFactory().createText();
        String nodeText = ((Element) node).text();
        text.setValue(nodeText);
        text.setSpace("preserve");
        R r = new R();
        r.getContent().add(text);
        r.setRPr(rPr);
        sdtContentBlock.getContent().add(r);

        wordMLPackage.getMainDocumentPart().getContent().add(sdtBlock);
    }

}
