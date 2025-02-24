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

package com.harbortek.helm.common.security;

import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CaptchaAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) authentication;
        String loginName = token.getPrincipal();
        String password = token.getCredentials();

        String captcha = token.getCaptcha();

        //1. 检查校验码
//			if (captcha == null) {
//				throw new ServiceException("验证码无效");
//			}
        //String lowerCaseCaptcha = captcha.toLowerCase();

        UserVo sysUser = userService.findOneUserByLoginName(loginName);
        if (sysUser == null) {
            throw new ServiceException("用户账号不存在");
        }

        //2. 校验用户名或密码是否正确
        String sysPassword = sysUser.getPassword();
        if (!sysPassword.equals(password)) {
            throw new ServiceException("用户账号和密码不匹配");
        }
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordCaptchaToken(loginName, password, null, simpleGrantedAuthorities);

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordCaptchaToken.class.isAssignableFrom(authentication);
    }
}
