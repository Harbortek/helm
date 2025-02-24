/**
 * @description code block module
 */

import './assets/index.less'
import withSlashCommand from './plugin'

const slashCommandModule = {
    // menus: [codeBlockMenuConf],
    menus: [],

    editorPlugin: withSlashCommand,
    renderElems: [],
    elemsToHtml: [],
    parseElemsHtml: [],
}

export default slashCommandModule
