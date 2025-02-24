/**
 * @description code block module
 */

import './assets/index.less'
import withTrackerItem from './plugin'
import { renderTrackerItemConf, renderTrackerItemDescriptionConf, renderTrackerItemTitleConf, renderTrackerItemExtraConf } from './render-elem'
import { parseTracerItemDescriptionHtmlConf, parseTracerItemTitleHtmlConf, parseTrackerItemHtmlConf, parseTracerItemExtraHtmlConf } from './parse-elem-html'
import { trackerItemDescToHtmlConf, trackerItemTitleToHtmlConf, trackerItemToHtmlConf, trackerItemExtraToHtmlConf } from './elem-to-html'

const trackerItemModule = {
    // menus: [codeBlockMenuConf],
    menus: [],

    editorPlugin: withTrackerItem,
    renderElems: [renderTrackerItemConf, renderTrackerItemDescriptionConf, renderTrackerItemTitleConf, renderTrackerItemExtraConf],
    elemsToHtml: [trackerItemToHtmlConf, trackerItemDescToHtmlConf, trackerItemTitleToHtmlConf, trackerItemExtraToHtmlConf],
    parseElemsHtml: [parseTrackerItemHtmlConf, parseTracerItemDescriptionHtmlConf, parseTracerItemTitleHtmlConf, parseTracerItemExtraHtmlConf],
}

export default trackerItemModule
