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
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.Constants;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.ConverterUtils;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.JcEnumMapper;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Jc;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.jsoup.nodes.Node;

/**
 * This class handles the conversion of HTML paragraphs to WordprocessingMLPackage format.
 */
public class ParagraphHandler implements TagHandler {
    private final String STYLE_PARAGRAPH = "0";

    /**
     * Converts the paragraph node to a WordprocessingMLPackage instance.
     *
     * @param node          The paragraph node to be converted.
     * @param wordMLPackage The WordprocessingMLPackage instance to which the converted paragraph will be added.
     */
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {


        String style = StringUtils.isNotBlank(node.attr(Constants.STYLE)) ? node.attr(Constants.STYLE) : null;
        String indent = StringUtils.substringBetween(style, "text-indent: ", "px;");
        String align = StringUtils.isNotBlank(node.attr("align")) ? node.attr("align") : null;
        PPr pPr = RunUtils.getObjectFactory().createPPr();
        if (align != null || indent != null) {
            if (align != null) {
                Jc jc = RunUtils.getObjectFactory().createJc();
                jc.setVal(JcEnumMapper.map(align));
                pPr.setJc(jc);
            }
            if (NumberUtils.isCreatable(indent)) {
                PPrBase.Ind ind = RunUtils.getObjectFactory().createPPrBaseInd();
                ind.setFirstLine(ConverterUtils.pxToDxa(Integer.parseInt(indent)));
                pPr.setInd(ind);
            }
        }
        PPrBase.PStyle pStyle = new PPrBase.PStyle();
        pStyle.setVal(STYLE_PARAGRAPH);
        pPr.setPStyle(pStyle);
        RunUtils.createParagraph(wordMLPackage, pPr);
    }

    /**
     * Returns whether the tag handler is repeatable.
     *
     * @return false, because the ParagraphHandler is not repeatable.
     */
    @Override
    public boolean isRepeatable() {
        return false;
    }
}
