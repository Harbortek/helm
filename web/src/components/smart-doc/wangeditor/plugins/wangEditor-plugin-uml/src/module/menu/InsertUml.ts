/**
 * @description upload attachment menu
 * @author wangfupeng
 */

import { DomEditor, IDomEditor, SlateRange, t } from '@wangeditor/editor'
import { IButtonMenu } from '@wangeditor/editor'
import { UML_SVG } from '../../constants/icon-svg'
import { insertUml } from './helper'

class InsertUMLMenu implements IButtonMenu {
  readonly title = t('uml')
  readonly iconSvg = UML_SVG
  readonly tag = 'button'

  getValue(editor: IDomEditor): string | boolean {
    // 无需获取 val
    return ''
  }

  isActive(editor: IDomEditor): boolean {
    // 任何时候，都不用激活 menu
    return false
  }

  exec(editor: IDomEditor, value: string | boolean) {
    const { } = this.getMenuConfig(editor)
    function getObjectValue(obj: any, property: string) {
      return obj[property];
    }
    //预留，与editorjs 集成
    if ('tool' in editor) {
      const e = getObjectValue(editor, 'tool').api.events;
      e.emit('mxgraph-open', {
        xml: '',
        isUpdate: false,
        show: true
      });
    }
  }

  isDisabled(editor: IDomEditor): boolean {
    const { selection } = editor
    if (selection == null) return true
    if (SlateRange.isExpanded(selection)) return true // 选区非折叠，禁用

    const selectedElems = DomEditor.getSelectedElems(editor)

    const hasVoidElem = selectedElems.some(elem => editor.isVoid(elem))
    if (hasVoidElem) return true // 选中了 void 元素，禁用

    const hasPreElem = selectedElems.some(elem => DomEditor.getNodeType(elem) === 'pre')
    if (hasPreElem) return true // 选中了 pre 原则，禁用

    return false
  }

  private getMenuConfig(editor: IDomEditor) {
    // 获取配置，见 `./config.js`
    return editor.getMenuConfig('insertUML')
  }
}

export default InsertUMLMenu
