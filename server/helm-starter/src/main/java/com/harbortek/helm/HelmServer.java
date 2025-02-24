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

package com.harbortek.helm;

import com.harbortek.helm.util.JsonUtils;
import com.harbortek.helm.util.SQLUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by hujun on 2018/1/3.
 */
@ServletComponentScan
@SpringBootApplication(scanBasePackages = "com.harbortek.helm")
@EnableScheduling
@Configuration
@EnableWebMvc
@EnableCaching
@EnableAsync
@Slf4j
public class HelmServer {

	public static void main(String[] args) {
		if(System.getenv("HELM_HOME") == null && System.getProperty("HELM_HOME")==null){
			log.error("HELM_HOME not found in your environment. Please set the HELM_HOME variable");
			return;
		}


		SQLUtils.loadEntityTables();
		JsonUtils.getObjectMapper();

		SpringApplication.run(HelmServer.class, args);

	}
}
