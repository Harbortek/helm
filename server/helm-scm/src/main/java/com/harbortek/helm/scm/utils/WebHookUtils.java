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

package com.harbortek.helm.scm.utils;

import com.harbortek.helm.scm.service.CodeRepositoryService;
import com.harbortek.helm.scm.service.impl.GitlabWebHookListener;
import com.harbortek.helm.scm.vo.CodeRepositoryVo;
import com.harbortek.helm.util.SpringContextUtil;
import org.gitlab4j.api.SystemHookManager;

import java.util.concurrent.ConcurrentHashMap;

public class WebHookUtils {
    static ThreadLocal<Long> repos = new ThreadLocal<Long>();

    private static final ConcurrentHashMap<Long, SystemHookManager> systemHookManager = new ConcurrentHashMap<>();

    public static  SystemHookManager getSystemHookManager(Long repositoryId) {
        CodeRepositoryService codeRepositoryService = SpringContextUtil.getBean(CodeRepositoryService.class);
        GitlabWebHookListener gitlabWebHookListener = SpringContextUtil.getBean(GitlabWebHookListener.class);;
        repos.set(repositoryId);
        return systemHookManager.computeIfAbsent(repositoryId, f->{
            CodeRepositoryVo codeRepository = codeRepositoryService.findCodeRepository(repositoryId);
            SystemHookManager webHookManager = new SystemHookManager();
            webHookManager.addListener(gitlabWebHookListener);
            webHookManager.setSecretToken(codeRepository.getWebHookToken() );

            return webHookManager;
        });
    }

    public static Long getCurrentRepository(){
        return repos.get();
    }


}
