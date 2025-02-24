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

package com.harbortek.helm.tracker.service.impl;

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.lang.id.NanoId;
import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.pipeline.dao.PipelineDao;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.dao.EnumCategoryDao;
import com.harbortek.helm.system.dao.EnumDao;
import com.harbortek.helm.system.entity.EnumCategoryEntity;
import com.harbortek.helm.system.entity.EnumItemEntity;
import com.harbortek.helm.system.service.*;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.tracker.constants.*;
import com.harbortek.helm.tracker.dao.*;
import com.harbortek.helm.tracker.entity.block.*;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.entity.view.ViewEntity;
import com.harbortek.helm.tracker.service.*;
import com.harbortek.helm.tracker.template.reader.ProjectTemplateReader;
import com.harbortek.helm.tracker.template.writer.ProjectTemplateWriter;
import com.harbortek.helm.tracker.util.TrackerUtils;
import com.harbortek.helm.tracker.vo.ProjectCopyVo;
import com.harbortek.helm.tracker.vo.ProjectRoleMemberVo;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkTypeVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.tracker.vo.smartpage.SmartPageVo;
import com.harbortek.helm.tracker.vo.template.ProjectTemplateVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.MultiOptionsField;
import com.harbortek.helm.tracker.vo.tracker.fields.OptionsField;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.tracker.vo.view.*;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("projectCopyService")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProjectCreateServiceImpl implements ProjectCreateService {

    final ProjectDao projectDao;
    final ProjectTemplateService projectTemplateService;
    final TrackerDao trackerDao;
    final RoleService roleService;
    final RoleMemberService roleMemberService;
    final ProjectRoleMemberService projectRoleMemberService;
    final UserService userService;
    final PermissionService permissionService;
    final ProjectPermissionService projectPermissionService;
    final TrackerService trackerService;
    final ViewDao viewDao;
    final ProjectPageService projectPageService;
    final ProjectPageDao projectPageDao;
    final TrackerItemDao trackerItemDao;
    final EnumService enumService;
    final TrackerLinkTypeService trackerLinkTypeService;
    final ProjectTemplateReader projectTemplateReader;
    final ProjectTemplateWriter projectTemplateWriter;

    private final SprintDao sprintDao;
    private final AttachmentDao attachmentDao;
    private final TrackerLinkDao trackerLinkDao;
    private final WorkHoursDao workHoursDao;
    private final HyperlinkDao hyperlinkDao;
    private final CommentDao commentDao;
    private final ChangeLogDao changeLogDao;
    private final PlanDao planDao;
    private final TargetVersionDao targetVersionDao;
    private final DeliverableDao deliverableDao;
    private final BaselineDao baselineDao;
    private final PipelineDao pipelineDao;
    private final ReviewDao reviewDao;
    private final DocDao docDao;
    private final TrackerItemHistoryDao trackerItemHistoryDao;
    private final DocumentHistoryDao documentHistoryDao;
    private final EnumCategoryDao enumCategoryDao;
    private final EnumDao enumDao;
    private final CollectionDao collectionDao;
    private final SmartPageService smartPageService;

    @Override
    public ProjectVo createProject(ProjectVo vo) {
        if (StringUtils.isEmpty(vo.getTemplateName())) {
            throw new ServiceException("未指定项目模版");
        }
        Long projectId = IDUtils.getId();
        ProjectEntity projectEntity = ProjectEntity.builder()
                .id(projectId)
                .name(vo.getName())
                .icon(vo.getIcon())
                .description(vo.getDescription())
                .keyName(vo.getKeyName())
                .categoryId(vo.getCategory().getId())
                .templateName(vo.getTemplateName())
                .ownerId(SecurityUtils.getCurrentUser().getId())
                .statusId(enumService.findOneEnumItemByCode(null,
                        EnumCodes.PROJECT_STATUS_MEANING,
                        ProjectStatusMeaning.NOT_STARTED).getId())
                .meaning(ProjectStatusMeaning.NOT_STARTED)
                .build();


        //1. 从项目模版复制数据
        ProjectTemplateVo templateVo = projectTemplateService.findByName(projectEntity.getTemplateName());
        if (projectEntity.getIcon()==null){
            projectEntity.setIcon(templateVo.getIcon());
        }
        if(ObjectUtils.isNotEmpty(templateVo.getMaxItemNo())){
            projectEntity.setMaxItemNo(templateVo.getMaxItemNo());
        }

        createProjectContent(templateVo,projectEntity);

        //2.1 创建项目
        projectDao.createProject(projectEntity);

        return vo;
    }

    private void createProjectContent(ProjectTemplateVo templateVo,ProjectEntity project){
        //2.2 保存项目角色
        List<ProjectRoleMemberVo> roleMemberVos = templateVo.getRoles();
        roleMemberVos.forEach(r->r.setId(Optional.ofNullable(r.getId()).orElse(IDUtils.getId())));
        List<RoleVo> roleVos = roleMemberVos.stream().map(r ->
                RoleVo.builder().id(r.getId()).name(r.getName()).scope(r.getScope()).ownerResourceId(project.getId())
                        .specialRole(r.getSpecialRole()).specialRoleType(r.getSpecialRoleType())
                        .build()).collect(Collectors.toList());
        roleService.createRoles(roleVos);
        projectRoleMemberService.createProjectRoleMembers(project.getId(),roleMemberVos);

        //2.3 保存项目权限
        List<PermissionVo> projectPermissions = new ArrayList<>();
        List<PermissionGrantVo> permissionGrantVos = templateVo.getPermissionGrants();
        permissionGrantVos.forEach(g -> {
            List<BaseIdentity> identityList = g.getGranted();
            identityList.forEach(identity -> {
                PermissionVo grant =
                        PermissionVo.builder().id(IDUtils.getId()).resourceId(project.getId())
                                .name(g.getPermissionName()).identity(identity)
                                .build();
                projectPermissions.add(grant);
            });
        });
        if(ObjectUtils.isNotEmpty(projectPermissions)){
            permissionService.grant(projectPermissions);
        }


        //2.4 保存项目枚举值
        List<EnumItemVo> enumItemVoList = templateVo.getEnumItems();
        List<EnumCategoryEntity> enumCategories = new ArrayList<>();
        List<EnumItemEntity> enumItems = new ArrayList<>();
        HashMap<String,Long> categoryMap=new HashMap<>();
        enumItemVoList.forEach(enumVo -> {
            if(!categoryMap.containsKey(enumVo.getCategoryName())){
                Long id = IDUtils.getId();
                categoryMap.put(enumVo.getCategoryName(),id);
                enumCategories.add(EnumCategoryEntity.builder().id(id)
                        .name(enumVo.getCategoryName()).projectId(project.getId()).build());
            }
            enumItems.add(EnumItemEntity.builder().id(IDUtils.getId())
                    .name(enumVo.getName())
                    .code(enumVo.getCode())
                    .description(enumVo.getDescription())
                    .categoryId(categoryMap.get(enumVo.getCategoryName()))
                    .projectId(project.getId())
                    .build());
        });
        if(ObjectUtils.isNotEmpty(enumCategories)){
            enumCategoryDao.batchCreateEnumCategories(enumCategories);
        }
        if(ObjectUtils.isNotEmpty(enumItems)){
            enumDao.batchCreateEnums(enumItems);
        }
        enumCategoryDao.findEnumCategories(null,null,null).getContent().forEach(category -> {
            categoryMap.put(category.getName(),category.getId());
        });

        //3.1 创建tracker
        Collection<TrackerVo> trackers = templateVo.getTrackers();
        List<ViewEntity> views = new ArrayList<>();
        List<PermissionVo> permissions=new ArrayList<>();
        trackers.forEach(tracker -> {
            tracker.setProjectId(project.getId());
            tracker.getTrackerFields().forEach(trackerField -> {
                if(!trackerField.getSystem()&&trackerField instanceof OptionsField){
                    ((OptionsField) trackerField).setEnumId(categoryMap.get(((OptionsField) trackerField).getEnumName()));
                }else if(!trackerField.getSystem()&&trackerField instanceof MultiOptionsField){
                    ((MultiOptionsField) trackerField).setEnumId(categoryMap.get(((MultiOptionsField) trackerField).getEnumName()));
                }
            });
            tracker.setId(Optional.ofNullable(tracker.getId()).orElse(IDUtils.getId()));
            tracker.getTrackerPermissions().forEach(permission -> {
                permission.getGranted().forEach(identity -> {
                    permissions.add(
                            PermissionVo.builder()
                                    .id(IDUtils.getId())
                                    .resourceId(tracker.getId())
                                    .identity(identity)
                                    .name(permission.getPermissionName())
                                    .build()
                    );
                });
            });
            //3.2 保存tracker视图
            ViewEntity viewAll = ViewEntity.builder().id(IDUtils.getId()).name("全部 " + tracker.getName())
                    .objectId(tracker.getId()).viewType("PUBLIC").display(true).system(true)
                    .viewConfig(buildViewConfig(tracker,null)).build();
            views.add(viewAll);
            tracker.getTrackerStatuses().forEach(s -> {
                ViewEntity view = ViewEntity.builder().id(IDUtils.getId()).name(s.getName())
                        .objectId(tracker.getId()).viewType("PUBLIC").display(true).system(true)
                        .viewConfig(buildViewConfig(tracker,s)).build();
                views.add(view);
            });
        });
        trackerService.createTrackers(trackers);
        viewDao.createViews(views);
        permissionService.grant(permissions);

        HashMap<String,Long> fieldMap=new HashMap<>();
        trackers.forEach(tracker -> {
            tracker.getTrackerFields().forEach(trackerField -> {
                fieldMap.put(trackerField.getName(),trackerField.getId());
            });
        });


        //4.1 保存trackerItem
        List<TrackerItemEntity> trackerItemEntities=new ArrayList<>();
        if(ObjectUtils.isNotEmpty(templateVo.getTrackerItems())){
            templateVo.getTrackerItems().forEach(trackerItemVo -> {
                if(ObjectUtils.isNotEmpty(trackerItemVo)){
                    TrackerItemEntity trackerItemEntity = DataUtils.toEntity(trackerItemVo, TrackerItemEntity.class);
                    Map<Long,String> values = new HashMap<>();
                    trackerItemEntity.getValues().forEach((k,v)->{
                        String[] split = v.split("_");
                        if(split.length>1&&ObjectUtils.isNotEmpty(fieldMap.get(split[0]))){
                            values.put(fieldMap.get(split[0]),split[1]);
                        }

                    });
                    trackerItemEntity.setValues(values);
                    trackerItemEntity.setId(Optional.ofNullable(trackerItemEntity.getId()).orElse(IDUtils.getId()));
                    trackerItemEntity.setProjectId(project.getId());
                    trackerItemEntity.setCreateDate(null);
                    trackerItemEntities.add(trackerItemEntity);
                }
            });
        }

        //4.2 初始化链接关系
        List<TrackerLinkTypeVo> linkTypeVos = TrackerUtils.buildDefaultTrackerLinkTypes();
        linkTypeVos.forEach(linkType->linkType.setProjectId(project.getId()));
        trackerLinkTypeService.batchCreateLinkTypes(linkTypeVos);

        //4.3 工作项关联
        List<TrackerLinkEntity> trackerLinkList = new ArrayList<>();
        List<TrackerLinkVo> trackerLinks = templateVo.getTrackerLinks();
        List<String> itemNos = trackerLinks.stream().flatMap(link -> Stream.of(link.getSourceItem().getItemNo(), link.getTargetItem().getItemNo())).collect(Collectors.toList());
        List<TrackerItemEntity> linkItemList = trackerItemEntities.stream().filter(item -> itemNos.contains(item.getItemNo())).collect(Collectors.toList());
        trackerLinks.forEach(link -> {
            Optional<TrackerItemEntity> sourceItem = linkItemList.stream().filter(item -> item.getItemNo().equals(link.getSourceItem().getItemNo())).findFirst();
            Optional<TrackerItemEntity> targetItem = linkItemList.stream().filter(item -> item.getItemNo().equals(link.getTargetItem().getItemNo())).findFirst();
            Optional<TrackerLinkTypeVo> linkTypeVo = linkTypeVos.stream().filter(linkType -> linkType.getCode().equals(link.getLinkType().getCode())).findFirst();
            if(sourceItem.isPresent()&&targetItem.isPresent()&&linkTypeVo.isPresent()){
                trackerLinkList.add(TrackerLinkEntity.builder().id(IDUtils.getId())
                        .sourceItemId(sourceItem.get().getId()).targetItemId(targetItem.get().getId())
                        .linkTypeId(linkTypeVo.get().getId()).build());
            }
        });
        if(ObjectUtils.isNotEmpty(trackerLinkList)){
            trackerLinkDao.batchCreateTrackerLinks(trackerLinkList);
        }

        //5.1 文档数据
        templateVo.getDocs().forEach(docVo -> {
            docVo.setId(Optional.ofNullable(docVo.getId()).orElse(IDUtils.getId()));
            docVo.getBlocks().forEach(block -> {
                block.setId(NanoId.randomNanoId());
                TemplateBlockData templateBlockData = (TemplateBlockData) block.getData();
                trackerItemEntities.stream().filter(item -> item.getItemNo().equals(templateBlockData.getItemNo()))
                        .findFirst().ifPresent(item -> {
                            block.getData().setRefId(item.getId());
                        });
                if(!BlockTypes.ALL_BLOCK_TYPES.contains(block.getType())){
                    trackers.stream().filter(tracker -> tracker.getName().equals(block.getType()))
                            .findFirst().ifPresent(tracker -> {
                                block.setType(tracker.getId().toString());
                            });
                    block.setData(TrackerItemBlockData.builder().refId(templateBlockData.getRefId())
                            .isTrackerItemLink(templateBlockData.getIsTrackerItemLink()).build());
                }else if(BlockTypes.TITLE.equals(block.getType())){
                    block.setData(TitleBlockData.builder().refId(templateBlockData.getRefId()).build());
                }else if(BlockTypes.HEADING.equals(block.getType())){
                    block.setData(HeaderBlockData.builder().refId(templateBlockData.getRefId())
                            .level(templateBlockData.getLevel()).build());
                }else if(BlockTypes.PARAGRAPH.equals(block.getType())){
                    block.setData(ParagraphBlockData.builder().refId(templateBlockData.getRefId()).build());
                }
            });
            docVo.setBlocks(docVo.getBlocks().stream()
                    .filter(block -> ObjectUtils.isNotEmpty(block.getData().getRefId())).collect(Collectors.toList()));
        });
        Collection<DocEntity> docEntities = DataUtils.toEntity(templateVo.getDocs(), DocEntity.class);
        docDao.batchSave(docEntities);

        //重排item_no
        if(ObjectUtils.isNotEmpty(trackerItemEntities)){
            Long maxItemNo = 1L;
            trackerItemEntities.sort(Comparator.comparing(TrackerItemEntity::getItemNo));
            for(TrackerItemEntity item:trackerItemEntities){
                item.setItemNo(String.valueOf(maxItemNo++));
            }
            project.setMaxItemNo(maxItemNo-1);
            trackerItemDao.createTrackerItems(trackerItemEntities);
        }
        //5.2 smart_page
        List<SmartPageVo> smartPages = templateVo.getSmartPages();
        smartPages.forEach(smartPageVo->{
            smartPageVo.setId(Optional.ofNullable(smartPageVo.getId()).orElse(IDUtils.getId()));
        });
        smartPageService.createSmartPages(smartPages);

        //6.1 项目页面数据
        List<ProjectPageVo> pages = templateVo.getPages();
        List<ProjectPageVo> pageVos = buildProjectPages(project.getId(),null, pages, 1);
        pageVos.forEach(page->{
            if(ProjectPageTypes.SMART_DOCUMENT.equals(page.getType())){
                docEntities.stream().filter(doc->doc.getName().equals(page.getName()))
                        .findFirst().ifPresent(docVo -> {
                            page.setSmartDocId(docVo.getId());
                        });
            }else if(ProjectPageTypes.SMART_PAGE.equals(page.getType())){
                smartPages.stream().filter(smartPage->smartPage.getName().equals(page.getName()))
                        .findFirst().ifPresent(smartPageVo -> {
                            page.setSmartPageId(smartPageVo.getId());
                        });
            }
        });
        projectPageService.batchCreateProjectPages(pageVos);

        //6.2 项目页面权限
        List<PermissionVo> pagePermissions = new ArrayList<>();
        roleMemberVos.forEach(r -> {
            if(r.getSpecialRole().equals(Boolean.FALSE)) {
                pageVos.forEach(page -> {
                    r.getPermissions().stream().filter(p -> p.getName().equals(page.getName())).findFirst()
                            .ifPresent(permission -> {
                                BaseIdentity baseIdentity = new BaseIdentity(new IdNameReference<>(r));
                                baseIdentity.setType(IdentityTypes.ROLE);
                                permission.getPermission().forEach(perm->{
                                    pagePermissions.add(
                                            PermissionVo.builder()
                                                    .id(IDUtils.getId())
                                                    .resourceId(page.getId())
                                                    .identity(baseIdentity)
                                                    .name(perm)
                                                    .build()
                                    );
                                });

                            });
                });
            }
        });
        permissionService.grant(pagePermissions);
    }

    //构建tracker view数据
    private TrackerItemViewConfig buildViewConfig(TrackerVo tracker, TrackerStatus s) {
        TrackerField trackerCalcField = findField(tracker.getTrackerFields(), SystemFields.CREATE_DATE);
        IdNameReference reference = new IdNameReference<>(trackerCalcField);
        TrackerOrderBy orderBy = TrackerOrderBy.builder()
                .field(reference)
                .orderBy("DESC")
                .build();
        ObjectFilter trackerItemsFilter=new ObjectFilter();
        if(s!=null){
            ConditionGroup conditionGroup = new ConditionGroup();
            conditionGroup.getConditions().add(FilterCondition.builder().field(SystemFields.STATUS).type(FieldTypes.STATUS).operator("INCL")
                    .value(Arrays.asList(s.getId())).build());
            trackerItemsFilter.getConditionGroups().add(conditionGroup);
        }

        return TrackerItemViewConfig.builder().filter(trackerItemsFilter)
                .orderBys(Arrays.asList(orderBy))
                .build();
    }

    private TrackerField findField(List<TrackerField> calcFields, String propertyName) {
        return calcFields.stream()
                .filter(f -> f.isSystem() && (CompareUtil.compare(f.getSystemProperty(), propertyName) == 0))
                .findFirst().get();
    }

    //树形结构projectPage平铺
    private List<ProjectPageVo> buildProjectPages(Long projectId,Long parentId, List<ProjectPageVo> pages,
                                                  int level) {
        List<ProjectPageVo> pageEntities = new ArrayList<>();
        int order = 1;
        for (ProjectPageVo pageVo : pages) {
            ProjectPageVo pageEntity = new ProjectPageVo();
            pageVo.setId(IDUtils.getId());
            pageEntity.setId(pageVo.getId());
            pageEntity.setParentId(parentId);
            pageEntity.setName(pageVo.getName());
            pageEntity.setIcon(pageVo.getIcon());
            pageEntity.setProjectId(projectId);
            pageEntity.setType(pageVo.getType());
            pageEntity.setComponentType(pageVo.getComponentType());
            pageEntity.setDefinition(pageVo.getDefinition());
            pageEntity.setFolder(pageVo.getFolder());
            pageEntity.setLevel(level);
            pageEntity.setOrder(order++);
            if(ObjectUtils.isNotEmpty(pageVo.getTracker())&&ObjectUtils.isNotEmpty(pageVo.getTracker().getReferTo())){
                pageEntity.setTracker(pageVo.getTracker());
            }

            if (!pageVo.getChildren().isEmpty()) {
                List<ProjectPageVo> children = buildProjectPages(projectId, pageEntity.getId(),
                        pageVo.getChildren(),
                        level + 1);
                pageEntities.addAll(children);
            }
            pageEntities.add(pageEntity);
        }
        return pageEntities;
    }

    public ProjectTemplateVo replaceTemplateId(ProjectTemplateVo templateVo){
        Map<Long,Long> roleMap =new HashMap<>();
        templateVo.getRoles().forEach(r->{r.setId(IDUtils.getId());});

        Map<Long,Long> linkTypeMap=new HashMap<>();

        Map<Long ,Long> trackerMap = new HashMap<>();

        return null;
    }

    @Override
    public ProjectVo copyProject(ProjectCopyVo sourceProject) {

        ProjectEntity targetProject= new ProjectEntity();
        ProjectEntity oneProject = projectDao.findOneProject(sourceProject.getId());
        targetProject.setOwnerId(oneProject.getOwnerId());
        targetProject.setStatusId(oneProject.getStatusId());
        targetProject.setCategoryId(oneProject.getCategoryId());

        targetProject.setId(IDUtils.getId());
        targetProject.setName(sourceProject.getName());
        if(ObjectUtils.isNotEmpty(sourceProject.getKeyName())){//存在重复
            targetProject.setKeyName(sourceProject.getKeyName());
        }
        if(ObjectUtils.isNotEmpty(sourceProject.getDescription())){
            targetProject.setDescription(sourceProject.getDescription());
        }
        File file=null;
        try{
            ProjectTemplateVo templateVo = projectTemplateService.findDataByProjectId(sourceProject.getId());
            //创建临时模板-复制项目
            templateVo.setName(targetProject.getName());
            file = projectTemplateWriter.writeFully(templateVo);

            Resource resource = new FileSystemResource(file);
            ProjectTemplateVo template = projectTemplateReader.readFully(resource);

            createProjectContent(template,targetProject);

            //1. create project
            projectDao.createProject(targetProject);
        }finally {
            if(file != null){
                deleteTemplateFile(file);
            }
        }

        return DataUtils.toVo(targetProject, ProjectVo.class);
    }

    private void deleteTemplateFile(File file){
        if (file.isFile()){
            file.delete();
        }else if (file.isDirectory()){
            File[] files = file.listFiles();
            for (int i = 0; files!=null && i < files.length; i++) {
                if (files[i].isFile()){
                    files[i].delete();
                }else if(files[i].isDirectory()) {
                    deleteTemplateFile(files[i]);
                }
            }

        }
        file.delete();
    }

//    private void oldCopyProject(ProjectCopyVo sourceProject){
//        ProjectEntity targetProject= new ProjectEntity();
//        //2. 从项目中复制角色数据
//        Map<Long,Long> roleMap =new HashMap<>();
////        List<ProjectRoleMemberVo> roleMemberVos =
////                projectRoleMemberService.findProjectRoleMembers(sourceProject.getId(),true);
////        //3. 保存项目角色
////        roleMemberVos.forEach(r->{r.setId(IDUtils.getId());});
////        projectRoleMemberService.createProjectRoleMembers(targetProject.getId(),roleMemberVos);
//
//
//        //3.3 复制项目权限
////        List<PermissionVo> projectPermissions = permissionService.findPermissions(null,null,null,
////                List.of(sourceProject.getId()));
////        projectPermissions.forEach(p->{
////            p.setId(IDUtils.getId());
////            p.setResourceId(targetProject.getId());
////        });
////        permissionService.grant(projectPermissions);
//
//
//        //4.复制关联类型
//        Map<Long,Long> linkTypeMap=new HashMap<>();
//        List<TrackerLinkTypeVo> linkTypes = trackerLinkTypeService.findLinkTypes(sourceProject.getId());
//        linkTypes.forEach(linkTypeVo->{
//            linkTypeVo.setProjectId(targetProject.getId());
//            Long oldId = linkTypeVo.getId();
//            linkTypeVo.setId(IDUtils.getId());
//            linkTypeMap.put(oldId,linkTypeVo.getId());
//        });
//        trackerLinkTypeService.batchCreateLinkTypes(linkTypes);
//
//        //4.2 枚举值设置
////        Map<Long,Long> enumCategoryMap = new HashMap<>();
////        List<EnumCategoryEntity> enumCategoryEntities = enumCategoryDao.findEnumCategoriesByProjectId(sourceProject.getId());
////        enumCategoryEntities.forEach(enumCategory -> {
////            enumCategory.setProjectId(targetProject.getId());
////            Long oldId = enumCategory.getId();
////            enumCategory.setId(IDUtils.getId());
////            enumCategoryMap.put(oldId,enumCategory.getId());
////        });
////        if(ObjectUtils.isNotEmpty(enumCategoryEntities)){
////            enumCategoryDao.batchCreateEnumCategories(enumCategoryEntities);
////        }
////        List<EnumItemEntity> enumItemEntities = enumDao.findEnumsByProjectId(sourceProject.getId());
////        enumItemEntities.forEach(enumItem -> {
////            enumItem.setProjectId(targetProject.getId());
////            enumItem.setCategoryId(enumCategoryMap.get(enumItem.getCategoryId()));
////            enumItem.setId(IDUtils.getId());
////        });
////        if(ObjectUtils.isNotEmpty(enumItemEntities)){
////            enumDao.batchCreateEnums(enumItemEntities);
////        }
//
//        //5. create trackers
//        List<TrackerVo> trackerList = DataUtils.toVo(trackerDao.findByProject(sourceProject.getId()),TrackerVo.class);
//        List<TrackerVo> newTrackerList = new ArrayList<>();
//        List<ViewEntity> newViewList = new ArrayList<>();
//
//        Map<Long ,Long> trackerMap = new HashMap<>();
//        trackerList.forEach(oneTracker -> {
//            oneTracker.setProjectId(targetProject.getId());
//            Long oldItemId= oneTracker.getId();
//            oneTracker.setId(IDUtils.getId());
//            trackerMap.put(oldItemId,oneTracker.getId());
//
//            //更新Tracker中roleId 和工作项属性中数据
////            this.changeCopyTracker(oneTracker,roleMap,enumCategoryMap);
//            newTrackerList.add(oneTracker);
//
//            List<ViewEntity> viewEntities = viewDao.findByObjectId(oldItemId,true);
//            viewEntities.forEach(view -> {
//                view.setObjectId(oneTracker.getId());
//                view.setId(IDUtils.getId());
//            });
//            if (!viewEntities.isEmpty()) {
//                newViewList.addAll(viewEntities);
//            }
//        });
//        if(!newTrackerList.isEmpty()){
//            trackerService.createTrackers(newTrackerList);
//        }
//        if(!newViewList.isEmpty()){
//            viewDao.createViews(newViewList);
//        }
//
//        //6.复制页面pages
////        List<ProjectPageEntity> pageEntities = projectPageDao.findPagesByProject(sourceProject.getId());
//        Map<Long,Long> pageMap= new HashMap<>();
////        pageEntities.forEach(pageVo -> {
////            if("wiki".equals(pageVo.getType())){
////                pageVo.getWatchers().forEach(user ->{
////                    user.setId(roleMap.get(user.getId()));
////                });
////            }
////            pageVo.setTrackerId(trackerMap.get(pageVo.getTrackerId()));
////            pageVo.setProjectId(targetProject.getId());
////            Long id=IDUtils.getId();
////            pageMap.put(pageVo.getId(),id);
////            pageVo.setId(id);
////            pageVo.getPageSettingTrackers().forEach(tracker ->{
////                tracker.setId(trackerMap.get(tracker.getId()));
////            });
////        });
//        //6.2更新页面parentId
////        pageEntities.forEach(pageVo -> {
////            if(ObjectUtils.isNotEmpty(pageVo.getParentId())
////                    &&ObjectUtils.isNotEmpty(pageMap.get(pageVo.getParentId()))){
////                pageVo.setParentId(pageMap.get(pageVo.getParentId()));
////            }
////        });
//        //7.1 项目组件-版本
//        Map<Long,Long> targetVersionMap = new HashMap<>();
//        List<TargetVersionEntity> targetVersions = targetVersionDao.findAll(sourceProject.getId());
//        targetVersions.forEach(targetVersion->{
//            targetVersion.setProjectId(targetProject.getId());
//            Long oldId = targetVersion.getId();
//            targetVersion.setId(IDUtils.getId());
//            targetVersionMap.put(oldId,targetVersion.getId());
//        });
//        if(ObjectUtils.isNotEmpty(targetVersions)) {
//            targetVersionDao.batchCreateTargetVersions(targetVersions);
//        }
//        //7.2 项目组件-迭代
//        Map<Long,Long> sprintMap = new HashMap<>();
//        Collection<SprintEntity> sprints = sprintDao.findSprints(sourceProject.getId());
//        sprints.forEach(sprint -> {
//            sprint.setProjectId(targetProject.getId());
//            Long  oldSprintId= sprint.getId();
//            sprint.setId(IDUtils.getId());
//            sprint.setTargetVersionId(targetVersionMap.get(sprint.getTargetVersionId()));
//            sprintMap.put(oldSprintId,sprint.getId());
//        });
//        if(ObjectUtils.isNotEmpty(sprints)){
//            sprintDao.batchCreateSprints(sprints);
//        }
//        //7.3 复制trackerItems
//        List<TrackerItemEntity> itemEntityList = trackerItemDao.findItemByTrackerIds(sourceProject.getId(),sourceProject.getTrackerList());
//
//        List<Long> internalTrackerIds = Arrays.asList(InternalTrackers.HEADING.getId(), InternalTrackers.PARAGRAPH.getId(),
//                InternalTrackers.TITLE.getId());
//        itemEntityList.addAll(trackerItemDao.findItemByTrackerIds(sourceProject.getId(),internalTrackerIds));
//        Map<Long,Long> itemMap =new HashMap<>();
//        itemEntityList.forEach(item->{
//            item.setProjectId(targetProject.getId());
//            Long newId = IDUtils.getId();
//            itemMap.put(item.getId(),newId);
//            item.setId(newId);
//            if(!internalTrackerIds.contains(item.getTrackerId())){
//                item.setTrackerId(trackerMap.get(item.getTrackerId()));
//            }
//            item.setSprintId(sprintMap.get(item.getSprintId()));
//            item.setRelatedWikis(
//                    item.getRelatedWikis().stream().map(pageMap::get).collect(Collectors.toList())
//            );
//        });
//        if(ObjectUtils.isNotEmpty(itemEntityList)) {
//            trackerItemDao.createTrackerItems(itemEntityList);
//        }
//        //7.4 复制TrackerItemsHistory
//        List<TrackerItemHistoryEntity> itemHistoryList = trackerItemHistoryDao.findByProjectId(sourceProject.getId());
//        itemHistoryList.forEach(item->{
//            item.setProjectId(targetProject.getId());
//            Long oldId = item.getObjectId();
//            Long newId = itemMap.get(oldId);
//            if(ObjectUtils.isEmpty(newId)){
//                newId=IDUtils.getId();
//            }
//            item.setObjectId(newId);
//            if(item.getHistoryId().contains(String.valueOf(oldId))){
//                item.setHistoryId(item.getHistoryId().replace(String.valueOf(oldId),String.valueOf(newId)));
//            }
//            item.setTrackerId(trackerMap.get(item.getTrackerId()));
//            item.setSprintId(sprintMap.get(item.getSprintId()));
//            item.setRelatedWikis(
//                    item.getRelatedWikis().stream().map(pageMap::get).collect(Collectors.toList())
//            );
//        });
//        if(ObjectUtils.isNotEmpty(itemHistoryList)) {
//            trackerItemHistoryDao.createTrackerItemHistories(itemHistoryList);
//        }
//        //7.4 复制DocumentsHistory
//        List<DocumentHistoryEntity> documentHistoryList= documentHistoryDao.findByProjectId(sourceProject.getId());
//        documentHistoryList.forEach(document->{
//            document.setProjectId(targetProject.getId());
////            document.setPageId(pageMap.get(document.getPageId()));
//            String historyId = document.getHistoryId();
//            String[] split = historyId.split("-");
//            if(split.length>1){
//                Long oldId = Long.parseLong(split[0]);
//                Long newId = pageMap.get(oldId);
//                if(ObjectUtils.isEmpty(newId)){
//                    newId=IDUtils.getId();
//                }
//                document.setObjectId(newId);
//                document.setHistoryId(document.getHistoryId().replace(split[0],String.valueOf(newId)));
//            }
//            document.getBlocks().forEach(docBlock -> {
//                docBlock.getData().setRefId(itemMap.get(docBlock.getData().getRefId()));
//                if(!BlockTypes.ALL_BLOCK_TYPES.contains(docBlock.getType())){
//                    docBlock.setType(String.valueOf(trackerMap.get(Long.parseLong(docBlock.getType()))));
//                }
//            });
//        });
//        if(ObjectUtils.isNotEmpty(documentHistoryList)) {
//            documentHistoryDao.createDocumentHistories(documentHistoryList);
//        }
//        //7.5 保存页面数据
////        pageEntities.forEach(page->{
////            if (ObjectUtils.isNotEmpty(page.getBlockTrackerItemRefs())){
////                String[] split = page.getBlockTrackerItemRefs().split(",");
////                List<String> newList = new ArrayList<>();
////                for (String s : split) {
////                    String[] split1 = s.split(":");
////                    if(split1.length>1){
////                        newList.add(split1[0]+":"+itemMap.get(Long.valueOf(split1[1])));
////                    }
////                }
////                page.setBlockTrackerItemRefs(StringUtils.join(newList,","));
////            }
////        });
////        if(ObjectUtils.isNotEmpty(pageEntities)) {
////            projectPageDao.createProjectPages(pageEntities);
////        }
//        //7.6 保存项目成员--页面权限
////        List<PermissionVo> pagePermissionList =
////                permissionService.findPermissions(PagePermissions.ALL_PAGE_PERMISSIONS,null,null,ObjectUtils.ids(pageEntities));
////        pagePermissionList.forEach(perm->{
////            perm.setId(IDUtils.getId());
////            perm.setResourceId(pageMap.get(perm.getResourceId())); //新的PAGE ID
////            BaseIdentity identity = new BaseIdentity();
////            identity.setId(roleMap.get(perm.getIdentity().getId()));
////            perm.setIdentity(identity);
////        });
////        permissionService.grant(pagePermissionList);
//        //8.1项目组件--测试计划
//        Page<TestRunEntity> testRuns = testRunDao.findTestRuns(sourceProject.getId(), null, null, null, null);
//        List<TestRunEntity> testRunList = testRuns.getContent();
//        Map<Long,Long> testRunMap =new HashMap<>();
//        testRunList.forEach(testRun ->{
//            testRun.setProjectId(targetProject.getId());
//            testRun.setSprintId(sprintMap.get(testRun.getSprintId()));
//            Long oldId=testRun.getId();
//            testRun.setId(IDUtils.getId());
//            testRunMap.put(oldId,testRun.getId());
//        });
//        if(ObjectUtils.isNotEmpty(testRunList)){
//            testRunDao.batchCreateTestRuns(testRunList);
//        }
//
//        //8.2 trackerItem 下组件
//        this.copyTrackerItemComponents(itemMap,linkTypeMap,testRunMap);
//
//        //9.复制项目各组件数据
//        this.copyProjectComponents(sourceProject,targetProject,testRunMap,trackerMap,itemMap,pageMap,sprintMap);
//    }


    /**
     * 复制项目时，复制trackerItem下各组件数据
     *
     * @param itemMap
     * @param linkTypeMap
     * @param testRunMap
     */
//    private void copyTrackerItemComponents(Map<Long, Long> itemMap, Map<Long, Long> linkTypeMap, Map<Long, Long> testRunMap) {
//        //1 文件
//        List<Long> oldItemIds = new ArrayList<>(itemMap.keySet());
//        List<AttachmentEntity> attachments = attachmentDao.findByObjectIds(oldItemIds);
//        attachments.forEach(attachment -> {
//            attachment.setId(IDUtils.getId());
//            attachment.setObjectId(itemMap.get(attachment.getObjectId()));
//        });
//        if(ObjectUtils.isNotEmpty(attachments)){
//            attachmentDao.batchCreateAttachment(attachments);
//        }
//        attachments=null;
//        //2.关联工作项
//        List<TrackerLinkEntity> linkEntityList = trackerLinkDao.findByItemIds(oldItemIds);
//        linkEntityList.forEach(link ->{
//            link.setId(IDUtils.getId());
//            link.setSourceItemId(itemMap.get(link.getSourceItemId()));
//            link.setTargetItemId(itemMap.get(link.getTargetItemId()));
//            link.setLinkTypeId(linkTypeMap.get(link.getLinkTypeId()));
//        });
//        if(ObjectUtils.isNotEmpty(linkEntityList)){
//            trackerLinkDao.batchCreateTrackerLinks(linkEntityList);
//        }
//        linkEntityList=null;
//        //3.工时
//        List<WorkHoursEntity> workHoursList = workHoursDao.findByObjectIds(oldItemIds);
//        workHoursList.forEach(workHours -> {
//            workHours.setId(IDUtils.getId());
//            workHours.setObjectId(itemMap.get(workHours.getObjectId()));
//        });
//        if(ObjectUtils.isNotEmpty(workHoursList)){
//            workHoursDao.batchCreateWorkHours(workHoursList);
//        }
//        workHoursList=null;
//        //4.链接
//        List<HyperlinkEntity> hyperlinkList = hyperlinkDao.findByObjectIds(oldItemIds);
//        hyperlinkList.forEach(hyperlink -> {
//            hyperlink.setId(IDUtils.getId());
//            hyperlink.setObjectId(itemMap.get(hyperlink.getObjectId()));
//        });
//        if(ObjectUtils.isNotEmpty(hyperlinkList)) {
//            hyperlinkDao.batchCreateHyperlink(hyperlinkList);
//        }
//        hyperlinkList=null;
//        //5.代码关联
//        //6.关联测试用例 --计划
//        List<TrackerTestResultLinkEntity> linkResults = trackerLinkResultsDao.findByTrackerItemIds(oldItemIds);
//        linkResults.forEach(linkResult ->{
//            linkResult.setId(IDUtils.getId());
//            linkResult.setTrackerItemId(itemMap.get(linkResult.getTrackerItemId()));
//            linkResult.setTestResultId(itemMap.get(linkResult.getTestResultId()));
//        });
//        if(ObjectUtils.isNotEmpty(linkResults)) {
//            trackerLinkResultsDao.batchCreateTrackerTestResultLinks(linkResults);
//        }
//        linkResults=null;
//        //7.评论
//        List<CommentEntity> commentList = commentDao.findByObjectIds(oldItemIds);
//        commentList.forEach(comment ->{
//            comment.setId(IDUtils.getId());
//            comment.setObjectId(itemMap.get(comment.getObjectId()));
//        });
//        if(ObjectUtils.isNotEmpty(commentList)) {
//            commentDao.batchCreateComment(commentList);
//        }
//        commentList=null;
//        //8.日志
//        List<ChangeLogEntity> changeLogList = changeLogDao.findByObjectIds(oldItemIds);
//        changeLogList.forEach(changeLog ->{
//            changeLog.setId(IDUtils.getId());
//            changeLog.setObjectId(itemMap.get(changeLog.getObjectId()));
//        });
//        if(ObjectUtils.isNotEmpty(changeLogList)) {
//            changeLogDao.batchCreateChangeLog(changeLogList);
//        }
//        changeLogList=null;
//
//    }


    /**
     * 复制项目各组件数据
     * @param projectCopyVo
     * @param projectEntity
     * @param testRunMap
     * @param trackerMap
     * @param itemMap
     * @param pageMap
     * @param sprintMap
     */
//    private void copyProjectComponents(ProjectCopyVo projectCopyVo, ProjectEntity projectEntity, Map<Long, Long> testRunMap, Map<Long, Long> trackerMap, Map<Long, Long> itemMap, Map<Long, Long> pageMap, Map<Long, Long> sprintMap){
//        //未知 T_PlanDependencies  T_TrackerMergeRequests 计划与发布统计
//        AtomicReference<String> oldId= new AtomicReference<>();
//        //2. 交付物
//        Map<String,Long> deliverableMap = new HashMap<>();
//        List<DeliverableEntity> deliverables = deliverableDao.findDeliverables(projectCopyVo.getId());
//        deliverables.forEach(deliverable->{
//            deliverable.setProjectId(projectEntity.getId());
//            oldId.set(deliverable.getId().toString());
//            deliverable.setId(IDUtils.getId());
//            deliverableMap.put(oldId.get(),deliverable.getId());
//        });
//        if(ObjectUtils.isNotEmpty(deliverables)) {
//            deliverableDao.batchCreateDeliverables(deliverables);
//        }
//
//        //3. 项目计划 里程碑 计划执行
//        Map<String,Long> planMap = new HashMap<>();
//        List<PlanEntity> plans = planDao.findPlans(projectCopyVo.getId());
//        plans.forEach(plan ->{
//            plan.setProjectId(projectEntity.getId());
//            oldId.set(plan.getId().toString());
//            plan.setId(IDUtils.getId());
//            planMap.put(oldId.get().toString(),plan.getId());
//        });
//        plans.forEach(plan->{
//            plan.setParentId(planMap.get(plan.getParentId()));
//            plan.setDeliverables(
//                    plan.getDeliverables().stream().map(deliverableMap::get).collect(Collectors.toList())
//            );
//            plan.setSprints(
//                    plan.getSprints().stream().map(sprintMap::get).collect(Collectors.toList())
//            );
//            plan.setItems(
//                    plan.getItems().stream().map(itemMap::get).collect(Collectors.toList())
//            );
//        });
//        if(ObjectUtils.isNotEmpty(plans)) {
//            planDao.batchCreatePlans(plans);
//        }
//
//        //4. 流水线
//        List<PipelineEntity> pipelines = pipelineDao.findBindingPipelines(projectCopyVo.getId());
//        pipelines.forEach(pipeline->{
//            pipeline.setProjectId(projectEntity.getId());
//            pipeline.setId(IDUtils.getId());
//        });
//        if(ObjectUtils.isNotEmpty(pipelines)){
//            pipelineDao.createPipelines(projectEntity.getId(),pipelines);
//        }
//
//        //5. 测试报告
//        List<TestReportEntity> testReports =testReportDao
//                .findTestReports(projectCopyVo.getId(),null,null).getContent();
//        testReports.forEach(testReport ->{
//            testReport.setProjectId(projectEntity.getId());
//            testReport.setId(IDUtils.getId());
//            testReport.setTestRunIds(
//                    testReport.getTestRunIds().stream().map(testRunMap::get).collect(Collectors.toList())
//            );
//        });
//        if(ObjectUtils.isNotEmpty(testReports)) {
//            testReportDao.batchCreateTestReports(testReports);
//        }
//
//        //6. 文档
//        Map<String,String> blockMap = new HashMap<>();
//        List<ProjectPageEntity> sourceProjectPages = projectPageDao.findByIds(new ArrayList<>(pageMap.keySet()),
//                                                              ProjectPageEntity.class);
//        List<Long> docIds = sourceProjectPages.stream().map(ProjectPageEntity::getSmartDocId).toList();
//        List<DocEntity> docEntityList = docDao.findByIds(new ArrayList<>(docIds));
//        docEntityList.forEach(doc -> {
//            doc.setId(IDUtils.getId());
////            doc.setPageId(pageMap.get(doc.getPageId()));
//            doc.getBlocks().forEach(block ->{
//                if(!BlockTypes.ALL_BLOCK_TYPES.contains(block.getType())){
//                    block.setType(String.valueOf(trackerMap.get(Long.parseLong(block.getType()))));
//                }
//                block.getData().setRefId((itemMap.get(block.getData().getRefId())));
//                oldId.set(block.getId());
//                block.setId(NanoId.randomNanoId());
//                blockMap.put(String.valueOf(oldId.get()),block.getId());
//            });
//        });
////        docEntityList.forEach(doc -> {
////            block.setParentBlockId(blockMap.get(block.getParentBlockId()));
////        });
//        if(ObjectUtils.isNotEmpty(docEntityList)){
//            docDao.batchSave(docEntityList);
//        }
//
//
//        //7. 集合
//        Map<String,Long> collectionMap = new HashMap<>();
//        Page<CollectionEntity> collectionsPage = collectionDao.findCollectionsByProjectId(projectCopyVo.getId(), null, Pageable.unpaged());
//        List<CollectionEntity> collections = collectionsPage.getContent();
//        collections.forEach(collection ->{
//            oldId.set(collection.getId().toString());
//            collection.setId(IDUtils.getId());
//            collectionMap.put(oldId.get(),collection.getId());
//            collection.setProjectId(projectEntity.getId());
//            List<Long> documents = new ArrayList<>();
//            collection.getDocuments().forEach(documentId ->{
//                documents.add(pageMap.get(documentId));
//            });
//            collection.setDocuments(documents);
//        });
//        if(ObjectUtils.isNotEmpty(collections)) {
//            collectionDao.batchCreateCollections(collections);
//        }
//
//
//        //8. 基线
//        List<BaselineEntity> baselines = baselineDao.findBaselinesByProjectId(projectCopyVo.getId(), null);
//        baselines.forEach(baseline->{
//            baseline.setProjectId(projectEntity.getId());
//            baseline.setId(IDUtils.getId());
//            Map<Long,List<String>> newWorkItems=new HashMap<>();
//            baseline.getWorkItemIds().forEach((key, value) -> {//更新trackerId和itemId
//                List<String> newWorkItemIds = new ArrayList<>();
//                value.forEach(workItemId -> {
//                    String[] split = workItemId.split("-");
//                    if (split.length > 1) {
//                        newWorkItemIds.add(itemMap.get(Long.parseLong(split[0])) + "-" + split[1]);
//                    }
//                });
//                newWorkItems.put(trackerMap.get(key), newWorkItemIds);
//            });
//            baseline.setWorkItemIds(newWorkItems);
//
//            List<String> documentIds = new ArrayList<>();
//            baseline.getDocumentIds().forEach(document -> {
//                String[] split = document.split("-");
//                if(split.length>1){
//                    documentIds.add(pageMap.get(Long.parseLong(split[0]))+"-"+split[1]);
//                }
//            });
//            baseline.setDocumentIds(documentIds);
//            if(BaselineTypes.TYPE_COLLECTION.equals(baseline.getType())){
//                baseline.setCollectionId(collectionMap.get(baseline.getCollectionId().toString()));
//            }
//
//        });
//        if(ObjectUtils.isNotEmpty(baselines)) {
//            baselineDao.batchCreateBaselines(baselines);
//        }
//        //9. 组件视图--我的事项等
//        List<ViewEntity> pageViewList=viewDao.findByObjectIds(new ArrayList<>(pageMap.keySet()));
//        pageViewList.forEach(view ->{
//            view.setObjectId(pageMap.get(view.getObjectId()));
//            view.setId(IDUtils.getId());
//        });
//        if(ObjectUtils.isNotEmpty(pageViewList)){
//            viewDao.createViews(pageViewList);
//        }
//    }

    /**
     * 复制项目中，修改tracker中角色
     * @param oneTracker
     * @param roleMap
     */
    private void changeCopyTracker(TrackerVo oneTracker,Map<Long,Long> roleMap,Map<Long,Long> enumCategoryMap){
        //更新工作项权限中的角色id
        oneTracker.getTrackerPermissions().forEach(perm -> {
            perm.getGranted().forEach(role ->{
                role.setId(roleMap.get(role.getId()));
            });
        });
        //更新工作项属性权限中的角色id
        oneTracker.getTrackerFields().forEach(field -> {
            if (field.getPermission() != null) {
                //SinglePermissions
                if (field.getPermission().getSinglePermissions() != null) {
                    field.getPermission().getSinglePermissions().forEach(role -> {
                        role.setId(roleMap.get(role.getId()));
                    });
                }
                //StatusPermissions
                else if (field.getPermission().getStatusPermissions() != null) {
                    field.getPermission().getStatusPermissions().values().forEach(userRoleReadEdit -> {
                        userRoleReadEdit.forEach(role -> {
                            role.setId(roleMap.get(role.getId()));
                        });
                    });
                }
            }
        });
        //更新工作项工作流中 权限的角色id
        oneTracker.getTrackerStateTransitions().forEach(transition ->{
            transition.getPermitted().forEach(role ->{
                role.setId(roleMap.get(role.getId()));
            });
        });
        //更新工作项通知的角色id
        oneTracker.getTrackerNotification().getSystemTrackerNotifications().forEach(notification ->{
            notification.getSubscribers().forEach(role ->{
                role.setId(roleMap.get(role.getId()));
            });
        });
        oneTracker.getTrackerNotification().getDefaultNotification().getSubscribers().forEach(role->{
            role.setId(roleMap.get(role.getId()));
        });
        oneTracker.getTrackerNotification().getCustomerTrackerNotifications().forEach(notification ->{
            notification.getSubscribers().forEach(role->{
                role.setId(roleMap.get(role.getId()));
            });
        });
        //更新工作项自定义属性对应枚举值
        oneTracker.getTrackerFields().forEach(field -> {
            if(!field.getSystem()){
                if(field instanceof OptionsField &&ObjectUtils.isValid(enumCategoryMap.get(((OptionsField) field).getEnumId()))){
                    ((OptionsField) field).setEnumId(enumCategoryMap.get(((OptionsField) field).getEnumId()));
                }else if(field instanceof MultiOptionsField &&ObjectUtils.isValid(enumCategoryMap.get(((MultiOptionsField) field).getEnumId()))){
                    ((MultiOptionsField) field).setEnumId(enumCategoryMap.get(((MultiOptionsField) field).getEnumId()));
                }
            }
        });
    }

}
