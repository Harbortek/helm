/**
 * @description parse elem html
 * @author wangfupeng
 */

import { DOMElement } from '../utils/dom'
import { IDomEditor, SlateDescendant, SlateElement } from '@wangeditor/editor'
import { UmlElement } from './custom-types'

function parseHtml(
  elem: DOMElement,
  children: SlateDescendant[],
  editor: IDomEditor
): SlateElement {
  const xml = elem.getAttribute('data-xml') || ''
  const uncodeXml = decodeURIComponent(xml);
  const src = elem.getAttribute('data-src') || ''
  return {
    type: 'uml',
    xml: uncodeXml,
    src: src,
    children: [{ text: '' }], // void node 必须有一个空白 text
  } as UmlElement
}

const parseHtmlConf = {
  selector: 'a[data-w-e-type="uml"]',
  parseElemHtml: parseHtml,
}

export default parseHtmlConf
