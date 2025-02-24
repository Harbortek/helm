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

import cn.hutool.json.JSONObject;
import com.harbortek.helm.ai.config.RAGApiConfig;
import com.harbortek.helm.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RAGFlowService {

    @Autowired
    private RAGApiConfig ragflowApiConfig;
    @Autowired
    private WebClient webClient;

    public Flux<Object> sendChatRequest(String message) throws Exception {
        String sessionId = this.createChatSession();

        Map<String, Object> request = new HashMap<>();
        request.put("question", message);
        request.put("session_id", sessionId);
        request.put("stream", true);

        return webClient.post()
                .uri(ragflowApiConfig.getBaseUrl() + "/api/v1/chats/" + ragflowApiConfig.getChatId() + "/completions")
                .header("Authorization", "Bearer " + ragflowApiConfig.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(Object.class);
    }

    public String createChatSession() throws Exception {
        Map<String, Object> bodyParams = new HashMap();
        bodyParams.put("name", "new session");
        Flux<String> response = webClient.post()
                .uri(ragflowApiConfig.getBaseUrl() + "/api/v1/chats/" + ragflowApiConfig.getChatId() + "/sessions")
                .header("Authorization", "Bearer " + ragflowApiConfig.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bodyParams)
                .retrieve()
                .bodyToFlux(String.class);
        String resp = StringUtils.join(response.collectList().block(),"");
        JSONObject respMap =JsonUtils.parseObject(resp);
        return respMap.getJSONObject("data").getStr("id");
    }

    public void deleteChatSession(List<String> sessionIds) throws Exception {
        Map<String, Object> bodyParams = new HashMap();

    }

}