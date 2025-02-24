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

import com.harbortek.helm.smartdoc.exporter.word.html2docx.converter.HtmlToOpenXMLConverter;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Color;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.RPr;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class FontHandlerTest {
    private final HtmlToOpenXMLConverter converter = new HtmlToOpenXMLConverter();

    @Test
    void testHandleTag_fontWithSizeAndColor() throws InvalidFormatException {
        String html = "<font size=\"16px\" color=\"#FF5733\">Sample Text</font>";
        WordprocessingMLPackage wordMLPackage = converter.convert(html);
        RPr rPr = RunUtils.getCurrentRPr(wordMLPackage);

        Color color = rPr.getColor();
        HpsMeasure size = rPr.getSz();
        HpsMeasure sizeCs = rPr.getSzCs();

        assertEquals("#FF5733", color.getVal());
        assertEquals(BigInteger.valueOf(24), size.getVal());
        assertEquals(BigInteger.valueOf(24), sizeCs.getVal());
    }

    @Test
    void testHandleTag_fontWithSizeOnly() throws InvalidFormatException {
        String html = "<font size=\"16px\">Sample Text</font>";
        WordprocessingMLPackage wordMLPackage = converter.convert(html);
        RPr rPr = RunUtils.getCurrentRPr(wordMLPackage);

        Color color = rPr.getColor();
        HpsMeasure size = rPr.getSz();
        HpsMeasure sizeCs = rPr.getSzCs();

        assertNull(color);
        assertEquals(BigInteger.valueOf(24), size.getVal());
        assertEquals(BigInteger.valueOf(24), sizeCs.getVal());
    }

    @Test
    void testHandleTag_fontWithColorOnly() throws InvalidFormatException {
        String html = "<font color=\"#FF5733\">Sample Text</font>";
        WordprocessingMLPackage wordMLPackage = converter.convert(html);
        RPr rPr = RunUtils.getCurrentRPr(wordMLPackage);

        Color color = rPr.getColor();
        HpsMeasure size = rPr.getSz();
        HpsMeasure sizeCs = rPr.getSzCs();

        assertEquals("#FF5733", color.getVal());
        assertNull(size);
        assertNull(sizeCs);
    }
}

