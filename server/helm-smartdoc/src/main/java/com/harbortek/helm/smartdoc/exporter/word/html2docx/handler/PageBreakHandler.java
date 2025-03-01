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
import org.docx4j.wml.Br;
import org.docx4j.wml.STBrType;
import org.jsoup.nodes.Node;

/**
 * The PageBreakHandler class is responsible for processing the custom
 * HTML page break tag (&lt;pb&gt;). It implements the TagHandler interface.
 */
public class PageBreakHandler implements TagHandler {

    /**
     * Handles the custom HTML page break tag by inserting a page break
     * in the provided WordprocessingMLPackage.
     *
     * @param node          The HTML node representing the page break tag.
     * @param wordMLPackage The WordprocessingMLPackage to which the page break is added.
     */
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {
        Br br = RunUtils.getObjectFactory().createBr();
        br.setType(STBrType.PAGE);
        RunUtils.getCurrentRun(wordMLPackage).getContent().add(br);
    }

    /**
     * Indicates whether the PageBreakHandler can be applied multiple times to the same content.
     * In this case, it returns false, as the page break should not be applied multiple times.
     *
     * @return A boolean value, true if the handler is repeatable, false otherwise.
     */
    @Override
    public boolean isRepeatable() {
        return false;
    }
}
