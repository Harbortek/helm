const BusPlugin = {
  install(Vue, options) {
    const vue = new Vue()
    Vue.prototype.$bus = vue
  }
}
export default BusPlugin 