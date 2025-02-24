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

package com.harbortek.helm.smartdoc.exporter.word.html2docx.converter;

import com.harbortek.helm.smartdoc.exporter.word.html2docx.core.TagHandler;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.core.TagHandlerFactory;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.PlainTextHandler;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.TableHandler;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.block.BlockHandler;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A converter that converts HTML content to OpenXML format.
 *
 * @author Denis Fesenko &lt;fesenkoden@gmail.com&gt;
 */
public class HtmlToOpenXMLConverter {

    private final Map<String, TagHandler> tagHandlerMap;
    private final Set<TagHandler> tagHandlers;

    /**
     * Default constructor. Initializes the converter with default tag handlers.
     */
    public HtmlToOpenXMLConverter() {
        this(null);
    }

    /**
     * Constructor with custom tag handlers. Initializes the converter with the provided custom tag handlers.
     *
     * @param customTagHandlerMap a map of custom tag handlers to be added to the default tag handlers
     */
    public HtmlToOpenXMLConverter(Map<String, TagHandler> customTagHandlerMap) {
        tagHandlerMap = new TagHandlerFactory().createTagHandlerMap();
        if (customTagHandlerMap != null) {
            tagHandlerMap.putAll(customTagHandlerMap);
        }
        tagHandlers = new HashSet<>();
    }

    /**
     * Converts the provided HTML content to OpenXML format and returns a WordprocessingMLPackage.
     *
     * @param html the HTML content to be converted
     * @return a WordprocessingMLPackage containing the converted content
     * @throws InvalidFormatException if there is an issue during the conversion process
     */
    public WordprocessingMLPackage convert(String html) throws InvalidFormatException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        return convert(html, wordMLPackage);
    }

    /**
     * Converts the given HTML content into a WordprocessingMLPackage.
     *
     * @param html          The input HTML content as a string.
     * @param wordMLPackage The WordprocessingMLPackage instance to populate with the converted content.
     * @return The modified WordprocessingMLPackage containing the converted content from the input HTML.
     */
    public WordprocessingMLPackage convert(String html, WordprocessingMLPackage wordMLPackage) {
        Document document = Jsoup.parseBodyFragment(html);
        traverseDocument(document, wordMLPackage);
        return wordMLPackage;
    }

    /**
     * Traverses the HTML document and processes each node with the appropriate tag handlers.
     *
     * @param document      the HTML document to be traversed
     * @param wordMLPackage the WordprocessingMLPackage that will store the converted content
     */
    private void traverseDocument(Document document, WordprocessingMLPackage wordMLPackage) {
        document.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                TagHandler tagHandler = tagHandlerMap.get(node.nodeName());
                if (tagHandler != null) {
                    if (tagHandler instanceof PlainTextHandler) {
                        List<Object> content = RunUtils.getCurrentParagraph(wordMLPackage).getContent();
                        if (content.isEmpty()) {
                            RunUtils.getCurrentParagraph(wordMLPackage).getContent().add(RunUtils.getObjectFactory().createR());
                        }
                        tagHandlers.forEach(handler -> handler.handleTag(node, wordMLPackage));
                        tagHandler.handleTag(node, wordMLPackage);
                    } else if (tagHandler instanceof TableHandler) {
                        ((TableHandler) tagHandler).addConverter(HtmlToOpenXMLConverter.this).handleTag(node, wordMLPackage);
                        node.remove(); //prevent of second convert
                    } else if (tagHandler instanceof BlockHandler) {
                        ((BlockHandler) tagHandler).addConverter(HtmlToOpenXMLConverter.this).handleTag(node, wordMLPackage);
                        node.remove(); //prevent of second convert
                    } else if (tagHandler.isRepeatable()) {
                        tagHandlers.add(tagHandler);
                    } else {
                        tagHandler.handleTag(node, wordMLPackage);
                    }
                }
            }

            @Override
            public void tail(Node node, int depth) {
                TagHandler tagHandler = tagHandlerMap.get(node.nodeName());
                if (!tagHandlers.isEmpty()) {
                    tagHandlers.remove(tagHandler);
                }
            }
        });
    }
}
