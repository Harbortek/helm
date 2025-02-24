/**
 * @description editor 插件，重写 editor API
 */

import { IDomEditor, DomEditor, SlatePoint,SlateElement,SlateTransforms } from "@wangeditor/editor";
const TITLE = 'title';
/**
 * 判断该 location 有没有命中 tracker-item
 * @param editor editor
 * @param location location
 */
function checkLocation(editor, location, checkType) {
    const trackerItems = Editor.nodes(editor, {
        at: location,
        match: n => {
            const type = DomEditor.getNodeType(n)
            return type === checkType
        },
    })
    let hasTracekrItem = false
    for (const table of trackerItems) {
        hasTracekrItem = true // 找到了 table
    }
    return hasTracekrItem
}
function checkDeleteHandlerStart(newEditor, checkType) {
    const { selection } = newEditor
    if (selection == null) return false

    const [cellNodeEntry] = Editor.nodes(newEditor, {
        match: n => DomEditor.checkNodeType(n, checkType),
    })
    if (cellNodeEntry) {
        const [, cellPath] = cellNodeEntry
        const start = DomEditor.start(newEditor, cellPath);

        if (SlatePoint.equals(selection.anchor, start)) {
            return true // 阻止删除 cell
        }
    }

    return false
}
function checkDeleteHandlerEnd(newEditor, checkType) {
    const { selection } = newEditor
    if (selection == null) return false

    const [cellNodeEntry] = Editor.nodes(newEditor, {
        match: n => DomEditor.checkNodeType(n, checkType),
    })
    if (cellNodeEntry) {
        const [, cellPath] = cellNodeEntry
        const end = DomEditor.end(newEditor, cellPath);

        if (SlatePoint.equals(selection.anchor, end)) {
            return true // 阻止删除 cell
        }
    }

    return false
}
function withTitle(editor) {
    const { deleteBackward, deleteForward, insertBreak, normalizeNode, insertData, insertNode, isVoid } = editor
    const newEditor = editor
    newEditor.insertBreak = () => {
        const [match] = Editor.nodes(newEditor, {
            match: n => {
                const type = DomEditor.getNodeType(n)
                return type == TITLE // 匹配 node.type 是 title 的 node
            },
            universal: true,
        })

        if (!match) {
            // 未匹配到
            insertBreak()
            return
        }
        const isAtLineEnd = DomEditor.isSelectionAtLineEnd(editor, match[1])

        // 如果在行末插入一个空 p，否则正常换行
        if (isAtLineEnd) {
            const p = { type: 'paragraph', children: [{ text: '' }] }
            SlateTransforms.insertNodes(newEditor, p, { mode: 'highest' })
        } else {
            insertBreak()
        }

        // 返回 editor ，重要！
        return newEditor
    }

    // 删除非 p 的文本 elem（如 header blockQuote 等），删除没有内容时，切换为 p
    newEditor.deleteBackward = unit => {

        const res = checkDeleteHandlerStart(newEditor, TITLE)
        if (res) return // 命中 TITLE ，自己处理删除

        // const { selection } = newEditor
        // if (selection) {
        //     //1. 防止从 TITLE 后面的 p 删除时，删除最后一个 desc
        //     const before = Editor.before(newEditor, selection) // 前一个 location
        //     if (before) {
        //         const isTitleOnBeforeLocation = checkLocation(newEditor, before, TITLE) // before 是否是 table
        //         const isTitleOnCurSelection = checkLocation(newEditor, selection, TITLE) // 当前是否是 tracker-item
        //         if (isTitleOnBeforeLocation && !isTitleOnCurSelection) {
        //             return // 如果当前不是 table ，前面是 table ，则不执行删除。否则会删除 table 最后一个 cell
        //         }
        //     }
        // }
        deleteBackward(unit)
    }
    newEditor.deleteForward = unit => {

        const { selection } = newEditor;
        const res = checkDeleteHandlerEnd(newEditor, TITLE)
        if (res) return
        if (selection) {
            //1. 防止从 TITLE 前面的 p 删除时，删除最后一个 desc
            const after = Editor.after(newEditor, selection) // 前一个 location
            if (after) {
                const isTitleOnAfterLocation = checkLocation(newEditor, after, TITLE) // before 是否是 table
                const isTitleOnCurSelection = checkLocation(newEditor, selection, TITLE) // 当前是否是 tracker-itemp
                if (isTitleOnAfterLocation && !isTitleOnCurSelection) {

                    return // 如果当前不是 table ，前面是 table ，则不执行删除。否则会删除 table 最后一个 cell
                }
            }
        }

        // 执行默认的删除
        deleteForward(unit)
    }
    return newEditor
}

export default withTitle
