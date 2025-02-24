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
import org.jsoup.nodes.Node;

/**
 * The BreakHandler class is responsible for processing the HTML line break tag (&lt;br&gt;).
 * It implements the TagHandler interface.
 */
public class BreakHandler implements TagHandler {

    /**
     * Handles the HTML line break tag by adding a break to the current run in the
     * provided WordprocessingMLPackage.
     *
     * @param node          The HTML node representing the line break tag.
     * @param wordMLPackage The WordprocessingMLPackage to which the break is added.
     */
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {
        RunUtils.getCurrentRun(wordMLPackage).getContent().add(RunUtils.getObjectFactory().createBr());
    }

    /**
     * Indicates whether the BreakHandler can be applied multiple times to the same content.
     * In this case, it returns false, as the line break should not be applied multiple times.
     *
     * @return A boolean value, true if the handler is repeatable, false otherwise.
     */
    @Override
    public boolean isRepeatable() {
        return false;
    }
}
