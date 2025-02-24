/**
 * @description attachment module entry
 * @author wangfupeng
 */

import './local' // 多语言

import { IModuleConf } from '@wangeditor/editor'
import widthUml from './plugin'
import renderElemConf from './render-elem'
import elemToHtmlConf from './elem-to-html'
import parseHtmlConf from './parse-elem-html'
import { insertUmlMenuConf, editUmlMenuConf } from './menu/index'

const module: Partial<IModuleConf> = {
  editorPlugin: widthUml,
  renderElems: [renderElemConf],
  elemsToHtml: [elemToHtmlConf],
  parseElemsHtml: [parseHtmlConf],
  menus: [insertUmlMenuConf, editUmlMenuConf],
}

export default module
