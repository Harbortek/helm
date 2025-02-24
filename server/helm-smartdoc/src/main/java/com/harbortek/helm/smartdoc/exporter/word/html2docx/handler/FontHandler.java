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
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.jsoup.nodes.Node;

/**
 * Handles the font-related properties of an HTML node and updates the corresponding
 * properties in a WordprocessingMLPackage instance.
 */
public class FontHandler implements TagHandler {
    private ObjectFactory factory = RunUtils.getObjectFactory();
    /**
     * Handles the font-related properties of the given HTML node and updates the
     * corresponding properties in the provided WordprocessingMLPackage instance.
     *
     * @param node          The HTML node to handle.
     * @param wordMLPackage The WordprocessingMLPackage instance to update.
     */
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {
        P p = RunUtils.getCurrentParagraph(wordMLPackage);
        R r = factory.createR();
        RPr rPr = factory.createRPr();
        r.setRPr(rPr);
        p.getContent().add(r);
        Node fontNode = ConverterUtils.findParentNode(node, "font");
        String nodeColor = fontNode.attr("color");
        if (StringUtils.isNotBlank(nodeColor)) {
            String fontColor = ConverterUtils.isHexColor(nodeColor)
                    ? nodeColor : ConverterUtils.rgbToHex(nodeColor, Constants.HEX_BLACK_COLOR);
            Color color = factory.createColor();
            color.setVal(fontColor);
            rPr.setColor(color);
        }

        String nodeSize = StringUtils.substringBefore(fontNode.attr("size"), "px");
        if (NumberUtils.isCreatable(nodeSize)) {
            int fontSize = Integer.parseInt(nodeSize);
            HpsMeasure hpsMeasure = factory.createHpsMeasure();
            hpsMeasure.setVal(ConverterUtils.pxToHalfPoints(fontSize));
            rPr.setSz(hpsMeasure);
            rPr.setSzCs(hpsMeasure);
        }



//        Node fontNode = ConverterUtils.findParentNode(node, "font");
//        ObjectFactory objectFactory = RunUtils.getObjectFactory();
//        RPr rPr = RunUtils.getCurrentRPr(wordMLPackage);
//
//        String nodeColor = fontNode.attr("color");
//        if (StringUtils.isNotBlank(nodeColor)) {
//            String fontColor = ConverterUtils.isHexColor(nodeColor)
//                    ? nodeColor : ConverterUtils.rgbToHex(nodeColor, Constants.HEX_BLACK_COLOR);
//            Color color = objectFactory.createColor();
//            color.setVal(fontColor);
//            rPr.setColor(color);
//        }
//
//        String nodeSize = StringUtils.substringBefore(fontNode.attr("size"), "px");
//        if (NumberUtils.isCreatable(nodeSize)) {
//            int fontSize = Integer.parseInt(nodeSize);
//            HpsMeasure hpsMeasure = objectFactory.createHpsMeasure();
//            hpsMeasure.setVal(ConverterUtils.pxToHalfPoints(fontSize));
//            rPr.setSz(hpsMeasure);
//            rPr.setSzCs(hpsMeasure);
//        }
    }

    /**
     * Indicates whether the FontHandler can be applied multiple times to the same node.
     *
     * @return true if the FontHandler can be applied multiple times; false otherwise.
     */
    @Override
    public boolean isRepeatable() {
        return true;
    }
}
