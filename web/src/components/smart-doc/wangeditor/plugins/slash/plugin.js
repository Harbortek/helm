import { IDomEditor, DomEditor, SlateEditor  } from '@wangeditor/editor'
import SlashCommandModal from './modal'
function withSlashCommand(editor) {
    const { insertText, isInline, isVoid } = editor
    const newEditor = editor
    const modal = new SlashCommandModal(newEditor)

    editor.insertText = (t) => {
        // 选过选中了 void 元素
        const elems = DomEditor.getSelectedElems(newEditor)
        const isSelectedVoidElem = elems.some(elem => newEditor.isVoid(elem))
        if (isSelectedVoidElem) {
            // insertText(t)
            return
        }

        if (t === '/') {
            setTimeout(() => {
                // 展示 modal （异步，以便准确获取光标位置）
                modal.show()
                // 监听，隐藏 modal（异步，等待 modal 渲染后再监听）
                setTimeout(() => {
                    function _hide() {
                        modal.hide()
                    }
                    newEditor.once('fullScreen', _hide.bind(modal))
                    newEditor.once('unFullScreen', _hide.bind(modal))
                    newEditor.once('scroll', _hide.bind(modal))
                    newEditor.once('modalOrPanelShow', _hide.bind(modal))
                    newEditor.once('modalOrPanelHide', _hide.bind(modal))

                    function hideOnChange() {
                        if (newEditor.selection != null) {
                            _hide.apply(modal)
                            newEditor.off('change', hideOnChange) // 及时解绑
                        }
                    }
                    newEditor.on('change', hideOnChange)
                }, 0)
            })
        } else {
            // 非 '/' 则执行默认行为
            insertText(t)
        }


    }
    return editor
}

export default withSlashCommand