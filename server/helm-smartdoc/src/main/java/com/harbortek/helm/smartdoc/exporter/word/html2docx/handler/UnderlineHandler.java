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
import org.docx4j.wml.RPr;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;
import org.jsoup.nodes.Node;

/**
 * This class handles the conversion of HTML underline tags to WordprocessingMLPackage format.
 */
public class UnderlineHandler implements TagHandler {

    /**
     * This method is called to process the input node and apply the underline formatting to
     * the corresponding WordprocessingMLPackage format.
     *
     * @param node          The input HTML node to be processed.
     * @param wordMLPackage The WordprocessingMLPackage instance where the underline formatting
     *                      will be applied.
     */
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {
        RPr rPr = RunUtils.getCurrentRPr(wordMLPackage);
        U u = RunUtils.getObjectFactory().createU();
        u.setVal(UnderlineEnumeration.SINGLE);
        rPr.setU(u);
    }

    /**
     * This method returns whether the tag handler can be applied to multiple tags.
     *
     * @return true, as the UnderlineHandler can be applied to multiple tags.
     */
    @Override
    public boolean isRepeatable() {
        return true;
    }
}
