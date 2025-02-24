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

package com.harbortek.helm.util;

public class XmlCharUtils {

    /**
     * &lt; < 小于号
     * &gt; > 大于号
     * &amp; & 和
     * &apos; ' 单引号
     * &quot; " 双引号
     * @param input
     * @return
     */
    public static String replaceXmlSpecChars(String input) {
        String value = input.replaceAll("&", "&amp;");
        value = value.replaceAll("<", "&lt;");
        value = value.replaceAll(">", "&gt;");
        value = value.replaceAll("'", "&apos;");
        value = value.replaceAll("\"", "&quot;");

        return value;
    }
}
