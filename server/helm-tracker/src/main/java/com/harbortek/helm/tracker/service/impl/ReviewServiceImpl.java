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

import com.harbortek.helm.tracker.dao.ReviewDao;
import com.harbortek.helm.tracker.entity.review.ReviewEntity;
import com.harbortek.helm.tracker.service.ReviewService;
import com.harbortek.helm.tracker.vo.review.ReviewSearchVo;
import com.harbortek.helm.tracker.vo.review.ReviewStatus;
import com.harbortek.helm.tracker.vo.review.ReviewVo;
import com.harbortek.helm.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewDao reviewDao;

    @Override
    public Page<ReviewVo> findByProjectId(Long projectId, ReviewSearchVo reviewSearchVo, Pageable pageable) {
        Page<ReviewEntity> reviewEntityList = reviewDao.findByProjectId(projectId, reviewSearchVo,pageable);
        return DataUtils.toVo(reviewEntityList, ReviewVo.class);
    }

    @Override
    public Page<ReviewVo> findAll(ReviewSearchVo reviewSearchVo, Pageable pageable) {
        Page<ReviewEntity> reviewEntityList = reviewDao.findAll(reviewSearchVo,pageable);
        return DataUtils.toVo(reviewEntityList, ReviewVo.class);
    }


    @Override
    public ReviewVo findOne(Long reviewId) {
        return DataUtils.toVo(reviewDao.findOne(reviewId),ReviewVo.class);
    }

    @Override
    public ReviewVo update(ReviewVo reviewVo) {
        ReviewEntity reviewEntity = DataUtils.toEntity(reviewVo, ReviewEntity.class);
        return DataUtils.toVo(reviewDao.updateReview(reviewEntity), ReviewVo.class);
    }

    @Override
    public void deleteOne(Long reviewId) {
        reviewDao.deleteOne(reviewId);
    }

    @Override
    public void updateReviewStatus(Long reviewId, String status) {
        reviewDao.updateReviewStatus(reviewId,status);
    }

    @Override
    public void updateReviewDones(Long reviewId, Long id) {
        reviewDao.updateReviewDones(reviewId,id);
    }

    @Override
    public void updateReviewersByReviewId(Long reviewId,List<Long> reviewers) {
        reviewDao.updateReviewersByReviewId(reviewId,reviewers);
    }
    @Override
    public void batchReviewResult(Long reviewId, List<ReviewStatus> reviewStatuses) {
        ReviewEntity review = reviewDao.findOne(reviewId);

        List<ReviewStatus> reviewStatusList = review.getReviewStatuses();
        List<Long> ids = reviewStatusList.stream().map(ReviewStatus::getObjectId).collect(Collectors.toList());
        reviewStatuses.forEach(item->{
            int index = ids.indexOf(item.getObjectId());
            if(index==-1){
                reviewStatusList.add(item);
            }else{
                reviewStatusList.get(index).setStatusId(item.getStatusId());
                reviewStatusList.get(index).setDescription(item.getDescription());
                reviewStatusList.get(index).setReviewerId(item.getReviewerId());
            }
        });
        review.setReviewStatuses(reviewStatusList);
        reviewDao.updateReview(review);
    }
}
