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

package com.harbortek.helm.tracker.util;

import lombok.Cleanup;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ResourceUtils {
    public static Resource getResource(Resource parent, String relativePath) {
        Resource target = null;
        if (parent instanceof ClassPathResource) {
            String rootPath = ((ClassPathResource) parent).getPath();
            if (!rootPath.endsWith("/")) {
                rootPath = rootPath + "/";
            }
            target = new ClassPathResource(rootPath + relativePath);
        } else if (parent instanceof FileSystemResource) {
            String rootPath = ((FileSystemResource) parent).getPath();
            if (!rootPath.endsWith("/")) {
                rootPath = rootPath + "/";
            }
            target = new FileSystemResource(rootPath + relativePath);
        }
        return target;
    }

    public static String getPath(Resource resource) {
        if (resource instanceof ClassPathResource) {
            return "classpath:"+ ((ClassPathResource) resource).getPath();
        } else if (resource instanceof FileSystemResource) {
            return "file:"+((FileSystemResource) resource).getPath();
        }
        return null;
    }

    public static Resource getResource(String location){
        if (location.startsWith("classpath:")){
            return new ClassPathResource(location.substring(10));
        }else if (location.startsWith("file:")){
            String path = location.substring(5);
            return new FileSystemResource(path);
        }
        return null;
    }

    public static void writeXml(File file, Document document) {
        file.getParentFile().mkdirs();
        try {
            @Cleanup FileOutputStream os = new FileOutputStream(file);
            // 5、设置生成xml的格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");

            // 6、生成xml文件
            XMLWriter xmlWriter = new XMLWriter(os, format);
            // 设置是否转义，默认使用转义字符
            xmlWriter.setEscapeText(false);
            xmlWriter.write(document);
            xmlWriter.close();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
