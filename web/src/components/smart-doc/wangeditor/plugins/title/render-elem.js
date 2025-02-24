import { h } from 'snabbdom'
import trackerItemApi from '../../../trackerItemApi';
import { DomEditor, SlateTransforms } from '@wangeditor/editor';
import TrackerItemFieldsShow from "@/components/tool/TrackerItemFieldsShow.vue";
import Vue from 'vue';

function renderTitle(
    elemNode,
    children,
    editor
) {
    return h(
        'p',
        {
            'data-w-e-type': elemNode.type,
        },
        children
    )
}
export const renderTitleConf = {
    type: 'title',
    renderElem: renderTitle,
}