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

package com.harbortek.helm.smartdoc.block2docx.html2docx.converter.handler;

import com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.ParagraphHandler;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ParagraphHandlerTest {

    @Test
    void handleTag_appliesParagraphFormatting() throws InvalidFormatException {
        // Arrange
        ParagraphHandler paragraphHandler = new ParagraphHandler();
        Node pNode = Jsoup.parse("<p align=\"left\" style=\"text-indent: 50px;\">Paragraph</p>").body().child(0);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

        // Act
        paragraphHandler.handleTag(pNode, wordMLPackage);

        // Assert
        P p = RunUtils.getCurrentParagraph(wordMLPackage);
        assertNotNull(p.getPPr());
        assertNotNull(p.getPPr().getInd());
        assertNotNull(p.getPPr().getInd().getFirstLine());
        assertEquals(750, p.getPPr().getInd().getFirstLine().intValue());

        assertNotNull(p.getPPr().getJc());
        assertEquals(JcEnumeration.LEFT, p.getPPr().getJc().getVal());
    }
}
