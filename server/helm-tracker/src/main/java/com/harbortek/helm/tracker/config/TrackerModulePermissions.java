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

package com.harbortek.helm.tracker.config;

import com.harbortek.helm.common.vo.PermissionDefinition;

public class TrackerModulePermissions {


    public final static String PROJECT_GROUP = "project";

    public static final String PROJECT_CREATE = "PROJECT_CREATE";

    public static final String PROJECT_LIST = "PROJECT_LIST";

    public static final String PROJECT_ENUM = "PROJECT_ENUM";

    public static final String SCM = "PROJECT_SCM";

    public static final String PIPELINE = "PROJECT_PIPELINE";

    public final static PermissionDefinition[] TRACKER_MODUlE_PERMISSIONS = new PermissionDefinition[]{
            new PermissionDefinition( PROJECT_CREATE, "",
                                      PROJECT_GROUP),
            new PermissionDefinition( PROJECT_LIST, "",
                                      PROJECT_GROUP),
            new PermissionDefinition( PROJECT_ENUM, "",
                                      PROJECT_GROUP),
            new PermissionDefinition( SCM, "",
                                      PROJECT_GROUP),
            new PermissionDefinition( PIPELINE,
                                     "",
                                      PROJECT_GROUP),
            };

}
