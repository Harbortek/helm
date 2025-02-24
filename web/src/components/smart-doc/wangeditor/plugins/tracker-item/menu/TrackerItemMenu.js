/**
 * @description insert code-block menu
 */

import { Editor, Element, Transforms, Node, Range } from 'slate'
import { IButtonMenu, IDomEditor, DomEditor, t } from '@wangeditor/editor'
import { TrackerItemElement } from '../custom-types'

class CodeBlockMenu {
    constructor() {
        this.title = t('codeBlock.title')
        this.iconSvg = ''
        this.tag = 'button'
    }

    getSelectTrackerItemElem(editor) {
        const trackerItemNode = DomEditor.getSelectedNodeByType(editor, 'tracker-item')
        if (trackerItemNode == null) return null
        if (trackerItemNode == null) return null
        if (DomEditor.getNodeType(trackerItemNode) !== 'tracker-item') return null

        return trackerItemNode
    }

    getValue(editor) {
        const elem = this.getSelectTrackerItemElem(editor)
        if (elem == null) return ''
        return elem.ref || ''
    }

    isActive(editor) {
        const elem = this.getSelectTrackerItemElem(editor)
        return !!elem
    }

    isDisabled(editor) {
        const { selection } = editor
        if (selection == null) return true

        const selectedElems = DomEditor.getSelectedElems(editor)

        const hasVoid = selectedElems.some(elem => editor.isVoid(elem))
        if (hasVoid) return true

        const isMatch = selectedElems.some(elem => {
            const type = DomEditor.getNodeType(elem)
            if (type === 'tracker-item' || type === 'paragraph') return true
        })
        if (isMatch) return false
        return true
    }

    exec(editor, value) {
        const active = this.isActive(editor)
        if (active) {
            this.changeToPlainText(editor)
        } else {
            this.changeToCodeBlock(editor, value.toString())
        }
    }

    changeToPlainText(editor) {
        const elem = this.getSelectTrackerItemElem(editor)
        if (elem == null) return

        const str = Node.string(elem)

        Transforms.removeNodes(editor, { mode: 'highest' })

        const pList = str.split('\n').map(s => {
            return { type: 'paragraph', children: [{ text: s }] }
        })
        Transforms.insertNodes(editor, pList, { mode: 'highest' })
    }

    changeToCodeBlock(editor, language) {
        const strArr = []
        const nodeEntries = Editor.nodes(editor, {
            match: n => editor.children.includes(n),
            universal: true,
        })
        for (let nodeEntry of nodeEntries) {
            const [n] = nodeEntry
            if (n) strArr.push(Node.string(n))
        }

        Transforms.removeNodes(editor, { mode: 'highest' })

        const newPreNode = {
            type: 'pre',
            children: [
                {
                    type: 'code',
                    language,
                    children: [
                        { text: strArr.join('\n') },
                    ],
                },
            ],
        }
        Transforms.insertNodes(editor, newPreNode, { mode: 'highest' })
    }
}

export default CodeBlockMenu
