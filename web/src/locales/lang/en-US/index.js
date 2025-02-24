// default lang
import system from './system'
import login from './login'
import menus from './menus'
import tracker from './tracker'
import smartpage from './smart-page'
import plan from './plan'
import scm from './scm'
import productline from './product-line'
import settings from './settings'
import vxetable from 'vxe-table/lib/locale/lang/en-US'
import global from "./global";
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
 }

