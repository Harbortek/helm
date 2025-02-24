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

package com.harbortek.helm.system.service;

import com.harbortek.helm.system.vo.NotificationVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

	NotificationVo sendNotification(NotificationVo notification);

	void readNotification(Long notification);

	void deleteNotification(NotificationVo notification);

	NotificationVo findOne(Long id);

    void sendNotifications(List<NotificationVo> notificationEntities);

	Page<NotificationVo> findUnreadNotifications(Long userId, Pageable pageable);

	Page<NotificationVo> findAllNotifications(Long userId, Pageable pageable);

	void readAllNotification(Long id);
}
