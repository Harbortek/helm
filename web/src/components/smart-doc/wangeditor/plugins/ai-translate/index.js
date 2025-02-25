import { Boot } from '@wangeditor/editor'
import withAiTranslate from './plugin'
import AiTranslateMenu from './menu'
const aiTranslateModule = {
    menus: [AiTranslateMenu],
    editorPlugin: withAiTranslate,
    renderElems: [],
    elemsToHtml: [],
    parseElemsHtml: [],
}
export default aiTranslateModule