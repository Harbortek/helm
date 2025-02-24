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

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.harbortek.helm.smartdoc.editor.operation.Operation;
import com.harbortek.helm.smartdoc.editor.operation.util.SlateOperationApplier;
import com.harbortek.helm.smartdoc.editor.operation.util.SlateParser;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.entity.block.DocEntity;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.block.BlockParser;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.service.DocService;
import com.harbortek.helm.tracker.service.TrackerItemService;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.JsonUtils;
import com.harbortek.helm.util.SecurityUtils;
import com.harbortek.helm.websocket.entity.WsMessage;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Log
public class OperationProcessor {
    private final String TOPIC_SMART_DOC_NOTICE_PREFIX = "/smart-doc/notice/";
    private OperationQueue operationQueue = new OperationQueue();
    private ExecutorService operationExecutorService = Executors.newSingleThreadExecutor();
    private ExecutorService operatorExecutorService = Executors.newSingleThreadExecutor();
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private Operator operator;
    @Autowired
    private DocService docService;
    @Autowired
    private TrackerItemService trackerItemService;

    public OperationProcessor() {
        startProcessing();
    }

    public void addOperation(Operation operation) {
        operationQueue.addOperation(operation);
    }

    private void startProcessing() {
        operationExecutorService.submit(() -> {
            while (true) {
                try {
                    Operation operation = operationQueue.takeOperation();
                    processOperation(operation);
                } catch (Exception e) {
                    log.severe(e.getMessage());
//                    Thread.currentThread().interrupt();
//                    break;
                }
            }
        });
        operatorExecutorService.submit(() -> {
            while (true)
                try {
                    operator.tryToSave();
                    Thread.sleep(8 * 1000);
                } catch (Exception e) {
                    log.severe(e.getMessage());
                }
        });
    }

    private void processOperation(Operation operation) throws IOException {
        Long docId = operation.getDocId();
        Long projectId = operation.getProjectId();
        Long userId = operation.getUserId();
        SecurityUtils.setCurrentUser(UserVo.builder().id(userId).build());
        SecurityUtils.set(SecurityUtils.PROJECT_ID, projectId);

        DocVo docVo = operator.get(docId);
        operator.updateStatus(docId, Operator.DocVoStatus.IN_USE);

        //真正的处理
        SlateOperationApplier slateOperationApplier = new SlateOperationApplier(trackerItemService);
        slateOperationApplier.applyOperation(docVo.getElements(), operation);

        operator.updateStatus(docId, Operator.DocVoStatus.UNUSED);

        WsMessage msg = new WsMessage<>(operation.getUserId() + "", "OP");
        this.broadcast(TOPIC_SMART_DOC_NOTICE_PREFIX + operation.getDocId(), msg);
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
}