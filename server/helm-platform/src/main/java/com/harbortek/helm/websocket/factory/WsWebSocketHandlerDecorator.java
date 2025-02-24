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

package com.harbortek.helm.websocket.factory;

import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.util.SecurityUtils;
import com.harbortek.helm.websocket.entity.WsPrincipal;
import com.harbortek.helm.websocket.util.WsUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.Optional;

public class WsWebSocketHandlerDecorator extends WebSocketHandlerDecorator {


    public WsWebSocketHandlerDecorator(WebSocketHandler delegate) {
        super(delegate);
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        HttpHeaders headers = session.getHandshakeHeaders();
        Optional.ofNullable(session.getPrincipal()).ifPresent(principal -> {
            if (principal instanceof WsPrincipal) {
                String name = principal.getName();
                Long userId = Long.parseLong(name);
                WsUtil.onLine(userId);
            }
        });
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Optional.ofNullable(session.getPrincipal()).ifPresent(principal -> {
            String name = principal.getName();
            Long userId = Long.parseLong(name);
            WsUtil.offLine(userId);
        });
        super.afterConnectionClosed(session, closeStatus);
    }

}
