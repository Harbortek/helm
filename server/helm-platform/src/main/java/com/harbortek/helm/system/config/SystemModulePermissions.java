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

package com.harbortek.helm.system.config;

import com.harbortek.helm.common.vo.PermissionDefinition;

public class SystemModulePermissions {
    public final static String MODULE = "system";

    public final static String SYS_SETTINGS = "system.settings";
    public final static String SYS_SETTINGS_DEPT = "SYSTEM_DEPT";
    public final static String SYS_SETTINGS_USER = "SYSTEM_USER";
    public final static String SYS_SETTINGS_ROLE = "SYSTEM_ROLE";
    public final static String SYS_SETTINGS_PARAM = "SYSTEM_PARAM";
    public final static String SYS_SETTINGS_ENUM = "SYSTEM_ENUM";
    public final static String SYS_SETTINGS_LOG = "SYSTEM_LOG";
    public final static String SYS_SETTINGS_SYSTEM = "system";

    public final static String PERSONAL_SETTINGS = "personal-settings";
    public final static String PERSONAL = "PERSONAL_SETTINGS";


    public final static PermissionDefinition[] SYSTEM_PERMISSIONS = new PermissionDefinition[]{
            new PermissionDefinition(
                    SYS_SETTINGS_DEPT,
                    "",
                    SYS_SETTINGS),
            new PermissionDefinition(
                    SYS_SETTINGS_USER,
                    "",
                    SYS_SETTINGS),
            new PermissionDefinition(
                    SYS_SETTINGS_ROLE,
                    "",
                    SYS_SETTINGS),
            new PermissionDefinition(
                    SYS_SETTINGS_PARAM,
                    "",
                    SYS_SETTINGS),
//            new PermissionDefinition(
//                    SYS_SETTINGS_ENUM,
//                    "",
//                    SYS_SETTINGS),
            new PermissionDefinition(
                    SYS_SETTINGS_LOG,
                    "",
                    SYS_SETTINGS),
            new PermissionDefinition(
                    SYS_SETTINGS_SYSTEM,
                    "",
                    SYS_SETTINGS),

            new PermissionDefinition(
                    PERSONAL,
                    "",
                    PERSONAL_SETTINGS),
            };

}
