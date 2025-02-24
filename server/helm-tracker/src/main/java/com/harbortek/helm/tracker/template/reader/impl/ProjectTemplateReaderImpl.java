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

package com.harbortek.helm.tracker.template.reader.impl;

import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.dao.UserDao;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.EnumCodes;
import com.harbortek.helm.tracker.constants.ObjectTypes;
import com.harbortek.helm.tracker.constants.PagePermissions;
import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.entity.block.TemplateBlockData;
import com.harbortek.helm.tracker.template.builder.EntityResolver;
import com.harbortek.helm.tracker.template.builder.PageXmlReader;
import com.harbortek.helm.tracker.template.builder.TrackerItemXmlReader;
import com.harbortek.helm.tracker.template.builder.TrackerXmlReader;
import com.harbortek.helm.tracker.template.reader.ProjectTemplateReader;
import com.harbortek.helm.tracker.util.ResourceUtils;
import com.harbortek.helm.tracker.vo.ProjectRoleMemberVo;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkTypeVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.permission.PagePermissionVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.tracker.vo.smartpage.SmartPageVo;
import com.harbortek.helm.tracker.vo.template.ProjectTemplateVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.Cleanup;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class ProjectTemplateReaderImpl implements ProjectTemplateReader, ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(ProjectTemplateReaderImpl.class);

    private ApplicationContext applicationContext;

    @Autowired
    EnumService enumService;

    @Autowired
    UserDao userDao;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public ProjectTemplateVo readTemplateBasicInfo(Resource parent) {
        Resource templateProperties = ResourceUtils.getResource(parent, "/template.properties");
        if (!templateProperties.exists()) {
            return null;
        }

        Properties props = new Properties();

        try {
            @Cleanup InputStream reader = templateProperties.getInputStream();
            props.load(reader);
            String name = props.getProperty("name");
            long maxItemNo= 0L;
            if(ObjectUtils.isNotEmpty(props.getProperty("maxItemNo"))){
                maxItemNo=Long.parseLong(props.getProperty("maxItemNo"));
            }
            String description = props.getProperty("description");
            String icon = props.getProperty("icon");
            String trackerPrefixToReplace = props.getProperty("trackerPrefixToReplace");
//            byte[] fileContent=null;
//            String encodedString=null;
//            if(ObjectUtils.isNotEmpty(icon)){
//                if(ResourceUtils.getResource(parent, "/" + icon).exists()){
//                    fileContent = FileUtils.readFileToByteArray(ResourceUtils.getResource(parent, "/" + icon).getFile());
//                }else{
//                    fileContent=icon.getBytes();
//                }
//                encodedString = Base64.getEncoder().encodeToString(fileContent);
//            }

            ProjectTemplateVo templateVo = new ProjectTemplateVo();
            templateVo.setName(name);
            templateVo.setMaxItemNo(maxItemNo);
            templateVo.setDescription(description);
            templateVo.setIcon(icon);
            templateVo.setTrackerPrefixToReplace(trackerPrefixToReplace);
            templateVo.setLocation(ResourceUtils.getPath(parent));

            return templateVo;

        } catch (IOException e) {
            logger.error("无法加载项目模版文件,{}", ResourceUtils.getPath(templateProperties));
            throw new RuntimeException(e);
        }
    }

    public ProjectTemplateVo readFully(Resource parent) {
        if (!parent.exists()){
            throw new ServiceException("无法读取项目模版");
        }
        ProjectTemplateVo vo = readTemplateBasicInfo(parent);
        EntityResolver entityResolver = new EntityResolver();

        initEntityResolver(entityResolver);

        //用户
        Page<UserEntity> users = userDao.findUsers(null, Pageable.unpaged(), null);
        users.forEach(user -> {
            entityResolver.register(DataUtils.toVo(user, UserVo.class), ObjectTypes.USER);
        });

        List<ProjectRoleMemberVo> roleMembers = readRoleMembers(parent,entityResolver);
        roleMembers.stream().forEach(r -> {
            entityResolver.register(r, ObjectTypes.PROJECT_ROLE_MEMBER);
        });

        List<PermissionGrantVo> permissionGrants = readPermissionGrants(parent,entityResolver);


        List<TrackerVo> trackers = readTrackers(parent,entityResolver);
        trackers.forEach(t -> {
            entityResolver.register(t,ObjectTypes.TRACKER);
            for (TrackerStatus trackerStatus : t.getTrackerStatuses()) {
                entityResolver.register(trackerStatus,ObjectTypes.TRACKER_STATUS);
            }
        });


        List<TrackerItemVo> trackerItems = readWorkItems(parent,entityResolver);

        List<ProjectPageVo> projectPages = readPages(parent,entityResolver);
        List<TrackerLinkVo> trackerLinks = readItemLinks(parent, entityResolver);
        List<DocVo> docVos = readDocs(parent, entityResolver);
        List<SmartPageVo> smartPageVos = readSmartPages(parent, entityResolver);
        List<EnumItemVo> enumItemVos = readEnums(parent, entityResolver);

        vo.setRoles(roleMembers);
        vo.setPermissionGrants(permissionGrants);
        vo.setTrackers(trackers);
        vo.setTrackerItems(trackerItems);
        vo.setPages(projectPages);
        vo.setTrackerLinks(trackerLinks);
        vo.setDocs(docVos);
        vo.setSmartPages(smartPageVos);
        vo.setEnumItems(enumItemVos);
        return vo;
    }

    private void initEntityResolver(EntityResolver entityResolver){
        Collection<EnumItemVo> meanings = enumService.findEnumItemsByCode(null,EnumCodes.TRACKER_STATUS_MEANING);
        meanings.forEach(meaning->{
            entityResolver.register(meaning,ObjectTypes.TRACKER_STATUS_MEANING);
        });

        Collection<EnumItemVo> trackerPriorities = enumService.findEnumItemsByCode(null, EnumCodes.TRACKER_PRIORITY);
        trackerPriorities.forEach(priority->{
            entityResolver.register(priority, ObjectTypes.TRACKER_PRIORITY);
        });

        Collection<EnumItemVo> trackerSeverities = enumService.findEnumItemsByCode(null, EnumCodes.TRACKER_SEVERITY);
        trackerPriorities.forEach(priority->{
            entityResolver.register(priority, ObjectTypes.TRACKER_SEVERITY);
        });

        Collection<EnumItemVo> trackerTypeVos = enumService.findEnumItemsByCode(null,EnumCodes.TRACKER_TYPE);
        trackerTypeVos.forEach(trackerTypeVo -> {
            entityResolver.register(trackerTypeVo, ObjectTypes.TRACKER_TYPE);
        });

    }

    private List<EnumItemVo> readEnums(Resource parent,EntityResolver entityResolver) {
        ArrayList<EnumItemVo> docVos = new ArrayList<>();
        Resource resource = ResourceUtils.getResource(parent, "/enum/enums.xml");
        SAXReader saxReader = new SAXReader();
        try {
            @Cleanup InputStream is = resource.getInputStream();
            Document document = saxReader.read(is);

            List<Node> nodes = document.selectNodes("enums/enum");
            for (Node node : nodes) {
                EnumItemVo enumItemVo = new EnumItemVo();
                enumItemVo.setCategoryName(node.valueOf("@category"));
                enumItemVo.setName(node.valueOf("@name"));
                enumItemVo.setCode(node.valueOf("@color"));
                enumItemVo.setBackgroundColor(node.valueOf("@backgroundColor"));
                enumItemVo.setOrdinary(Integer.parseInt(node.valueOf("@ordinary")));
                enumItemVo.setSystem(Boolean.parseBoolean(node.valueOf("@system")));

                docVos.add(enumItemVo);
            }
            return docVos;
        } catch (IOException | DocumentException ex) {
            logger.error("无法读取 {}", ResourceUtils.getPath(resource),ex);
        }
        return Collections.EMPTY_LIST;
    }

    private List<DocVo> readDocs(Resource parent,EntityResolver entityResolver) {
        ArrayList<DocVo> docVos = new ArrayList<>();
        String path = ResourceUtils.getPath(parent)+"/doc/*.xml";
        try {
            Resource[] resources = applicationContext.getResources(path);
            SAXReader saxReader = new SAXReader();
            for (Resource resource : resources) {
                @Cleanup InputStream is = resource.getInputStream();
                Document document = saxReader.read(is);
                List<Node> nodes = document.selectNodes("docs/doc");
                for (Node node : nodes) {
                    DocVo docVo = new DocVo();
                    docVo.setName(node.valueOf("@page"));
                    docVo.setVersion(Long.parseLong(node.valueOf("@version")));
                    List<Node> blockNodes = node.selectNodes("blocks/block");
                    List<DocBlock> docBlocks= new ArrayList<>();
                    blockNodes.forEach(blockNode -> {
                        TemplateBlockData templateBlockData;
                        if(ObjectUtils.isNotEmpty(blockNode.valueOf("@isTrackerItemLink"))){
                            templateBlockData=TemplateBlockData.builder().itemNo(blockNode.valueOf("@code"))
                                    .isTrackerItemLink(Boolean.parseBoolean(blockNode.valueOf("@isTrackerItemLink")))
                                    .build();
                        }else if(ObjectUtils.isNotEmpty(blockNode.valueOf("@level"))){
                            templateBlockData = TemplateBlockData.builder().level(Long.valueOf(blockNode.valueOf("@level")))
                                                                 .itemNo(blockNode.valueOf("@code")).build();
                        }else{
                            templateBlockData = TemplateBlockData.builder().itemNo(blockNode.valueOf("@code")).build();
                        }
                        docBlocks.add(new DocBlock(null,blockNode.valueOf("@type"), templateBlockData));
                    });
                    docVo.setBlocks(docBlocks);
                    docVos.add(docVo);
                }
            }

        } catch (IOException | DocumentException ex) {
            logger.error("无法读取 {}",ex);
        }
        return docVos;
    }
    private List<SmartPageVo> readSmartPages(Resource parent,EntityResolver entityResolver) {
        ArrayList<SmartPageVo> smartPageVos = new ArrayList<>();
        String path = ResourceUtils.getPath(parent)+"/smart-page/*.xml";
        try {
            Resource[] resources = applicationContext.getResources(path);
            SAXReader saxReader = new SAXReader();
            for (Resource resource : resources) {
                @Cleanup InputStream is = resource.getInputStream();
                Document document = saxReader.read(is);

                Node node = document.selectSingleNode("smart-page");
                SmartPageVo smartPageVo = new SmartPageVo();
                smartPageVo.setName(node.valueOf("@page"));
                smartPageVo.setScope(node.valueOf("@scope"));
                Node definition = node.selectSingleNode("definition");
                smartPageVo.setDefinition(definition.getStringValue());
                smartPageVos.add(smartPageVo);
            }
        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }
        return smartPageVos;
    }

    private List<ProjectPageVo> readPages(Resource parent,EntityResolver entityResolver) {
        Resource resource = ResourceUtils.getResource(parent, "/pages/pages.xml");
        return new PageXmlReader(entityResolver).build(parent,resource);
    }

    private List<TrackerItemVo> readWorkItems(Resource parent,EntityResolver entityResolver) {
        Resource[] resources = new Resource[0];
        ArrayList<TrackerItemVo> trackerItems = new ArrayList<>();
        try {
            resources = applicationContext.getResources(ResourceUtils.getPath(parent) + "/work-items/*");
            for (Resource resource : resources) {
                TrackerItemVo item = new TrackerItemXmlReader(entityResolver).build(resource);
                entityResolver.register(item,ObjectTypes.TRACKER_ITEM);
                trackerItems.add(item);
            }
        } catch (IOException ex) {
            logger.error("无法读取 {}", ResourceUtils.getPath(parent)+ "/work-items/*",ex);
        }
        return trackerItems;
    }

    private List<TrackerLinkVo> readItemLinks(Resource parent,EntityResolver entityResolver) {
        ArrayList<TrackerLinkVo> trackerLinks = new ArrayList<>();
        Resource resource = ResourceUtils.getResource(parent, "/item-links/item-links.xml");
        SAXReader saxReader = new SAXReader();
        try {
            @Cleanup InputStream is = resource.getInputStream();
            Document document = saxReader.read(is);

            List<Node> nodes = document.selectNodes("item-links/link");
            for (Node node : nodes) {
                TrackerLinkVo linkVo = new TrackerLinkVo();
                linkVo.setSourceItem(TrackerItemVo.builder().itemNo(node.valueOf("@sourceItem")).build());
                linkVo.setTargetItem(TrackerItemVo.builder().itemNo(node.valueOf("@targetItem")).build());
                linkVo.setLinkType(TrackerLinkTypeVo.builder().code(node.valueOf("@linkType")).build());
                trackerLinks.add(linkVo);
            }

            return trackerLinks;
        } catch (IOException | DocumentException ex) {
            logger.error("无法读取 {}", ResourceUtils.getPath(resource),ex);
        }
        return Collections.EMPTY_LIST;
    }

    private List<TrackerVo> readTrackers(Resource parent,EntityResolver entityResolver) {
        ArrayList<TrackerVo> trackers = new ArrayList<>();
        try {
            String path = ResourceUtils.getPath(parent) + "/trackers/*.xml";
            Resource[] resources = applicationContext.getResources(path);
            for (Resource resource : resources) {
                TrackerVo tracker = new TrackerXmlReader(entityResolver).build(resource);
                if (tracker!=null) {
                    trackers.add(tracker);
                }else{
                    logger.error("无法读取 {}", ResourceUtils.getPath(resource));
                }
            }
        } catch (IOException ex) {
            logger.error("无法读取 {}", ResourceUtils.getPath(parent)+ "/trackers/*.xml",ex);
        }
        return trackers;
    }

    private List<PermissionGrantVo> readPermissionGrants(Resource parent,EntityResolver entityResolver) {
        Resource resource = ResourceUtils.getResource(parent, "/roles/permissions.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resource.getInputStream());

            List<Node> nodes = document.selectNodes("permissions/permission");
            ArrayList<PermissionGrantVo> grantVos = new ArrayList<>();
            for (Node node : nodes) {
                PermissionGrantVo grantVo = new PermissionGrantVo();
                String permissionName = node.valueOf("@name");
                grantVo.setPermissionName(permissionName);
                grantVos.add(grantVo);

                List<Node> roleNodes = node.selectNodes("grant-to-role");
                for (Node child : roleNodes) {
                    String roleName = child.getText();
                    ProjectRoleMemberVo role =
                            (ProjectRoleMemberVo) entityResolver.findByName(roleName,
                                                                            ObjectTypes.PROJECT_ROLE_MEMBER,
                                                                            ProjectRoleMemberVo.class);
                    BaseIdentity identity = new BaseIdentity(new IdNameReference(role));
                    identity.setType(IdentityTypes.ROLE);
                    grantVo.getGranted().add(identity);
                }
                List<Node> specialRoleNodes = node.selectNodes("grant-to-special-role");
                for (Node child : specialRoleNodes) {
                    String roleName = child.getText();
                    ProjectRoleMemberVo role =
                            entityResolver.findByName(roleName, ObjectTypes.PROJECT_ROLE_MEMBER,ProjectRoleMemberVo.class);
                    BaseIdentity identity = new BaseIdentity(new IdNameReference(role));
                    identity.setType(IdentityTypes.SPECIAL_ROLE);
                    grantVo.getGranted().add(identity);
                }
                List<Node> userNodes = node.selectNodes("grant-to-user");
                for (Node child : userNodes) {
                    String userName = child.getText();
                    UserVo userVo = entityResolver.findByName(userName, ObjectTypes.USER, UserVo.class);
                    BaseIdentity identity = new BaseIdentity(new IdNameReference(userVo));
                    identity.setType(IdentityTypes.USER);
                    grantVo.getGranted().add(identity);
                }
            }

            return grantVos;
        } catch (IOException | DocumentException ex) {
            logger.error("无法读取 {}", ResourceUtils.getPath(resource),ex);
        }
        return Collections.EMPTY_LIST;
    }

    private List<ProjectRoleMemberVo> readRoleMembers(Resource parent,EntityResolver entityResolver) {

        Resource resource = ResourceUtils.getResource(parent, "/roles/roles.xml");
        SAXReader saxReader = new SAXReader();
        try {
            @Cleanup InputStream is = resource.getInputStream();
            Document document = saxReader.read(is);

            List<Node> nodes = document.selectNodes("roles/role");
            ArrayList<ProjectRoleMemberVo> roles = new ArrayList<>();
            for (Node node : nodes) {
                ProjectRoleMemberVo role = new ProjectRoleMemberVo();
                role.setName(node.valueOf("@name"));
                role.setSpecialRole(Boolean.valueOf(node.valueOf("@specialRole")));
                role.setScope(SpecialRole.SCOPE_PROJECT);
                //role.setAutoIncludeProjectOwner(Boolean.valueOf(node.valueOf("@autoIncludeProjectOwner")));
                //角色
                List<UserVo> memberVos=new ArrayList<>();
                List<Node> members = node.selectNodes("members/member");
                for (Node member : members) {
                    UserVo user = entityResolver.findByName(member.valueOf("@name"), ObjectTypes.USER, UserVo.class);
                    memberVos.add(user);
                }
                role.setMembers(memberVos);
                //权限
                List<PagePermissionVo> permissions = new ArrayList<>();
                List<Node> permissionNodes = node.selectNodes("permissions/permission");
                for (Node permissionNode : permissionNodes) {
                    String permissionName = permissionNode.valueOf("@name");
                    PagePermissionVo permissionVo = PagePermissionVo.builder().name(permissionName)
                            .permission(new ArrayList<>()).build();
                    PagePermissions.ALL_PAGE_PERMISSIONS.forEach(perm -> {
                        String permValue = permissionNode.valueOf("@"+ perm);
                        if(ObjectUtils.isNotEmpty(permValue)&&permValue.equals("true")){
                            permissionVo.getPermission().add(perm);
                        }
                    });
                    permissions.add(permissionVo);
                }
                role.setPermissions(permissions);

                roles.add(role);
            }

            roles.addAll(buildSystemSpecialRoles());

            return roles;
        } catch (IOException | DocumentException ex) {
            logger.error("无法读取 {}", ResourceUtils.getPath(resource),ex);
        }
        return Collections.EMPTY_LIST;
    }

    private List<ProjectRoleMemberVo> buildSystemSpecialRoles() {
        List<ProjectRoleMemberVo> specialRoles = Arrays.asList(
                ProjectRoleMemberVo.builder().name("项目负责人").specialRoleType(SpecialRole.PROJECT_OWNER)
                                   .scope(SpecialRole.SCOPE_PROJECT).build(),
                ProjectRoleMemberVo.builder().name("所有项目成员").specialRoleType(SpecialRole.PROJECT_ALL_MEMBERS)
                                   .scope(SpecialRole.SCOPE_PROJECT)
                                   .build(),
                ProjectRoleMemberVo.builder().name("所有人").specialRoleType(SpecialRole.ALL_USERS)
                                   .scope(SpecialRole.SCOPE_PROJECT).build(),
                ProjectRoleMemberVo.builder().name("工作项负责人").specialRoleType(SpecialRole.TRACKER_OWNER)
                                   .scope(SpecialRole.SCOPE_TRACKER).build(),
                ProjectRoleMemberVo.builder().name("工作项创建者").specialRoleType(SpecialRole.TRACKER_CREATOR)
                                   .scope(SpecialRole.SCOPE_TRACKER).build(),
                ProjectRoleMemberVo.builder().name("工作项关注者").specialRoleType(SpecialRole.TRACKER_WATCHER)
                                   .scope(SpecialRole.SCOPE_TRACKER).build());
        specialRoles.forEach(f -> {
            f.setSpecialRole(true);
        });
        return specialRoles;
    }


}
