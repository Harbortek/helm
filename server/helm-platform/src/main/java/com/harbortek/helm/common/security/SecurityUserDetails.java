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

package com.harbortek.helm.common.security;

import com.harbortek.helm.system.vo.UserVo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by DK on 2017/5/25.
 */
public class SecurityUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

	private UserVo userEntity;

    private Collection<? extends SimpleGrantedAuthority> authorities = new ArrayList<>();

    public SecurityUserDetails(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public SecurityUserDetails(UserVo userEntity){
    	this.userEntity = userEntity;
    	this.username = this.userEntity.getLoginName();
    	this.password = this.userEntity.getPassword();
	}

	public UserVo getUserEntity() {
		return userEntity;
	}

	public void setUserEntity(UserVo userEntity) {
		this.userEntity = userEntity;
		this.username = this.userEntity.getLoginName();
		this.password = this.userEntity.getPassword();
	}

	public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setAuthorities(Collection<? extends SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
