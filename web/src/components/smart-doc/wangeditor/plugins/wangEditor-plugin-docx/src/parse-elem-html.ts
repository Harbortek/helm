import { Descendant, Text } from 'slate'
import { IDomEditor } from '@wangeditor/core'
import $, { DOMElement } from './utils/dom'

function parseHtml(elem: DOMElement, children: Descendant[], editor: IDomEditor) {
  const $elem = $(elem)
  console.log($elem.children(), '$$$')

  // children = children.filter(child => {
  //   if (Text.isText(child)) return true
  //   if (editor.isInline(child)) return true
  //   return false
  // })

  // if (children.length === 0) {
  //   children = [{ text: $elem.text().replace(/\s+/gm, ' ') }]
  // }

  return {
    type: 'link',
    url: $elem.attr('href') || '',
    target: $elem.attr('target') || '',
    // @ts-ignore
    children,
  }
}

export const dividerToHtmlConf = {
  selector: 'span',
  parseElemHtml: parseHtml,
}
