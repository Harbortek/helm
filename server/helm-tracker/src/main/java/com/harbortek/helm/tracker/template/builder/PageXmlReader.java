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

package com.harbortek.helm.tracker.template.builder;

import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.tracker.constants.ObjectTypes;
import com.harbortek.helm.tracker.util.ResourceUtils;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import lombok.Cleanup;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PageXmlReader {
    private final Logger logger = LoggerFactory.getLogger(PageXmlReader.class);
    EntityResolver entityResolver;

    public PageXmlReader(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public List<ProjectPageVo> build(Resource parent,Resource resource){
        SAXReader saxReader = new SAXReader();
        try {
            @Cleanup InputStream is = resource.getInputStream();
            Document document = saxReader.read(is);
            Node root = document.selectSingleNode("/pages");
            ArrayList<ProjectPageVo> pages = new ArrayList<>();
            return readFromXml(parent,root,pages);
        } catch (IOException | DocumentException e) {
            logger.error("无法加载项目模版文件,{}", resource.getFilename());
        }
        return null;
    }

    private List<ProjectPageVo> readFromXml(Resource parent,Node parentNode,List<ProjectPageVo> pageHolder) throws DocumentException, IOException {
        List<Node> children = parentNode.selectNodes("*");
        for (Node node : children) {
            ProjectPageVo page = new ProjectPageVo();
            page.setName(node.valueOf("@name"));
            page.setIcon(node.valueOf("@icon"));
            String tagName = node.getName();
            if ("page".equals(tagName)){
                page.setFolder(false);
                String type = node.valueOf("@type");
                String componentType = node.valueOf("@componentType");
                if(!node.valueOf("@definition").isEmpty()){
                    String definition = readDefinitionXml(parent,node.valueOf("@definition"));
                    page.setDefinition(definition);
                }
                page.setType(type);
                page.setComponentType(componentType);
                if(!node.valueOf("@tracker").isEmpty()){
                    TrackerVo trackerVo = entityResolver.findByName(node.valueOf("@tracker"), ObjectTypes.TRACKER, TrackerVo.class);
                    page.setTracker(new IdNameReference<>(trackerVo));
                }
            }else if ("folder".equals(tagName)){
                page.setFolder(true);
                readFromXml(parent,node,page.getChildren());
            }
            pageHolder.add(page);
        }

        return pageHolder;
    }


    private String readDefinitionXml(Resource parent,String path) throws IOException, DocumentException {
        SAXReader saxReader = new SAXReader();
        Resource resource = ResourceUtils.getResource(parent,path);
        @Cleanup InputStream is = resource.getInputStream();
        Document document = saxReader.read(is);
        Node root = document.selectSingleNode("definition");
        String stringValue = root.getStringValue();
        return stringValue;
    }
}
