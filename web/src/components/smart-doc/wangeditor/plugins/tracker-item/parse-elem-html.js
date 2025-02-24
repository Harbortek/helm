/**
 * @description parse html
 */

import $ from 'lodash'
import { DomEditor } from '@wangeditor/editor'

function parseTrackerItemHtml(elem, children, editor) {
    const $elem = $(elem)
    // ref
    const ref = $elem.attr('data-x-ref') || ''

    return {
        type: 'tracker-item',
        ref,
        children: children,
    }
}

export const parseTrackerItemHtmlConf = {
    selector: 'p[data-w-e-type="tracker-item"]', // 匹配 <pre> 下的 <code>
    parseElemHtml: parseTrackerItemHtml,
}

function parseTracerItemTitle(elem, children, editor) {
    const $elem = $(elem)
    //ref
    const ref = $elem.attr('data-x-ref') || ''
    return {
        ref,
        type: 'tracker-item-title',
        children: children
    }
}

export const parseTracerItemTitleHtmlConf = {
    selector: 'p[data-w-e-type="tracker-item-title"]', // 匹配 <pre> 下的 <code>
    parseElemHtml: parseTracerItemTitle,
}

function parseTracerItemDescription(elem, children, editor) {
    const $elem = $(elem)
    //ref
    const ref = $elem.attr('data-x-ref') || ''
    return {
        ref,
        type: 'tracker-item-description',
        children: children,
    }
}

export const parseTracerItemDescriptionHtmlConf = {
    selector: 'p[data-w-e-type="tracker-item-description"]', // 匹配 <pre> 下的 <code>
    parseElemHtml: parseTracerItemDescription,
}


function parseTracerItemExtra(elem, children, editor) {
    const $elem = $(elem)
    //ref
    const ref = $elem.attr('data-x-ref') || ''
    return {
        ref,
        type: 'tracker-item-extra',
        children: [{ type: "paragraph", children: [{ text: "" }] }
        ],
    }
}

export const parseTracerItemExtraHtmlConf = {
    selector: 'p[data-w-e-type="tracker-item-extra"]', // 匹配 <pre> 下的 <code>
    parseElemHtml: parseTracerItemExtra,
}