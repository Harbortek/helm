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
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.jsoup.nodes.Node;

/**
 * The ItalicHandler class is responsible for processing italic HTML tags
 * (both &lt;i&gt; and &lt;em&gt; tags). It implements the TagHandler interface.
 */
public class ItalicHandler implements TagHandler {
    private ObjectFactory factory = RunUtils.getObjectFactory();
    /**
     * Handles the italic HTML tag by applying the italic style to the
     * current run properties (RPr) in the provided WordprocessingMLPackage.
     *
     * @param node          The HTML node representing the italic tag.
     * @param wordMLPackage The WordprocessingMLPackage to which the italic style is applied.
     */
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {
        P p = RunUtils.getCurrentParagraph(wordMLPackage);
        R r = factory.createR();
        RPr rPr = factory.createRPr();
        rPr.setI(RunUtils.createBooleanDefaultTrue());
        rPr.setICs(RunUtils.createBooleanDefaultTrue());
        r.setRPr(rPr);
        p.getContent().add(r);


//        RPr rPr = RunUtils.getCurrentRPr(wordMLPackage);
//        rPr.setI(RunUtils.createBooleanDefaultTrue());
//        rPr.setICs(RunUtils.createBooleanDefaultTrue());
    }

    /**
     * Indicates whether the ItalicHandler can be applied multiple times to the same content.
     * In this case, it returns true, as the italic style can be applied multiple times.
     *
     * @return A boolean value, true if the handler is repeatable, false otherwise.
     */
    @Override
    public boolean isRepeatable() {
        return true;
    }
}
