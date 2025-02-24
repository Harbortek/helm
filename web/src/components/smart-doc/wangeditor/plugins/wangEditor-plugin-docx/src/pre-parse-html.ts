/**
 * @description pre-parse html
 * @author wangfupeng
 */

import $, { DOMElement, getTagName } from './utils/dom'

// V4 font-size 对应关系（V4 使用 <font size="1">xxx</font> 格式）
const FONT_SIZE_MAP_FOR_V4 = {
  '1': '12px',
  '2': '14px',
  '3': '16px',
  '4': '19px',
  '5': '24px',
  '6': '32px',
  '7': '48px',
}

/**
 * pre-prase font ，兼容 V4
 * @param fontElem fontElem
 */
function preParse(elem: DOMElement): DOMElement {
  const $elem = $(elem)
  // console.log($elem, '$elem')

  // console.log($elem[0], $elem.attr('outerHTML'), '$font');

  // $elem.empty();
  // $elem.html('尺度上试探一个新高度，当然，我们更欢迎<strong>非主流</strong>外的东西')

  // const tagName = getTagName($font)
  // if (tagName !== 'font') return fontElem

  // 处理 size （V4 使用 <font size="1">xxx</font> 格式）
  // const size = $font.attr('size') || ''
  // if (size) {
  //   $font.removeAttr('size')
  //   $font.css('font-size', FONT_SIZE_MAP_FOR_V4[size])
  // }

  // // 处理 face （V4 使用 <font face="黑体">xx</font> 格式）
  // const face = $font.attr('face') || ''
  // if (face) {
  //   $font.removeAttr('face')
  //   $font.css('font-family', face)
  // }

  return $elem[0]
}

export const preParseHtmlConf = {
  selector: 'span',
  preParseHtml: preParse,
}
