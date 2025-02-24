//作为基座的app-helper类
import microApp from "@micro-zoe/micro-app";
/**
 * 发送数据给子应用
 * @param {string} subAppName  子应用名称 not null
 * @param {object} data 数据
 */
function send(subAppName, data) {
  data = data || {};
  microApp.setData(subAppName, data);
}
/**
 * 向基座应用和所有子应用发送数据
 * @param {object} data
 */
function sendGlobal(data) {
  data = data || {};
  microApp.setGlobalData(data);
}
/**
 * 从子应用获取数据
 * @param {string} subAppName 子应用名称 not null
 * @returns
 */
function receive(subAppName) {
  const childData = microApp.getData(subAppName);
  return childData;
}
/**
 * 获取全局数据
 * @returns
 */
function receiveGlobal() {
  const globalData = microApp.getGlobalData();
  return globalData;
}
/**
 * 绑定监听函数
 * @param {string} subAppName 子应用名称
 * @param {Function} dataListener 绑定函数
 * @param {boolean} autoTrigger  在初次绑定监听函数时如果有缓存数据，是否需要主动触发一次，默认为false
 */
function addDataListener(subAppName, dataListener, autoTrigger) {
  autoTrigger = autoTrigger || false;
  microApp.addDataListener(subAppName, dataListener, autoTrigger);
}
/**
 * 清空所有监听subAppName子应用的函数
 * @param {string} subAppName
 */
function clearDataListener(subAppName) {
  microApp.clearDataListener(subAppName);
}
/**
 * 绑定Global监听函数
 * @param {Function} dataListener 绑定函数
 * @param {boolean} autoTrigger  在初次绑定监听函数时如果有缓存数据，是否需要主动触发一次，默认为false
 */
function addGlobalDataListener(dataListener, autoTrigger) {
  autoTrigger = autoTrigger || false;
  microApp.addGlobalDataListener(dataListener, autoTrigger);
}
/**
 * 解绑Global监听函数
 * @param {Function} dataListener
 */
function removeGlobalDataListener(dataListener) {
  microApp.removeGlobalDataListener(dataListener);
}
/**
 * 清空基座应用绑定的所有全局数据监听函数
 */
function clearGlobalDataListener() {
  microApp.clearGlobalDataListener();
}
/**
 * 通信包
 */
const COMM = {
  send,
  sendGlobal,
  receive,
  receiveGlobal,
  addDataListener,
  clearDataListener,
  addGlobalDataListener,
  removeGlobalDataListener,
  clearGlobalDataListener,
};
/**
 * 状态包
 */
const STATE = {
  /**
   * 用户
   */
  user() {},
  /**
   * 权限
   */
  permissions() {},
};
export {
  COMM,
  STATE,
};
