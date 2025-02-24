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

import com.harbortek.helm.system.service.LogService;
import com.harbortek.helm.system.vo.LogVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "日志管理")
@RequestMapping(value = "/sys/log")
public class LogApi {
    @Autowired
    LogService logService;

    @Parameter(name="查询日志列表接口")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<Page<LogVo>> listParams(String keyword, Pageable pageable) {
        Page<LogVo> logs = logService.findLogs(keyword, pageable);
        return ResponseEntity.ok(logs);
    }

    @Parameter(name="查询日志信息接口")
    @RequestMapping(value = "/{logId}", method = RequestMethod.GET)
    public ResponseEntity<LogVo> getLog(@PathVariable Long logId) {
        LogVo p = logService.findOneLog(logId);
        return ResponseEntity.ok(p);
    }

}
