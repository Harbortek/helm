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

package com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.block;

import com.harbortek.helm.smartdoc.exporter.word.html2docx.converter.HtmlToOpenXMLConverter;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.core.TagHandler;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.docx4j.wml.ObjectFactory;


public abstract class BlockHandler implements TagHandler {

    protected ObjectFactory factory = RunUtils.getObjectFactory();
    protected final String STYLE_PARAGRAPH = "0";
    protected HtmlToOpenXMLConverter converter;

    public BlockHandler addConverter(HtmlToOpenXMLConverter converter) {
        this.converter = converter;
        return this;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }



}
