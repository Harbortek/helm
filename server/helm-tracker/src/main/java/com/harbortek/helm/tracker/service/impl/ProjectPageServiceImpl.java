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

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.tracker.constants.DatasetConstants;
import com.harbortek.helm.tracker.constants.PageScopes;
import com.harbortek.helm.tracker.constants.SystemVariables;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.tracker.constants.ChangeLogMessages;
import com.harbortek.helm.tracker.constants.FieldTypes;
import com.harbortek.helm.tracker.constants.PagePermissions;
import com.harbortek.helm.tracker.constants.ProjectPageTypes;
import com.harbortek.helm.tracker.dao.ChangeLogDao;
import com.harbortek.helm.tracker.dao.DocDao;
import com.harbortek.helm.tracker.dao.ProjectPageDao;
import com.harbortek.helm.tracker.entity.block.DocEntity;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.tracker.service.ProjectPageService;
import com.harbortek.helm.tracker.service.SmartPageService;
import com.harbortek.helm.tracker.service.TrackerService;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.smartpage.*;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.util.*;
import com.harbortek.helm.util.tree.TreeNode;
import com.harbortek.helm.util.tree.TreeNodeAction;
import com.harbortek.helm.util.tree.TreeUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectPageServiceImpl implements ProjectPageService {
    private final Logger logger = LoggerFactory.getLogger(ProjectPageServiceImpl.class);
    @Autowired
    ProjectPageDao projectPageDao;
    @Autowired
    ChangeLogDao changeLogDao;

    @Autowired
    SmartPageService smartPageService;

    @Autowired
    DocDao docDao;

    @Autowired
    PermissionService permissionService;

    @Autowired
    RoleService roleService;


    @Autowired
    TrackerService trackerService;

    public ProjectPageVo findByDocId(Long docId) {
        ProjectPageEntity entity = projectPageDao.findByDocId(docId);
        ProjectPageVo pageVo = DataUtils.toVo(entity,ProjectPageVo.class);
        if (ProjectPageTypes.SMART_PAGE.equals(pageVo.getType()) ) {
            pageVo.setSystemVariables(Arrays.asList(
                    SystemVariableVo.builder().id(SystemVariables.CURRENT_PROJECT_ID).name("当前项目ID").type(
                            DatasetConstants.COLUMN_NUMBER).value(String.valueOf(entity.getProjectId())).build(),
                    SystemVariableVo.builder().id(SystemVariables.CURRENT_USER_ID).name("当前用户ID").type(
                                    DatasetConstants.COLUMN_NUMBER)
                            .value(String.valueOf(SecurityUtils.getCurrentUser().getId())).build()
            ));
        }
        return pageVo;
    }

    public ProjectPageVo findOneProjectPage(Long projectPageId) {
        ProjectPageEntity entity = projectPageDao.findOneProjectPage(projectPageId);
        ProjectPageVo pageVo = DataUtils.toVo(entity,ProjectPageVo.class);
        if (ProjectPageTypes.SMART_PAGE.equals(pageVo.getType()) ) {
            pageVo.setSystemVariables(Arrays.asList(
                    SystemVariableVo.builder().id(SystemVariables.CURRENT_PROJECT_ID).name("当前项目ID").type(
                            DatasetConstants.COLUMN_NUMBER).value(String.valueOf(entity.getProjectId())).build(),
                    SystemVariableVo.builder().id(SystemVariables.CURRENT_USER_ID).name("当前用户ID").type(
                                            DatasetConstants.COLUMN_NUMBER)
                                    .value(String.valueOf(SecurityUtils.getCurrentUser().getId())).build()
                                                   ));
        }
        return pageVo;
    }

    public List<ProjectPageEntity> findOneAndChildrenByProjectPage(Long id) {
        ProjectPageEntity p = projectPageDao.findById(id, ProjectPageEntity.class);
        List<ProjectPageEntity> children = projectPageDao.findOwnChildrenByProjectPage(id);
        List<ProjectPageEntity> entities = new ArrayList<>();
        entities.add(p);
        entities.addAll(children);
        return entities;
    }

    public void updateProjectPage(ProjectPageVo page) {
        ProjectPageEntity entity = projectPageDao.findOneProjectPage(page.getId());
        if (ObjectUtils.isNotEmpty(page.getDefinition())) {
            entity.setDefinition(page.getDefinition());
        }
        if (ObjectUtils.isNotEmpty(page.getBlockTrackerItemRefs())) {
            entity.setBlockTrackerItemRefs(page.getBlockTrackerItemRefs());
        }
        if (ObjectUtils.isNotEmpty(page.getTracker())) {
            entity.setTrackerId(page.getTracker().getId());
        }
        if (page.getPageSettingTrackers() != null) {
            entity.setPageSettingTrackers(page.getPageSettingTrackers());
        }
        projectPageDao.updateProjectPage(entity);
    }

    public void updateProjectPageBasicInfo(ProjectPageVo page) {
        ProjectPageEntity entity = DataUtils.toEntity(page, ProjectPageEntity.class);

        projectPageDao.updateProjectPageBasicInfo(entity);
    }

    @Override
    public List<ProjectPageVo> buildTree(Long projectId) {
        List<ProjectPageVo> pages = DataUtils.toVo(projectPageDao.findByProject(projectId), ProjectPageVo.class);
        List<TreeNode<ProjectPageVo>> treeNodes = TreeUtils.listToTree(pages, "id", "parentId");
        TreeUtils.traversTree(null, treeNodes, new TreeNodeAction<ProjectPageVo>() {
            @Override
            public void doAction(TreeNode<ProjectPageVo> parent, int index, TreeNode<ProjectPageVo> node) {
                if (parent != null) {
                    parent.getObject().getChildren().add(node.getObject());
                }
            }
        });
        return treeNodes.stream().map(TreeNode::getObject).collect(Collectors.toList());
    }

    @Override
    public List<ProjectPageVo> findByProjectId(Long projectId) {
        if(projectId==null){
            return null;
        }
        List<ProjectPageEntity> byProject = projectPageDao.findByProject(projectId);
        List<ProjectPageVo> pages = DataUtils.toVo(byProject, ProjectPageVo.class);
        List<TreeNode<ProjectPageVo>> treeNodes = TreeUtils.listToTree(pages, "id", "parentId");
        TreeUtils.traversTree(null, treeNodes, new TreeNodeAction<ProjectPageVo>() {
            @Override
            public void doAction(TreeNode<ProjectPageVo> parent, int index, TreeNode<ProjectPageVo> node) {
                if (parent != null) {
                    parent.getObject().getChildren().add(node.getObject());
                }
            }
        });

        return TreeUtils.treeToList(treeNodes);
    }

    @Override
    public List<ProjectPageVo> findPagesByIds(List<Long> pageIds) {
        if(ObjectUtils.isEmpty(pageIds)){
            return null;
        }
        List<ProjectPageEntity> pageEntities = projectPageDao.findByIds(pageIds, ProjectPageEntity.class);
        return DataUtils.toVo(pageEntities, ProjectPageVo.class);
    }

    @Override
    public ProjectPageVo createProjectPage(ProjectPageVo pageVo) {
        ProjectPageEntity page = DataUtils.toEntity(pageVo, ProjectPageEntity.class);
        page.setId(IDUtils.getId());

        //创建报表页面
        if (ProjectPageTypes.SMART_PAGE.equals(pageVo.getType()) ){
            SmartPageVo smartPage = new SmartPageVo();
            smartPage.setObjectId(pageVo.getProjectId());
            smartPage.setScope(PageScopes.SCOPE_PROJECT);
            smartPage.setName(pageVo.getName());

            PageDefinitionVo pageDefinition = new PageDefinitionVo();
            pageDefinition.setDatasets(createDefaultDatasets(page.getProjectId()));
            smartPage.setDefinition(JsonUtils.toJSONString(pageDefinition));
            smartPage = smartPageService.createSmartPage(smartPage);
            page.setSmartPageId(smartPage.getId());
        }else{
            DocEntity doc = new DocEntity();
            doc.setId(IDUtils.getId());
            doc.setName(pageVo.getName());
            doc.setVersion(1L);
            doc.setBlocks(new ArrayList<>());
            docDao.saveDoc(doc);
            page.setSmartDocId(doc.getId());
        }

        page = projectPageDao.createProjectPage(page);
        final Long resourceId = page.getId();

        //增加项目管理员对该页面的访问权限
        List<RoleVo> roles = roleService.findRolesByResourceId(page.getProjectId());
        List<PermissionVo> permissionVos = new ArrayList<>();
        for(RoleVo  r: roles){
            if (!r.getSpecialRole()) {
                PagePermissions.ALL_PAGE_PERMISSIONS.forEach(permission -> {
                    PermissionVo permissionVo = new PermissionVo();
                    permissionVo.setId(IDUtils.getId());
                    permissionVo.setIdentity(
                            BaseIdentity.builder().type(IdentityTypes.ROLE).referTo(new IdNameReference<>(r)).build());
                    permissionVo.setName(permission);
                    permissionVo.setResourceId(resourceId);
                    permissionVos.add(permissionVo);
                });
            }
        }
        permissionService.grant(permissionVos);
        //重置项目权限缓存
        PermissionCacheUtils.evictGrantedPermissions(page.getProjectId());

        return DataUtils.toVo(page, ProjectPageVo.class);
    }

    /**
     * 创建默认数据集
     * @param projectId
     * @return
     */
    private List<DatasetVo> createDefaultDatasets(Long projectId) {
        List<TrackerVo> trackers = trackerService.findByProject(projectId,false);
        List<DatasetVo> datasets = new ArrayList<>();
        for (TrackerVo tracker : trackers) {
            DatasetVo dataset = new DatasetVo();
            dataset.setId(IDUtils.getId());
            dataset.setName(tracker.getName());
            dataset.setType(DatasetConstants.DATASET_TYPE_SQL);
            dataset.setSql(buildDatasetSQL(tracker));
            dataset.setFields(buildDatasetFields(tracker));
            datasets.add(dataset);
        }
        return datasets;
    }

    private List<DatasetField> buildDatasetFields(TrackerVo tracker) {
        List<DatasetField> fields = new ArrayList<>();
        fields.add(DatasetField.builder().field("工作项ID").name("工作项ID").type(DatasetConstants.COLUMN_NUMBER).build());
        fields.add(DatasetField.builder().field("项目ID").name("项目ID").type(DatasetConstants.COLUMN_NUMBER).build());
        fields.add(DatasetField.builder().field("项目名称").name("项目名称").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("工作项类型").name("工作项类型").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("工作项编号").name("工作项编号").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("标题").name("标题").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("描述").name("描述").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("状态").name("状态").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("工作项通用状态").name("工作项通用状态").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("优先级").name("优先级").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("严重程度").name("严重程度").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("迭代").name("迭代").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("创建者").name("创建者").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("创建日期").name("创建日期").type(DatasetConstants.COLUMN_DATE).build());
        fields.add(DatasetField.builder().field("更新者").name("更新者").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("更新日期").name("更新日期").type(DatasetConstants.COLUMN_DATE).build());
        fields.add(DatasetField.builder().field("分配日期").name("分配日期").type(DatasetConstants.COLUMN_DATE).build());
        fields.add(DatasetField.builder().field("当前处理人").name("当前处理人").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("关闭日期").name("关闭日期").type(DatasetConstants.COLUMN_DATE).build());
        fields.add(DatasetField.builder().field("负责人").name("负责人").type(DatasetConstants.COLUMN_STRING).build());
        fields.add(DatasetField.builder().field("父工作项ID").name("父工作项ID").type(DatasetConstants.COLUMN_NUMBER).build());
        fields.add(DatasetField.builder().field("计划完成时间").name("计划完成时间").type(DatasetConstants.COLUMN_DATE).build());
        fields.add(DatasetField.builder().field("计划开始时间").name("计划开始时间").type(DatasetConstants.COLUMN_DATE).build());
        fields.add(DatasetField.builder().field("实际完成时间").name("实际完成时间").type(DatasetConstants.COLUMN_DATE).build());
        fields.add(DatasetField.builder().field("实际开始时间").name("实际开始时间").type(DatasetConstants.COLUMN_DATE).build());
        fields.add(DatasetField.builder().field("进度").name("进度").type(DatasetConstants.COLUMN_NUMBER).build());
        fields.add(DatasetField.builder().field("预期工作时长").name("预期工作时长").type(DatasetConstants.COLUMN_NUMBER).build());
        fields.add(DatasetField.builder().field("已登记工作时长").name("已登记工作时长").type(DatasetConstants.COLUMN_NUMBER).build());
        fields.add(DatasetField.builder().field("剩余工作时长").name("剩余工作时长").type(DatasetConstants.COLUMN_NUMBER).build());
        List<TrackerField> trackerFields =  tracker.getTrackerFields();
        for (TrackerField trackerField : trackerFields) {
            if (!trackerField.isSystem()){
                if (Objects.equals(trackerField.getInputType(), FieldTypes.INTEGER) || Objects.equals(trackerField.getInputType(), FieldTypes.DECIMAL)) {
                    fields.add(DatasetField.builder().field(trackerField.getName()).name(trackerField.getName())
                                           .type(DatasetConstants.COLUMN_NUMBER).build());
                }else if (Objects.equals(trackerField.getInputType(), FieldTypes.DATE)) {
                    fields.add(DatasetField.builder().field(trackerField.getName()).name(trackerField.getName())
                                           .type(DatasetConstants.COLUMN_DATE).build());
                }else if (!Objects.equals(trackerField.getInputType(), FieldTypes.TABLE) && !Objects.equals(trackerField.getInputType(), FieldTypes.TEST_STEP)) {
                    fields.add(DatasetField.builder().field(trackerField.getName()).name(trackerField.getName())
                                           .type(DatasetConstants.COLUMN_STRING).build());
                }
            }
        }
        return fields;
    }

    private String buildDatasetSQL(TrackerVo tracker) {
        String select = """
                with `{TRACKER_NAME}` as (select items.id                                   as `工作项ID`,
                                       items.project_id                                     as `项目ID`,
                                       p.name                                               as `项目名称`,
                                       case items.tracker_id
                                           when -1 then '章节'
                                           when -2 then '标题'
                                           when -3 then '段落'
                                           else tracker.name
                                           end                                               as `工作项类型`,
                                       upper(concat(concat(p.key_name, '-'), items.item_no)) as `工作项编号`,
                                       items.name                                            AS `标题`,
                                       items.description                                     as `描述`,
                                       status.name                                           as `状态`,
                                       meaning.name                                          as `工作项通用状态`,
                                       priority_enum.name                                    as `优先级`,
                                       severity.name                                         as `严重程度`,
                                       sprint.name                                           as `迭代`,
                                       u1.name                                               as `创建者`,
                                       items.create_date                                     as `创建日期`,
                                       u2.name                                               as `更新者`,
                                       items.last_modified_date                              as `更新日期`,
                                       items.assigned_date                                   as `分配日期`,
                                       u3.name                                               as `当前处理人`,
                                       items.close_date                                      as `关闭日期`,
                                       u4.name                                               as `负责人`,
                                       items.parent_id                                       as `父工作项ID`,
                                       items.plan_end_date                                   as `计划完成时间`,
                                       items.plan_start_date                                 as `计划开始时间`,
                                       items.real_end_date                                   as `实际完成时间`,
                                       items.real_start_date                                 as `实际开始时间`,
                                       items.progress                                        as `进度`,
                                       items.estimate_working_hours                          as `预期工作时长`,
                                       items.registered_working_hours                        as `已登记工作时长`,
                                       items.remaining_working_hours                         as `剩余工作时长`
                """;

        String where ="""
                                from tracker_items items
                                         left join projects p on (items.project_id = p.id)
                                         left join trackers tracker on (items.tracker_id = tracker.id)
                                         left join enum_items priority_enum on (items.priority_id = priority_enum.id)
                                         left join enum_items meaning on (items.meaning_id = meaning.id)
                                         left join enum_items severity on (items.severity_id = severity.id)
                                         left join enum_items status on (items.status_id = status.id)
                                         left join sprints sprint on items.sprint_id = sprint.id
                                         left join users u1 on items.create_by = u1.id
                                         left join users u2 on items.last_modified_by = u2.id
                                         left join users u3 on items.assigned_to_id = u3.id
                                         left join users u4 on items.owner_id = u4.id
                                where items.deleted = 0
                                    and items.project_id = {PROJECT_ID}
                                    and items.tracker_id = {TRACKER_ID}
                )
                select *
                from `{TRACKER_NAME}`
                """;
        StringBuilder sb = new StringBuilder();
        sb.append(select);
        List<TrackerField> trackerFields =  tracker.getTrackerFields();
        for (TrackerField trackerField : trackerFields) {
            if (!trackerField.isSystem()){
                if (!Objects.equals(trackerField.getInputType(), FieldTypes.TABLE) && !Objects.equals(trackerField.getInputType(), FieldTypes.TEST_STEP)) {
                    sb.append(",\n");
                    sb.append("json_extract(items.values,'$.\"").append(trackerField.getId()).append("\"') as `").append(trackerField.getName()).append("`");
                }
            }
        }
        sb.append(where);
        String sql = sb.toString();
        sql = sql.replace("{TRACKER_NAME}", tracker.getName());
        sql = sql.replace("{TRACKER_ID}", String.valueOf(tracker.getId()));
        sql = sql.replace("{PROJECT_ID}", String.valueOf(tracker.getProjectId()));
        return sql;
    }

    @Override
    public void deleteProjectPage(ProjectPageVo page) {
        List<ProjectPageEntity> pageList = projectPageDao.findPagesByParentId(page.getId());
        if(ObjectUtils.isNotEmpty(pageList)){
            throw  new RuntimeException("该页面下存在子页面，无法删除");
        }
        projectPageDao.deleteProjectPage(page.getId());

        //删除项目成员对该页面的访问权限
        permissionService.unGrantByResourceId(page.getId());
    }

    @Override
    public void changeProjectPageOrder(List<ProjectPageVo> pageList) {
        Collection<ProjectPageEntity> pages = DataUtils.toEntity(pageList, ProjectPageEntity.class);
        AtomicInteger index = new AtomicInteger(1);
        pages.forEach(p -> p.setOrder(index.getAndIncrement()));
        projectPageDao.changeProjectPageOrder(pages);
    }

    @Override
    public void batchCreateProjectPages(List<ProjectPageVo> pageEntities) {
        pageEntities.forEach(pageVo->{
            //创建报表页面
            if (ProjectPageTypes.SMART_PAGE.equals(pageVo.getType())
                && pageVo.getSmartPageId()==null){
                SmartPageVo smartPage = new SmartPageVo();
                smartPage.setObjectId(pageVo.getProjectId());
                smartPage.setScope(PageScopes.SCOPE_PROJECT);
                smartPage = smartPageService.createSmartPage(smartPage);
                pageVo.setSmartPageId(smartPage.getId());
            }
        });
        projectPageDao.createProjectPages(DataUtils.toEntity(pageEntities, ProjectPageEntity.class));
    }

    @Override
    public ProjectPageVo findPageByComponentType(Long projectId, String componentType) {
        ProjectPageEntity pageEntity= projectPageDao.findPageByComponentType(projectId,componentType);
        return DataUtils.toVo(pageEntity, ProjectPageVo.class);
    }

    @Override
    public List<Long> findPageIdsByProjectId(Long projectId) {
        return projectPageDao.findPageIdsByProjectId(projectId);
    }

    @Override
    public void watch(Long pageId, BaseIdentity watch) {    //wiki页面关注
        ProjectPageEntity pageEntity = projectPageDao.findOneProjectPage(pageId);
//        if (ObjectUtils.isNotEmpty(pageEntity.getWatchers())) {
//            pageEntity.getWatchers().add(watch);
//        } else {
//            pageEntity.setWatchers(new ArrayList<>(Collections.singletonList(watch)));
//        }
        projectPageDao.updateProjectPage(pageEntity);
        changeLogDao.createChangeLog(pageId, ChangeLogMessages.TRACKER_ITEM_NOTIFICATION, "关注了页面",
                pageEntity, "", "");
    }

    @Override
    public void cancelWatch(Long pageId, BaseIdentity watch) {
        ProjectPageEntity pageEntity = projectPageDao.findOneProjectPage(pageId);
//        if (ObjectUtils.isNotEmpty(pageEntity.getWatchers())) {
//            pageEntity.getWatchers().remove(watch);
//            projectPageDao.updateProjectPage(pageEntity);
//            changeLogDao.createChangeLog(pageId, ChangeLogMessages.TRACKER_ITEM_NOTIFICATION, "取消关注了页面",
//                    pageEntity, "", "");
//        }
    }
}
