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

import com.harbortek.helm.system.service.NotificationService;
import com.harbortek.helm.system.vo.NotificationVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "系统管理-通知管理")
@RequestMapping(value = "/sys/notification")
public class NotificationApi {
	@Autowired
	NotificationService notificationService;

	@Parameter(name="查询未读通知列表")
	@RequestMapping(value = "/list/unread", method = RequestMethod.GET)
	public ResponseEntity<Page<NotificationVo>> findUnreadNotifications(Pageable pageable){
		Page<NotificationVo> notifications =
				notificationService.findUnreadNotifications(SecurityUtils.getCurrentUser().getId(), pageable);
		Page<NotificationVo> page=DataUtils.toVo(notifications, NotificationVo.class);
		return ResponseEntity.ok(page);
	}

	@Parameter(name="查询所有通知列表")
	@RequestMapping(value = "/list/all", method = RequestMethod.GET)
	public ResponseEntity<Page<NotificationVo>> findAllNotifications(Pageable pageable){
		Page<NotificationVo> notifications = notificationService.findAllNotifications(SecurityUtils.getCurrentUser().getId(),pageable);
		Page<NotificationVo> page=DataUtils.toVo(notifications, NotificationVo.class);
		return ResponseEntity.ok(page);
	}

	@Parameter(name="阅读一条通知")
	@RequestMapping(value = "/read/{id}", method = RequestMethod.POST)
	public ResponseEntity<Void> readIt(@PathVariable Long id){
		notificationService.readNotification(id);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="阅读一条通知")
	@RequestMapping(value = "/read/all", method = RequestMethod.POST)
	public ResponseEntity<Void> readAll(Pageable pageable){
		notificationService.readAllNotification(SecurityUtils.getCurrentUser().getId());
		return ResponseEntity.ok().build();
	}



}
