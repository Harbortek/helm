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

package com.harbortek.helm.websocket.util;

import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.util.SecurityUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.concurrent.CopyOnWriteArraySet;

public class WsUtil {

    private static final CopyOnWriteArraySet<Long> ONLINE_USERS = new CopyOnWriteArraySet();

    public static boolean onLine() {
        UserVo user = SecurityUtils.getCurrentUser();
        if (ObjectUtils.isNotEmpty(user) && ObjectUtils.isNotEmpty(user.getId()))
            return onLine(user.getId());
        return false;
    }

    public static boolean onLine(Long userId) {
        return ONLINE_USERS.add(userId);
    }

    public static boolean offLine() {
        UserVo user = SecurityUtils.getCurrentUser();
        if (ObjectUtils.isNotEmpty(user) && ObjectUtils.isNotEmpty(user.getId()))
            return offLine(user.getId());
        return false;
    }

    public static boolean offLine(Long userId) {
        return ONLINE_USERS.remove(userId);
    }

    public static boolean isOnLine(Long userId) {
        return ONLINE_USERS.contains(userId);
    }


}
