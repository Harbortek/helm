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

package com.harbortek.helm.smartdoc.config;

import com.harbortek.helm.common.config.UserLocaleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
@Component
public class SmartDocMessages {
    @Autowired
    UserLocaleResolver userLocaleResolver;
    private static String baseName = "i18n/smartdoc";

    public String getMessage(String code){
        Locale locale = userLocaleResolver.getLocal();
        return getMessage(code,null,code,locale);
    }
    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.toString());
        messageSource.setBasename(baseName);
        String content;
        try {
            content = messageSource.getMessage(code, args, locale);
        } catch (Exception e) {
            content = defaultMessage;
        }
        return content;
    }
}
