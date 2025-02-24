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

package com.harbortek.helm.smartdoc.exporter.word.html2docx.core;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.nodes.Node;

/**
 * The TagHandler interface defines the contract for handling custom tags
 * within an HTML document and manipulating WordprocessingMLPackage content
 * accordingly. Implementations of this interface are responsible for
 * processing and applying the required modifications based on the tag logic.
 */
public interface TagHandler {

    /**
     * Handles the custom tag within the HTML document and updates the
     * WordprocessingMLPackage content accordingly.
     *
     * @param node          The HTML node representing the custom tag to be processed.
     * @param wordMLPackage The WordprocessingMLPackage to be manipulated
     *                      based on the tag logic.
     */
    void handleTag(Node node, WordprocessingMLPackage wordMLPackage);

    /**
     * Determines whether the tag handler can be applied multiple times within
     * a document.
     *
     * @return True if the tag is repeatable; otherwise, false.
     */
    boolean isRepeatable();
}
