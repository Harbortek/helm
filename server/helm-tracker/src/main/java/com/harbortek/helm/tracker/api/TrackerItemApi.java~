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

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.tracker.constants.Associations;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.tracker.entity.log.HyperlinkEntity;
import com.harbortek.helm.tracker.entity.log.WorkHoursEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.service.TrackerItemService;
import com.harbortek.helm.tracker.vo.chart.ProjectCardInfo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.log.AttachmentVo;
import com.harbortek.helm.tracker.vo.log.CommentVo;
import com.harbortek.helm.tracker.vo.log.TrackerLogVo;
import com.harbortek.helm.tracker.vo.log.WorkHoursVo;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.tracker.vo.view.FilterCondition;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import com.harbortek.helm.util.excel.ExcelLogs;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Tag(name = "项目管理")
@RequestMapping(value = "/tracker/project/items")
public class TrackerItemApi {
    @Autowired
    TrackerItemService itemService;

    @Parameter(name="查询工作项条目列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    ResponseEntity<Page<TrackerItemVo>> findTrackerItems(@RequestParam(value = "projectId", required = false) Long projectId,
                                                         @RequestParam(value = "trackerId", required = false) Long trackerId,
                                                         @RequestParam(value = "sprintId", required = false) Long sprintId,
                                                         @RequestBody(required = false) ObjectFilter filter,
                                                         @RequestParam(value = "keyword", required = false) String keyword,
                                                         @RequestParam(required = false) Boolean isTest,
                                                         Pageable pageable) {
        Page<TrackerItemVo> trackerItems = itemService.findTrackerItems(projectId,trackerId,sprintId, filter,keyword
                ,isTest,pageable);
        return ResponseEntity.ok(trackerItems);
    }

    @Parameter(name="查询工作项条目树形列表")
    @RequestMapping(value = "/list-tree", method = RequestMethod.POST)
    ResponseEntity<Page<TrackerItemVo>> findTrackerItemsTree(@RequestParam(value = "projectId", required = false) Long projectId,
                                                         @RequestParam(value = "trackerId", required = false) Long trackerId,
                                                         @RequestParam(value = "sprintId", required = false) Long sprintId,
                                                         @RequestBody(required = false) ObjectFilter filter,
                                                         @RequestParam(value = "keyword", required = false) String keyword,
                                                         @RequestParam(required = false) Boolean linkDire,
                                                         @RequestParam(required = false) Long linkType,
                                                         @RequestParam(required = false) Integer linkLevel,
                                                         Pageable pageable) {
        Page<TrackerItemVo> trackerItems = itemService.findTrackerItemsTree(projectId,trackerId,sprintId, filter,keyword
                ,linkDire,linkType,linkLevel,pageable);
        return ResponseEntity.ok(trackerItems);
    }

    @Parameter(name="获取分组下的条目")
    @RequestMapping(value = "/group", method = RequestMethod.POST)
    ResponseEntity<List<FilterCondition>> findGroupItems(@RequestParam(value = "projectId", required = false) Long projectId,
                                                         @RequestParam(value = "trackerId", required = false) Long trackerId,
                                                         @RequestParam(value = "sprintId", required = false) Long sprintId,
                                                         @RequestBody(required = false) ObjectFilter filter,
                                                         @RequestParam(value = "keyword", required = false) String keyword){
        List<FilterCondition> filterConditions = itemService.findGroupItems(projectId,trackerId,sprintId, filter,keyword);
        return ResponseEntity.ok(filterConditions);
    }

    @Parameter(name="创建工作项条目")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<TrackerItemVo> createTrackerItem(@RequestBody TrackerItemVo trackerItemVo) {
        TrackerItemVo vo = itemService.createTrackerItem(trackerItemVo);
        return ResponseEntity.ok(vo);
    }

    @Parameter(name="批量更新工作项条目")
    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    ResponseEntity<Void> batchUpdateTrackerItem(@RequestBody List<TrackerItemVo> trackerItemVos) {
        itemService.batchUpdateTrackerItem(trackerItemVos);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询工作项条目")
    @RequestMapping(value = "/{itemId}", method = RequestMethod.GET)
    ResponseEntity<TrackerItemVo> findOneTrackerItem(@PathVariable Long itemId) {
        TrackerItemVo vo = itemService.findOneTrackerItem(itemId);
        return ResponseEntity.ok(vo);
    }

    @Parameter(name="根据ids查询工作项条目列表")
    @RequestMapping(value = "/byIds", method = RequestMethod.POST)
    ResponseEntity<List<TrackerItemVo>> findTrackerItemByIds(@RequestBody List<Long> itemIds) {
        List<TrackerItemVo> vos = itemService.findTrackerItemByIds(itemIds);
        return ResponseEntity.ok(vos);
    }
    @Parameter(name="删除工作项条目")
    @RequestMapping(value = "/{itemId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteOneTrackerItem(@PathVariable Long itemId) {
        itemService.deleteOneTrackerItem(itemId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除工作项条目")
    @RequestMapping(value = "/deleteByTrackerId", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteByTrackerId(@RequestParam Long trackerId) {
        itemService.deleteTrackerItemsByTrackerId(trackerId);
        return ResponseEntity.ok().build();
    }
    @Parameter(name="批量删除工作项条目")
    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
    ResponseEntity<Void> batchDeleteTrackerItem(@RequestBody List<Long> itemIds) {
        itemService.batchDeleteTrackerItem(itemIds);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="导出工作项条目列表")
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    ResponseEntity<Void>exportTrackerItems(HttpServletRequest request, HttpServletResponse response,
                                                            @RequestParam(value = "projectId", required = false) Long projectId,
                                                            @RequestParam(value = "trackerId", required = false) Long trackerId,
                                                            @RequestParam(value = "sprintId", required = false) Long sprintId,
                                                            @RequestParam(required = false) List<Long> selectedRowIds,
                                                            @RequestBody(required = false) ObjectFilter filter,
                                                            Pageable pageable) {
        itemService.exportTrackerItems(request,response,projectId,trackerId,sprintId, selectedRowIds,filter,
                PageRequest.of(0,Integer.MAX_VALUE,pageable.getSort()));

        return ResponseEntity.ok().build();
    }

    @Parameter(name="导出工作项条目模板")
    @RequestMapping(value = "/import-demo", method = RequestMethod.GET)
    ResponseEntity<Void>downloadImportDome(HttpServletRequest request, HttpServletResponse response,
                                           @RequestParam(value = "trackerId", required = false) Long trackerId) {
        itemService.downloadImportDome(request,response,trackerId);

        return ResponseEntity.ok().build();
    }
    @Parameter(name="导入工作项条目")
    @RequestMapping(value = "/import", method = RequestMethod.GET)
    ResponseEntity<ExcelLogs>importTrackerItems(@RequestParam Long trackerId,
                                              @RequestParam String uploadedFile) {
        ExcelLogs result = itemService.importTrackerItems(trackerId,uploadedFile);

        return ResponseEntity.ok(result);
    }

    @Parameter(name="修改工作项系统字段")
    @RequestMapping(value = "/{itemId}/changeSystemField", method = RequestMethod.POST)
    ResponseEntity<Void> changeSystemField(@PathVariable Long itemId,@RequestBody(required = false) Map<String,Object> newValue) {
        itemService.systemFieldChanged(itemId,newValue.get("systemProperty").toString(),newValue.get("newValue"));
        return ResponseEntity.ok().build();
    }

    @Parameter(name="关联工作项")
    @RequestMapping(value = "/{itemId}/link", method = RequestMethod.POST)
    ResponseEntity<Void> createTrackerLink(@PathVariable Long itemId, @RequestBody List<TrackerLinkVo> trackerLinkVo) {
        itemService.batchCreateTrackerLinks(itemId,trackerLinkVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除关联工作项")
    @RequestMapping(value = "/{itemId}/link", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteTrackerLink(@PathVariable Long itemId,@RequestBody TrackerLinkVo trackerLinkVo) {
        itemService.deleteTrackerLink(itemId,trackerLinkVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="修改关联工作项")
    @RequestMapping(value = "/{itemId}/link", method = RequestMethod.PUT)
    ResponseEntity<Boolean> updateTrackerLink(@PathVariable Long itemId,@RequestBody TrackerLinkVo trackerLinkVo) {
        boolean re= itemService.updateTrackerLink(itemId,trackerLinkVo);
        return ResponseEntity.ok(re);
    }

    @Parameter(name="查询链接")
    @RequestMapping(value = "/{itemId}/hyperlink", method = RequestMethod.GET)
    ResponseEntity<List<HyperlinkEntity>> findHyperlinkByObjectId(@PathVariable Long itemId) {
        List<HyperlinkEntity> hyperlinkEntities = itemService.findHyperlinkByObjectId(itemId);
        return ResponseEntity.ok(hyperlinkEntities);
    }
    @Parameter(name="添加链接")
    @RequestMapping(value = "/{itemId}/hyperlink", method = RequestMethod.PUT)
        ResponseEntity<HyperlinkEntity> updateHyperlink(@PathVariable Long itemId, @RequestBody HyperlinkEntity hyperlink) {
        hyperlink.setObjectId(itemId);
        HyperlinkEntity hyperlinkEntity = itemService.updateHyperlink(hyperlink);
        return ResponseEntity.ok(hyperlinkEntity);
    }

    @Parameter(name="删除关联链接")
    @RequestMapping(value = "/{hyperlinkId}/hyperlink", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteHyperlink(@PathVariable Long hyperlinkId) {
        itemService.deleteHyperlink(hyperlinkId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="关注工作项")
    @RequestMapping(value = "/{itemId}/add-watch", method = RequestMethod.PUT)
    ResponseEntity<Void> addWatch(@PathVariable Long itemId,@RequestBody BaseIdentity watch) {
        itemService.watch(itemId,watch);
        return ResponseEntity.ok().build();
    }
    @Parameter(name="取消关注")
    @RequestMapping(value = "/{itemId}/cancel-watch", method = RequestMethod.PUT)
    ResponseEntity<Void> cancelWatch(@PathVariable Long itemId,@RequestBody BaseIdentity watch) {
        itemService.cancelWatch(itemId,watch);
        return ResponseEntity.ok().build();
    }


    @Parameter(name="关联Wiki")
    @RequestMapping(value = "/{itemId}/relatedWikis", method = RequestMethod.GET)
    ResponseEntity<Void> relatedWikis(@PathVariable Long itemId,@RequestParam List<Long> relatedWikiIds) {
        itemService.relatedWikis(itemId,relatedWikiIds);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="修改工作项自定义字段")
    @RequestMapping(value = "/{itemId}/changeCustomerField", method = RequestMethod.POST)
    ResponseEntity<Void> changeCustomerField(@PathVariable Long itemId,@RequestBody(required = false) Map<String,Object> newValue) {
        itemService.fieldChanged(itemId,Long.parseLong(newValue.get("fieldId").toString()),newValue.get("newValue"));
        return ResponseEntity.ok().build();
    }

    @Parameter(name="执行状态步骤")
    @RequestMapping(value = "/{itemId}/stateChange", method = RequestMethod.POST)
    ResponseEntity<TrackerStatus> stateChange(@PathVariable Long itemId,
                                           @RequestParam Long stateTransitionId) {
        TrackerStatus trackerStatus=itemService.stateChanged(itemId,stateTransitionId);
        return ResponseEntity.ok(trackerStatus);
    }


    @Parameter(name="查询工作项评论和变更记录列表")
    @RequestMapping(value = "/{itemId}/comments", method = RequestMethod.GET)
    ResponseEntity<List<TrackerLogVo>> findTrackerLogs(@PathVariable Long itemId,@RequestParam Boolean includeComment,
                                             @RequestParam Boolean includeChangeLog,
                                             @RequestParam Boolean includeAttachment,
                                             @RequestParam Boolean includeAssociation) {
        List<TrackerLogVo> logs = itemService.findCTrackerLogs(itemId, includeComment, includeChangeLog,
                                                               includeAttachment,
                                                               includeAssociation);
        return ResponseEntity.ok(logs);
    }

    @Parameter(name="发表评论")
    @RequestMapping(value = "/{itemId}/comments", method = RequestMethod.POST)
    ResponseEntity<Void> createComment(@PathVariable Long itemId, @RequestBody CommentVo comment) {
        itemService.createComment(comment);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新评论")
    @RequestMapping(value = "/{itemId}/comments", method = RequestMethod.PUT)
    ResponseEntity<Void> saveComment(@PathVariable Long itemId, @RequestBody CommentVo comment) {
        itemService.saveComment(comment);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="回复评论")
    @RequestMapping(value = "/{itemId}/comments/reply", method = RequestMethod.POST)
    ResponseEntity<Void> replyComment(@PathVariable Long itemId, @RequestBody CommentVo comment) {
        itemService.replyComment(comment);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除评论")
    @RequestMapping(value = "/{itemId}/comments", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteComment(@PathVariable Long itemId, @RequestBody CommentVo comment) {
        itemService.deleteComment(comment);
        return ResponseEntity.ok().build();
    }


    @Parameter(name="上传附件")
    @RequestMapping(value = "/{itemId}/attachments", method = RequestMethod.POST)
    ResponseEntity<AttachmentVo> uploadAttachment(@PathVariable Long itemId, @RequestBody AttachmentVo attachmentVo) {
        AttachmentVo vo =  itemService.uploadAttachment(itemId,attachmentVo);
        return ResponseEntity.ok(vo);
    }
    @Parameter(name="删除附件")
    @RequestMapping(value = "/{itemId}/attachments", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteAttachment(@PathVariable Long itemId, @RequestBody AttachmentVo attachmentVo) {
        itemService.deleteAttachment(itemId,attachmentVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询附件")
    @RequestMapping(value = "/{itemId}/attachments", method = RequestMethod.GET)
    ResponseEntity<List<AttachmentVo>> findAttachmentsByObjectId(@PathVariable Long itemId) {
        List<AttachmentVo> attachmentVos=itemService.findAttachmentsByObjectId(itemId);
        return ResponseEntity.ok(attachmentVos);
    }

    @Parameter(name="查询工时")
    @RequestMapping(value = "/{itemId}/workHours", method = RequestMethod.GET)
    ResponseEntity<List<WorkHoursVo>> findWorkHours(@PathVariable Long itemId) {
        List<WorkHoursVo> workHours = itemService.findWorkHours(itemId);
        return ResponseEntity.ok(workHours);
    }

    @Parameter(name="登记工时")
    @RequestMapping(value = "/{itemId}/workHours", method = RequestMethod.POST)
    ResponseEntity<Void> createWorkHours(@PathVariable Long itemId, @RequestBody WorkHoursEntity workHours) {
        itemService.createWorkHours(workHours);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="修改工时")
    @RequestMapping(value = "/{itemId}/workHours", method = RequestMethod.PUT)
    ResponseEntity<Void> saveWorkHours(@PathVariable Long itemId, @RequestBody WorkHoursEntity workHours) {
        itemService.saveWorkHours(workHours);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除工时")
    @RequestMapping(value = "/{itemId}/workHours", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteWorkHours(@PathVariable Long itemId, @RequestBody WorkHoursEntity workHours) {
        itemService.deleteWorkHours(workHours);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="成员剩余可登记时长")
    @RequestMapping(value = "/{memberId}/workHours/member", method = RequestMethod.GET)
    ResponseEntity<Double> remainingRegistrableTime(@PathVariable Long memberId, @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginDate) {
        Double rs=itemService.remainingRegistrableTime(memberId,beginDate);
        return ResponseEntity.ok(rs);
    }

    @Parameter(name="查询关联关系")
    @RequestMapping(value = "/associations", method = RequestMethod.GET)
    ResponseEntity<List<String>> findAssociations() {
        List<String> allAssociations = Arrays.stream(Associations.values()).map(Associations::getId).collect(
                Collectors.toList());
        return ResponseEntity.ok(allAssociations);
    }

    @Parameter(name="修改工作迭代")
    @RequestMapping(value = "/sprint", method = RequestMethod.PUT)
    ResponseEntity<Void> updateTrackerItemSprint(Long sprintId,@RequestBody List<Long> trackerItemIds) {
        itemService.updateTrackerItemSprint(sprintId,trackerItemIds);
        return ResponseEntity.ok().build();
    }
//    @Parameter(name="修改测试用例步骤")
//    @RequestMapping(value = "{itemId}/testStep", method = RequestMethod.PUT)
//    ResponseEntity<Void> updateTrackerItemTestStep(@PathVariable Long itemId,
//                                                   @RequestBody TrackerTestStep testStep) {
//        itemService.updateTrackerItemTestStep(itemId,testStep);
//        return ResponseEntity.ok().build();
//    }
//    @Parameter(name="修改测试用例步骤顺序")
//    @RequestMapping(value = "{itemId}/testSteps", method = RequestMethod.PUT)
//    ResponseEntity<Void> updateTrackerItemTestSteps(@PathVariable Long itemId,
//                                                   @RequestBody List<TrackerTestStep> testSteps) {
//        itemService.updateTrackerItemTestSteps(itemId,testSteps);
//        return ResponseEntity.ok().build();
//    }
    @Parameter(name="根据sprintId查询tracker")
    @RequestMapping(value = "/sprint/{sprintId}/trackers", method = RequestMethod.GET)
    ResponseEntity<List<TrackerEntity>> findTrackersBySprint(@PathVariable Long sprintId) {
        List<TrackerEntity> trackers=itemService.findTrackersBySprint(sprintId);
        return ResponseEntity.ok(trackers);
    }

    @Parameter(name="查询项目工作项日增加趋势图")
    @RequestMapping(value = "/cardInfo/{projectId}", method = RequestMethod.GET)
    ResponseEntity<ProjectCardInfo> cardInfo(@PathVariable Long projectId) {
        ProjectCardInfo data = itemService.findProjectCardInfo(projectId);
        return ResponseEntity.ok(data);
    }

    @Parameter(name="查询工作项关联关系")
    @RequestMapping(value = "/trackerLinks", method = RequestMethod.POST)
    ResponseEntity<List<TrackerLinkVo>> findTrackerLinksByItemIds(@RequestBody List<Long> itemIds) {
        List<TrackerLinkVo> data =itemService.findTrackerLinksByItemIds(itemIds);
        return ResponseEntity.ok(data);
    }

    @Parameter(name="批量更新工作项关联关系")
    @RequestMapping(value = "/updateMatrixLinks", method = RequestMethod.POST)
    ResponseEntity<Void> updateMatrixLinks(@RequestBody List<TrackerLinkEntity> trackerLinks) {
        itemService.updateMatrixLinks(trackerLinks);
        return ResponseEntity.ok().build();
    }
}
