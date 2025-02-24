import { request, METHOD } from "@/utils/request";
export function findProducts(productLineId, keyword) {
  return request({
    url: `/product-line/${productLineId}/product/list`,
    method: METHOD.GET,
    params: { keyword: keyword },
  });
}
export function findOneProduct(productLineId, id) {
  return request({
    url: `/product-line/${productLineId}/product/` + id,
    method: METHOD.GET,
  });
}
export function createProduct(productLineId, parameter) {
  return request({
    url: `/product-line/${productLineId}/product/`,
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateProduct(productLineId, parameter) {
  return request({
    url: `/product-line/${productLineId}/product/`,
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteProduct(productLineId, id) {
  return request({
    url: `/product-line/${productLineId}/product/` + id,
    method: METHOD.DELETE,
  });
}

export function findAvailableProjects(productLineId, id) {
  return request({
    url: `/product-line/${productLineId}/product/` + id + "/project/list",
    method: METHOD.GET,
  });
}
