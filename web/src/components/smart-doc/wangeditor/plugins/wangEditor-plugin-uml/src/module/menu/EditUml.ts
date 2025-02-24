/**
 * @description download attachment menu
 * @author wangfupeng
 */

import { DomEditor, IDomEditor, SlateRange, t } from '@wangeditor/editor'
import { IButtonMenu } from '@wangeditor/editor'
import { EDIT_SVG } from '../../constants/icon-svg'
import { UmlElement } from '../custom-types'

class EditUmlMenu implements IButtonMenu {
    readonly title = t('uml')
    readonly iconSvg = EDIT_SVG
    readonly tag = 'button'
    readonly alwaysEnable = true

    getValue(editor: IDomEditor): string | boolean {
        const umlElem = this.getSelectedElem(editor)
        if (umlElem == null) return ''
        return umlElem.xml
    }

    isActive(editor: IDomEditor): boolean {
        // 任何时候，都不用激活 menu
        return false
    }

    exec(editor: IDomEditor, value: string | boolean) {
        if (!value) return
        if (typeof value !== 'string') return
        function getObjectValue(obj: any, property: string) {
            return obj[property];
        }
        //预留，与editorjs 集成
        if ('tool' in editor) {
            const e = getObjectValue(editor, 'tool').api.events;
            e.emit('mxgraph-open', {
                isUpdate: true,
                xml: value,
                show: true
            });
        }
    }

    isDisabled(editor: IDomEditor): boolean {
        const { selection } = editor
        if (selection == null) return true
        if (SlateRange.isExpanded(selection)) return true // 选区非折叠，禁用

        // 未匹配到 attachment node 则禁用
        const umlElem = this.getSelectedElem(editor)
        if (umlElem == null) return true

        return false
    }

    private getSelectedElem(editor: IDomEditor): UmlElement | null {
        const node = DomEditor.getSelectedNodeByType(editor, 'uml')
        if (node == null) return null
        return node as UmlElement
    }
}

export default EditUmlMenu