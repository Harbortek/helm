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

package com.harbortek.helm.tracker.api;

import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.common.vo.IdNameVo;
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.tracker.service.PlanService;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.log.AttachmentVo;
import com.harbortek.helm.tracker.vo.plan.DeliverableVo;
import com.harbortek.helm.tracker.vo.plan.GanttVo;
import com.harbortek.helm.tracker.vo.plan.PlanVo;
import com.harbortek.helm.tracker.vo.plan.SprintVo;
import com.harbortek.helm.util.SecurityUtils;
import com.harbortek.helm.util.excel.ExcelUtil;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/tracker/plan")
public class PlanApi {
    @Autowired
    PlanService planService;

    @Autowired
    FileService fileService;

    @Parameter(name="查询计划列表")
    @RequestMapping(value = "/gantt", method = RequestMethod.GET)
    ResponseEntity<GanttVo> buildGantt(
            @RequestParam(value = "projectId", required = false) Long projectId) {
        GanttVo ganttVo = planService.buildGantt(projectId);
        return ResponseEntity.ok(ganttVo);
    }

    @Parameter(name="查询计划列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<PlanVo>> findPlans(
            @RequestParam(value = "projectId", required = false) Long projectId) {
        Collection<PlanVo> plans = planService.findPlans(projectId);
        return ResponseEntity.ok(plans);
    }

    @Parameter(name="自动排期")
    @RequestMapping(value = "/auto", method = RequestMethod.POST)
    ResponseEntity<Void> autoPlans(
            @RequestParam(value = "projectId", required = false) Long projectId) {
        planService.autoPlans(projectId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询里程碑列表")
    @RequestMapping(value = "/milestones", method = RequestMethod.GET)
    ResponseEntity<Collection<PlanVo>> findMilestones(@RequestParam(value = "projectId") Long projectId) {
        Collection<PlanVo> plans = planService.findMilestones(projectId);
        return ResponseEntity.ok(plans);
    }

    @Parameter(name="查询一个计划")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<PlanVo> findOnePlan(@PathVariable Long id) {
        PlanVo plan = planService.findOnePlan(id);
        return ResponseEntity.ok(plan);
    }

    @Parameter(name="创建一个计划")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<PlanVo> createPlan(@RequestBody PlanVo planVo) {
        PlanVo result = planService.createPlan(planVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个计划")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<PlanVo> updatePlan(@RequestBody PlanVo planVo) {
        PlanVo result = planService.updatePlan(planVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个计划")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/import-ms-project", method = RequestMethod.POST)
    ResponseEntity<Void> importMSProject(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @RequestParam(value = "projectId", required = false) Long projectId,
                                     @RequestParam(value = "file", required = false) String mppFile){

        InputStream is = null;
        try {
            is = fileService.download(mppFile);
            planService.importMPP(projectId, is);
        } catch (Exception e) {
            throw new ServiceException("无法打开上传文件");
        }finally {
            IOUtils.closeQuietly(is);
        }

        return ResponseEntity.ok().build();
    }

    @Parameter(name="导出计划列表")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    ResponseEntity<Void> exportPlans(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam(value = "projectId", required = false) Long projectId) {
        Collection<PlanVo> plans = planService.findPlans(projectId);

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

        headers.put(PlanVo.Fields.seqNumber, "序号");
        headers.put(PlanVo.Fields.itemNo, "编号");
        headers.put(PlanVo.Fields.type, "类型");
        headers.put(IdNameVo.Fields.name, "名称");
        headers.put(PlanVo.Fields.progress, "进度");
        headers.put(PlanVo.Fields.owner, "负责人");
        headers.put(PlanVo.Fields.planStartDate, "开始日期");
        headers.put(PlanVo.Fields.planEndDate, "结束日期");
        headers.put(PlanVo.Fields.duration, "工期");
        headers.put(PlanVo.Fields.preTasks, "前置计划");
        headers.put(PlanVo.Fields.postTasks, "后置计划");
        headers.put(PlanVo.Fields.sprints, "迭代");

        List<Map<String, Object>> data = plans.stream().map(item -> {
            Map<String, Object> row = new HashMap<>();
            row.put(PlanVo.Fields.seqNumber, item.getSeqNumber());
            row.put(PlanVo.Fields.itemNo, item.getItemNo());
            row.put(PlanVo.Fields.type, item.getType());
            //序号里有几个点，就意味着有几级缩进
            int size = StringUtils.countMatches(item.getSeqNumber(), ".") * 4 +
                    StringUtils.length(StringUtils.trim(item.getName()));
            row.put(IdNameVo.Fields.name, StringUtils.leftPad(StringUtils.trim(item.getName()),size));
            row.put(PlanVo.Fields.progress, item.getProgress() != null && item.getProgress()>0 ? item.getProgress()+
                    "%" :
                    "-");
            row.put(PlanVo.Fields.owner, item.getOwner() != null ? item.getOwner().getName() : "-");
            row.put(PlanVo.Fields.planStartDate, item.getPlanStartDate() != null ? item.getPlanStartDate() : "-");
            row.put(PlanVo.Fields.planEndDate, item.getPlanEndDate() != null ? item.getPlanEndDate() : "-");
            row.put(PlanVo.Fields.duration, item.getDuration());
            row.put(PlanVo.Fields.preTasks,
                    item.getPreTasks().stream().map(PlanVo::getSeqNumber)
                        .collect(Collectors.joining(",")));
            row.put(PlanVo.Fields.postTasks,
                    item.getPostTasks().stream().map(PlanVo::getSeqNumber)
                        .collect(Collectors.joining(",")));
            row.put(PlanVo.Fields.sprints,
                    item.getSprints().stream().map(IdNameVo::getName).collect(Collectors.joining(",")));
            return row;
        }).collect(Collectors.toList());

        try {
            ExcelUtil.processResponseHeader(request, response, "项目计划");
            ExcelUtil.exportExcel(headers, data, response.getOutputStream());

        } catch (Exception e) {
        }
        return ResponseEntity.ok().build();
    }

    @Parameter(name="设置里程碑交付物")
    @RequestMapping(value = "/{planId}/deliverables", method = RequestMethod.POST)
    ResponseEntity<Void> updateDeliverables(@PathVariable Long planId, @RequestBody List<DeliverableVo> deliverables) {
        planService.updateDeliverables(planId, deliverables);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="上传附件")
    @RequestMapping(value = "/deliverable/{deliverableId}/attachments", method = RequestMethod.POST)
    ResponseEntity<AttachmentVo> uploadAttachment(@PathVariable Long deliverableId,
                                                  @RequestBody AttachmentVo attachmentVo) {
        AttachmentVo vo = planService.uploadAttachment(deliverableId, attachmentVo);
        return ResponseEntity.ok(vo);
    }

    @Parameter(name="上传附件")
    @RequestMapping(value = "/deliverable/{deliverableId}", method = RequestMethod.POST)
    ResponseEntity<Void> updateDeliverable(@PathVariable Long deliverableId,
                                           @RequestBody DeliverableVo deliverableVo) {
        planService.updateDeliverable(deliverableId, deliverableVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询里程碑列表")
    @RequestMapping(value = "/deliverable/list", method = RequestMethod.GET)
    ResponseEntity<Collection<DeliverableVo>> findDeliverables(@RequestParam(value = "projectId") Long projectId) {
        Collection<DeliverableVo> deliverables = planService.findDeliverables(projectId);
        return ResponseEntity.ok(deliverables);
    }


    @Parameter(name="查询待执行计划列表")
    @RequestMapping(value = "/waitExecute", method = RequestMethod.GET)
    ResponseEntity<Collection<PlanVo>> findWaitExecutePlans(@RequestParam(value = "projectId") Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUser().getId();
        Collection<PlanVo> plans = planService.findWaitExecutePlans(projectId, currentUserId);
        return ResponseEntity.ok(plans);
    }

    @Parameter(name="关联迭代")
    @RequestMapping(value = "/{planId}/associateSprints", method = RequestMethod.POST)
    ResponseEntity<Void> associateSprints(@PathVariable Long planId,
                                          @RequestBody List<SprintVo> sprints) {
        planService.associateSprints(planId, sprints);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="取消关联迭代")
    @RequestMapping(value = "/{planId}/dissociateSprints", method = RequestMethod.POST)
    ResponseEntity<Void> dissociateSprints(@PathVariable Long planId,
                                           @RequestBody List<SprintVo> sprints) {
        planService.dissociateSprints(planId, sprints);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="关联工作项")
    @RequestMapping(value = "/{planId}/associateTrackerItems", method = RequestMethod.POST)
    ResponseEntity<Void> associateTrackerItems(@PathVariable Long planId,
                                               @RequestBody List<TrackerItemVo> trackerItems) {
        planService.associateTrackerItems(planId, trackerItems);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="取消关联工作项")
    @RequestMapping(value = "/{planId}/dissociateTrackerItems", method = RequestMethod.POST)
    ResponseEntity<Void> dissociateTrackerItems(@PathVariable Long planId,
                                                @RequestBody List<TrackerItemVo> trackerItems) {
        planService.dissociateTrackerItems(planId, trackerItems);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新页面排序")
    @RequestMapping(value = "/changeOrder", method = RequestMethod.POST)
    ResponseEntity<Void> changePlanOrders(@RequestBody List<PlanVo> planVos) {
        planService.changeOrder(planVos);
        return ResponseEntity.ok().build();
    }

}
