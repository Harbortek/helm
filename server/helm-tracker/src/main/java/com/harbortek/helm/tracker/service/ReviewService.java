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

import com.harbortek.helm.tracker.vo.review.ReviewSearchVo;
import com.harbortek.helm.tracker.vo.review.ReviewStatus;
import com.harbortek.helm.tracker.vo.review.ReviewVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    public Page<ReviewVo> findByProjectId(Long projectId, ReviewSearchVo reviewSearchVo, Pageable pageable) ;

    public Page<ReviewVo> findAll(ReviewSearchVo reviewSearchVo, Pageable pageable) ;

    public ReviewVo findOne(Long reviewId) ;

    public ReviewVo update(ReviewVo reviewVo) ;

    public void deleteOne(Long reviewId) ;

    void updateReviewStatus(Long reviewId, String status);

    void updateReviewDones(Long reviewId, Long id);

    void updateReviewersByReviewId(Long reviewId, List<Long> reviewers);

    void batchReviewResult(Long reviewId, List<ReviewStatus> reviewStatuses);
}
