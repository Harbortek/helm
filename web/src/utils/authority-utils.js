/**
 * 判断是否有路由的权限
 * @param authority 路由权限配置
 * @param permissions 用户权限集合
 * @returns {boolean|*}
 */
function hasPermission(authority, permissions) {
  let required = '*'
  if (typeof authority === 'string') {
    required = authority
  } else if (typeof authority === 'object') {
    required = authority.permission
  }
  return required === '*' || (permissions && permissions.findIndex(item => item === required || item.id === required) !== -1)
}


/**
 * 根据权限配置过滤菜单数据
 * @param menuData
 * @param permissions
 * @param roles
 */
function filterMenu(menuData, permissions, roles) {
  // return menuData.filter(menu => {
  //   if (menu.meta && menu.meta.invisible === undefined) {
  //     if (!hasAuthority(menu, permissions, roles)) {
  //       return false
  //     }
  //   }
  //   if (menu.children && menu.children.length > 0) {
  //     menu.children = filterMenu(menu.children, permissions, roles)
  //   }
  //   return true
  // })
  return menuData;
}

export {filterMenu}
