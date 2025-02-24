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

import com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.PlainTextHandler;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.RPr;
import org.jsoup.nodes.TextNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class PlainSlateTextHandlerTest {

    @Test
    void handleTag_appliesPlainText() throws InvalidFormatException {
        // Arrange
        PlainTextHandler plainTextHandler = new PlainTextHandler();
        TextNode plainTextNode = new TextNode("Plain text");
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

        // Act
        plainTextHandler.handleTag(plainTextNode, wordMLPackage);

        // Assert
        RPr rPr = RunUtils.getCurrentRPr(wordMLPackage);
        assertNull(rPr.getB());
        assertNull(rPr.getI());
        assertNull(rPr.getU());
    }
}
