import { DomEditor } from '@wangeditor/editor'
import { fetchSSE } from '../ai/fetchSSE'
import Cookies from 'js-cookie'

function withAiProofread(editor) {
  const { insertData } = editor
  const newEditor = editor

  newEditor.insertData = (...args) => {
    insertData(...args)
  }

  return newEditor
}

export default withAiProofread