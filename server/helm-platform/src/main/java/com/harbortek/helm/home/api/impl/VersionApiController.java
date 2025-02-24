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

package com.harbortek.helm.home.api.impl;

import com.harbortek.helm.home.api.VersionApi;
import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
public class VersionApiController implements VersionApi {
    @Override
    public ResponseEntity<String> version() {
        File file = new File("/image_built_at");
        String version = "";
        if (file.exists()) {
            try {
                version = "-"+FileUtils.readFileToString(file);
            } catch (IOException e) {

            }
        }
        return  ResponseEntity.ok("V2.1" + version);
    }
}
