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

package com.harbortek.helm.ai.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.ai.config.OpenAiConfig;
import com.harbortek.helm.ai.config.RAGApiConfig;
import com.harbortek.helm.util.JsonUtils;
import com.theokanning.openai.completion.chat.*;
import io.reactivex.Flowable;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jooq.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnExpression("'${openai.api.baseUrl}' != null and '${openai.api.baseUrl}' != ''")
public class OpenAiService {
    @Autowired
    private OpenAiConfig openAiConfig;
    @Autowired
    private com.theokanning.openai.service.OpenAiService openAiService;

    public Flowable<ChatCompletionChunk> streamChat(List<ChatMessage> messages) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(openAiConfig.getModel())
                .messages(messages)
                .n(1)
                .stream(true)
                .maxTokens(2048)
                .build();
        return openAiService.streamChatCompletion(chatCompletionRequest);
    }


    public JSONObject listModels() {
        JSONObject m1 = new JSONObject();
        m1.put("id", openAiConfig.getModel());
        m1.put("object", "model");
        m1.put("created", System.currentTimeMillis());
        m1.put("owned_by", "openai");
        JSONArray arr = new JSONArray();
        arr.add(m1);
        JSONObject data = new JSONObject();
        data.put("data", arr);
        return data;
    }
}