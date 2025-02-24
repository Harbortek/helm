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

package com.harbortek.helm.websocket.handler;

import com.harbortek.helm.websocket.entity.WsPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Slf4j
public class PrincipalHandshakeHandler extends DefaultHandshakeHandler {
    private JwtDecoder jwtDecoder;

    public PrincipalHandshakeHandler(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;

            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // 从请求中提取 Cookie
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("Authorization".equals(cookie.getName())) {
                        String bearerToken = cookie.getValue();
                        // 验证令牌格式并提取用户信息
                        if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer")) {
                            try {
                                // 移除 "Bearer " 前缀，从令牌中提取用户信息(username), 并设置到认证信息中
                                String tokenWithoutPrefix = bearerToken.substring(9);
                                Jwt jwt = this.jwtDecoder.decode(tokenWithoutPrefix);
                                Long userId = jwt.getClaim("userId");
                                if (userId == null) {
                                    return null;
                                } else {
                                    return new WsPrincipal(userId.toString(),null);
                                }
                            } catch (Exception e) {
                                log.error("Failed to process authentication token.", e);
                            }
                        }
                        break;
                    }
                }
            }
        }
        return null;
    }
}
