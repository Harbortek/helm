import { request, METHOD } from "@/utils/request";
export function findTrackerItems(
  projectId,
  trackerId,
  sprintId,
  filter,
  keyword,
  pageable,
  sort,
  isTest
) {
  let config = {};
  return request({
    url:
      "/tracker/project/items/list?" + sort?.map((_) => `sort=${_}`).join("&"),
    method: METHOD.POST,
    params: {
      projectId: projectId,
      trackerId: trackerId,
      sprintId: sprintId,
      keyword: keyword,
      ...pageable,
      isTest,
    },
    data: filter,
  });
}

export function findTrackerItemsTree(
  projectId,
  trackerId,
  sprintId,
  filter,
  keyword,
  pageable,
  sort,
  linkDire,
  linkType,
  linkLevel,
) {
  let config = {};
  return request({
    url:
      "/tracker/project/items/list-tree?" + sort?.map((_) => `sort=${_}`).join("&"),
    method: METHOD.POST,
    params: {
      projectId: projectId,
      trackerId: trackerId,
      sprintId: sprintId,
      keyword: keyword,
      ...pageable,
      linkDire,
      linkType,
      linkLevel
    },
    data: filter,
  });
}


export function findGroupItems(
  projectId,
  trackerId,
  sprintId,
  filter,
  keyword
) {
  let config = {};
  return request({
    url: "/tracker/project/items/group",
    method: METHOD.POST,
    params: {
      projectId: projectId,
      trackerId: trackerId,
      sprintId: sprintId,
      keyword: keyword,
    },
    data: filter,
  });
}

export function exportTrackerItems(
  projectId,
  trackerId,
  sprintId,
  filter,
  selectedRowIds,
  pageable,
  sort
) {
  return request({
    url:
      "/tracker/project/items/export?" +
      sort.map((_) => `sort=${_}`).join("&") +
      "&" +
      selectedRowIds.map((_) => `selectedRowIds=${_}`).join("&"),
    method: METHOD.POST,
    params: {
      projectId: projectId,
      trackerId: trackerId,
      sprintId: sprintId,
      ...pageable,
    },
    data: filter,
    responseType: "blob",
  });
}
export function downloadImportDome(trackerId) {
  return request({
    url: `/tracker/project/items/import-demo`,
    method: METHOD.GET,
    params: {
      trackerId,
    },
    responseType: "blob",
  });
}
export function importTrackerItems(trackerId, uploadedFile) {
  return request({
    url: `/tracker/project/items/import`,
    method: METHOD.GET,
    params: {
      trackerId,
      uploadedFile,
    },
  });
}
export function findOneTrackerItem(itemId) {
  return request({
    url: `/tracker/project/items/${itemId}`,
    method: METHOD.GET,
  });
}
export function findTrackerItemByIds(itemIds) {
  return request({
    url:"/tracker/project/items/byIds",
    method: METHOD.POST,
    data: itemIds,
  });
}
export function createTrackerItem(parameter) {
  return request({
    url: "/tracker/project/items",
    method: METHOD.POST,
    data: parameter,
  });
}
export function batchUpdateTrackerItem(parameter) {
  return request({
    url: "/tracker/project/items/batch",
    method: METHOD.PUT,
    data: parameter,
  });
}

export function updateTrackerItem(parameter) {
  return request({
    url: "/tracker/project/items",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteTrackerItem(itemId) {
  return request({
    url: `/tracker/project/items/${itemId}`,
    method: METHOD.DELETE,
  });
}
export function deleteTrackerItemsByTrackerId(trackerId) {
  return request({
    url: `/tracker/project/items/deleteByTrackerId`,
    method: METHOD.DELETE,
    params: { trackerId: trackerId },
  });
}
export function batchDeleteTrackerItem(parameter) {
  return request({
    url: "/tracker/project/items/batch",
    method: METHOD.DELETE,
    data: parameter,
  });
}
export function createTrackerLink(itemId, newVal) {
  return request({
    url: `/tracker/project/items/${itemId}/link`,
    method: METHOD.POST,
    data: newVal,
  });
}
export function deleteTrackerLink(itemId, newVal) {
  return request({
    url: `/tracker/project/items/${itemId}/link`,
    method: METHOD.DELETE,
    data: newVal,
  });
}
export function findHyperlinkByObjectId(itemId) {
  return request({
    url: `/tracker/project/items/${itemId}/hyperlink`,
    method: METHOD.GET,
  });
}
export function updateHyperlink(itemId, newVal) {
  return request({
    url: `/tracker/project/items/${itemId}/hyperlink`,
    method: METHOD.PUT,
    data: newVal,
  });
}
export function deleteHyperlink(hyperlinkId) {
  return request({
    url: `/tracker/project/items/${hyperlinkId}/hyperlink`,
    method: METHOD.DELETE,
  });
}

export function updateTrackerLink(itemId, newVal) {
  return request({
    url: `/tracker/project/items/${itemId}/link`,
    method: METHOD.PUT,
    data: newVal,
  });
}
export function addWatch(itemId, newVal) {
  return request({
    url: `/tracker/project/items/${itemId}/add-watch`,
    method: METHOD.PUT,
    data: newVal,
  });
}
export function cancelWatch(itemId, newVal) {
  return request({
    url: `/tracker/project/items/${itemId}/cancel-watch`,
    method: METHOD.PUT,
    data: newVal,
  });
}

export function relatedWikis(itemId, newVal) {
  return request({
    url:
      `/tracker/project/items/${itemId}/relatedWikis?` +
      newVal.map((_) => `relatedWikiIds=${_}`).join("&"),
    method: METHOD.GET,
  });
}

export function changeSystemField(itemId, systemProperty, newValue) {
  return request({
    url: `/tracker/project/items/${itemId}/changeSystemField`,
    method: METHOD.POST,
    // params: { systemProperty: systemProperty},
    data: {systemProperty,newValue}
  });
}
export function changeCustomerField(itemId, fieldId, newValue) {
  return request({
    url: `/tracker/project/items/${itemId}/changeCustomerField`,
    method: METHOD.POST,
    // params: { fieldId: fieldId, newValue: newVal },
    data: {fieldId,newValue}
  });
}

export function stateChange(itemId, stateTransitionId) {
  return request({
    url: `/tracker/project/items/${itemId}/stateChange`,
    method: METHOD.POST,
    params: { stateTransitionId: stateTransitionId },
  });
}

export function findTrackerLogs(
  itemId,
  includeComment,
  includeChangeLog,
  includeAttachment,
  includeAssociation
) {
  return request({
    url: `/tracker/project/items/${itemId}/comments`,
    method: METHOD.GET,
    params: {
      includeComment: includeComment,
      includeChangeLog: includeChangeLog,
      includeAttachment: includeAttachment,
      includeAssociation: includeAssociation,
    },
  });
}
export function createComment(itemId, comment) {
  Object.assign(comment, { objectId: itemId, type: "COMMENT" });
  return request({
    url: `/tracker/project/items/${itemId}/comments`,
    method: METHOD.POST,
    data: comment,
  });
}
export function saveComment(itemId, comment) {
  Object.assign(comment, { objectId: itemId, type: "COMMENT" });
  return request({
    url: `/tracker/project/items/${itemId}/comments`,
    method: METHOD.PUT,
    data: comment,
  });
}
export function deleteComment(itemId, comment) {
  Object.assign(comment, { objectId: itemId, type: "COMMENT" });
  return request({
    url: `/tracker/project/items/${itemId}/comments`,
    method: METHOD.DELETE,
    data: comment,
  });
}
export function replyComment(itemId, comment) {
  Object.assign(comment, { objectId: itemId, type: "COMMENT" });
  return request({
    url: `/tracker/project/items/${itemId}/comments/reply`,
    method: METHOD.POST,
    data: comment,
  });
}

export function uploadAttachment(itemId, attachment) {
  Object.assign(attachment, { objectId: itemId, type: "ATTACHMENT" });
  return request({
    url: `/tracker/project/items/${itemId}/attachments`,
    method: METHOD.POST,
    data: attachment,
  });
}
export function deleteAttachment(itemId, attachment) {
  Object.assign(attachment, { objectId: itemId, type: "ATTACHMENT" });
  return request({
    url: `/tracker/project/items/${itemId}/attachments`,
    method: METHOD.DELETE,
    data: attachment,
  });
}
export function findAttachmentsByObjectId(itemId) {
  return request({
    url: `/tracker/project/items/${itemId}/attachments`,
    method: METHOD.GET,
  });
}

export function findWorkHours(itemId) {
  return request({
    url: `/tracker/project/items/${itemId}/workHours`,
    method: METHOD.GET,
  });
}
export function createWorkHours(itemId, workHours) {
  Object.assign(workHours, { objectId: itemId, type: "WORKHOURS" });
  return request({
    url: `/tracker/project/items/${itemId}/workHours`,
    method: METHOD.POST,
    data: workHours,
  });
}
export function saveWorkHours(itemId, workHours) {
  Object.assign(workHours, { objectId: itemId, type: "WORKHOURS" });
  return request({
    url: `/tracker/project/items/${itemId}/workHours`,
    method: METHOD.PUT,
    data: workHours,
  });
}
export function deleteWorkHours(itemId, workHours) {
  Object.assign(workHours, { objectId: itemId, type: "WORKHOURS" });
  return request({
    url: `/tracker/project/items/${itemId}/workHours`,
    method: METHOD.DELETE,
    data: workHours,
  });
}
export function remainingRegistrableTime(memberId, beginDate) {
  return request({
    url: `/tracker/project/items/${memberId}/workHours/member`,
    method: METHOD.GET,
    params: beginDate,
  });
}
export function findAssociations() {
  return request({
    url: `/tracker/project/items/associations`,
    method: METHOD.GET,
  });
}
export function updateTrackerItemSprint(sprintId, trackerItemIds) {
  return request({
    url: `/tracker/project/items/sprint`,
    method: METHOD.PUT,
    params: { sprintId },
    data: trackerItemIds,
  });
}
export function findTrackersBySprint(sprintId) {
  return request({
    url: `/tracker/project/items/sprint/${sprintId}/trackers`,
    method: METHOD.GET,
  });
}

export function cardInfo(projectId) {
  return request({
    url: `/tracker/project/items/cardInfo/${projectId}`,
    method: METHOD.GET,
  });
}

export function updateTrackerItemTestStep(itemId, testStep) {
  return request({
    url: `/tracker/project/items/${itemId}/testStep`,
    method: METHOD.PUT,
    data: testStep,
  });
}
export function updateTrackerItemTestSteps(itemId, testSteps) {
  return request({
    url: `/tracker/project/items/${itemId}/testSteps`,
    method: METHOD.PUT,
    data: testSteps,
  });
}
export function findTrackerLinksByItemIds(itemIds) {
  return request({
    url:
      "/tracker/project/items/trackerLinks",
    method: METHOD.POST,
    data: itemIds
  });
}
export function updateMatrixLinks(trackerLInks) {
  return request({
    url:
      "/tracker/project/items/updateMatrixLinks",
      method: METHOD.POST,
      data: trackerLInks,
  });
}