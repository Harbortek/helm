import { request, METHOD } from "@/utils/request";

import Cookies from "js-cookie";

export function createOrganization(data) {
  return request({
    url: "/sys/org/create",
    method: METHOD.POST,
    data: data,
  });
}

export function deleteOrganization(orgId) {
  return request({
    url: `/sys/org/delete/${orgId}`,
    method: METHOD.POST,
  });
}

export function updateOrganization(data) {
  return request({
    url: "/sys/org/update",
    method: METHOD.POST,
    data: data,
  });
}

export function findOrganizationById(orgId) {
  return request({
    url: `/sys/org/findById/${orgId}`,
    method: METHOD.GET,
  });
}

export function findOrganizations(page, pageSize) {
  const paramData = {
    page: page,
    size: pageSize,
  };
  return request({
    url: "/sys/org/finadAll",
    method: METHOD.GET,
    params: paramData,
  });
}

export function findOrganizationsWithCount(page, pageSize) {
  const paramData = {
    page: page,
    pageSize: pageSize,
  };
  return request({
    url: "/sys/org/findOrganizationsWithCount",
    method: METHOD.GET,
    params: paramData,
  });
}

export function findOrganizationTree() {
  return request({
    url: "/sys/org/findOrganizationTree",
    method: METHOD.GET,
  });
}

export function moveUpOrganization(upOrgId) {
  return request({
    url: `/sys/org/moveUp/${upOrgId}`,
    method: METHOD.POST,
  });
}

export function moveDownOrganization(downOrgId) {
  return request({
    url: `/sys/org/moveDown/${downOrgId}`,
    method: METHOD.POST,
  });
}

export function importOrganizationCheck(uploadFile) {
  return request({
    url: "/sys/org/importcheck",
    method: METHOD.POST,
    params: { uploadFile: uploadFile },
  });
}

export function importOrganization(uploadFile) {
  return request({
    url: "/sys/org/import",
    method: METHOD.POST,
    params: { uploadFile: uploadFile },
  });
}

export function exportOrganization() {
  const accessToken = Cookies.get("Authorization");
  console.log(accessToken);
  const url =
    process.env.VUE_APP_API_BASE_URL +
    `/sys/org/export?access_token=${accessToken}`;
  var iframe = document.createElement("iframe");
  iframe.style.display = "none";
  iframe.src = url;
  iframe.onload = function () {
    document.body.removeChild(iframe);
  };
  document.body.appendChild(iframe);
}

export function importOrganizationTemplate() {
  return request({
    url: "/sys/org/template",
    method: METHOD.GET,
    responseType: "blob",
  });
}

export function isExistsByName(name) {
  return request({
    url: "/sys/org/exist-name/" + name,
    method: METHOD.GET,
  });
}
