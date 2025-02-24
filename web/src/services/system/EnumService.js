import { request, METHOD } from "@/utils/request";

export function findEnumCategories(keyword, projectId, page, size) {
  return request({
    url: "/sys/enum/category/list",
    method: METHOD.GET,
    params: { keyword: keyword, projectId: projectId, page: page, size: size },
  });
}

export function createEnumCategory(category) {
  return request({
    url: "/sys/enum/category",
    method: METHOD.POST,
    data: category,
  });
}

export function updateEnumCategory(category) {
  return request({
    url: "/sys/enum/category",
    method: METHOD.PUT,
    data: category,
  });
}

export function deleteEnumCategory(categoryId) {
  return request({
    url: `/sys/enum/category/${categoryId}`,
    method: METHOD.DELETE,
  });
}

export function isExistsByName(projectId, name) {
  return request({
    url: "/sys/enum/category/existsName",
    method: METHOD.GET,
    params: { name: name, projectId: projectId },
  });
}
export function findEnums(params) {
  return request({
    url: "/sys/enum/list",
    method: METHOD.GET,
    params: params,
  });
}
export function updateEnums(categoryId, projectId, enums) {
  return request({
    url: "/sys/enum",
    method: METHOD.POST,
    params: { categoryId: categoryId, projectId: projectId },
    data: enums,
  });
}

export function findEnumsByCode(categoryCode, projectId) {
  return request({
    url: "/sys/enum/listByCode",
    method: METHOD.GET,
    params: { categoryCode: categoryCode, projectId: projectId },
  });
}

export function testScript(enumCategory) {
  return request({
    url: "/sys/enum/testScript",
    method: METHOD.POST,
    data: enumCategory,
  });
}

export function findEnumItemById(itemId) {
  return request({
    url: "/sys/enum",
    method: METHOD.GET,
    params: { itemId: itemId },
  });
}
