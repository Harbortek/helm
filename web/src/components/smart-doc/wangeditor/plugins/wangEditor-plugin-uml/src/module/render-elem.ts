/**
 * @description render elem
 * @author wangfupeng
 */

import { h, VNode } from 'snabbdom'
import { DomEditor, IDomEditor, SlateElement } from '@wangeditor/editor'
import { UmlElement } from './custom-types'
function getObjectValue(obj: any, property: string): any {
  return obj[property];
}
function renderUml(elem: SlateElement, children: VNode[] | null, editor: IDomEditor): VNode {
  const isDisabled = editor.isDisabled()

  // 当前节点是否选中
  const selected = DomEditor.isNodeSelected(editor, elem)

  // 构建 vnode
  const { src = '', xml = '' } = elem as UmlElement
  const vnode = h(
    'span',
    {
      props: {
        contentEditable: false, // 不可编辑
      },
      style: {
        display: 'inline-block', // inline
        marginLeft: '3px',
        marginRight: '3px',
        border:
          selected && !isDisabled
            ? '2px solid var(--w-e-textarea-selected-border-color)' // wangEditor 提供了 css var https://www.wangeditor.com/v5/theme.html
            : '2px solid transparent',
        borderRadius: '3px',
        padding: '0 3px',
        // backgroundColor: '#f1f1f1',
        cursor: isDisabled ? 'pointer' : 'inherit',
      },
      on: {
        // disable 时，点击下载附件
        click() {
          if (!isDisabled) return
          if (xml) {
            const e = getObjectValue(editor, 'currentBlock').tool.api.events;
            e.emit('mxgraph-open', {
              isUpdate: true,
              xml: xml,
              show: true
            });
          }
        },
      },
    },
    [
      h('img', {
        props: {
          src: src
        },
        style: {
        },
      })
    ]
  )

  return vnode
}

const conf = {
  type: 'uml', // 节点 type ，重要！！！
  renderElem: renderUml,
}

export default conf
