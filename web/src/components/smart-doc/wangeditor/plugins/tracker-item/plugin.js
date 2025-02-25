/**
 * @description editor 插件，重写 editor API
 */

import { IDomEditor, DomEditor, SlateEditor, SlateTransforms, SlateElement, SlatePoint } from '@wangeditor/editor'

const DESC = 'tracker-item-description';
const TITLE = 'tracker-item-title';
const EXTRA = 'tracker-item-extra';
const TRACKER_ITEM = 'tracker-item';
function getTopSelectedElemsBySelection(editor) {
    return SlateEditor.nodes(editor, {
        at: editor.selection || undefined,
        match: n => DomEditor.findPath(editor, n).length === 1, // 只匹配顶级元素
    })
}
/**
 * 判断该 location 有没有命中 tracker-item
 * @param editor editor
 * @param location location
 */
function checkLocation(editor, location, checkType) {
    const trackerItems = SlateEditor.nodes(editor, {
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

    const [cellNodeEntry] = SlateEditor.nodes(newEditor, {
        match: n => DomEditor.checkNodeType(n, checkType),
    })
    if (cellNodeEntry) {
        const [, cellPath] = cellNodeEntry
        const start = SlateEditor.start(newEditor, cellPath)

        if (SlatePoint.equals(selection.anchor, start)) {
            return true // 阻止删除 cell
        }
    }

    return false
}
function checkDeleteHandlerEnd(newEditor, checkType) {
    const { selection } = newEditor
    if (selection == null) return false

    const [cellNodeEntry] = SlateEditor.nodes(newEditor, {
        match: n => DomEditor.checkNodeType(n, checkType),
    })
    if (cellNodeEntry) {
        const [, cellPath] = cellNodeEntry
        const end = SlateEditor.end(newEditor, cellPath)

        if (SlatePoint.equals(selection.anchor, end)) {
            return true // 阻止删除 cell
        }
    }

    return false
}

function withTrackerItem(editor) {
    const { deleteBackward, deleteForward, insertBreak, normalizeNode, insertData, insertNode, isVoid } = editor
    const newEditor = editor
    // setTimeout(() => {
    //     // beforeInput 事件不能识别 shift+enter ，所以自己绑定 DOM 事件
    //     const { $textArea } = DomEditor.getTextarea(newEditor)
    //     if ($textArea == null) return
    //     $textArea.on('keydown', e => {
    //         const event = e
    //         const isShift = event.shiftKey || event.metaKey
    //         if (event.key === 'Enter' && isShift) {
    //             // ctrl+enter 触发换行
    //             newEditor.insertBreak()
    //         }
    //     })
    // })
    newEditor.insertBreak = () => {
        const { selection } = newEditor;
        //1、如果是tracker-item-description，则在本节点内换行
        const selectedDescNode = DomEditor.getSelectedNodeByType(newEditor, DESC)
        if (selectedDescNode != null) {
            // 选中了 tracker-item-description ，则在 内部 内换行
            newEditor.insertText('\n')
            return
        }
        //2、如果是tracker-item-title，则在description下 换行
        const selectedTitleNode = DomEditor.getSelectedNodeByType(newEditor, TITLE)
        if (selectedTitleNode != null) {
            const after = SlateEditor.after(newEditor, selection) // 前一个 location
            if (after) {
                const isDescOnAfterLocation = checkLocation(newEditor, after, DESC) // before 是否是 desc
                if (isDescOnAfterLocation) {
                    //在最前面插入换行
                    SlateTransforms.select(newEditor, after);
                    return // 如果当前不是 table ，前面是 table ，则不执行删除。否则会删除 table 最后一个 cell
                }
            }
            newEditor.insertText('\n')
            return
        }

        insertBreak();
    }

    // 删除非 p 的文本 elem（如 header blockQuote 等），删除没有内容时，切换为 p
    newEditor.deleteBackward = unit => {

        const res = checkDeleteHandlerStart(newEditor, TRACKER_ITEM)
        if (res) return // 命中 tracker-item ，自己处理删除

        const { selection } = newEditor
        if (selection) {
            //1. 防止从 tracker-item 后面的 p 删除时，删除最后一个 desc
            const before = SlateEditor.before(newEditor, selection) // 前一个 location
            if (before) {
                const isTrackerItemOnBeforeLocation = checkLocation(newEditor, before, TRACKER_ITEM) // before 是否是 table
                const isTrackerItemOnCurSelection = checkLocation(newEditor, selection, TRACKER_ITEM) // 当前是否是 tracker-item
                if (isTrackerItemOnBeforeLocation && !isTrackerItemOnCurSelection) {
                    return // 如果当前不是 table ，前面是 table ，则不执行删除。否则会删除 table 最后一个 cell
                }
            }
            //2.防止删除title
            const titleRes = checkDeleteHandlerStart(newEditor, TITLE)
            if (titleRes) {
                return
            }
            //3.防止删除desc
            const descRes = checkDeleteHandlerStart(newEditor, DESC)
            console.log(descRes);
            if (descRes) {
                return
            }

        }
        deleteBackward(unit)
    }
    newEditor.deleteForward = unit => {

        const { selection } = newEditor;
        const res = checkDeleteHandlerEnd(newEditor, TRACKER_ITEM)
        if (res) return
        if (selection) {
            //1. 防止从 tracker-item 前面的 p 删除时，删除最后一个 desc
            const after = SlateEditor.after(newEditor, selection) // 前一个 location
            if (after) {
                const isTrackerItemOnAfterLocation = checkLocation(newEditor, after, TRACKER_ITEM) // before 是否是 table
                const isTrackerItemOnCurSelection = checkLocation(newEditor, selection, TRACKER_ITEM) // 当前是否是 tracker-item
                if (isTrackerItemOnAfterLocation && !isTrackerItemOnCurSelection) {

                    return // 如果当前不是 table ，前面是 table ，则不执行删除。否则会删除 table 最后一个 cell
                }
            }
            const titleRes = checkDeleteHandlerEnd(newEditor, TITLE)
            if (titleRes) {
                return
            }

            const descRes = checkDeleteHandlerEnd(newEditor, DESC)
            if (descRes) {
                return
            }
        }

        // 执行默认的删除
        deleteForward(unit)
    }

    newEditor.normalizeNode = ([node, path]) => {
        const { type, ref } = node;
        if (SlateElement.isElement(node) && type === TRACKER_ITEM) {
            // 如果段落节点没有子节点，添加一个空的文本节点
            const titleChild = node.children.find(child => child.type === TITLE)
            const descChild = node.children.find(child => child.type === DESC)
            const extraChild = node.children.find(child => child.type === EXTRA)
            if (!titleChild) {
                SlateTransforms.insertNodes(editor, {
                    ref,
                    type: TITLE,
                    children: [{ text: "" }]
                    ,
                }, { at: [...path, 0] });
            }
            if (!descChild) {
                SlateTransforms.insertNodes(editor, {
                    ref,
                    type: DESC,
                    children: [{ text: "" }]
                }, { at: [...path, 1] });
            }
            if (!extraChild) {
                SlateTransforms.insertNodes(editor, {
                    ref,
                    type: EXTRA,
                    children: [{ text: "" }]
                }, { at: [...path, 2] });
            }
            return; // 修复后返回，避免重复处理
        }
        // 执行默认行为
        return normalizeNode([node, path])
    }

    newEditor.isVoid = (elem) => {
        const type = DomEditor.getNodeType(elem)
        if (type === EXTRA) return false
        return isVoid(elem)
    }

    // 返回 editor ，重要！
    return newEditor
}

export default withTrackerItem
