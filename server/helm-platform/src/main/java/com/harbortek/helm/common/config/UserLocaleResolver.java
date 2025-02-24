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

package com.harbortek.helm.common.config;

import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.util.SecurityUtils;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
@Configuration
public class UserLocaleResolver implements LocaleResolver {
	@Autowired
	private HttpServletRequest request;

	public Locale getLocal() {
		return resolveLocale(request);
	}

	/**
	 * 从HttpServletRequest中获取Locale
	 *
	 * @param httpServletRequest    httpServletRequest
	 * @return                      语言Local
	 */
	@Override
	public Locale resolveLocale(HttpServletRequest httpServletRequest) {
		UserVo user  = SecurityUtils.getCurrentUser();
		String language = user.getLanguage();

		Locale locale = Locale.getDefault();
		//如果请求的链接中携带了 国际化的参数
		if (!StringUtils.isEmpty(language)){
			//zh_CN
			String[] s = language.split("_");
			//国家，地区
			locale = new Locale(s[0], s[1]);
		}
		return locale;
	}

	/**
	 * 用于实现Locale的切换。比如SessionLocaleResolver获取Locale的方式是从session中读取，但如果
	 * 用户想要切换其展示的样式(由英文切换为中文)，那么这里的setLocale()方法就提供了这样一种可能
	 *
	 * @param request               HttpServletRequest
	 * @param httpServletResponse   HttpServletResponse
	 * @param locale                locale
	 */
	@Override
	public void setLocale(@NonNull HttpServletRequest request, @Nullable HttpServletResponse httpServletResponse, @Nullable Locale locale) {

	}
}
