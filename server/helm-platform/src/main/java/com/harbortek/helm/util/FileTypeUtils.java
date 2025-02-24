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

import cn.hutool.core.io.IoUtil;

import java.io.InputStream;

public class FileTypeUtils {
    /**
     * 判断字符串是否是纯文本
     */
    public static boolean isText(InputStream in){
        String s = new String(IoUtil.readBytes(in,100)) ;
        String s2 = s.replaceAll(
                "[\u4e00-\u9fa5a-zA-Z0-9ßöäü\\.\\*!\"§\\$\\%&/()=\\?@~'#:,;\\"+
                        "+><\\|\\[\\]\\{\\}\\^°²³\\\\ \\n\\r\\t_\\-`´âêîô"+
                        "ÂÊÔÎáéíóàèìòÁÉÍÓÀÈÌÒ©‰¢£¥€±¿»«¼½¾™ª]", "");
        // will delete all text signs

        double d = (double)(s.length() - s2.length()) / (double)(s.length());
        // percentage of text signs in the text
        return d > 0.95;
    }
}
