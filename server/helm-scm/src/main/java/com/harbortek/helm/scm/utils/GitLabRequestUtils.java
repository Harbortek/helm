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

package com.harbortek.helm.scm.utils;

import com.harbortek.helm.common.client.SkipSslVerificationHttpRequestFactory;
import lombok.extern.slf4j.Slf4j;
import org.parboiled.common.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
@Slf4j
public class GitLabRequestUtils {
    public static RestTemplate httpRestTemplate(String token) {
        ClientHttpRequestFactory factory = httpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        // 可以添加消息转换
        //restTemplate.setMessageConverters(...);
        // 可以增加拦截器
        if (StringUtils.isNotEmpty(token)) {
            restTemplate.setInterceptors(
                    Collections.singletonList(new OAuth2AuthenticationInterceptor(token)));
        }
        return restTemplate;
    }

    public static RestTemplate httpsRestTemplate(String token) {
        RestTemplate restTemplate = new RestTemplate(httpsRequestFactory());
        // 可以添加消息转换
        //restTemplate.setMessageConverters(...);
        // 可以增加拦截器
        if (StringUtils.isNotEmpty(token)) {
            restTemplate.setInterceptors(
                    Collections.singletonList(new OAuth2AuthenticationInterceptor(token)));
        }
        return restTemplate;
    }

    public static ClientHttpRequestFactory httpRequestFactory() {
        return new SkipSslVerificationHttpRequestFactory();
    }

    public static ClientHttpRequestFactory httpsRequestFactory() {
        return new SkipSslVerificationHttpRequestFactory();
    }
}
