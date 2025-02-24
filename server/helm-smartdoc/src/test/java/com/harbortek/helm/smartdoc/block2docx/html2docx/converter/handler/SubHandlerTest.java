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

import com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.SubHandler;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.R;
import org.docx4j.wml.STVerticalAlignRun;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubHandlerTest {

    @Test
    void handleTag() throws InvalidFormatException {
        Element subElement = new Element(Tag.valueOf("sub"), "");
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        SubHandler subHandler = new SubHandler();
        subHandler.handleTag(subElement, wordMLPackage);
        R currentRun = RunUtils.getCurrentRun(wordMLPackage);
        STVerticalAlignRun vertAlignVal = currentRun.getRPr().getVertAlign().getVal();
        assertEquals(STVerticalAlignRun.SUBSCRIPT, vertAlignVal);
    }
}

