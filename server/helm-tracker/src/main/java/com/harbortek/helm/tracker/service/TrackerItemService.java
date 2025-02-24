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

package com.harbortek.helm.tracker.service;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.tracker.entity.log.HyperlinkEntity;
import com.harbortek.helm.tracker.entity.log.WorkHoursEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public interface TrackerItemService {
    Page<TrackerItemVo> findTrackerItems(Long projectId, Long trackerId, Long sprintId, ObjectFilter filter,
                                         String keyword, Boolean isTest, Pageable pageable);
    Page<TrackerItemVo> findTrackerItemsTree(Long projectId, Long trackerId, Long sprintId, ObjectFilter filter,
                                             String keyword, Boolean LinkDire, Long linkType, Integer linkLevel, Pageable pageable);

    TrackerItemVo createTrackerItem(TrackerItemVo trackerItemVo);

    void updateTrackerItem(TrackerItemVo trackerItemVo);

    TrackerItemVo findOneTrackerItem(Long itemId);

    List<TrackerItemVo> findTrackerItemByIds(List<Long> itemIds);
    List<TrackerItemVo> findByTrackerIds(Long projectId, List<Long> trackerIds);
    void deleteOneTrackerItem(Long itemId);

    void batchDeleteTrackerItem(List<Long> itemIds);

    TrackerStatus stateChanged(Long itemId, Long stateTransitionId);

    void fieldChanged(Long itemId, Long fieldId, Object newValue);

    void systemFieldChanged(Long itemId, String systemProperty, Object newValue);

    List<TrackerLogVo> findCTrackerLogs(Long itemId, Boolean includeComment, Boolean includeChangeLog, Boolean includeAttachment, Boolean includeAssociation);

    void createComment(CommentVo comment);

    void saveComment(CommentVo comment);

    void deleteComment(CommentVo comment);

    void createWorkHours(WorkHoursEntity workHours);

    void saveWorkHours(WorkHoursEntity workHours);

    void deleteWorkHours(WorkHoursEntity workHours);

    void replyComment(CommentVo comment);

    AttachmentVo uploadAttachment(Long itemId, AttachmentVo attachmentVo);

    void deleteAttachment(Long itemId, AttachmentVo attachmentVo);

    List<AttachmentVo> findAttachmentsByObjectId(Long objectId);

    void batchCreateTrackerLinks(Long itemId, List<TrackerLinkVo> trackerLink);
    void deleteTrackerLink(Long itemId, TrackerLinkVo trackerLink);
    boolean updateTrackerLink(Long itemId, TrackerLinkVo trackerLink);
    List<HyperlinkEntity> findHyperlinkByObjectId(Long objectId);
    HyperlinkEntity updateHyperlink(HyperlinkEntity hyperlink);
    void deleteHyperlink(Long hyperlinkId);

    Double remainingRegistrableTime(Long memberId, Date beginDate);

    List<WorkHoursVo> findWorkHours(Long itemId);

    void updateTrackerItemSprint(Long sprintId,List<Long> trackerItemIds);

    void relatedWikis(Long itemId, List<Long> relatedWikiIds);

    void exportTrackerItems(HttpServletRequest request, HttpServletResponse response, Long projectId, Long trackerId, Long sprintId, List<Long> selectedRowIds, ObjectFilter filter, PageRequest of);

    void downloadImportDome(HttpServletRequest request, HttpServletResponse response, Long trackerId);

    ExcelLogs importTrackerItems(Long trackerId, String uploadedFile);

    void watch(Long itemId, BaseIdentity watch);

    void cancelWatch(Long itemId, BaseIdentity watch);

    void batchUpdateTrackerItem(List<TrackerItemVo> trackerItemVos);

    List<TrackerEntity> findTrackersBySprint(Long sprintId);

    ProjectCardInfo findProjectCardInfo(Long projectId);

//    void updateTrackerItemTestStep(Long itemId, TrackerTestStep testStep);
//
//    void updateTrackerItemTestSteps(Long itemId, List<TrackerTestStep> testSteps);

    List<FilterCondition> findGroupItems(Long projectId, Long trackerId, Long sprintId, ObjectFilter filter, String keyword);

    void deleteTrackerItemsByTrackerId(Long trackerId);

    List<TrackerLinkVo> findTrackerLinksByItemIds(List<Long> itemIds);

    void updateMatrixLinks(List<TrackerLinkEntity> trackerLinks);


     void setFieldValue(TrackerItemVo trackerItem, Long fieldId, Object value);
}
