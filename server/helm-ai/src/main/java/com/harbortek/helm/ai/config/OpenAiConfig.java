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

package com.harbortek.helm.ai.config;

import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import lombok.Data;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@Data
@ConfigurationProperties(prefix = "openai.api")
public class OpenAiConfig {

    private String baseUrl;
    private String apiKey;
    private String chatId;
    private String model;
    private Long timeout = 10L;

    @Bean
    public OpenAiService getOpenAiService() {
        OpenAiService openAiService = new OpenAiService(apiKey, Duration.ofSeconds(timeout), baseUrl);
//        OkHttpClient client = new OkHttpClient.Builder()
//                //连接池
//                .connectionPool(new ConnectionPool(Runtime.getRuntime().availableProcessors() * 2, 30, TimeUnit.SECONDS))
//                //自定义的拦截器,如重试拦截器,日志拦截器,负载均衡拦截器等
//                // .addInterceptor(new RetryInterceptor())
//                // .addInterceptor(new LogInterceptor())
//                // .addInterceptor(new LoadBalanceInterceptor())
//                //添加代理
//                // .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxyHost", 8080)))
//                .connectTimeout(2, TimeUnit.SECONDS)
//                .writeTimeout(3, TimeUnit.SECONDS)
//                .readTimeout(10, TimeUnit.SECONDS)
//                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
//                .build();
////4.2 自定义Retorfit配置
//        Retrofit retrofit = OpenAiService.defaultRetrofit(client, OpenAiService.defaultObjectMapper(), baseUrl);
//        OpenAiApi openAiApi = retrofit.create(OpenAiApi.class);
//        OpenAiService openAiService = new OpenAiService(openAiApi);
        return openAiService;
    }

}