import { request, METHOD } from "@/utils/request";
import {
  findProjectUsers
} from "@/services/tracker/ProjectRoleMemberService";
export function findReviewsByProjectId(projectId, pageable, searchData) {
  return request({
    url: `/review/list?projectId=${projectId}`,
    method: METHOD.GET,
    params: {
      page: pageable.page,
      size: pageable.size,
      ...searchData
    },
  });
}
export function findAll(pageable, searchData) {
  return request({
    url: `/review/list`,
    method: METHOD.GET,
    params: {
      page: pageable.page,
      size: pageable.size,
      ...searchData
    },
  });
}
export function findOneReview(reviewId) {
  return request({
    url: `/review/${reviewId}`,
    method: METHOD.GET,
  });
}
export function updateOneReview(reviewVo) {
  return request({
    url: `/review`,
    method: METHOD.POST,
    data: reviewVo
  });
}
export function updateReviewersByReviewId(reviewId,reviewers) {
  return request({
    url: `/review/${reviewId}`,
    method: METHOD.PUT,
    data: reviewers
  });
}
export function deleteOneReview(reviewId) {
  return request({
    url: `/review/${reviewId}`,
    method: METHOD.DELETE
  });
}
export function findReviewStatusList() {
  return request({
    url: `/review/status-list`,
    method: METHOD.GET
  });
}
export function findUsers(projectId) {
  alert(projectId);
  return findProjectUsers(projectId)
}

export function finishReview(reviewId) {
  return request({
    url: `/review/${reviewId}/finish`,
    method: METHOD.POST
  });
}

export function finishReviewByMe(reviewId) {
  return request({
    url: `/review/${reviewId}/finish-by-me`,
    method: METHOD.POST
  });
}

export function batchReviewResult(reviewId,reviewStatuses) {
  return request({
    url: `/review/${reviewId}/batch-review-result`,
    method: METHOD.POST,
    data: reviewStatuses
  });
}
