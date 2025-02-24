/**
 * 全局注册SVG为ICON组件
 * 使用方式： <a-icon :component="StringSvg"/>
 *
 */

import Vue from "vue";
const files = require.context("../../../public/static/fonts/svg", true, /\.svg$/);
// 筛选出svg文件夹下所有的svg文件，注意此时files是个函数，
// 包含3个静态属性：keys(函数，可以获取文件路径)、
// resolve(函数，可以传入keys返回的文件路径获取被解析后得到的模块 id，通常也是文件路径)
// id(context module 的模块 id. 它可能在你使用 module.hot.accept 时会用到)
// files函数可以传入keys返回的文件路径得到module对象，在module对象的default属性中保存了组件实例
files.keys().forEach((k) => {
  const module = files(k);
  const comInstance = module.default || module;
  const comName = comInstance.name || k.replace(/\.\/(.*)\.svg$/i, "$1");
  Vue.component(comName, comInstance);
});
