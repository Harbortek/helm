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

import com.harbortek.helm.scm.service.CodeRepositoryService;
import com.harbortek.helm.scm.vo.*;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/scm/code/{projectId}")
@Slf4j
public class ProjectCodeRepositoryApi {
    @Autowired
    CodeRepositoryService repositoryService;


    @Parameter(name="查询代码仓当前项目信息")
    @RequestMapping(value = "/project", method = RequestMethod.GET)
    ResponseEntity<ScmProjectVo> findProject(@PathVariable Long projectId) {
        ScmProjectVo project = repositoryService.executeFindProject(projectId);
        return ResponseEntity.ok(project);
    }

    @Parameter(name="查询代码仓分支列表")
    @RequestMapping(value = "/branches", method = RequestMethod.GET)
    ResponseEntity<List<BranchVo>> findBranches(@PathVariable Long projectId,
                                                @RequestParam(required = false)String keyword) {
        List<BranchVo> branches = repositoryService.executeFindBranches(projectId, keyword);
        return ResponseEntity.ok(branches);
    }

    @Parameter(name="查询代码仓TAG列表")
    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    ResponseEntity<List<TagVo>> findFiles(@PathVariable Long projectId,@RequestParam(required = false)String keyword) {

        List<TagVo> tags = repositoryService.executeFindTags(projectId, keyword);

        return ResponseEntity.ok(tags);
    }

    @Parameter(name="查询代码仓文件列表")
    @RequestMapping(value = "/files", method = RequestMethod.GET)
    ResponseEntity<List<FileVo>> findFiles(@PathVariable Long projectId, @RequestParam String path,
                                           @RequestParam String branchName,@RequestParam(required = false) String parentId) {

        List<FileVo> files = repositoryService.executeFindFiles(projectId, path, branchName, parentId);

        return ResponseEntity.ok(files);
    }

    @Parameter(name="查询代码仓文件内容")
    @RequestMapping(value = "/fileContent", method = RequestMethod.GET)
    ResponseEntity<FileVo> findFileContent(@PathVariable Long projectId, @RequestParam String path,
                                           @RequestParam String branchName) {

        FileVo rawContent = repositoryService.executeFindFileContent(projectId, path, branchName);

        return ResponseEntity.ok(rawContent);
    }

    @Parameter(name="下载代码仓文件内容")
    @RequestMapping(value = "/fileDownload", method = RequestMethod.GET)
    ResponseEntity downloadFile(HttpServletResponse response, @PathVariable Long projectId, @RequestParam String path,
                                @RequestParam String branchName) {

        try {
            Collection<MimeType> mimetypes = new ExtensionMimeDetector().getMimeTypesFileName(path);
            if (!mimetypes.isEmpty()) {
                eu.medsea.mimeutil.MimeType mimeType = mimetypes.iterator().next();
                response.setHeader(HttpHeaders.CONTENT_TYPE, mimeType.getMediaType() + "/" + mimeType.getSubType());
            }

            String contentDisposition;
            String fileName = FilenameUtils.getName(path);
            contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''"
                    + URLEncoder.encode(fileName);
            response.setHeader("Content-disposition", contentDisposition);

            InputStream inputStream = repositoryService.executeDownloadFile(projectId, path, branchName);
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception ex) {
            log.error("error", ex);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @Parameter(name="查询代码仓最新提交")
    @RequestMapping(value = "/lastCommit", method = RequestMethod.GET)
    ResponseEntity<CommitVo> findLastCommit(@PathVariable Long projectId, @RequestParam String path,
                                           @RequestParam String branchName) {

        CommitVo commit = repositoryService.executeFindLastCommit(projectId, path, branchName);

        return ResponseEntity.ok(commit);
    }
}
