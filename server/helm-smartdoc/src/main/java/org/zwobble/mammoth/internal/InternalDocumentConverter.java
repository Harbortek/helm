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

package org.zwobble.mammoth.internal;

import org.zwobble.mammoth.internal.archives.Archive;
import org.zwobble.mammoth.internal.archives.InMemoryArchive;
import org.zwobble.mammoth.internal.archives.ZippedArchive;
import org.zwobble.mammoth.internal.conversion.DocumentToHtml;
import org.zwobble.mammoth.internal.conversion.DocumentToHtmlOptions;
import org.zwobble.mammoth.internal.conversion.RawText;
import org.zwobble.mammoth.internal.documents.Document;
import org.zwobble.mammoth.internal.docx.EmbeddedStyleMap;
import org.zwobble.mammoth.internal.docx.ReadResult;
import org.zwobble.mammoth.internal.html.Html;
import org.zwobble.mammoth.internal.html.HtmlNode;
import org.zwobble.mammoth.internal.results.InternalResult;
import org.zwobble.mammoth.internal.styles.StyleMap;
import org.zwobble.mammoth.internal.styles.parsing.StyleMapParser;
import org.zwobble.mammoth.internal.util.PassThroughException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.zwobble.mammoth.internal.docx.DocumentReader.readDocument;

public class InternalDocumentConverter {
    private final DocumentToHtmlOptions options;

    public InternalDocumentConverter(DocumentToHtmlOptions options) {
        this.options = options;
    }

    public InternalResult<String> convertToHtml(InputStream stream) throws IOException {
        return PassThroughException.unwrap(() ->
                                                   withDocxFile(stream, zipFile ->
                                                           convertToHtml(Optional.empty(), zipFile)));
    }

    public InternalResult<String> convertToHtml(File file) throws IOException {
        return PassThroughException.unwrap(() ->
                                                   withDocxFile(file, zipFile ->
                                                           convertToHtml(Optional.of(file.toPath()), zipFile)));
    }

    private InternalResult<String> convertToHtml(Optional<Path> path, Archive zipFile) {
        Optional<StyleMap> styleMap = readEmbeddedStyleMap(zipFile).map(StyleMapParser::parse);
        DocumentToHtmlOptions conversionOptions = styleMap.map(options::addEmbeddedStyleMap).orElse(options);

        return readDocument(path, zipFile)
                .flatMap(nodes -> DocumentToHtml.convertToHtml(nodes, conversionOptions))
                .map(Html::stripEmpty)
                .map(Html::collapse)
                .map(Html::write);
    }

    private Optional<String> readEmbeddedStyleMap(Archive zipFile) {
        return PassThroughException.wrap(() -> EmbeddedStyleMap.readStyleMap(zipFile));
    }

    public InternalResult<String> extractRawText(InputStream stream) throws IOException {
        return PassThroughException.unwrap(() ->
                                                   withDocxFile(stream, zipFile ->
                                                           extractRawText(Optional.empty(), zipFile)));
    }

    public InternalResult<String> extractRawText(File file) throws IOException {
        return PassThroughException.unwrap(() ->
                                                   withDocxFile(file, zipFile ->
                                                           extractRawText(Optional.of(file.toPath()), zipFile)));
    }

    private InternalResult<String> extractRawText(Optional<Path> path, Archive zipFile) {
        return readDocument(path, zipFile)
                .map(RawText::extractRawText);
    }

    private static <T> T withDocxFile(File file, Function<Archive, T> function) throws IOException {
        try (Archive zipFile = new ZippedArchive(file)) {
            return function.apply(zipFile);
        }
    }

    private static <T> T withDocxFile(InputStream stream, Function<Archive, T> function) throws IOException {
        try (Archive zipFile = InMemoryArchive.fromStream(stream)) {
            return function.apply(zipFile);
        }
    }
}
