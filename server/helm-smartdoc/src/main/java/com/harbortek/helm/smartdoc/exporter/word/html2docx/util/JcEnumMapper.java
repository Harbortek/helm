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

package com.harbortek.helm.smartdoc.exporter.word.html2docx.util;

import org.docx4j.wml.JcEnumeration;

/**
 * JcEnumMapper is a utility class that maps string values to their corresponding JcEnumeration constants.
 */
public class JcEnumMapper {

    /**
     * Maps the given string value to the corresponding JcEnumeration constant.
     *
     * @param align The string value representing a JcEnumeration constant.
     * @return The JcEnumeration constant that matches the given string value or null if the input is null.
     * @throws IllegalArgumentException If the input string doesn't match any JcEnumeration constant.
     */
    public static JcEnumeration map(String align) {
        if (align == null) {
            return null;
        }

        JcEnumeration jcEnumeration;

        switch (align) {
            case "left":
                jcEnumeration = JcEnumeration.LEFT;
                break;
            case "right":
                jcEnumeration = JcEnumeration.RIGHT;
                break;
            case "center":
                jcEnumeration = JcEnumeration.CENTER;
                break;
            case "justify":
                jcEnumeration = JcEnumeration.BOTH;
                break;
            default:
                throw new IllegalArgumentException("Unexpected align value: " + align);
        }

        return jcEnumeration;
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private JcEnumMapper() {
    }
}
