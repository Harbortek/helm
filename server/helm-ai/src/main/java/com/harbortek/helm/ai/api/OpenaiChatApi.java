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

package com.harbortek.helm.ai.api;

import com.harbortek.helm.ai.service.OpenAiService;
import com.harbortek.helm.ai.service.RAGFlowService;
import com.harbortek.helm.util.JsonUtils;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.reactivex.Flowable;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/ai/openai/v1")
@Log
public class OpenaiChatApi {
    @Autowired
    private OpenAiService openAiService;

    @PostMapping(path = "/chat/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody Map<String, Object> request) throws Exception {
        SseEmitter emitter = new SseEmitter(60_000L); // 60秒超时

        String model = (String) request.get("model");
        Object messagesObj = request.get("messages");
        List<ChatMessage> messages = JsonUtils.toList(JsonUtils.toJSONString(messagesObj), ChatMessage.class);
        Flowable<ChatCompletionChunk> flowable = this.openAiService.streamChat(messages);

        flowable.subscribe(
                chunk -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .data(chunk, MediaType.APPLICATION_JSON)
                                .id(UUID.randomUUID().toString()));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                },
                emitter::completeWithError,
                emitter::complete
        );

        // 处理客户端断开连接
        emitter.onCompletion(() -> {
            // 可以在这里添加清理逻辑
            log.info("SSE connection completed");
        });

        emitter.onTimeout(() -> {
            log.warning("SSE connection timed out");
            emitter.complete();
        });

        return emitter;
    }

    /**
     * 实现list models
     */
    @GetMapping("/models")
    public void listModels(HttpServletResponse response) {
        Map map = this.openAiService.listModels();
        try {
            response.getWriter().write(JsonUtils.toJSONString(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
