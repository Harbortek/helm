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

package com.harbortek.helm.pipeline.jenkins;

import java.util.Collections;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import com.harbortek.helm.common.client.SkipSslVerificationHttpRequestFactory;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class JenkinsRequestUtils {

    public static RestTemplate httpRestTemplate(String username,String password) {
        ClientHttpRequestFactory factory = httpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        // 可以增加拦截器
        restTemplate.setInterceptors(
                Collections.singletonList(new BasicAuthenticationInterceptor(username,
                                                                             password)));
        return restTemplate;
    }

    public static RestTemplate httpsRestTemplate(String username,String password) {
        RestTemplate restTemplate = new RestTemplate(httpsRequestFactory());
        // 可以增加拦截器
        restTemplate.setInterceptors(
                Collections.singletonList(new BasicAuthenticationInterceptor(username,
                                                                             password)));
        return restTemplate;
    }

    public static ClientHttpRequestFactory httpRequestFactory() {
        return new SkipSslVerificationHttpRequestFactory();
    }

    public static ClientHttpRequestFactory httpsRequestFactory() {
        return new SkipSslVerificationHttpRequestFactory();
    }


}
