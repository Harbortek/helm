/**
 * @description menu entry
 * @author wangfupeng
 */

import InsertUMLMenu from './InsertUml'
import EditUmlMenu from './EditUml'
import { genUmlMenuConfig } from './config'

export const insertUmlMenuConf = {
  key: 'insertUML', // menu key ，唯一。注册之后，可配置到工具栏
  factory() {
    return new InsertUMLMenu()
  },

  // 默认的菜单菜单配置，将存储在 editorConfig.MENU_CONF[key] 中
  // 创建编辑器时，可通过 editorConfig.MENU_CONF[key] = {...} 来修改
  config: genUmlMenuConfig(),
}

export const editUmlMenuConf = {
  key: 'editUML', // menu key ，唯一。注册之后，可配置到工具栏
  factory() {
    return new EditUmlMenu()
  },

  // 默认的菜单菜单配置，将存储在 editorConfig.MENU_CONF[key] 中
  // 创建编辑器时，可通过 editorConfig.MENU_CONF[key] = {...} 来修改
  config: genUmlMenuConfig(),
}


