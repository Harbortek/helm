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

package com.harbortek.helm.smartdoc.exporter.word.html2docx.handler;

import com.harbortek.helm.smartdoc.exporter.word.html2docx.core.TagHandler;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.jsoup.nodes.Node;


public class HeadingHandler implements TagHandler {
    //对应模板中的StypeId
    private final String STYLE_HEADING1 = "1";
    private final String STYLE_HEADING2 = "2";
    private final String STYLE_HEADING3 = "3";
    private final String STYLE_HEADING4 = "4";
    private final String STYLE_HEADING5 = "5";
    private final String STYLE_HEADING6 = "6";
    private final String STYLE_PARAGRAPH = "0";

    private final Long ABSTRACT_NUM_ID = 4L;

    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {

        // 设置段落样式为"Heading 1"
        PPr pPr = RunUtils.getObjectFactory().createPPr();
        PPrBase.PStyle pStyle = new PPrBase.PStyle();
        String level = node.nodeName().substring(1);
        pStyle.setVal(
                this.selectStyleId(level)
        );
        pPr.setPStyle(pStyle);
//        PPrBase.NumPr numPr = RunUtils.getObjectFactory().createPPrBaseNumPr();
//        PPrBase.NumPr.NumId numId = RunUtils.getObjectFactory().createPPrBaseNumPrNumId();
//        numId.setVal(BigInteger.valueOf(Long.parseLong(level)));
//        numPr.setNumId(numId);
//        pPr.setNumPr(numPr);
        RunUtils.createParagraph(wordMLPackage,pPr);
    }

    private String selectStyleId(String level) {
        switch (level) {
            case "1":
                return STYLE_HEADING1;
            case "2":
                return STYLE_HEADING2;
            case "3":
                return STYLE_HEADING3;
            case "4":
                return STYLE_HEADING4;
            case "5":
                return STYLE_HEADING5;
            case "6":
                return STYLE_HEADING6;
            default:
                return STYLE_PARAGRAPH;
        }
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }
}
