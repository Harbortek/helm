/**
 * @description helper fns
 * @author wangfupeng
 */

import Uppy, { UppyFile } from '@uppy/core'
import { IDomEditor } from '@wangeditor/editor'
import { createUploader } from '@wangeditor/editor'
import { UmlElement } from '../custom-types'

function getUmlMenuConfig(editor: IDomEditor) {
  // 获取配置，见 `./config.js`
  return editor.getMenuConfig('UML')
}

/**
 * 插入 attachment 节点
 * @param fileName fileName
 * @param link link
 */
export function insertUml(editor: IDomEditor, src: string, xml: string) {
  if (!xml) return

  // 还原选区
  editor.restoreSelection()

  // 插入节点
  const umlElem: UmlElement = {
    type: 'uml',
    xml,
    src,
    children: [{ text: '' }],
  }
  editor.insertNode(umlElem)
  editor.move(1)

}

