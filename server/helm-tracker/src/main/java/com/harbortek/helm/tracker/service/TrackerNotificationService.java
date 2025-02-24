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

package com.harbortek.helm.tracker.service;

import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;

public interface TrackerNotificationService {
    void sendSystemNotification(String eventType, TrackerEntity tracker, TrackerItemEntity item, UserVo currentUser);

    void sendCustomerNotification(TrackerField field, TrackerEntity tracker,
                                  TrackerItemEntity item,UserVo currentUser);
}
