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

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.tracker.constants.ReviewStatusTypes;
import com.harbortek.helm.tracker.service.ReviewService;
import com.harbortek.helm.tracker.vo.review.ReviewSearchVo;
import com.harbortek.helm.tracker.vo.review.ReviewStatus;
import com.harbortek.helm.tracker.vo.review.ReviewVo;
import com.harbortek.helm.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/review")
public class ReviewApi {
    @Autowired
    private ReviewService reviewService;

    @Parameter(name="review 列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Page<ReviewVo>> findByProjectId(
            Pageable pageable, ReviewSearchVo reviewSearchVo) {
        Page<ReviewVo> page = reviewService.findAll( reviewSearchVo, pageable);
        return ResponseEntity.ok(page);
    }

    @Parameter(name="review  状态 列表")
    @RequestMapping(value = "/status-list", method = RequestMethod.GET)
    ResponseEntity<JSONArray> findReviewStatusByProjectId() {
        JSONArray list = new JSONArray() {{
            add(new JSONObject() {{
                put("value", ReviewStatusTypes.DONE);
                put("label", "完成");
            }});
            add(new JSONObject() {{
                put("value", ReviewStatusTypes.IN_PROGRESS);
                put("label", "进行中");
            }});

        }};
        return ResponseEntity.ok(list);
    }

    @Parameter(name="查询review")
    @RequestMapping(value = "/{reviewId}", method = RequestMethod.GET)
    ResponseEntity<ReviewVo> findOneReview(
            @PathVariable(value = "reviewId", required = false) Long reviewId) {
        ReviewVo reviewVo = reviewService.findOne(reviewId);
        return ResponseEntity.ok(reviewVo);
    }
    @Parameter(name="更新REVIEW的Reviewers")
    @RequestMapping(value = "/{reviewId}", method = RequestMethod.PUT)
    ResponseEntity<Void> updateReviewersByReviewId(
            @PathVariable(value = "reviewId") Long reviewId,@RequestBody  Long[] reviewers) {
        reviewService.updateReviewersByReviewId(reviewId, Arrays.stream(reviewers).collect(Collectors.toList()));
        return ResponseEntity.ok().build();
    }
    @Parameter(name="新增/修改review")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<ReviewVo> createOrUpdateReview(@RequestBody ReviewVo reviewVo) {
        ReviewVo vo = reviewService.update(reviewVo);
        return ResponseEntity.ok(vo);
    }

    @Parameter(name="删除review")
    @RequestMapping(value = "/{reviewId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteOneReview(@PathVariable Long reviewId) {
        reviewService.deleteOne(reviewId);
        return ResponseEntity.ok().build();
    }
    @Parameter(name="结束REVIEW")
    @RequestMapping(value = "/{reviewId}/finish", method = RequestMethod.POST)
    ResponseEntity<Void> finishReview(@PathVariable Long reviewId) {
        reviewService.updateReviewStatus(reviewId, ReviewStatusTypes.DONE);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="我已评审")
    @RequestMapping(value = "/{reviewId}/finish-by-me", method = RequestMethod.POST)
    ResponseEntity<Void> finishReviewByMe(@PathVariable Long reviewId) {
        reviewService.updateReviewDones(reviewId, SecurityUtils.getCurrentUser().getId());
        return ResponseEntity.ok().build();
    }

    @Parameter(name="批量设置评审结果")
    @RequestMapping(value = "/{reviewId}/batch-review-result", method = RequestMethod.POST)
    ResponseEntity<Void> batchReviewResult(@PathVariable Long reviewId, @RequestBody List<ReviewStatus> reviewStatuses) {
        reviewService.batchReviewResult(reviewId, reviewStatuses);
        return ResponseEntity.ok().build();
    }
}
