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

import com.harbortek.helm.ai.service.RAGFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/ai/rag")
public class RagFlowChatApi {
    @Autowired
    private RAGFlowService ragFlowService;

    @GetMapping(path = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestParam String prompt) throws Exception {
        SseEmitter emitter = new SseEmitter(60_000L); // 60秒超时

        // 调用Ragflow API（假设返回Flux<String>）
        Flux<Object> ragflowResponse = this.ragFlowService.sendChatRequest(prompt);

        // 处理流式响应
        ragflowResponse.subscribe(
                chunk -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .data(chunk)
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
            System.out.println("SSE connection completed");
        });

        emitter.onTimeout(() -> {
            System.out.println("SSE connection timed out");
            emitter.complete();
        });

        return emitter;
    }


}
