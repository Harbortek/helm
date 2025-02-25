import { h } from 'snabbdom'
import { DomEditor, SlateTransforms } from '@wangeditor/editor';
import TrackerItemFieldsShow from "@/components/tool/TrackerItemFieldsShow.vue";
import Vue from 'vue';
import { size } from 'lodash';

function getPageSettingsByTrackerId(page, trackerId) {
    for (let i = 0; i < page.pageSettingTrackers.length; i++) {
        const item = page.pageSettingTrackers[i];
        if (item.id == trackerId) {
            return {
                content: item.content,
                fields: item.fields
            };
        }
    }
}
function renderTrackerItem(
    elemNode,
    children,
    editor
) {
    const { ref, trackerItem, type } = elemNode;
    let trackerIcon = elemNode.trackerIcon

    if (trackerItem) {
        const item = trackerItem;
        item && (trackerIcon = item.tracker.icon)

    }
    return h(
        'p',
        // HTML 属性、样式、事件
        {
            'data-w-e-type': type,
            'data-x-ref': ref,
            style: {
                "--tracker-icon": `url("/static/fonts/svg/${trackerIcon}.svg")`,
                //         border: selected // 选中/不选中，样式不一样
                //   ? '2px solid var(--w-e-textarea-selected-border-color)' // wangEditor 提供了 css var https://www.wangeditor.com/v5/theme.html
                //   : '2px solid transparent',
                // borderRadius: '3px',
            }
        },
        // 子节点
        children
    )
}
function renderTrackerItemDescription(
    elemNode,
    children,
    editor
) {
    const page = editor.getConfig().EXTEND_CONF.pageConfig;
    const parentNode = DomEditor.getParentNode(editor, elemNode);
    const item = parentNode?.trackerItem;
    if (parentNode && item) {
        const config = getPageSettingsByTrackerId(page, item.tracker.id);
        if (config) {
            if (config.content.indexOf('description') < 0) {
                children = [];
            }
        }
    }
    return h(
        'p',
        // HTML 属性、样式、事件
        {
            'data-w-e-type': elemNode.type,
            'data-x-ref': elemNode.ref
        },
        children
    )
}
function renderTrackerItemTitle(
    elemNode,
    children,
    editor
) {
    const { type, ref } = elemNode;
    let itemNo = elemNode.itemNo;
    const page = editor.getConfig().EXTEND_CONF.pageConfig;
    const parentNode = DomEditor.getParentNode(editor, elemNode);
    const item = parentNode?.trackerItem;

    if (parentNode && item) {
        const config = getPageSettingsByTrackerId(page, item.tracker.id);
        if (config) {
            if (config.content.indexOf('title') < 0) {
                children = [];
            }
        }
    }

    if (item) {
        item && (itemNo = '#' + page.projectkeyName + '-' + item.itemNo);
    }
    return h(
        'p',
        // HTML 属性、样式、事件
        {
            'data-w-e-type': type,
            'data-x-ref': ref,
        },
        // 子节点
        [
            h('span', {
                style: {
                    color: '#338fe5',
                    'font-weight': 'bold',
                    'margin-right': '5px'
                },
                attrs: {
                    itemNo: itemNo,
                },
                contentEditable: false
            }, ''),
            ...children
        ]
    )
}
function renderFields(editor, ref, item, dom, readOnly) {
    const page = editor.getConfig().EXTEND_CONF.pageConfig;
    const config = getPageSettingsByTrackerId(page, item.tracker.id);
    if (item && config?.fields) {
        const child = document.createElement('div');
        dom.appendChild(child);
        const vm = new Vue({
            components: {
                'my-comp': TrackerItemFieldsShow, // 注册自定义组件
            },
            render(vh) {
                const children = [];
                config.fields.forEach((field, index) => {
                    const child = vh('my-comp', {
                        key: ref + '-' + index,
                        props: {
                            projectId: item.project.id,
                            trackerItem: item,
                            fields: field,
                            readOnly: readOnly,
                        },
                    });
                    children.push(child);
                });
                return vh('div', {
                    class: 'tracker-item-extra-fields',
                    style: {
                        display: 'flex',
                        flexWrap: 'wrap',
                        width: '100%',
                        marginTop: '2px'
                    }
                }, children);
            },
        });
        vm.$mount(child);
    }
}
function renderTrackerItemExtra(
    elemNode,
    children,
    editor
) {
    const { type, ref } = elemNode;
    const { readOnly } = editor?.getConfig();

    const parentNode = DomEditor.getParentNode(editor, elemNode);
    const item = parentNode?.trackerItem;
    return h(
        'p',

        // HTML 属性、样式、事件
        {
            hook: {
                insert: (vnode) => {
                    if (item) {
                        renderFields(editor, ref, item, vnode.elm, readOnly);
                    }
                    // console.log('虚拟节点已渲染到 DOM 中', elemNode, vnode);
                },
            },
            key: elemNode.ref,
            'data-w-e-type': type,
            'data-x-ref': ref,
            contentEditable: false,
            style: {
                width: '100%',
            }
        },
        // children
        []
    )
}
export const renderTrackerItemConf = {
    type: 'tracker-item',
    renderElem: renderTrackerItem,
}
export const renderTrackerItemDescriptionConf = {
    type: 'tracker-item-description',
    renderElem: renderTrackerItemDescription,
}
export const renderTrackerItemTitleConf = {
    type: 'tracker-item-title',
    renderElem: renderTrackerItemTitle,
}

export const renderTrackerItemExtraConf = {
    type: 'tracker-item-extra',
    renderElem: renderTrackerItemExtra,
}