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

package com.harbortek.helm.smartdoc.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.harbortek.helm.smartdoc.config.OperationProcessor;
import com.harbortek.helm.smartdoc.editor.operation.Operation;
import com.harbortek.helm.smartdoc.editor.operation.Operations;
import com.harbortek.helm.tracker.service.DocService;
import com.harbortek.helm.util.JsonUtils;
import com.harbortek.helm.websocket.entity.WsMessage;
import com.harbortek.helm.websocket.entity.WsPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DocWS {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private DocService docService;

    @Autowired
    private OperationProcessor operationProcessor;

    private final String TOPIC_SMART_DOC_NOTICE_PREFIX = "/smart-doc/notice/";

    @MessageMapping("/smart-doc/modified/{docId}")
    public void modifiedv2(Principal principal, @DestinationVariable Long docId, @Payload String payloadStr)
            throws Exception {
        Long userId = Long.parseLong(principal.getName());
        String projectId = ((WsPrincipal) principal).getProjectId();

        //0. 转payload为数组，并遍历获取ref

        List<Operation> operations = Operations.parse(payloadStr);
        operations.forEach(operation -> {
            operation.setDocId(docId);
            operation.setUserId(userId);
            operation.setProjectId(Long.parseLong(projectId));
            operationProcessor.addOperation(operation);
        });
        WsMessage msg = new WsMessage<>(principal.getName(), "IN");
        this.broadcast(TOPIC_SMART_DOC_NOTICE_PREFIX + docId, msg);
    }

    /**
     * 广播消息
     *
     * @param topic   主题
     * @param message 消息体
     */
    private void broadcast(String topic, WsMessage message) throws JsonProcessingException {
        message.setTopic(topic);

        // 广播发送消息
        this.messagingTemplate.convertAndSendToUser(
                message.getUserId(),
                topic,
                JsonUtils.getObjectMapper().writeValueAsString(message));
    }

    /**
     * 点对点发送消息
     * <p>
     * 模拟 张三 给 李四 发送消息场景
     *
     * @param principal 当前用户
     * @param userId    接收消息的用户
     * @param message   消息内容
     */
    @MessageMapping("/sendToUser/{userId}")
    public void sendToUser(Principal principal, @DestinationVariable String userId, String message) {

        String sender = principal.getName(); // 发送人
        String receiver = userId; // 接收人

        log.info("发送人:{}; 接收人:{}", sender, receiver);
        // 发送消息给指定用户 /user/{username}/queue/greeting
        messagingTemplate.convertAndSendToUser(receiver, "/queue/greeting", new WsMessage(userId, message, null));
    }

    @RequestMapping(value = "/ws/info", method = RequestMethod.GET)
    ResponseEntity<List<String>> info() {
        return ResponseEntity.ok(null);
    }

}