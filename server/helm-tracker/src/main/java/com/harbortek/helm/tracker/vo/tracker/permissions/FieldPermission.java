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

/*
 * 项目名称:浩博ELM协同平台
 * Copyright 南京浩博科技有限公司 2023 版权所有
 *
 */

package com.harbortek.helm.tracker.vo.tracker.permissions;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.IdNameReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuperBuilder
@Data
@EqualsAndHashCode
@FieldNameConstants
@NoArgsConstructor
public class FieldPermission implements Serializable {
	public final static String UNRESTRICTED = "Unrestricted";
	public final static String SINGLE = "Single";
	public final static String SAME_AS = "Same As";
	public final static String PER_STATUS = "PerStatus";


	String type;

	List<BaseIdentity> singlePermissions = new ArrayList<>();

	Map<Object, List<BaseIdentity>> statusPermissions;

	IdNameReference sameAsField;

//	@SuperBuilder
//	@Data
//	@EqualsAndHashCode
//	@FieldNameConstants
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class UserRoleReadEdit {
//		Collection<UserReadEdit> participants;
//		Collection<RoleReadEdit> roles;
//	}
//
//	@SuperBuilder
//	@Data
//	@EqualsAndHashCode
//	@FieldNameConstants
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class UserReadEdit {
//		IdNameReference<UserVo> user;
//
//		Boolean read;
//		Boolean edit;
//	}
//
//	@SuperBuilder
//	@Data
//	@EqualsAndHashCode
//	@FieldNameConstants
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class RoleReadEdit {
//		IdNameReference<ProjectRoleMemberVo> roleMember;
//
//		Boolean read;
//		Boolean edit;
//	}
//
//
//	public static FieldPermission unrestricted(){
//		return FieldPermission.builder().type(UNRESTRICTED).build();
//	}
//
//	public static FieldPermission singleReadEdit(Collection<ProjectRoleMemberVo> roles){
//		Collection<RoleReadEdit> single =
//				roles.stream().filter(r->!r.getSpecialRole()).map( r -> new RoleReadEdit(new IdNameReference<>(r),
//																						 true,true) )
//				.collect(Collectors.toList());
//		return FieldPermission.builder().type(SINGLE).singlePermissions(new UserRoleReadEdit(Collections.EMPTY_LIST, single)).build();
//	}
//
//	public static FieldPermission singleRead(Collection<ProjectRoleMemberVo> roles){
//		Collection<RoleReadEdit> single = roles.stream().filter(r->!r.getSpecialRole()).map( r -> new RoleReadEdit(new IdNameReference<>(r),true,false) )
//				.collect(Collectors.toList());
//		return FieldPermission.builder().type(SINGLE).singlePermissions(new UserRoleReadEdit(Collections.EMPTY_LIST, single)).build();
//	}
//
//	public static FieldPermission singleNone(Collection<ProjectRoleMemberVo> roles){
//		Collection<RoleReadEdit> single = roles.stream().filter(r->!r.getSpecialRole()).map( r -> new RoleReadEdit(new IdNameReference<>(r),false,false) )
//				.collect(Collectors.toList());
//		return FieldPermission.builder().type(SINGLE).singlePermissions(new UserRoleReadEdit(Collections.EMPTY_LIST, single)).build();
//	}
//
//	public static FieldPermission perStatus(Collection<TrackerStatus> trackerStatuses){
//		//TODO
//		return null;
//	}

}
