import { Descendant, Text } from 'slate'
import { IDomEditor } from '@wangeditor/core'
import $, { DOMElement, getStyleValue, Dom7Array } from './utils/dom'

export type ColorText = {
  text: string
  color?: string
  bgColor?: string
  fontSize?: string
  fontFamily?: string
}

/**
 * $text 是否匹配 tags
 * @param $text $text
 * @param selector selector 如 'b,strong' 或 'sub'
 */
function isMatch($text: Dom7Array, selector: string): boolean {
  if ($text.length === 0) return false

  if ($text[0].matches(selector)) return true

  if ($text.find(selector).length > 0) return true

  return false
}

export function parseStyleHtml(text: DOMElement, node: Descendant, editor: IDomEditor): any {
  const $text = $(text)
  if (!Text.isText(node)) return node

  const textNode = node as ColorText
  const textNodeList: ColorText[] = []

  function recursiveTraversal(ele: Dom7Array, cb: (ele: Dom7Array) => void) {
    const { nodeName, childNodes } = ele[0]

    if (ele.children().length === 0) {
      cb(ele)
      return
    }

    if (nodeName.toLowerCase() === 'span' && childNodes.length > 1) {
      cb(ele)

      childNodes.forEach(child => {
        const { textContent } = child
        const textObj: any = {
          text: textContent,
        }

        const { nodeType } = child
        if (nodeType === 3) {
          textNodeList.push(textObj)
        } else if (nodeType === 1) {
          const $text = $(child)

          // bold
          if (isMatch($text, 'b,strong')) {
            textObj.bold = true
          }

          // italic
          if (isMatch($text, 'i,em')) {
            textObj.italic = true
          }

          // underline
          if (isMatch($text, 'u')) {
            textObj.underline = true
          }

          textNodeList.push(textObj)
        }
      })
      return
    }

    cb(ele)
    ele.children().forEach((childEle: Element) => {
      cb($(childEle))
      recursiveTraversal($(childEle), cb)
    })
  }

  // 如果要引用@wangeditor/basic-modules就需要改源码发布，先手动执行必要的
  recursiveTraversal($text, ($ele: Dom7Array) => {
    const color = getStyleValue($ele, 'color')

    if (color) {
      textNode.color = color
    }

    const fontFamily = getStyleValue($ele, 'font-family')

    if (fontFamily) {
      textNode.fontFamily = fontFamily
    }

    const fontSize = getStyleValue($ele, 'font-size')

    if (fontSize) {
      textNode.fontSize = fontSize
    }

    let bgColor = getStyleValue($ele, 'background-color')
    if (!bgColor) bgColor = getStyleValue($ele, 'background') // word 背景色
    if (bgColor) {
      textNode.bgColor = bgColor
    }
  })

  // console.log(textNodeList, textNode, 'ttt')

  if (textNodeList.length) {
    textNodeList.forEach(item => {
      if (textNode.color) item.color = textNode.color
      if (textNode.fontFamily) item.fontFamily = textNode.fontFamily
      if (textNode.fontSize) item.fontSize = textNode.fontSize
      if (textNode.bgColor) item.bgColor = textNode.bgColor
    })

    return textNodeList
  }
  return textNode
}
