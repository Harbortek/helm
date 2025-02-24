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

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UsernamePasswordCaptchaToken extends AbstractAuthenticationToken {
	private String principal;
	private String credentials;
	private String captcha;

	public UsernamePasswordCaptchaToken(String principal, String credentials, String captcha) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
		this.captcha = captcha;
		setAuthenticated(false);
	}

	public UsernamePasswordCaptchaToken(String principal, String credentials, String captcha, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		this.captcha = captcha;
		setAuthenticated(true);
	}

	public String getCaptcha() {
		return captcha;
	}

	@Override
	public String getCredentials() {
		return credentials;
	}

	@Override
	public String getPrincipal() {
		return principal;
	}
}
