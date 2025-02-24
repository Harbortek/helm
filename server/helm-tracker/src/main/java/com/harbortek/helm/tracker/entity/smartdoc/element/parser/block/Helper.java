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

package com.harbortek.helm.tracker.entity.smartdoc.element.parser.block;

import java.util.regex.Pattern;

/**
 * @description parse-html helper functions
 * @author wangfupeng
 */
public class Helper {
    private static final Pattern REPLACE_SPACE_160_REG = Pattern.compile(String.valueOf('\u00A0'));

    /**
     * Replace charCode 160 spaces (converted from `&nbsp`) with charCode 32 spaces (default in JavaScript).
     * @param str input string
     * @return modified string
     */
    public static String replaceSpace160(String str) {
        return REPLACE_SPACE_160_REG.matcher(str).replaceAll(" ");
    }
}
