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

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

public class WsWsHandlerFactory implements WebSocketHandlerDecoratorFactory {


    @Override
    public WebSocketHandler decorate(WebSocketHandler webSocketHandler) {
        return new WsWebSocketHandlerDecorator(webSocketHandler);
    }
}
