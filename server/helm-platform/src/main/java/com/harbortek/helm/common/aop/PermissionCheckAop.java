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

package com.harbortek.helm.common.aop;

import com.harbortek.helm.common.annotation.PermissionCheck;
import com.harbortek.helm.common.exception.UnauthorizedExpcetion;
import com.harbortek.helm.system.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 切点类
 *
 * @author LJN
 * @version 1.0
 * @since 2015-05-05 Pm 20:35
 */
@Aspect
@Component
public class PermissionCheckAop {

	@Autowired
	PermissionService permissionService;

	private static final Logger logger = LoggerFactory.getLogger(PermissionCheckAop.class);

	@Pointcut("@annotation(com.harbortek.helm.common.annotation.PermissionCheck)")
	public void controllerAspect() {
	}

	/**
	 * 前置通知 用于拦截Controller层操作
	 *
	 * @param joinPoint 切点
	 */
	@Before("controllerAspect()")
	public void doBefore(JoinPoint joinPoint) {
		PermissionCheck annotation = (PermissionCheck) getControllerMethodDescription(joinPoint);
		if (annotation != null && annotation.value() != null && StringUtils.isNotEmpty(annotation.value())) {
			String permission = annotation.value();
			String resource = annotation.resource();


			if (!permissionService.hasPermission(annotation.value(),null)){
//				throw new UnauthorizedExpcetion();
			}
		}
	}

	/**
	 * 操作异常记录
	 *
	 * @descript
	 * @param point
	 * @param e
	 * @author LJN
	 * @date 2015年5月5日
	 * @version 1.0
	 */
//	@AfterThrowing(pointcut = "controllerAspect()", throwing = "e")
//	public void doAfterThrowing(JoinPoint point, Throwable e) {
//		SysLog logForm = new SysLog();
//		Map<String, Object> map = null;
//		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		try {
//			map = getControllerMethodDescription(point);
//			SysUser user = userMapper.getUserByAccount(userDetails.getUsername(), userDetails.getPassword());
//			logForm.setUserId(user.getUserId());
//			logForm.setModule(map.get("methods").toString());
//			logForm.setContent(e.toString());
//			logMapper.insertLog(logForm);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//	}

	/**
	 * 前置通知 用于拦截Controller层记录用户的操作
	 *
	 * @param joinPoint 切点
	 */
//	@Around("controllerAspect()")
//	public Object doController(ProceedingJoinPoint point) {
//		Object result = null;
//		// 执行方法名
//		String methodName = point.getSignature().getName();
//		String className = point.getTarget().getClass().getSimpleName();
//		SysLog logForm = new SysLog();
//		Map<String, Object> map = null;
//		// 当前用户
//		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		try {
//			SysUser user = userMapper.getUserByAccount(userDetails.getUsername(), userDetails.getPassword());
//			map = getControllerMethodDescription(point);
//			logForm.setUserId(user.getUserId());
//			logForm.setModule(map.get("methods").toString());
//			logForm.setContent(map.get("description").toString());
//			logMapper.insertLog(logForm);
//			// *========控制台输出=========*//
//			System.out.println("=====通知开始=====");
//			System.out.println("请求方法:" + className + "." + methodName + "()");
//			System.out.println("方法描述:" + map);
//			System.out.println("=====通知结束=====");
//
//		} catch (Exception e) {
//			// 记录本地异常日志
//			logger.error("====通知异常====");
//			logger.error("异常信息:{}", e.getMessage());
//		}
//		return result;
//	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解
	 *
	 * @param joinPoint 切点
	 * @return 方法描述
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public Annotation getControllerMethodDescription(JoinPoint joinPoint) {
		try {
			String targetName = joinPoint.getTarget().getClass().getName();
			String methodName = joinPoint.getSignature().getName();
			Object[] arguments = joinPoint.getArgs();
			Class targetClass = Class.forName(targetName);
			Method[] methods = targetClass.getMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					Class[] clazzs = method.getParameterTypes();
					if (clazzs.length == arguments.length) {
						return method.getAnnotation(PermissionCheck.class);
					}
				}
			}
		} catch (Exception e) {

		}
		return null;
	}
}
