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
import org.docx4j.wml.Text;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * The PlainTextHandler class is an implementation of the TagHandler interface. It is responsible for handling plain text
 * nodes in an HTML document and adding them to a WordprocessingMLPackage as a Text object.
 */
public class PlainTextHandler implements TagHandler {

    /**
     * Handles a plain text node by creating a Text object and adding it to the WordprocessingMLPackage.
     *
     * @param node          the plain text node to be handled
     * @param wordMLPackage the WordprocessingMLPackage to which the Text object will be added
     */
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {
        Text text = RunUtils.getObjectFactory().createText();
        String nodeText = ((TextNode) node).text();
        text.setValue(nodeText);
        text.setSpace("preserve");
        RunUtils.getCurrentRun(wordMLPackage).getContent().add(text);
    }

    /**
     * Determines if the tag handler is repeatable. In this implementation, it is not repeatable.
     *
     * @return false, as the PlainTextHandler is not repeatable
     */
    @Override
    public boolean isRepeatable() {
        return false;
    }
}
