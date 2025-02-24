import { request, METHOD } from "@/utils/request";
export function findProductLines(keyword) {
  return request({
    url: "/product-line/list",
    method: METHOD.GET,
    params: { keyword: keyword },
  });
}
export function findOneProductLine(id) {
  return request({
    url: "/product-line/" + id,
    method: METHOD.GET,
  });
}
export function createProductLine(parameter) {
  return request({
    url: "/product-line",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateProductLine(parameter) {
  return request({
    url: "/product-line",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteProductLine(id) {
  return request({
    url: "/product-line/" + id,
    method: METHOD.DELETE,
  });
}
