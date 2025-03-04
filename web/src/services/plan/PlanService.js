import { request,download,   METHOD } from "@/utils/request";
import Cookies from "js-cookie";
export function buildGantt(projectId) {
  return request({
    url: "/tracker/plan/gantt",
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}

export function findPlans(projectId, keyword) {
  return request({
    url: "/tracker/plan/list",
    method: METHOD.GET,
    params: { projectId: projectId, keyword: keyword },
  });
}

export function autoPlan(projectId) {
  return request({
    url: "/tracker/plan/auto",
    method: METHOD.POST,
    params: { projectId: projectId },
  });
}

export function findMilestones(projectId) {
  return request({
    url: "/tracker/plan/milestones",
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}

export function findWaitExecutePlans(projectId) {
  return request({
    url: "/tracker/plan/waitExecute",
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}

export function findOnePlan(testRunId) {
  return request({
    url: `/tracker/plan/${testRunId}`,
    method: METHOD.GET,
  });
}
export function createPlan(parameter) {
  return request({
    url: "/tracker/plan",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updatePlan(parameter) {
  return request({
    url: "/tracker/plan",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deletePlan(id) {
  return request({
    url: "/tracker/plan/" + id,
    method: METHOD.DELETE,
  });
}
export function updateDeliverables(testRunId, data) {
  return request({
    url: `/tracker/plan/${testRunId}/deliverables`,
    method: METHOD.POST,
    data: data,
  });
}

export function uploadAttachment(deliverableId, attachment) {
  Object.assign(attachment, { objectId: deliverableId, type: "ATTACHMENT" });
  return request({
    url: `/tracker/plan/deliverable/${deliverableId}/attachments`,
    method: METHOD.POST,
    data: attachment,
  });
}

export function updateDeliverable(deliverableId, data) {
  return request({
    url: `/tracker/plan/deliverable/${deliverableId}`,
    method: METHOD.POST,
    data: data,
  });
}

export function findDeliverables(projectId) {
  return request({
    url: `/tracker/plan/deliverable/list`,
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}

export function associateSprints(testRunId, sprints) {
  return request({
    url: `/tracker/plan//${testRunId}/associateSprints`,
    method: METHOD.POST,
    data: sprints,
  });
}

export function dissociateSprints(testRunId, sprints) {
  return request({
    url: `/tracker/plan//${testRunId}/dissociateSprints`,
    method: METHOD.POST,
    data: sprints,
  });
}

export function associateTrackerItems(testRunId, trackerItems) {
  return request({
    url: `/tracker/plan//${testRunId}/associateTrackerItems`,
    method: METHOD.POST,
    data: trackerItems,
  });
}

export function dissociateTrackerItems(testRunId, trackerItems) {
  return request({
    url: `/tracker/plan//${testRunId}/dissociateTrackerItems`,
    method: METHOD.POST,
    data: trackerItems,
  });
}

export function changePlanOrders(plans) {
  return request({
    url: `/tracker/plan/changeOrder`,
    method: METHOD.POST,
    data: plans,
  });
}

export function importMPP(projectId, file) {
  return request({
    url: `/tracker/plan/import-ms-project`,
    method: METHOD.POST,
    params: { projectId: projectId, file: file },
  });
}

export function exportPlans(projectId) {

  let url =`/tracker/plan/export`;

  download(url, {projectId:projectId});
}
