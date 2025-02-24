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

package com.harbortek.helm.common.filter;

import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.util.SecurityUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CaptchaTokenFilter implements Filter {

    UserService userService;

    JwtDecoder jwtDecoder;

    public CaptchaTokenFilter() {
    }

    public CaptchaTokenFilter(UserService userService, JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
    }

    public void setJwtDecoder(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setCharacterEncoding("UTF-8");

        String accessToken = getAccessTokenFromHeader(request);

        if (StringUtils.isEmpty(accessToken)) {
            accessToken = getAccessTokenFromParameter(request);
        }

        if (StringUtils.isEmpty(accessToken)) {
            accessToken = getAccessTokenFromCookie(request);
        }

        // Get authorization header and validate
        if (StringUtils.isEmpty(accessToken)) {
            log.error("No access token found in request. {}", request.getRequestURL());
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication =
                SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
        if (authentication == null) {
            try {
                BearerTokenAuthenticationToken token = new BearerTokenAuthenticationToken(accessToken);
                token.setAuthenticated(true);
                SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(token);
                Jwt jwt = jwtDecoder.decode(accessToken);
                Long userId = jwt.getClaim("userId");
                UserVo user = userService.findOneUser(userId);
                if (user == null) {
                    token.setAuthenticated(false);
                } else {
                    token.setAuthenticated(true);
                    user.setRemoteAddress(request.getRemoteAddr());
                    SecurityUtils.setCurrentUser(user);
                }

            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        } else if (authentication instanceof JwtAuthenticationToken) {
            try {
                JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
                Long userId = token.getToken().getClaim("userId");
                UserVo user = userService.findOneUser(userId);
                if(user==null){
                    token.setAuthenticated(false);
                }else{
                    token.setAuthenticated(true);
                    user.setRemoteAddress(request.getRemoteAddr());
                    SecurityUtils.setCurrentUser(user);
                }

            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromParameter(HttpServletRequest request) {
        String auth = request.getParameter("access_token");
        if (StringUtils.isNotEmpty(auth)) {
            if (StringUtils.startsWith(auth, "Bearer")) {
                return StringUtils.substring(auth, 7);
            } else {
                return auth;
            }
        }
        return null;
    }

    private String getAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (HttpHeaders.AUTHORIZATION.equals(cookie.getName())) {
                    try {
                        String auth = URLDecoder.decode(cookie.getValue(), String.valueOf(StandardCharsets.UTF_8));
                        if (StringUtils.isNotEmpty(auth)) {
                            if (StringUtils.startsWith(auth, "Bearer")) {
                                return StringUtils.substring(auth, 7);
                            } else {
                                return auth;
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                    }
                    break;
                }
            }
        }
        return null;
    }

    private String getAccessTokenFromHeader(HttpServletRequest request) {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotEmpty(auth) && !"undefined".equals(auth)) {
            if (StringUtils.startsWith(auth, "Bearer")) {
                return StringUtils.substring(auth, 7);
            } else {
                return auth;
            }
        }
        return null;
    }
}
