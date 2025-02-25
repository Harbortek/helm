import { Boot } from '@wangeditor/editor'
import withAiProofread from './plugin'
import AiProofreadMenu from './menu'
const aiProofreadModule = {
    menus: [AiProofreadMenu],
    editorPlugin: withAiProofread,
    renderElems: [],
    elemsToHtml: [],
    parseElemsHtml: [],
}
export default aiProofreadModule