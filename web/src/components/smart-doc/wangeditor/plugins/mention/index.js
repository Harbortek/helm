/**
 * @description code block module
 */

import './assets/index.less'
import withMentionCommand from './plugin'
import { renderMention } from './render-elem'
const mentionCommandModule = {
    // menus: [codeBlockMenuConf],
    menus: [],

    editorPlugin: withMentionCommand,
    renderElems: [renderMention],
    elemsToHtml: [],
    parseElemsHtml: [],
}

export default mentionCommandModule
