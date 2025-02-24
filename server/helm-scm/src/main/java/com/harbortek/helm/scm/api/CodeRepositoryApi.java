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

package com.harbortek.helm.scm.api;

import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.scm.service.CodeRepositoryService;
import com.harbortek.helm.scm.vo.CodeRepositoryVo;
import com.harbortek.helm.scm.vo.ProjectCodeRepositoryVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/scm/repository")
public class CodeRepositoryApi {

    @Autowired
    CodeRepositoryService repositoryService;

    @Parameter(name="查询代码仓列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<List<CodeRepositoryVo>> findCodeRepositories() {
        List<CodeRepositoryVo> result =repositoryService.findCodeRepositories();
        return ResponseEntity.ok(result);
    }


    @Parameter(name="初始化代码仓")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    ResponseEntity<CodeRepositoryVo> createCodeRepository(@RequestBody CodeRepositoryVo codeRepositoryVo) {
        codeRepositoryVo.setDeleted(true);
        CodeRepositoryVo result =repositoryService.createCodeRepository(codeRepositoryVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="Enable代码仓")
    @RequestMapping(value = "{repositoryId}/enable", method = RequestMethod.POST)
    ResponseEntity<Void> enableCodeRepository(@PathVariable Long repositoryId) {
        CodeRepositoryVo repository = repositoryService.findCodeRepository(repositoryId);
        repository.setDeleted(false);
        repositoryService.updateCodeRepository(repository);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新代码仓")
    @RequestMapping(value = "{repositoryId}", method = RequestMethod.PUT)
    ResponseEntity<Void> updateCodeRepository(@PathVariable Long repositoryId,
                                              @RequestBody CodeRepositoryVo codeRepositoryVo) {
        codeRepositoryVo.setId(repositoryId);
        repositoryService.updateCodeRepository(codeRepositoryVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除代码仓")
    @RequestMapping(value = "{repositoryId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteCodeRepository(@PathVariable Long repositoryId) {
        repositoryService.deleteCodeRepository(repositoryId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询代码仓")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    ResponseEntity<CodeRepositoryVo> findCodeRepository(@PathVariable Long repositoryId) {
        CodeRepositoryVo repository = repositoryService.findCodeRepository(repositoryId);
        return ResponseEntity.ok(repository);
    }

    @Parameter(name="查询代码仓项目列表信息")
    @RequestMapping(value = "{repositoryId}/projects", method = RequestMethod.GET)
    ResponseEntity<List<BaseVo>> findProjects(@PathVariable Long repositoryId) {

        return ResponseEntity.ok(repositoryService.executeFindProjects(repositoryId));
    }


    @Parameter(name="查询代码仓是否已被项目使用")
    @RequestMapping(value = "{repositoryId}/usedInProject", method = RequestMethod.GET)
    ResponseEntity<Boolean> checkRepositoryUsedInProject(@PathVariable Long repositoryId) {
        return ResponseEntity.ok(repositoryService.checkRepositoryUsedInProject(repositoryId));
    }


    @Parameter(name="绑定项目代码仓")
    @RequestMapping(value = "{repositoryId}/bind", method = RequestMethod.POST)
    ResponseEntity<Void> bindProject(@PathVariable Long repositoryId,
                                     @RequestBody ProjectCodeRepositoryVo projectCodeRepositoryVo) {
        repositoryService.bindProject(repositoryId,projectCodeRepositoryVo);
        return ResponseEntity.ok().build();
    }
    @Parameter(name="解除绑定项目代码仓")
    @RequestMapping(value = "{repositoryId}/unbind", method = RequestMethod.POST)
    ResponseEntity<Void> unbindProject(@PathVariable Long repositoryId,@RequestParam Long projectId) {
        repositoryService.unbindProject(repositoryId,projectId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询当前项目绑定的代码仓")
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    ResponseEntity<ProjectCodeRepositoryVo> findCurrentBindProject(@RequestParam Long projectId) {
        ProjectCodeRepositoryVo result = repositoryService.findCurrentBindProject(projectId);
        return ResponseEntity.ok(result);
    }


    @Parameter(name="重置WebHook")
    @RequestMapping(value = "/{repositoryId}/resetWebHook", method = RequestMethod.POST)
    ResponseEntity<Void> resetWebHook(@PathVariable Long repositoryId) {
        repositoryService.resetWebHook(repositoryId);
        return ResponseEntity.ok().build();
    }
}
