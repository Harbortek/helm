/**
 * @description elem to html
 * @author wangfupeng
 */

import { SlateElement } from '@wangeditor/editor'
import { UmlElement } from './custom-types'

// 生成 html 的函数
function umlToHtml(elem: SlateElement, childrenHtml: string): string {
  const { xml = '' ,src= '' } = elem as UmlElement
  const codeXml = encodeURIComponent(xml)
  const html =  `<a data-w-e-type="uml" data-w-e-is-void data-w-e-is-inline data-xml="${codeXml}" data-src="${src}" ></a>`;
  return html;
}

// 配置
const conf = {
  type: 'uml', // 节点 type ，重要！！！
  elemToHtml: umlToHtml,
}

export default conf
