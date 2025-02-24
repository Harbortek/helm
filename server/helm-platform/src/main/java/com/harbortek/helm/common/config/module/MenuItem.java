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

package com.harbortek.helm.common.config.module;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class MenuItem {
	private String code;
	private String name;
	private String path;
	private String component;
	private String redirect;
	private Collection<MenuItem> children = new ArrayList<>();
	private Integer id;
	private Integer parentId;
	/**
	 * 是否微应用
	 */
	private Boolean isMicro = false;
	//微应用基础PATH
	private String microBasePath;
	private MenuItemMeta meta;

	public MenuItem() {
	}

	public MenuItem(String code, String name, String path, String component, String redirect, Boolean isMicro,String microBasePath,
			String title, String icon, String permission) {
		this.code = code;
		this.name = name;
		this.path = path;
		this.component = component;
		this.redirect = redirect;
		this.isMicro = isMicro;
		this.microBasePath = microBasePath;
		this.meta = new MenuItemMeta(title, icon, false, permission, true);
	}

	public MenuItem(String code, String name, String path, String component, String redirect, Boolean isMicro,String microBasePath,String title, String icon,
			String permission, boolean show) {
		this.code = code;
		this.name = name;
		this.path = path;
		this.component = component;
		this.redirect = redirect;
		this.isMicro = isMicro;
		this.microBasePath = microBasePath;
		this.meta = new MenuItemMeta(title, icon, false, permission, show);
	}

	public MenuItem(String code, String name, String path, String component, String redirect,  Boolean isMicro,String microBasePath,String title, String icon,
			String permission, boolean show, Collection<MenuItem> children) {
		this.code = code;
		this.name = name;
		this.path = path;
		this.component = component;
		this.redirect = redirect;
		this.isMicro = isMicro;
		this.microBasePath = microBasePath;
		this.meta = new MenuItemMeta(title, icon, false, permission, show);
		this.children = children;
	}

	public MenuItem(String code, String name, String path, String component, String redirect, Boolean isMicro,String microBasePath,
			String title, String icon, String permission, Collection<MenuItem> children) {
		this(code, name, path, component, redirect, isMicro, microBasePath,title, icon, permission);
		this.children = children;
	}

	@Data
	@AllArgsConstructor
	@Builder(toBuilder = true)
	public static class MenuItemMeta {
		private String title;
		private String icon;
		private boolean keepAlive;
		private String permission;
		private boolean show = true;
	}

}
