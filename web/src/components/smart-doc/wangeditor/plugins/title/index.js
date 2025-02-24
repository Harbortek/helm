/**
 * @description  module
 */

import './assets/index.less'
import withTitle from './plugin'
import { renderTitleConf } from './render-elem'
import { parseTitleHtmlConf } from './parse-elem-html'
import { titleToHtmlConf } from './elem-to-html'

const titleModule = {
    menus: [],
    editorPlugin: withTitle,
    renderElems: [renderTitleConf],
    elemsToHtml: [parseTitleHtmlConf],
    parseElemsHtml: [titleToHtmlConf],
}
export default titleModule
