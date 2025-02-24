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

package com.harbortek.helm.util;


import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.vo.UserVo;

import java.util.Map;

public class SecurityUtils {

    public final static String PROJECT_ID = "PROJECT-ID";
    public final static String PRODUCT_LINE_ID = "PRODUCT-LINE-ID";

	static ThreadLocal<Map<String,Object>> context = new ThreadLocal<Map<String,Object>>();

	public static UserVo getCurrentUser() {
		return (UserVo) get("USER");
	}

	public static void setCurrentUser(UserVo user) {
		set("USER",user);
	}

	public static Object get(String key) {

        Map<String,Object> values = context.get();
        if (values == null) {
            return null;
        }
        return values.get(key);
    }

    public static void set(String key,Object value) {

        Map<String,Object> values = context.get();
        if (values == null) {
            values = new java.util.HashMap<String,Object>();
        }
        values.put(key,value);
        context.set(values);
    }

    public static void checkPermission(String permission,Long resourceId){
        if (!SpringContextUtil.getBean(PermissionService.class).hasPermission(permission,resourceId)){
            throw new ServiceException("没有权限");
        }
    }
}
