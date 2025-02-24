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
import com.harbortek.helm.scm.service.GitlabService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping(value = "/oauth/")
public class CallbackApi {
    @Autowired
    CodeRepositoryService repositoryService;

    @Autowired
    GitlabService gitlabService;

    @Parameter(name="gitlabCallback")
    @RequestMapping(value = "{repositoryId}/gitlab/callback", method = RequestMethod.GET, produces= MediaType.TEXT_HTML_VALUE)
    void gitlabCallback(HttpServletResponse response, @PathVariable Long repositoryId,
                                            @RequestParam String code) throws IOException {

        gitlabService.createAccessTokenByCode(repositoryId, code);

        String content = "<html><script>window.close()</script></html>";
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println(content);
        writer.close();
    }
}
