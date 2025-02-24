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

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.system.vo.PageTrackerPermissions;
import com.harbortek.helm.system.vo.TrackerFieldsPermissions;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;

import java.util.List;
import java.util.Map;

public interface ProjectPermissionService {

    List<PermissionGrantVo> findPermissionGrantList(Long projectId);

    void grantPermission(Long projectId, String permission, BaseIdentity identity);

    void unGrantPermission(Long grantId, String permission, BaseIdentity identity);


    List<Long> findProjectIdsByPermission(Long userId, String permissionName);

//    TrackerFieldsPermissions findTrackerItemCustomFieldPerms(Long projectId, Long trackerId);
}
