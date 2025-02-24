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

import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateDescendant;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserRegister {


    public static final Map<String, ParseElemHtmlFn> PARSE_ELEM_HTML_CONF = new HashMap<>();

    public static final List<PreParseHtmlConf> PRE_PARSE_HTML_CONF_LIST = new ArrayList<>();
    public static final List<ParseStyleHtmlFn> PARSE_STYLE_HTML_FN_LIST = new ArrayList<>();

    public static void registerPreParseHtmlConf(PreParseHtmlConf conf) {
        PRE_PARSE_HTML_CONF_LIST.add(conf);
    }
    public static void registerParseStyleHtmlHandler(ParseStyleHtmlFn fn) {
        PARSE_STYLE_HTML_FN_LIST.add(fn);
    }
    public static void registerParseElemHtmlConf(ParseElemHtmlConf conf) {
        PARSE_ELEM_HTML_CONF.put(conf.getSelector(), conf.getParseElemHtml());
    }

    //判断SlateText对象只有一个text属性
    public static boolean isSingleText(SlateDescendant node) {
        //判断node 对象只有text 一个属性，通过反射判断
        Field[] fields = node.getClass().getFields();
        if (fields.length == 1 && fields[0].getName().equals("text")) {
            return true;
        } else {
            return false;
        }
    }
    // Common text tags
    public static final String[] TEXT_TAGS = {
            "span", "b", "strong", "i", "em", "s", "strike", "u", "font", "sub", "sup"
    };

    public static boolean isVoid(SlateNode node) {
        return false;
    }

    public static boolean isInline(SlateDescendant child) {
        return true;
    }
}
