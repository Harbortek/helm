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

import com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.*;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.block.*;

import java.util.HashMap;
import java.util.Map;

/**
 * The TagHandlerFactory class is responsible for creating a map
 * of supported HTML tags and their corresponding handlers.
 * Each handler is responsible for processing a specific HTML tag.
 */
public class TagHandlerFactory {

    /**
     * Creates and returns a map of supported HTML tags and their corresponding handlers.
     * The keys in the map are tag names, and the values are instances of the appropriate
     * TagHandler subclasses for processing the tags.
     *
     * @return A map of supported HTML tags and their corresponding handlers.
     */
    public Map<String, TagHandler> createTagHandlerMap() {
        Map<String, TagHandler> tagHandlerMap = new HashMap<>();
        tagHandlerMap.put("font", new FontHandler());
        tagHandlerMap.put("table", new TableHandler());
        tagHandlerMap.put("sub", new SubHandler());
        tagHandlerMap.put("sup", new SupHandler());
        tagHandlerMap.put("hr", new HrHandler());
        tagHandlerMap.put("u", new UnderlineHandler());
        tagHandlerMap.put("span", new SpanHandler());
        tagHandlerMap.put("br", new BreakHandler());
        tagHandlerMap.put("pb", new PageBreakHandler());
        tagHandlerMap.put("p", new ParagraphHandler());
        tagHandlerMap.put("b", new BoldHandler());
        tagHandlerMap.put("strong", new BoldHandler());
        tagHandlerMap.put("i", new ItalicHandler());
        tagHandlerMap.put("em", new ItalicHandler());
        tagHandlerMap.put("#text", new PlainTextHandler());

        tagHandlerMap.put("b-title", new BTitleHandler());
        tagHandlerMap.put("b-p", new BParagraphHandler());
        tagHandlerMap.put("b-span-id", new BSpanIdHandler());
        tagHandlerMap.put("b-span-code", new BSpanCodeHandler());
        tagHandlerMap.put("b-span-title", new BSpanTitleHandler());
        tagHandlerMap.put("h1", new HeadingHandler());
        tagHandlerMap.put("h2", new HeadingHandler());
        tagHandlerMap.put("h3", new HeadingHandler());
        tagHandlerMap.put("h4", new HeadingHandler());
        tagHandlerMap.put("h5", new HeadingHandler());
        tagHandlerMap.put("h6", new HeadingHandler());
        tagHandlerMap.put("img", new ImageHandler());
        tagHandlerMap.put("a", new AHandler());
        return tagHandlerMap;
    }
}
