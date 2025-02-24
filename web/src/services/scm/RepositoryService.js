import { request, METHOD } from "@/utils/request";
import Cookies from "js-cookie";
export function createRopsitory(repo) {
  return request({
    url: `/scm/repository/`,
    method: METHOD.POST,
    data: repo,
  });
}
export function enableRopsitory(id) {
  return request({
    url: `/scm/repository/${id}/enable`,
    method: METHOD.POST,
  });
}
export function findRopsitory(id) {
  return request({
    url: `/scm/repository/${id}`,
    method: METHOD.GET,
  });
}
export function updateRopsitory(repo) {
  return request({
    url: `/scm/repository/${repo.id}`,
    method: METHOD.PUT,
    data: repo,
  });
}
export function findRopsitories() {
  return request({
    url: `/scm/repository/list`,
    method: METHOD.GET,
  });
}
export function deleteRopsitory(id) {
  return request({
    url: `/scm/repository/${id}`,
    method: METHOD.DELETE,
  });
}

export function findProjects(id) {
  return request({
    url: `/scm/repository/${id}/projects`,
    method: METHOD.GET,
  });
}

export function bindProject(id, projectRepo) {
  return request({
    url: `/scm/repository/${id}/bind`,
    method: METHOD.POST,
    data: projectRepo,
  });
}

export function unbindProject(id, projectId) {
  return request({
    url: `/scm/repository/${id}/unbind`,
    method: METHOD.POST,
    params: { projectId: projectId },
  });
}

export function findCurrentBindProject(projectId) {
  return request({
    url: `/scm/repository/current`,
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}

export function resetWebHook(id) {
  return request({
    url: `/scm/repository/${id}/resetWebHook`,
    method: METHOD.POST,
  });
}

export function checkRepositoryUsedInProject(id) {
  return request({
    url: `/scm/repository/${id}/resetWebHook`,
    method: METHOD.GET,
  });
}

export function findFiles(projectId, path, branchName, parentId) {
  return request({
    url: `/scm/code/${projectId}/files`,
    method: METHOD.GET,
    params: { path: path, branchName: branchName, parentId: parentId },
  });
}

export function findFileContent(projectId, path, branchName) {
  return request({
    url: `/scm/code/${projectId}/fileContent`,
    method: METHOD.GET,
    params: { path: path, branchName: branchName },
  });
}

export function findBranches(projectId, keyword) {
  return request({
    url: `/scm/code/${projectId}/branches`,
    method: METHOD.GET,
    params: { keyword: keyword },
  });
}

export function findTags(projectId, keyword) {
  return request({
    url: `/scm/code/${projectId}/tags`,
    method: METHOD.GET,
    params: { keyword: keyword },
  });
}

export function findProjectInfo(projectId) {
  return request({
    url: `/scm/code/${projectId}/project`,
    method: METHOD.GET,
  });
}

export function findLastCommit(projectId, path, branchName) {
  return request({
    url: `/scm/code/${projectId}/lastCommit`,
    method: METHOD.GET,
    params: { path: path, branchName: branchName },
  });
}

export function fileDownload(projectId, path, branchName) {
  // const accessToken = localStorage.get(ACCESS_TOKEN)
  const accessToken = Cookies.get("Authorization");
  const downloadUrl =
    process.env.VUE_APP_API_BASE_URL +
    `/scm/code/${projectId}/fileDownload?access_token=${accessToken}&path=${path}&branchName=${branchName}`;
  var iframe = document.createElement("iframe");
  iframe.style.display = "none";
  iframe.src = downloadUrl;
  iframe.onload = function () {
    document.body.removeChild(iframe);
  };
  document.body.appendChild(iframe);
}
