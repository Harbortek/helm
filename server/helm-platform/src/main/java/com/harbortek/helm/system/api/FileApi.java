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

package com.harbortek.helm.system.api;

import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.util.DateUtils;
import com.harbortek.helm.util.IDUtils;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

@Controller
@Slf4j
public class FileApi {
    static Logger logger = LoggerFactory.getLogger(FileApi.class);

    @Autowired
    FileService fileService;

    @Parameter(name = "增加文件")

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam(value = "uploadFile", required = true) MultipartFile uploadFile) {
        String destPath = DateUtils.getCurrDate();
        try {
            String url = fileService.upload(uploadFile, destPath);
            String fileName = uploadFile.getOriginalFilename();

            Map<String, String> result = new HashMap<>();
            result.put("id", IDUtils.getId() + "");
            result.put("origin_name", fileName);
            result.put("url", url);
            result.put("fileSize", uploadFile.getSize() + "");
            result.put("status", "done");

            return ResponseEntity.ok(result);

        }catch (Exception e){
            log.error("文件上传失败", e);
            throw new ServiceException("文件上传失败");
        }
    }

    @Parameter(name = "增加文件")

    @RequestMapping(value = "/uploadFile2", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> uploadFile2(MultipartFile file, MultipartFile image) {
        if (file == null) {
            file = image;
        }
        try {
            String destPath = DateUtils.getCurrDate();

            String url = fileService.upload(file, destPath);
            String fileName = file.getOriginalFilename();

            Map<String, Object> result = new HashMap<>();
            Map<String, String> fileMap = new HashMap<>();

            fileMap.put("url", "/api/file?path=" + url);
            fileMap.put("alt", fileName);
            result.put("data", fileMap);

            result.put("errno", 0);
            return ResponseEntity.ok(result);
        } catch (Throwable e) {
            log.error("文件上传失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("errno", 1);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    ResponseEntity<Void> downloadFile(HttpServletResponse response,
                                      @RequestParam("path") String path) {
        try {
            Collection<eu.medsea.mimeutil.MimeType> mimetypes = new ExtensionMimeDetector().getMimeTypesFileName(path);
            if (!mimetypes.isEmpty()) {
                eu.medsea.mimeutil.MimeType mimeType = mimetypes.iterator().next();
                response.setHeader(HttpHeaders.CONTENT_TYPE, mimeType.getMediaType() + "/" + mimeType.getSubType());
            }

            String contentDisposition;
            String fileName = FilenameUtils.getName(path);
            contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''"
                    + URLEncoder.encode(fileName);
            response.setHeader("Content-disposition", contentDisposition);

            InputStream inputStream = fileService.download(path);
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Throwable ex) {
            logger.error("error", ex);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Parameter(name = "获取文件")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    ResponseEntity<Void> downloadFileWithName(HttpServletResponse response, @RequestParam("name") String name,
                                        @RequestParam("url") String url) {
        try {
            Collection<eu.medsea.mimeutil.MimeType> mimetypes = new ExtensionMimeDetector().getMimeTypesFileName(url);
            if (!mimetypes.isEmpty()) {
                eu.medsea.mimeutil.MimeType mimeType = mimetypes.iterator().next();
                response.setHeader(HttpHeaders.CONTENT_TYPE, mimeType.getMediaType() + "/" + mimeType.getSubType());
            }

            String contentDisposition;
            String fileName = name;
            contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''"
                    + URLEncoder.encode(fileName);
            response.setHeader("Content-disposition", contentDisposition);

            InputStream inputStream = fileService.download(url);
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Throwable ex) {
            logger.error("error", ex);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
