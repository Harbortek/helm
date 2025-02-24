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

package com.harbortek.helm.system.api;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.common.config.module.ModuleManager;
import com.harbortek.helm.common.security.UsernamePasswordCaptchaToken;
import com.harbortek.helm.system.config.SystemMessages;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.LoginVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.util.LogUtils;
import com.harbortek.helm.util.MD5Utils;
import com.harbortek.helm.util.RandImageUtil;
import com.harbortek.helm.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Date;

@RestController
@Tag(name = "用户登录")
public class LoginApi {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtEncoder jwtEncoder;

	@Autowired
	UserService userService;

	@Autowired
	ModuleManager moduleManager;

	@Autowired
	PermissionService permissionService;

	@Autowired
	SystemMessages systemMessages;
	private final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

	@Parameter(name="登录接口")
	@RequestMapping(value = "/auth/login", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> login(HttpServletRequest request, @RequestBody LoginVo loginVo) {

		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordCaptchaToken(
					loginVo.getUsername(), loginVo.getPassword(), loginVo.getCaptcha()));
			String loginName = (String) authentication.getPrincipal();
			UserVo loginUser = userService.findOneUserByLoginName(loginName);
			loginUser.setLastLogin(new java.util.Date());

			loginUser.setRemoteAddress(request.getRemoteAddr());
			SecurityUtils.setCurrentUser(loginUser);
			Instant now = Instant.now();
			long expiry = 3600*8L ; // 8 hour
			if (loginVo.getRememberMe()) {
				expiry = 3600L * 24 * 30; // 1 month
			}

			JwtClaimsSet claims = JwtClaimsSet.builder().claim("userId", loginUser.getId())
					.expiresAt(now.plusSeconds(expiry)).build();

			String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
			JSONObject body = new JSONObject();
			body.set("access_token","Bearer "+ token);
			body.set("token_type", "bearer");
			body.set("expires_in", Date.from(now.plusSeconds(expiry)));

			// 更新最后一次登录时间
			userService.updateUserLastLogin(loginUser.getId());
			LogUtils.log("系统管理","登录","用户 {0} 成功登录",loginUser.getName());

			return ResponseEntity.ok().body(body);
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}


	@Parameter(name="查询用户信息接口")
	@RequestMapping(value = "/user/info", method = RequestMethod.GET)
	public ResponseEntity<UserVo> getUserInfo() {
		UserVo user = SecurityUtils.getCurrentUser();
		user = userService.findOneUser(user.getId());
		user.setPermissions(permissionService.loadPermissions(user));
		return ResponseEntity.ok(user);
	}

	/**
	 * 后台生成图形验证码 ：有效
	 *
	 * @param response
	 * @param key
	 */
	@Parameter(name="获取验证码")
	@GetMapping(value = "/auth/verifyCode")
	public ResponseEntity<String> randomImage(HttpServletResponse response, @RequestParam("key") String key) {
		try {
			// 生成验证码
			String code = RandomUtil.randomString(BASE_CHECK_CODES, 4);
			// 存到redis中
			String lowerCaseCode = code.toLowerCase();

			String origin = lowerCaseCode + key;
			String realKey = MD5Utils.convertMD5(origin);

//			redisUtil.set(realKey, lowerCaseCode, 60);
//			log.info("获取验证码，Redis key = {}，checkCode = {}", realKey, code);
			// 返回前端
			String base64 = RandImageUtil.generate(code);
			return ResponseEntity.ok().body(base64);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("验证码生成失败");
		}
	}

	@Parameter(name="登出接口")
	@RequestMapping(value = "/auth/logout", method = RequestMethod.POST)
	public ResponseEntity<Void> logout() {
		LogUtils.log("系统管理","登出","用户 {0} 成功登出",SecurityUtils.getCurrentUser().getName());
		return ResponseEntity.ok().build();
	}

}
