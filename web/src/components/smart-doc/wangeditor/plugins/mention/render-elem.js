import { h } from 'snabbdom'
import trackerItemApi from '../../../trackerItemApi';
import { DomEditor, SlateTransforms } from '@wangeditor/editor';
import TrackerItemFieldsShow from "@/components/tool/TrackerItemFieldsShow.vue";
import Vue from 'vue';

function renderMention(
    elemNode,
    children,
    editor
) {
    const { icon = '', label = '' } = elemNode;

    // 构建 vnode
    const vnode = h(
        'span',
        {
            props: {
                contentEditable: false, // 不可编辑
            },
            style: {
                marginLeft: '3px',
                marginRight: '3px',
                backgroundColor: 'var(--w-e-textarea-slight-bg-color)',
                borderRadius: '3px',
                padding: '0 3px',
            },
            dataset: {
                wEType: 'mention',
                icon,
                label,
            },
        },
        `@${label}`,
    );

    return vnode;
}
export const renderTitleConf = {
    type: 'mention',
    renderElem: renderMention,
}