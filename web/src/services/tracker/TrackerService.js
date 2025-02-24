import { request, METHOD } from "@/utils/request";
export function findTrackers(parameter) {
  return request({
    url: "tracker/project/tracker/list",
    method: METHOD.GET,
    params: parameter,
  });
}
export function findOneTracker(trackerId) {
  return request({
    url: `tracker/project/tracker/${trackerId}`,
    method: METHOD.GET,
  });
}

export function findTrackerByIds(trackerIds) {
  // 根据 trackerIds 查找 tracker
  if (trackerIds && trackerIds.length > 0) {
    return request({
      url:
        `tracker/project/tracker/findByIds?` +
        trackerIds.map((_) => `trackerIds=${_}`).join("&"),
      method: METHOD.GET,
    });
  } else {
    // 如果 trackerIds 为空或长度为0，直接返回一个空数组的 Promise
    return Promise.resolve([]);
  }
}

export function createTracker(parameter) {
  return request({
    url: "tracker/project/tracker",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateTracker(trackerId, parameter) {
  return request({
    url: `tracker/project/tracker/${trackerId}`,
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteTracker(trackerId) {
  return request({
    url: `tracker/project/tracker/${trackerId}`,
    method: METHOD.DELETE,
  });
}

export function updateTrackerOrdinary(trackers) {
  return request({
    url: `tracker/project/tracker/sort`,
    method: METHOD.POST,
    data: trackers,
  });
}

export function copyTracker(tracker) {
  return request({
    url: `tracker/project/tracker/copy`,
    method: METHOD.POST,
    data: tracker,
  });
}
export function copyTrackerFields(trackerId, fields) {
  return request({
    url: `tracker/project/tracker/${trackerId}/copy-fields`,
    method: METHOD.POST,
    data: fields,
  });
}

export function findTrackerFields(trackerId) {
  return request({
    url: `/tracker/project/tracker/${trackerId}/fields`,
    method: METHOD.GET,
  });
}
export function createTrackerField(trackerId, field) {
  return request({
    url: `/tracker/project/tracker/${trackerId}/fields`,
    method: METHOD.POST,
    data: field,
  });
}
export function updateTrackerField(trackerId, field) {
  return request({
    url: `/tracker/project/tracker/${trackerId}/fields`,
    method: METHOD.PUT,
    data: field,
  });
}
export function deleteTrackerField(trackerId, field) {
  return request({
    url: `/tracker/project/tracker/${trackerId}/fields`,
    method: METHOD.DELETE,
    data: field,
  });
}

export function updateTrackerLayout(trackerId, layout) {
  return request({
    url: `tracker/project/tracker/${trackerId}/updateLayout`,
    method: METHOD.PUT,
    data: layout,
  });
}

export function updateTrackerPermission(trackerId, rolePermission) {
  return request({
    url: `tracker/project/tracker/${trackerId}/updatePermission`,
    method: METHOD.PUT,
    data: rolePermission,
  });
}

export function findTrackerLayout(trackerId, layoutName) {
  return request({
    url: `tracker/project/tracker/${trackerId}/findLayout`,
    method: METHOD.GET,
    params: { layoutName: layoutName },
  });
}

export function createTrackerStatus(trackerId, trackerStatus) {
  return request({
    url: `tracker/project/tracker/${trackerId}/status`,
    method: METHOD.POST,
    data: trackerStatus,
  });
}
export function updateTrackerStatus(trackerId, trackerStatus) {
  return request({
    url: `tracker/project/tracker/${trackerId}/status`,
    method: METHOD.PUT,
    data: trackerStatus,
  });
}
export function deleteTrackerStatus(trackerId, trackerStatus) {
  return request({
    url: `tracker/project/tracker/${trackerId}/status`,
    method: METHOD.DELETE,
    data: trackerStatus,
  });
}

export function findTrackerStatus(trackerId) {
  return request({
    url: `tracker/project/tracker/${trackerId}/status`,
    method: METHOD.GET,
  });
}

export function sortTrackerStatus(trackerId, trackerStatusList) {
  return request({
    url: `tracker/project/tracker/${trackerId}/status/sort`,
    method: METHOD.POST,
    data: trackerStatusList,
  });
}

export function setInitialTrackerStatus(trackerId, trackerStatus) {
  return request({
    url: `tracker/project/tracker/${trackerId}/status/setInitial`,
    method: METHOD.POST,
    data: trackerStatus,
  });
}

export function trackerStatusUseByTransition(trackerId, trackerStatus) {
  return request({
    url: `tracker/project/tracker/${trackerId}/status/useByTransition`,
    method: METHOD.POST,
    data: trackerStatus,
  });
}

export function trackerStatusUseByWorkItem(trackerId, trackerStatus) {
  return request({
    url: `tracker/project/tracker/${trackerId}/status/useByWorkItem`,
    method: METHOD.POST,
    data: trackerStatus,
  });
}

export function replaceWorkItemStatus(
  trackerId,
  oldTrackerStatusId,
  newTrackerStatusId
) {
  return request({
    url: `tracker/project/tracker/${trackerId}/status/replaceWorkItemStatus`,
    method: METHOD.POST,
    params: {
      oldTrackerStatusId: oldTrackerStatusId,
      newTrackerStatusId: newTrackerStatusId,
    },
  });
}

export function updateTrackerStateTransitions(trackerId, transitions) {
  return request({
    url: `tracker/project/tracker/${trackerId}/stateTransitions`,
    method: METHOD.PUT,
    data: transitions,
  });
}

export function findTrackerStateTransitions(trackerId) {
  return request({
    url: `tracker/project/tracker/${trackerId}/stateTransitions`,
    method: METHOD.GET,
  });
}

export function createTrackerStateTransition(trackerId, stateTransition) {
  return request({
    url: `tracker/project/tracker/${trackerId}/transition`,
    method: METHOD.POST,
    data: stateTransition,
  });
}
export function updateTrackerStateTransition(trackerId, stateTransition) {
  return request({
    url: `tracker/project/tracker/${trackerId}/transition`,
    method: METHOD.PUT,
    data: stateTransition,
  });
}
export function deleteTrackerStateTransition(trackerId, stateTransition) {
  return request({
    url: `tracker/project/tracker/${trackerId}/transition`,
    method: METHOD.DELETE,
    data: stateTransition,
  });
}

export function renameTrackerStateTransition(trackerId, stateTransition) {
  return request({
    url: `tracker/project/tracker/${trackerId}/transition/rename`,
    method: METHOD.POST,
    data: stateTransition,
  });
}
export function grantTrackerStateTransitionPermitted(
  trackerId,
  transitionId,
  identity
) {
  return request({
    url: `tracker/project/tracker/${trackerId}/transition/grant`,
    method: METHOD.POST,
    params: { transitionId: transitionId },
    data: identity,
  });
}
export function unGrantTrackerStateTransitionPermitted(
  trackerId,
  transitionId,
  identity
) {
  return request({
    url: `tracker/project/tracker/${trackerId}/transition/unGrant`,
    method: METHOD.POST,
    params: { transitionId: transitionId },
    data: identity,
  });
}

export function findTrackerPermissions(trackerId) {
  return request({
    url: `tracker/project/tracker/${trackerId}/permissions`,
    method: METHOD.GET,
  });
}

export function findTrackerPermissionsDefault() {
  return request({
    url: `tracker/project/tracker/permissions/default`,
    method: METHOD.GET,
  });
}

export function grantTrackerPermission(trackerId, permission, identity) {
  return request({
    url: `tracker/project/tracker/${trackerId}/permissions/grant`,
    method: METHOD.POST,
    params: { permissionName: permission },
    data: identity,
  });
}
export function unGrantTrackerPermission(trackerId, permission, identity) {
  return request({
    url: `tracker/project/tracker/${trackerId}/permissions/unGrant`,
    method: METHOD.POST,
    params: { permissionName: permission },
    data: identity,
  });
}

export function findSystemEventDefault() {
  return request({
    url: `tracker/project/tracker/notifications/system/default`,
    method: METHOD.GET,
  });
}

export function subscribeSystemEvent(trackerId, eventName, identity) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/system/subscribe`,
    method: METHOD.POST,
    params: { eventName: eventName },
    data: identity,
  });
}
export function unSubscribeSystemEvent(trackerId, eventName, identity) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/system/unsubscribe`,
    method: METHOD.POST,
    params: { eventName: eventName },
    data: identity,
  });
}
export function subscribeCustomerEvent(trackerId, fieldId, identity) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/customer/subscribe`,
    method: METHOD.POST,
    params: { fieldId: fieldId },
    data: identity,
  });
}
export function unSubscribeCustomerEvent(trackerId, fieldId, identity) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/customer/unsubscribe`,
    method: METHOD.POST,
    params: { fieldId: fieldId },
    data: identity,
  });
}
export function subscribeAllCustomerEvent(trackerId, identity) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/customer/all/subscribe`,
    method: METHOD.POST,
    data: identity,
  });
}
export function unSubscribeAllCustomerEvent(trackerId, identity) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/customer/all/unsubscribe`,
    method: METHOD.POST,
    data: identity,
  });
}
export function createCustomerNotification(trackerId, notification) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/customer/`,
    method: METHOD.POST,
    data: notification,
  });
}
export function deleteCustomerNotification(trackerId, notification) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/customer/`,
    method: METHOD.DELETE,
    data: notification,
  });
}

export function updateSystemEventNoticeType(
  trackerId,
  eventName,
  notification
) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/system/noticeType`,
    method: METHOD.POST,
    params: { eventName: eventName },
    data: notification,
  });
}
export function updateCustomerEventNoticeType(
  trackerId,
  fieldId,
  notification
) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/customer/noticeType`,
    method: METHOD.POST,
    params: { fieldId: fieldId },
    data: notification,
  });
}
export function updateAllCustomerEventNoticeType(trackerId, notification) {
  return request({
    url: `tracker/project/tracker/${trackerId}/notifications/customer/all/noticeType`,
    method: METHOD.POST,
    data: notification,
  });
}
export function updateTrackerName(trackerId, tracker) {
  return request({
    url: `tracker/project/tracker/${trackerId}/nameAndType`,
    method: METHOD.PUT,
    data: tracker,
  });
}