/**
 * @description render elem
 * @author wangfupeng
 */

import { h, VNode } from 'snabbdom'
import { DomEditor, IDomEditor, SlateElement } from '@wangeditor/editor'
import { MentionElement } from './custom-types'

function renderMention(elem: SlateElement, children: VNode[] | null, editor: IDomEditor): VNode {
  // 当前节点是否选中
  const selected = DomEditor.isNodeSelected(editor, elem)
  const { info } = elem as MentionElement
  console.log('info', info)
  // 构建 vnode
  const vnode = h(
    'span',
    {
      props: {
        contentEditable: false, // 不可编辑
        id: info.id
      },
      style: {
        marginLeft: '3px',
        marginRight: '3px',
        backgroundColor: 'var(--w-e-textarea-slight-bg-color)',
        border: selected // 选中/不选中，样式不一样
          ? '2px solid var(--w-e-textarea-selected-border-color)' // wangEditor 提供了 css var https://www.wangeditor.com/v5/theme.html
          : '2px solid transparent',
        borderRadius: '3px',
        padding: '0 3px',
        width: '14px',
        height: '14px',
        cursor: 'pointer'
      },
    },
    [
      h('img', {
        src: info.avatar,
        style: {
          'border-radius': '50%',
          width: '12px',
          height: '12px',
          'margin-right': '2px',
          'position': 'relative',
          top: '-2px'
        },
        dataset: {
          id: info.id
        }
      }),
      `${info.name}`
    ])

  return vnode
}

const conf = {
  type: 'mention', // 节点 type ，重要！！！
  renderElem: renderMention,
}

export default conf
