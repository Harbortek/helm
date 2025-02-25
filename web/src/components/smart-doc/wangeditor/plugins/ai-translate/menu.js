import { Boot, SlateTransforms, SlateRange } from '@wangeditor/editor'
import { fetchSSE } from '../ai/fetchSSE'
import Cookies from 'js-cookie'
import { showPromptDialog, showConfirmDialog } from './dialog'

const LANGUAGES = [
    { label: '英语', value: 'en' },
    { label: '日语', value: 'ja' },
    { label: '韩语', value: 'ko' },
    { label: '法语', value: 'fr' },
    { label: '德语', value: 'de' },
    { label: '西班牙语', value: 'es' },
    { label: '俄语', value: 'ru' },
]

class TranslateMenu {
    constructor() {
        this.title = 'AI';
        this.tag = 'button';
        this.iconSvg = `<svg t="1740024381830" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4456" width="200" height="200"><path d="M736.448 376.192a64.032 64.032 0 0 1-3.136 90.464L335.488 837.696a64.032 64.032 0 0 1-87.328-93.6l397.824-371.04a64 64 0 0 1 90.464 3.136z" fill="#8942E3" p-id="4457"></path><path d="M291.936 870.88l-2.88-0.032c-21.344-0.736-41.152-9.792-55.712-25.408s-22.176-35.968-21.44-57.344a79.36 79.36 0 0 1 25.376-55.68l397.824-371.072a80.128 80.128 0 0 1 113.056 3.936 80.096 80.096 0 0 1-3.936 113.056L346.4 849.376a79.232 79.232 0 0 1-54.464 21.504z m397.696-499.008c-11.712 0-23.456 4.256-32.704 12.864L259.104 755.776a47.712 47.712 0 0 0-15.232 33.44c-0.448 12.8 4.128 25.024 12.864 34.4s20.64 14.784 33.44 15.232a48.32 48.32 0 0 0 34.4-12.832l397.856-371.072a48.096 48.096 0 0 0 2.368-67.84 48.064 48.064 0 0 0-35.168-15.232z" p-id="4458"></path><path d="M724.768 387.104a48 48 0 0 1-2.368 67.84l-116.992 109.12a48 48 0 1 1-65.472-70.208l116.992-109.12a48 48 0 0 1 67.84 2.368z" fill="#C292FF" p-id="4459"></path><path d="M224 256c129.952 0 171.488-144 162.464-192 18.048 40 54.144 144 189.536 192-108.32 24-135.392 120-135.392 192-27.072-48-108.288-172.8-216.608-192z" fill="#F0DD35" p-id="4460"></path><path d="M440.608 464a16.128 16.128 0 0 1-13.952-8.128c-21.696-38.496-100.672-165.504-205.472-184.096A16.064 16.064 0 0 1 224 240c42.368 0 77.696-16.672 104.992-49.6 34.56-41.664 46.432-98.464 41.76-123.456a16 16 0 0 1 30.304-9.536l4.384 9.952c18.496 42.656 56.992 131.36 175.936 173.536a16 16 0 0 1-1.888 30.72c-110.88 24.576-122.848 131.52-122.848 176.384a15.968 15.968 0 0 1-16.032 16zM285.696 260.736c63.232 32.8 112.96 93.664 143.072 137.952 8.544-49.952 33.44-112.8 102.08-144.448-72-35.936-110.752-87.808-133.632-129.76a225.056 225.056 0 0 1-43.584 86.336c-19.104 23.04-42.08 39.84-67.936 49.92z" p-id="4461"></path><path d="M512 768c129.984 0 171.488-144 162.464-192 18.048 40 54.144 144 189.536 192-108.32 24-135.392 120-135.392 192-27.072-48-108.288-172.8-216.608-192z" fill="#F0DD35" p-id="4462"></path><path d="M728.64 976a16.128 16.128 0 0 1-13.952-8.128c-21.728-38.496-100.768-165.568-205.472-184.128A16 16 0 0 1 512 752c42.368 0 77.696-16.672 104.992-49.6 34.56-41.696 46.432-98.464 41.76-123.424a15.968 15.968 0 0 1 30.304-9.536l4.32 9.856c18.496 42.656 56.96 131.424 175.968 173.632a16 16 0 0 1-1.888 30.72c-110.88 24.576-122.848 131.52-122.848 176.384a15.936 15.936 0 0 1-15.968 15.968z m-154.944-203.264c63.2 32.768 112.928 93.696 143.072 137.984 8.544-49.984 33.472-112.832 102.08-144.512-72-35.904-110.752-87.744-133.632-129.728a224.704 224.704 0 0 1-43.584 86.304 166.784 166.784 0 0 1-67.936 49.952z" p-id="4463"></path></svg>`;
        this.showDropdown = true;
        this.items = LANGUAGES.map(lang => ({
            key: lang.value,
            text: lang.label
        }))
    }
    // 是否禁用菜单
    isDisabled(editor) {
        return false
    }
    getValue() {
        return ''
    }

    isActive() {
        return false
    }

    exec(editor, value) {
        const selection = editor.selection;
        if (selection == null) return

        const selectedText = editor.getSelectionText()
        if (!selectedText) return

        this.translate(editor, selectedText, value)
    }

    async translate(editor, text, targetLang) {
        // 显示弹框让用户输入提示词
        const response = await showPromptDialog(text)
        if (response === null) return // 用户取消输入
        setTimeout(() => {
            editor.restoreSelection();
            const selection = editor.selection;
            if (response.type == 'append') {
                // 按照要求，追加内容
                const endPoint = SlateRange.end(editor.selection);
                SlateTransforms.insertText(editor, response.content, {
                    at: endPoint,
                });

            } else if (response.type == 'replace') {
                //替换selection 文本
                SlateTransforms.insertText(editor, response.content, { at: selection });
                // SlateTransforms.delete(editor, { at: selection });
                // SlateTransforms.insertText(editor, response.content, { at: selection });
            }
        }, 0);
    }
}
// 注册菜单
const aiTranslateMenuConf = {
    key: 'aiTranslate',
    factory() {
        return new TranslateMenu()
    }
}
export default aiTranslateMenuConf
