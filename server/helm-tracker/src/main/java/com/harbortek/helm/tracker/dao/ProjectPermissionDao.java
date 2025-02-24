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

package com.harbortek.helm.tracker.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.util.ObjectUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProjectPermissionDao  extends BaseJdbcDao {

    public List<Long> findProjectIdsByUserId(Long userId, String permissionName) {

        String sql = """
                select distinct id from (
                                            select projects.id,
                                                   projects.owner_id,
                                                   p.name,
                                                   p.identity_id,
                                                   p.identity_type,
                                                   roles.id as role_id,
                                                   roles.special_role_type,
                                                   rm.user_id
                                            from projects
                                                     left join permissions p on projects.id = p.resource_id
                                                     left join roles on roles.owner_resource_id = projects.id and roles.id = p.identity_id and
                                                                        (p.identity_type = 'IDENTITY_ROLE' or p.identity_type = 'IDENTITY_SPECIAL_ROLE')
                                                     left join role_members rm on roles.id = rm.role_id
                
                                            where p.name = :permissionName
                                        ) t
                where
                      (identity_type='IDENTITY_SPECIAL_ROLE' and  special_role_type='PROJECT_OWNER' and t.owner_id=:userId)
                   or (identity_type='IDENTITY_SPECIAL_ROLE' and  special_role_type='PROJECT_ALL_MEMBERS' and t.owner_id=:userId)
                   or (identity_type='IDENTITY_SPECIAL_ROLE' and  special_role_type='ALL_USERS')
                   or (identity_type='IDENTITY_USER' and identity_id=:userId)
                   or (identity_type='IDENTITY_ROLE' and user_id=:userId);
                """;
        Map<String,Object> params = new HashMap<>();
        params.put("userId",userId);
        params.put("permissionName",permissionName);
        return ObjectUtils.ids(find(sql, params, ProjectEntity.class));
    }
}
