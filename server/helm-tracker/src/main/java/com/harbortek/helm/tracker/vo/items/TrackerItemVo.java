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

package com.harbortek.helm.tracker.vo.items;

import com.harbortek.helm.common.entity.IdName;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.common.vo.IdNameVo;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.entity.log.HyperlinkEntity;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.log.AttachmentVo;
import com.harbortek.helm.tracker.vo.plan.SprintVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.JsonUtils;
import com.harbortek.helm.util.ObjectUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.*;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
public class TrackerItemVo extends BaseVo {

    Long parentId;

    IdNameReference<ProjectVo> project;

    IdNameReference<TrackerVo> tracker;

    String itemNo;

    IdNameReference<UserVo> owner;

    EnumItemVo priority;

    EnumItemVo severity;

    Long revision;

    TrackerStatus status;
    Long statusId;

    EnumItemVo meaning;

    /**
     * 计划开始时间
     */
    Date planStartDate;

    /**
     * 计划结束时间
     */
    Date planEndDate;

    /**
     * 实际开始时间
     */
    Date realStartDate;
    /**
     * 实际结束时间
     */
    Date realEndDate;

    /**
     * 进度
     */
    Integer progress;

    /**
     * 关闭时间
     */
    Date closeDate;

    /**
     * 预计花费工时
     */
    Double estimateWorkingHours;
    /**
     * 已登记工时
     */
    Double registeredWorkingHours;

    /**
     * 剩余工时
     */
    Double remainingWorkingHours;

    /**
     * 所属迭代
     */
    @Schema(hidden = true)
    IdNameReference<SprintVo> sprint;

    Map<Long, String> values = new HashMap<>();

    IdNameReference<UserVo> assignedTo;

    Date assignedDate;

//    Date dueDate;

    List<BaseIdentity> watchers = new ArrayList<>();

    List<TrackerLinkVo> relatedWorkItems = new ArrayList<>();

    List<Long> relatedWikis = new ArrayList<>();

    //关联文件
    List<AttachmentVo> attachments = new ArrayList<>();
    //关联链接
    List<HyperlinkEntity> hyperlinks = new ArrayList<>();

    String permission;

//    Date resolution;
//
//    Date resolvedOn;

    /**
     * 测试用例类型
     */
//    EnumItemVo testCaseType;

    /**
     * 测试用例步骤
     */
//    List<TrackerTestStep> testSteps = new ArrayList<>();

    /**
     * 测试用例前置条件
     */
//    String precondition;

    /**
     * 测试执行结果
     */
//    EnumItemVo result;
    /**
     * 测试用例执行结果备注
     */
//    String resultDesc;

    public Object getCustomerFieldValue(TrackerField field,Class<?> clazz) {
        return  JsonUtils.toObject( values.get(field.getId()), clazz);
    }

    public void setCustomerFieldValue(TrackerField field, Object value) {
        if (ObjectUtils.isNotEmpty(field)) {
            values.put(field.getId(), JsonUtils.toJSONString(value));
        }
    }


    public TrackerStatus getStatus() {
        if (this.tracker == null) {
            return null;
        }
        if (this.statusId == null) {
            return null;
        }
        if(this.tracker.getReferTo() != null&&
                this.tracker.getReferTo() instanceof TrackerVo){
            TrackerVo object = this.tracker.getReferTo();
            TrackerVo.class.isAssignableFrom(object.getClass());
            List<TrackerStatus> statuses = object.getTrackerStatuses();
            if (statuses == null || statuses.isEmpty()) {
                return null;
            }
            Optional<TrackerStatus> status =
                    statuses.stream().filter(item -> item.getId().equals(this.statusId)).findFirst();
            return status.orElse(null);
        }

        return null;
    }

    public String getIcon() {
        if (this.tracker == null) {
            return null;
        }
        Object object = this.tracker.getReferTo();
        if (object != null && IdName.class.isAssignableFrom(object.getClass())) {
            return ((IdName) object).getIcon();
        }
        return null;
    }

    public EnumItemVo getMeaning() {
        TrackerStatus status = getStatus();
        if (status != null) {
            return status.getMeaning();
        }
        return null;
    }

    public EnumItemVo getTrackerType() {
        if (this.tracker == null) {
            return null;
        }
        Object object = this.tracker.getReferTo();
        if (object != null && TrackerVo.class.isAssignableFrom(object.getClass())) {
            TrackerVo trackerVo = (TrackerVo) object;
            return trackerVo.getTrackerType();
        }
        return null;
    }

    /**
     * 检查是否有关联关系重复的工作项
     */
    public boolean hasDuplicateRelatedWorkItem(TrackerLinkVo compareLinkVo) {
        for (TrackerLinkVo linkVo : this.relatedWorkItems) {
            if (linkVo.getTargetItem().getId().equals(compareLinkVo.getTargetItem().getId())
                    && linkVo.getSourceItem().getId().equals(compareLinkVo.getSourceItem().getId())
                    && linkVo.getLinkType().getCode().equals(compareLinkVo.getLinkType().getCode())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "TrackerItemVo{" +
                "parentId=" + parentId +
                ", project=" + project +
                ", tracker=" + tracker +
                ", itemNo='" + itemNo + '\'' +
                ", owner=" + owner +
                ", priority=" + priority +
                ", severity=" + severity +
                ", revision=" + revision +
                ", status=" + status +
                ", statusId=" + statusId +
                ", meaning=" + meaning +
                ", planStartDate=" + planStartDate +
                ", planEndDate=" + planEndDate +
                ", realStartDate=" + realStartDate +
                ", realEndDate=" + realEndDate +
                ", progress=" + progress +
                ", closeDate=" + closeDate +
                ", estimateWorkingHours=" + estimateWorkingHours +
                ", registeredWorkingHours=" + registeredWorkingHours +
                ", remainingWorkingHours=" + remainingWorkingHours +
                ", sprint=" + sprint +
                ", values=" + values +
                ", assignedTo=" + assignedTo +
                ", assignedDate=" + assignedDate +
                ", watchers=" + watchers +
                ", relatedWorkItems=" + relatedWorkItems +
                ", relatedWikis=" + relatedWikis +
                ", attachments=" + attachments +
                ", hyperlinks=" + hyperlinks +
                '}';
    }
}
