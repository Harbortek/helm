import { request, METHOD } from "@/utils/request";
export function createPipelineRepository(repo) {
  return request({
    url: `/pipeline/repository/`,
    method: METHOD.POST,
    data: repo,
  });
}
export function findPipelineRepository(id) {
  return request({
    url: `/pipeline/repository/${id}`,
    method: METHOD.GET,
  });
}
export function updatePipelineRepository(repo) {
  return request({
    url: `/pipeline/repository/`,
    method: METHOD.PUT,
    data: repo,
  });
}
export function findPipelineRepositories() {
  return request({
    url: `/pipeline/repository/list`,
    method: METHOD.GET,
  });
}
export function deletePipelineRepository(id) {
  return request({
    url: `/pipeline/repository/${id}`,
    method: METHOD.DELETE,
  });
}
export function testPipelineRepository(repo) {
  return request({
    url: `/pipeline/repository/test`,
    method: METHOD.POST,
    data: repo,
  });
}

export function findPipelines(repositoryId) {
  return request({
    url: `/pipeline/repository/${repositoryId}/pipelines`,
    method: METHOD.GET,
  });
}

export function findBindingPipelines(projectId) {
  return request({
    url: `/pipeline/project/${projectId}/pipelines`,
    method: METHOD.GET,
  });
}

export function bindingPipeline(projectId, pipelines) {
  return request({
    url: `/pipeline/project/${projectId}/bind`,
    method: METHOD.POST,
    data: pipelines,
  });
}

export function unbindingPipeline(projectId, pipelines) {
  return request({
    url: `/pipeline/project/${projectId}/unbind`,
    method: METHOD.POST,
    data: pipelines,
  });
}

export function findExecutionStages(repositoryId, pipeline, build) {
  return request({
    url: `/pipeline/job/stages`,
    method: METHOD.POST,
    data: { repositoryId: repositoryId, pipeline: pipeline, buildInfo: build },
  });
}

export function findExecutionLog(repositoryId, stage, step) {
  return request({
    url: `/pipeline/job/log`,
    method: METHOD.POST,
    data: { repositoryId: repositoryId, stage: stage, step: step },
  });
}
