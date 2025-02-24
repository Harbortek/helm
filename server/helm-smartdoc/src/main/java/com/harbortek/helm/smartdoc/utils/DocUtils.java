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

package com.harbortek.helm.smartdoc.utils;

import cn.hutool.json.JSONArray;
import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DocUtils {
    public static List<DocBlock> parseBlocks(String newBlocks) {
        List<DocBlock> docBlocks = new ArrayList<>();
        JSONArray blockArrays = null;
        try {
            blockArrays = new JSONArray(newBlocks);
            for (int i = 0; i < blockArrays.size(); i++) {
                String blockStr = blockArrays.getStr(i);
                DocBlock docBlock = DocBlock.newBlock(blockStr);
                docBlocks.add(docBlock);
            }
        } catch (Exception e) {
            throw new ServiceException("文档格式错误，请检查文档格式!");
        }
        return docBlocks;
    }

    public static boolean diff(String blocksJSON, String blocksJSON1) {
        return StringUtils.compare(blocksJSON,blocksJSON1)!=0;
    }

    public static void copyTrackerItem(TrackerItemVo src,TrackerItemVo dest){
        dest.setTracker(src.getTracker());
        dest.setName(src.getName());
        dest.setDescription(src.getDescription());
        dest.setProject(src.getProject());
//        dest.setTestCaseType(src.getTestCaseType());
        dest.setRemainingWorkingHours(src.getRemainingWorkingHours());
        dest.setRegisteredWorkingHours(src.getRegisteredWorkingHours());
        dest.setEstimateWorkingHours(src.getEstimateWorkingHours());
        dest.setCloseDate(src.getCloseDate());
        dest.setProgress(src.getProgress());
        dest.setRealEndDate(src.getRealEndDate());
        dest.setRealStartDate(src.getRealStartDate());
        dest.setPlanEndDate(src.getPlanEndDate());
        dest.setPlanStartDate(src.getPlanStartDate());
        dest.setPriority(src.getPriority());
        dest.setAssignedDate(src.getAssignedDate());
        dest.setAssignedTo(src.getAssignedTo());
        dest.setStatus(src.getStatus());
        dest.setStatusId(src.getStatusId());
        dest.setOwner(src.getOwner());
//        dest.setTestSteps(src.getTestSteps());
        dest.setValues(src.getValues());
    }

}
