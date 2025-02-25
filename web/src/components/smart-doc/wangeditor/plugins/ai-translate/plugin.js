import { DomEditor } from '@wangeditor/editor'


function withAiTranslate(editor) {
  const { insertData } = editor
  const newEditor = editor

  newEditor.insertData = (...args) => {
    insertData(...args)
  }


  return newEditor
}

export default withAiTranslate