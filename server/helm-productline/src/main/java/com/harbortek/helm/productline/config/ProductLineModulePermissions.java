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

package com.harbortek.helm.productline.config;

import com.harbortek.helm.common.vo.PermissionDefinition;

public interface ProductLineModulePermissions {
    String PRODUCT_LINE_GROUP = "productLine";
    String PRODUCT_LINE_CREATE = "PRODUCT_LINE_CREATE";

    String PRODUCT_LINE_LIST = "PRODUCT_LINE_LIST";

    String REVIEW_GROUP = "review";

    String REVIEW_CREATE = "REVIEW_CREATE";

    String REVIEW_LIST = "REVIEW_LIST";

    PermissionDefinition[] PROJECT_MODUlE_PERMISSIONS = new PermissionDefinition[]{
            new PermissionDefinition(PRODUCT_LINE_CREATE, "",
                                     PRODUCT_LINE_GROUP),
            new PermissionDefinition(PRODUCT_LINE_LIST, "",
                                     PRODUCT_LINE_GROUP),
            new PermissionDefinition( REVIEW_CREATE, "",
                    REVIEW_GROUP),
            new PermissionDefinition( REVIEW_LIST, "",
                    REVIEW_GROUP)
            };
}
