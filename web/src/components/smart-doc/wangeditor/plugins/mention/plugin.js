import { DomEditor, SlateEditor, SlateRange, SlateTransforms } from '@wangeditor/editor';

function getMentionConfig(editor) {
    if (!editor) return;
    const { EXTEND_CONF } = editor.getConfig();
    const { mentionConfig } = EXTEND_CONF;
    return mentionConfig;
}

function renderMentionEl(editor) {
    setTimeout(() => {
        const { showMention } = getMentionConfig(editor);
        showMention();
    });
}

function destoryMention(editor) {
    setTimeout(() => {
        const { hideMention } = getMentionConfig(editor);
        hideMention();
    });
}

function insertMention(editor, character) {
    if (!editor) return;
    const mention = {
        type: 'mention',
        children: [{ text: '' }],
        value: character.value,
        label: character.label,
        info: character,
    };

    editor.insertNode(mention);
    SlateTransforms.move(editor);
}
function withMention(editor) {
    const { onChange, isInline, isVoid } = editor;
    const { showMention, hideMention } = getMentionConfig(editor);
    const newEditor = editor;

    newEditor.onChange = () => {
        onChange();
        // 有预选失去焦点时恢复选取
        if (!newEditor.isFocused()) {
            newEditor.restoreSelection();
        }
    };

    newEditor.isInline = (elem) => {
        const type = DomEditor.getNodeType(elem);
        if (type === 'mention') {
            return true;
        }

        return isInline(elem);
    };

    newEditor.isVoid = (elem) => {
        const type = DomEditor.getNodeType(elem);
        if (type === 'mention') {
            return true;
        }

        return isVoid(elem);
    };

    newEditor.insertText = (t) => {
        // 选过选中了 void 元素
        const elems = DomEditor.getSelectedElems(newEditor)
        const isSelectedVoidElem = elems.some(elem => newEditor.isVoid(elem))
        if (isSelectedVoidElem) {
            // insertText(t)
            return
        }

        if (t === '@') {
            setTimeout(() => {
                // 展示 modal （异步，以便准确获取光标位置）
                showMention()
                // 监听，隐藏 modal（异步，等待 modal 渲染后再监听）
                setTimeout(() => {
                    newEditor.once('fullScreen', hideMention)
                    newEditor.once('unFullScreen', hideMention)
                    newEditor.once('scroll', hideMention)
                    newEditor.once('modalOrPanelShow', hideMention)
                    newEditor.once('modalOrPanelHide', hideMention)

                    function hideOnChange() {
                        if (newEditor.selection != null) {
                            hideMention()
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
    return newEditor;
};

export default withMention;