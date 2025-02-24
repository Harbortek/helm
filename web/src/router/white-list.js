/**
 * 路由白名单
 */
const paths = [`/login`]; //根据路由fullPath匹配
/**
 * 判断路由是否包含在该配置中
 * @param route vue-router 的 route 对象
 * @returns {boolean}
 */
function isInWhiteList(route) {
  return paths.includes(route.path);
}
export default isInWhiteList;
