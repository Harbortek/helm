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
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Color;
import org.docx4j.wml.Highlight;
import org.jsoup.nodes.Node;

/**
 * The SpanHandler class is an implementation of the TagHandler interface. It is responsible for handling
 * span nodes in an HTML document, specifically dealing with background colors and adding them as highlights
 * in a WordprocessingMLPackage.
 */
public class SpanHandler implements TagHandler {

    /**
     * Handles a span node by adding its background color as a highlight in the WordprocessingMLPackage.
     *
     * @param node          the span node to be handled
     * @param wordMLPackage the WordprocessingMLPackage to which the highlight will be added
     */
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {
        Node spanNode = ConverterUtils.findParentNode(node, "span");
        String style = StringUtils.isNotBlank(spanNode.attr(Constants.STYLE)) ? spanNode.attr(Constants.STYLE) : null;
        String bgColor = StringUtils.substringBetween(style, "background-color: ", ";");
        if (bgColor != null) {
            Highlight highlight = RunUtils.getObjectFactory().createHighlight();
            highlight.setVal(ConverterUtils.isHexColor(bgColor)
                    ? bgColor : ConverterUtils.rgbToHex(bgColor, Constants.HEX_WHITE_COLOR));
            //skip unsupported color
            if (highlight.getVal() == null) {
                highlight.setVal(Constants.HEX_WHITE_COLOR);
            }
            RunUtils.getCurrentRPr(wordMLPackage).setHighlight(highlight);
        }
        String colorStyle = StringUtils.substringBetween(style, "color: ", ";");
        if(StringUtils.isNotEmpty(colorStyle)){
            Color color = RunUtils.getObjectFactory().createColor();
            color.setVal(ConverterUtils.isHexColor(colorStyle)
                    ? colorStyle : ConverterUtils.rgbToHex(colorStyle, Constants.HEX_WHITE_COLOR));
            //skip unsupported color
            if (color.getVal() == null) {
                color.setVal(Constants.HEX_WHITE_COLOR);
            }
            RunUtils.getCurrentRPr(wordMLPackage).setColor(color);
        }
    }

    /**
     * Determines if the tag handler is repeatable. In this implementation, it is repeatable.
     *
     * @return true, as the SpanHandler is repeatable
     */
    @Override
    public boolean isRepeatable() {
        return true;
    }
}
