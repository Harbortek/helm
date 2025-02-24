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

import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.vo.EnumCategoryVo;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.constants.*;
import com.harbortek.helm.tracker.service.ProjectDataInitService;
import com.harbortek.helm.tracker.service.ProjectTemplateService;
import com.harbortek.helm.tracker.service.ViewService;
import com.harbortek.helm.tracker.template.reader.ProjectTemplateReader;
import com.harbortek.helm.tracker.vo.view.ViewVo;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ProjectDataInitServiceImpl implements ProjectDataInitService {
    @Autowired
    ProjectTemplateService projectTemplateService;

    @Autowired
    ProjectTemplateReader projectTemplateReader;

    @Autowired
    EnumService enumService;

    @Autowired
    ViewService viewService;

    @Override
    public void initializeProjectData(ApplicationContext applicationContext) {


        initSystemPredefinedStatus();

        initSystemPredefinedCategories();

        initSystemPredefinedStatusMeanings();

        initSystemPredefinedTrackerPriorities();

        initSystemPredefinedTrackerSeverities();

        initSystemPredefinedTrackerTypes();

        initSystemPredefinedTestRunStatus();

        initSystemPredefinedTestResults();

//        initSystemPredefinedTrackerAssociationTypes();

        initSystemPredefinedViews();

    }


    private void initSystemPredefinedViews() {
        if (viewService.findByObjectId(1L).isEmpty()) {
            List<EnumItemVo> projectStatus = enumService.findEnumItemsByCode(null, EnumCodes.PROJECT_STATUS_MEANING);
            List<ViewVo> views = new ArrayList<>();
            ViewVo viewAll = ViewVo.builder().id(IDUtils.getId()).name("全部")
                                   .objectId(1L).viewType("PUBLIC").display(true).system(true)
                                   .build();
            views.add(viewAll);
            projectStatus.forEach(item -> {
                ViewVo view = ViewVo.builder().id(IDUtils.getId()).name(item.getName())
                                    .objectId(1L).viewType("PUBLIC").display(true).system(true)
                                    .build();
                views.add(view);
            });
            viewService.batchCreateViews(views);
        }
    }

    void initSystemPredefinedStatus() {
        if (!enumService.existsCategoryCode(EnumCodes.PROJECT_STATUS_MEANING)) {

            EnumCategoryVo projectStatus =
                    EnumCategoryVo.builder().id(IDUtils.getId()).name("项目状态").code(EnumCodes.PROJECT_STATUS_MEANING)
                                  .system(true).build();
            enumService.createEnumCategory(projectStatus);
            List<EnumItemVo> list = Arrays.asList(
                    EnumItemVo.builder().id(IDUtils.getId()).name("未开始").categoryId(projectStatus.getId()).code(
                            ProjectStatusMeaning.NOT_STARTED).color("#338fe5").system(true).build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("进行中").categoryId(projectStatus.getId()).code(
                            ProjectStatusMeaning.ONGOING).color("#e39f48").system(true).build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("延迟中").categoryId(projectStatus.getId()).code(
                            ProjectStatusMeaning.DELAYED).color("#e39f48").system(true).build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("已完成").categoryId(projectStatus.getId()).code(
                            ProjectStatusMeaning.ENDED).color("#24b47e").system(true).build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("已关闭").categoryId(projectStatus.getId()).code(
                            ProjectStatusMeaning.CLOSED).color("#24b47e").system(true).build()
                                                 );
            enumService.batchCreateEnumItems(list);
        }
    }

    void initSystemPredefinedCategories() {
        if (!enumService.existsCategoryCode(EnumCodes.PROJECT_CATEGORY)) {

            EnumCategoryVo projectCategory =
                    EnumCategoryVo.builder().id(IDUtils.getId()).name("项目分类").code(EnumCodes.PROJECT_CATEGORY)
                                  .system(true).build();
            enumService.createEnumCategory(projectCategory);

            List<EnumItemVo> list = Arrays.asList(
                    EnumItemVo.builder().id(IDUtils.getId()).name("需求管理项目").categoryId(projectCategory.getId())
                              .build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("测试管理项目").categoryId(projectCategory.getId())
                              .build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("功能安全项目").categoryId(projectCategory.getId())
                              .build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("ASPICE项目").categoryId(projectCategory.getId())
                              .build()
                                                 );
            enumService.batchCreateEnumItems(list);

        }
    }

    void initSystemPredefinedTrackerPriorities() {
        if (!enumService.existsCategoryCode(EnumCodes.TRACKER_PRIORITY)) {

            EnumCategoryVo trackerPriority =
                    EnumCategoryVo.builder().id(IDUtils.getId()).name("工作项优先级").code(EnumCodes.TRACKER_PRIORITY)
                                  .system(true).build();
            enumService.createEnumCategory(trackerPriority);

            List<EnumItemVo> list = Arrays.asList(
                    EnumItemVo.builder().id(IDUtils.getId()).name("最高").description("将会直接阻碍进程的问题。")
                              .categoryId(trackerPriority.getId())
                              .color("rgb(255, 255, 255)").backgroundColor("rgb(230, 52, 34)").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("较高").description("可能会阻碍进程的严重问题。")
                              .categoryId(trackerPriority.getId())
                              .color("rgb(255, 106, 57)").backgroundColor("rgb(255, 233, 226)").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("普通").description("有可能影响进程的潜在问题。")
                              .categoryId(trackerPriority.getId())
                              .color("rgb(48, 127, 226)").backgroundColor("rgb(224, 236, 251)").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("较低").description("容易解决的小问题。")
                              .categoryId(trackerPriority.getId())
                              .color("rgb(0, 179, 136)").backgroundColor("rgb(217, 244, 237)").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("最低").description("对进展影响甚微，微不足道的问题。")
                              .categoryId(trackerPriority.getId())
                              .color("rgb(144, 144, 144)").backgroundColor("rgb(239, 239, 239)").build()
                                                 );
            enumService.batchCreateEnumItems(list);
        }
    }

    void initSystemPredefinedTrackerSeverities() {
        if (!enumService.existsCategoryCode(EnumCodes.TRACKER_SEVERITY)) {

            EnumCategoryVo trackerSeverity =
                    EnumCategoryVo.builder().id(IDUtils.getId()).name("工作项严重级别").code(EnumCodes.TRACKER_SEVERITY)
                                  .system(true).build();
            enumService.createEnumCategory(trackerSeverity);

            List<EnumItemVo> list = Arrays.asList(
                    EnumItemVo.builder().id(IDUtils.getId()).name("必须有").description("如果不包含，则产品不可行。")
                              .categoryId(trackerSeverity.getId())
                              .color("rgb(255, 255, 255)").backgroundColor("rgb(230, 52, 34)").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("应该有").description("这些功能很重要，但不是必需的。")
                              .categoryId(trackerSeverity.getId())
                              .color("rgb(255, 106, 57)").backgroundColor("rgb(255, 233, 226)").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("可以有")
                              .description("这些要求是客户期望的，但不是必需的。")
                              .categoryId(trackerSeverity.getId())
                              .color("rgb(48, 127, 226)").backgroundColor("rgb(224, 236, 251)").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("这次不会有")
                              .description("最不重要，最低回报项目，或在当下是不适合的要求。")
                              .categoryId(trackerSeverity.getId())
                              .color("rgb(0, 179, 136)").backgroundColor("rgb(217, 244, 237)").build());
            enumService.batchCreateEnumItems(list);
        }
    }

    void initSystemPredefinedStatusMeanings() {
        if (!enumService.existsCategoryCode(EnumCodes.TRACKER_STATUS_MEANING)) {

            EnumCategoryVo trackerStatusMeaning =
                    EnumCategoryVo.builder().id(IDUtils.getId()).name("工作项状态分类")
                                  .code(EnumCodes.TRACKER_STATUS_MEANING).system(true).build();
            enumService.createEnumCategory(trackerStatusMeaning);

            List<EnumItemVo> list = Arrays.asList(
                    EnumItemVo.builder().id(IDUtils.getId()).name("未开始").code(
                                      TrackerStatusMeaning.NOT_STARTED).categoryId(trackerStatusMeaning.getId())
                              .color("#338fe5").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("进行中").code(TrackerStatusMeaning.ONGOING)
                              .categoryId(trackerStatusMeaning.getId())
                              .color("#e39f48").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("已解决").code(TrackerStatusMeaning.ENDED)
                              .categoryId(trackerStatusMeaning.getId())
                              .color("rgb(230, 52, 34)").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("已关闭").code(TrackerStatusMeaning.CLOSED)
                              .categoryId(trackerStatusMeaning.getId())
                              .color("#24b47e").build()
                                                 );
            enumService.batchCreateEnumItems(list);
        }
    }

    void initSystemPredefinedTrackerTypes() {
        if (!enumService.existsCategoryCode(EnumCodes.TRACKER_TYPE)) {

            EnumCategoryVo trackerType =
                    EnumCategoryVo.builder().id(IDUtils.getId()).name("工作项类型分类").code(EnumCodes.TRACKER_TYPE)
                                  .system(true).build();
            enumService.createEnumCategory(trackerType);

            List<EnumItemVo> list = Arrays.asList(
                    EnumItemVo.builder().id(IDUtils.getId()).name("需求").categoryId(trackerType.getId())
                              .icon("requirement").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("用户故事").categoryId(trackerType.getId())
                              .icon("userStory").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("任务").categoryId(trackerType.getId())
                              .icon("task").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("风险").categoryId(trackerType.getId())
                              .icon("risk").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("测试用例").categoryId(trackerType.getId())
                              .icon("testCase").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("缺陷").categoryId(trackerType.getId())
                              .icon("bug").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("变更请求").categoryId(trackerType.getId())
                              .icon("changeRequest").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("发布").categoryId(trackerType.getId())
                              .icon("publish").build()
                                                 );
            enumService.batchCreateEnumItems(list);
        }
    }

    void initSystemPredefinedTestRunStatus() {
        if (!enumService.existsCategoryCode(EnumCodes.TEST_RUN_STATUS)) {

            EnumCategoryVo testRunStatusCategory =
                    EnumCategoryVo.builder().id(IDUtils.getId()).name("测试运行状态").code(EnumCodes.TEST_RUN_STATUS)
                                  .system(true).build();
            enumService.createEnumCategory(testRunStatusCategory);

            List<EnumItemVo> list = Arrays.asList(
                    EnumItemVo.builder().id(IDUtils.getId()).name("未开始").code(
                                      TestRunStatus.NOT_STARTED).categoryId(testRunStatusCategory.getId())
                              .color("#338fe5").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("进行中").code(TestRunStatus.ONGOING)
                              .categoryId(testRunStatusCategory.getId())
                              .color("#e39f48").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).name("已完成").code(TestRunStatus.ENDED)
                              .categoryId(testRunStatusCategory.getId())
                              .color("#24b47e").build()
                                                 );
            enumService.batchCreateEnumItems(list);
        }
    }

    void initSystemPredefinedTestResults() {
        if (!enumService.existsCategoryCode(EnumCodes.TEST_CASE_RESULT)) {

            EnumCategoryVo testResultCategory =
                    EnumCategoryVo.builder().id(IDUtils.getId()).name("测试用例执行结果")
                                  .code(EnumCodes.TEST_CASE_RESULT)
                                  .system(true).build();
            enumService.createEnumCategory(testResultCategory);

            List<EnumItemVo> list = Arrays.asList(
                    EnumItemVo.builder().id(IDUtils.getId()).code(TestResults.PASSED).name("通过")
                              .categoryId(testResultCategory.getId()).color("#00a865").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).code(TestResults.BLOCKED).name("阻塞")
                              .categoryId(testResultCategory.getId()).color("#f59300").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).code(TestResults.SKIPPED).name("跳过")
                              .categoryId(testResultCategory.getId()).color("#87888a").build(),
                    EnumItemVo.builder().id(IDUtils.getId()).code(TestResults.FAILED).name("失败")
                              .categoryId(testResultCategory.getId()).color("#eb3723").build()
                                                 );
            enumService.batchCreateEnumItems(list);
        }
    }


//    void initSystemPredefinedTrackerAssociationTypes() {
//        if (!enumService.existsCategoryCode(EnumCodes.TRACKER_ASSOCIATION_TYPE)) {
//
//            EnumCategoryVo trackerAssociationType =
//                    EnumCategoryVo.builder().id(IDUtils.getId()).name("工作项关联关系")
//                                  .code(EnumCodes.TRACKER_ASSOCIATION_TYPE)
//                                  .system(true).build();
//            enumService.createEnumCategory(trackerAssociationType);
//
//            List<EnumItemVo> list = Arrays.asList(
//                    EnumItemVo.builder().id(IDUtils.getId()).name("拥有父亲").code(Associations.HAS_PARENT)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("实现了").code(Associations.IMPLEMENTS)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("依赖于").code(Associations.DEPENDS_ON)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("重复了").code(Associations.DUPLICATES)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("拥有后续").code(Associations.HAS_FOLLOW_UP)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("由此触发").code(Associations.IS_TRIGGERED_BY)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("影响了").code(Associations.AFFECTS)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("引用").code(Associations.RELATES_TO)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("作为父亲").code(Associations.IS_PARENT_OF)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("阻塞了").code(Associations.BLOCKS)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("与之重复").code(Associations.IS_DUPLICATED_BY)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("是其后续").code(Associations.FOLLOWS)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("被验证").code(Associations.IS_VERIFIED_BY)
//                              .categoryId(trackerAssociationType.getId()).build(),
//                    EnumItemVo.builder().id(IDUtils.getId()).name("被引用").code(Associations.IS_RELATED_TO)
//                              .categoryId(trackerAssociationType.getId()).build()
//                                                 );
//            enumService.batchCreateEnumItems(list);
//        }
//    }


//	private void initProjectTemplate(ApplicationContext applicationContext){
//		// init project template
//		Resource[] resources = new Resource[0];
//		try {
//			resources = applicationContext.getResources("classpath:project-template/*");
//
//			for (Resource resource : resources) {
//				ProjectTemplateVo templateVo = projectTemplateReader.readTemplateBasicInfo(resource);
//				if (templateVo==null){
//					continue;
//				}
//				//templateVo.setLocation("classpath:project-template/"+resource.getFilename());
//				ProjectTemplateEntity oldTemplate = projectTemplateService.findByName(templateVo.getName());
//				if (oldTemplate==null){
//					ProjectTemplateEntity templateEntity = ObjectUtils.toEntity(templateVo,ProjectTemplateEntity.class);
//					templateEntity.setId(IDUtils.getId());
//					projectTemplateService.createProjectTemplate(templateEntity);
//				}else{
//					ProjectTemplateEntity templateEntity = ObjectUtils.toEntity(templateVo,ProjectTemplateEntity.class);
//					templateEntity.setId(oldTemplate.getId());
//					projectTemplateService.updateProjectTemplate(templateEntity);
//				}
//
////				@Cleanup Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
////				String content = IOUtils.toString(reader);
////
////				TemplateXmlReader templateXmlReader = new TemplateXmlReader(new StringReader(content));
////				ProjectTemplateVo templateVo = templateXmlReader.buildTemplate();
////
////				ProjectTemplateEntity oldTemplate = projectTemplateService.findByName(templateVo.getName());
////				if (oldTemplate==null){
////					ProjectTemplateEntity templateEntity = ObjectUtils.toEntity(templateVo,ProjectTemplateEntity.class);
////					templateEntity.setId(IDUtils.getId());
////					templateEntity.setContent(content);
////					projectTemplateService.createProjectTemplate(templateEntity);
////				}else{
////					ProjectTemplateEntity templateEntity = ObjectUtils.toEntity(templateVo,ProjectTemplateEntity.class);
////					templateEntity.setId(oldTemplate.getId());
////					templateEntity.setContent(content);
////					projectTemplateService.updateProjectTemplate(templateEntity);
////				}
//			}
//		} catch (IOException ex) {
//			logger.error("Can not load project template resources",ex);
//		}
//	}
}
