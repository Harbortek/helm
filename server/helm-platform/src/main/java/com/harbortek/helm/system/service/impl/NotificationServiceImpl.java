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

package com.harbortek.helm.system.service.impl;

import com.harbortek.helm.system.dao.NotificationDao;
import com.harbortek.helm.system.entity.NotificationEntity;
import com.harbortek.helm.system.service.NotificationService;
import com.harbortek.helm.system.vo.NotificationVo;
import com.harbortek.helm.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    NotificationDao notificationDao;
    @Override
    public NotificationVo sendNotification(NotificationVo notification) {
        NotificationEntity
                notificationEntity = DataUtils.toEntity(notification, NotificationEntity.class);
        return DataUtils.toVo( notificationDao.send(notificationEntity), NotificationVo.class);
    }

    @Override
    public void readNotification(Long id) {
        notificationDao.readIt(id);
    }

    @Override
    public void deleteNotification(NotificationVo notification) {
        notificationDao.delete(notification.getId());
    }

    @Override
    public NotificationVo findOne(Long id) {
        return DataUtils.toVo( notificationDao.findById(id), NotificationVo.class);
    }

    @Override
    public void sendNotifications(List<NotificationVo> notificationEntities) {
        notificationDao.batchCreate(DataUtils.toEntity(notificationEntities, NotificationEntity.class));
    }

    @Override
    public Page<NotificationVo> findUnreadNotifications(Long userId, Pageable pageable) {
        return DataUtils.toVo(notificationDao.findNotifications(userId, true,false,pageable), NotificationVo.class);
    }

    @Override
    public Page<NotificationVo> findAllNotifications(Long userId, Pageable pageable) {
        return DataUtils.toVo(notificationDao.findNotifications(userId,true,true,pageable), NotificationVo.class);
    }

    @Override
    public void readAllNotification(Long userId) {
        notificationDao.readAllNotification(userId);
    }
}
