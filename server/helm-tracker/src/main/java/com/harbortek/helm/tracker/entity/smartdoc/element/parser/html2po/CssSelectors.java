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

package com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po;

import java.util.HashMap;
import java.util.Map;

public class CssSelectors {
    public static final String H1_SELECTOR = "h1:not([data-w-e-type])";
    public static final String H2_SELECTOR = "h2:not([data-w-e-type])";
    public static final String H3_SELECTOR = "h3:not([data-w-e-type])";
    public static final String H4_SELECTOR = "h4:not([data-w-e-type])";
    public static final String H5_SELECTOR = "h5:not([data-w-e-type])";
    public static final String[] TEXT_TAGS = {
            "span",
            "b",
            "strong",
            "i",
            "em",
            "s",
            "strike",
            "u",
            "font",
            "sub",
            "sup"
    };


}
