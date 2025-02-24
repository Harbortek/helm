/**
 * @description examples entry
 * @author wangfupeng
 */

import { createEditor, createToolbar, Boot, i18nChangeLanguage } from '@wangeditor/editor'
// import { cleanDocx } from '@udecode/plate-serializer-docx'
// import juice from 'juice'
import module from '../src/index'

Boot.registerModule(module)

// i18nChangeLanguage('en')

// 创建编辑器
const editor = createEditor({
  selector: '#editor-container',
  config: {
    // customPaste: (editor: any, event: any) => {
    //   // event 是 ClipboardEvent 类型，可以拿到粘贴的数据
    //   // 可参考 https://developer.mozilla.org/zh-CN/docs/Web/API/ClipboardEvent
    //   const html = event.clipboardData.getData('text/html') // 获取粘贴的 html
    //   const rtf = event.clipboardData.getData('text/rtf') // 获取 rtf 数据（如从 word wsp 复制粘贴）
    //   const html1 = html.replaceAll(/<style>(\s*)<!--/g, '<style>')
    //   const html2 = juice(html)
    //   const html3 = cleanDocx(html2, rtf)
    //   editor.dangerouslyInsertHtml(html3)
    //   // 阻止默认的粘贴行为
    //   event.preventDefault()
    //   return false
    // },
  },
})
const toolbar = createToolbar({
  editor,
  selector: '#toolbar-container',
})

// @ts-ignore 为了便于调试，暴露到 window
window.editor = editor
// @ts-ignore
window.toolbar = toolbar
