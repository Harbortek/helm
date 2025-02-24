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
import org.docx4j.wml.CTVerticalAlignRun;
import org.docx4j.wml.STVerticalAlignRun;
import org.jsoup.nodes.Node;

/**
 * The SupHandler class is an implementation of the TagHandler interface. It is responsible for handling
 * sup (superscript) nodes in an HTML document and adding them to a WordprocessingMLPackage as a CTVerticalAlignRun
 * with the SUPERSCRIPT value.
 */
public class SupHandler implements TagHandler {

    /**
     * Handles a superscript node by creating a CTVerticalAlignRun object with the SUPERSCRIPT value and adding it to
     * the WordprocessingMLPackage.
     *
     * @param node          the superscript node to be handled
     * @param wordMLPackage the WordprocessingMLPackage to which the CTVerticalAlignRun object will be added
     */
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {
        CTVerticalAlignRun vertAlign = RunUtils.getObjectFactory().createCTVerticalAlignRun();
        vertAlign.setVal(STVerticalAlignRun.SUPERSCRIPT);
        RunUtils.getCurrentRPr(wordMLPackage).setVertAlign(vertAlign);
    }

    /**
     * Determines if the tag handler is repeatable. In this implementation, it is repeatable.
     *
     * @return true, as the SupHandler is repeatable
     */
    @Override
    public boolean isRepeatable() {
        return true;
    }
}
