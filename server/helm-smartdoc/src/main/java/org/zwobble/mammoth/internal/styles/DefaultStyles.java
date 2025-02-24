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

package org.zwobble.mammoth.internal.styles;

import org.zwobble.mammoth.internal.styles.parsing.StyleMapParser;

import static org.zwobble.mammoth.internal.util.Lists.list;

public class DefaultStyles {
    public static final StyleMap DEFAULT_STYLE_MAP = StyleMapParser.parseStyleMappings(list(
            "p.Heading1 => h1.heading1:fresh",
            "p.Heading2 => h2.heading2:fresh",
            "p.Heading3 => h3.heading3:fresh",
            "p.Heading4 => h4.heading4:fresh",
            "p.Heading5 => h5.heading5:fresh",
            "p.Heading6 => h6.heading6:fresh",
            "p[style-name='Heading 1'] => h1.heading1:fresh",
            "p[style-name='Heading 2'] => h2.heading2:fresh",
            "p[style-name='Heading 3'] => h3.heading3:fresh",
            "p[style-name='Heading 4'] => h4.heading4:fresh",
            "p[style-name='Heading 5'] => h5.heading5:fresh",
            "p[style-name='Heading 6'] => h6.heading6:fresh",
            "p[style-name='heading 1'] => h1.heading1:fresh",
            "p[style-name='heading 2'] => h2.heading2:fresh",
            "p[style-name='heading 3'] => h3.heading3:fresh",
            "p[style-name='heading 4'] => h4.heading4:fresh",
            "p[style-name='heading 5'] => h5.heading5:fresh",
            "p[style-name='heading 6'] => h6.heading6:fresh",

            "r[style-name='Strong'] => strong",

            "p[style-name='footnote text'] => p:fresh",
            "r[style-name='footnote reference'] =>",
            "p[style-name='endnote text'] => p:fresh",
            "r[style-name='endnote reference'] =>",
            "p[style-name='annotation text'] => p:fresh",
            "r[style-name='annotation reference'] =>",

            // LibreOffice
            "p[style-name='Footnote'] => p:fresh",
            "r[style-name='Footnote anchor'] =>",
            "p[style-name='Endnote'] => p:fresh",
            "r[style-name='Endnote anchor'] =>",

            "p:unordered-list(1) => ul > li:fresh",
            "p:unordered-list(2) => ul|ol > li > ul > li:fresh",
            "p:unordered-list(3) => ul|ol > li > ul|ol > li > ul > li:fresh",
            "p:unordered-list(4) => ul|ol > li > ul|ol > li > ul|ol > li > ul > li:fresh",
            "p:unordered-list(5) => ul|ol > li > ul|ol > li > ul|ol > li > ul|ol > li > ul > li:fresh",
            "p:ordered-list(1) => ol > li:fresh",
            "p:ordered-list(2) => ul|ol > li > ol > li:fresh",
            "p:ordered-list(3) => ul|ol > li > ul|ol > li > ol > li:fresh",
            "p:ordered-list(4) => ul|ol > li > ul|ol > li > ul|ol > li > ol > li:fresh",
            "p:ordered-list(5) => ul|ol > li > ul|ol > li > ul|ol > li > ul|ol > li > ol > li:fresh",

            "r[style-name='Hyperlink'] =>",

            "table => table.w-e-panel-content-table:fresh",

            "p[style-name='Normal'] => p:fresh"));
}
