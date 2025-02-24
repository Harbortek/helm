// default lang
// import system from './system'
import login from "./login";
import settings from "./settings";
import menus from "./menus";
import global from "./global";
import system from "./system";
import tracker from "./tracker";
import smartpage from "./smart-page";
import plan from "./plan";
import scm from "./scm";
import productline from "./product-line";
import vxetable from "vxe-table/lib/locale/lang/zh-CN";
import config from "./config";
export default {
  ...login,
  ...settings,
  ...menus,
  ...system,
  ...tracker,
  ...smartpage,
  ...global,
  ...vxetable,
  ...plan,
  ...scm,
  ...productline,
  ...config,
};
