import { IModuleConf } from '@wangeditor/core'
import { preParseHtmlConf } from './pre-parse-html'
// import { dividerToHtmlConf } from './parse-elem-html'
import { parseStyleHtml } from './parseStyleHtml'

const docx: Partial<IModuleConf> = {
  parseStyleHtml,
  preParseHtml: [preParseHtmlConf],
  // parseElemsHtml: [dividerToHtmlConf],
}

export default docx
