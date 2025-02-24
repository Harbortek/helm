import Vue from 'vue'
import store from '@/store'
// import actions from './action'
import actions from './action/index'
/**
 * Action 权限指令
 * 指令用法：
 *  - 在需要控制 action 级别权限的组件上使用 v-action:[method] , 如下：
 *    <i-button v-action:add >添加用户</a-button>
 *    <a-button v-action:delete>删除用户</a-button>
 *    <a v-action:edit @click="edit(record)">修改</a>
 *
 *  - 当前用户没有权限时，组件上使用了该指令则会被隐藏
 *  - 当后台权限跟 pro 提供的模式不同时，只需要针对这里的权限过滤进行修改即可
 *
 *  @see https://github.com/vueComponent/ant-design-vue-pro/pull/53
 */
// const action = Vue.directive('action', {
//   inserted: function (el, binding, vnode) {
//     const perm = actions[binding.arg]
//     const userPerms = store.getters['account/user'].permissions||[]
//     if (!userPerms.includes(perm)) {
//       el.parentNode && el.parentNode.removeChild(el) || (el.style.display = 'none')
//     }
//   }
// })
const action = Vue.directive('action', {
  inserted: function (el, binding, vnode) {
    const perm = binding.value
    const permSplit=perm?.split("|") //WRITE-pageId
    const permissions = store.getters['account/user'].permissions||[]
    if(permSplit.length > 0){
      let matched = permissions?.find((v) => {
        if (v.name === permSplit[0]) {
          if (permSplit.length>1&&permSplit[1]) {
            return v.resourceId === permSplit[1];
          }
          return true;
        } else {
          return false;
        }
      });
      if(matched){
        return;
      }
    }
    el.parentNode && el.parentNode.removeChild(el) || (el.style.display = 'none')
  }
})

export default action
