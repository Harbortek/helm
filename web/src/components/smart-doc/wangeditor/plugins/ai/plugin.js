/**
 * @description editor 插件，重写 editor API
 */

import Cookies from 'js-cookie';
import AutoCompleteAI from './AutoCompleteAI';
function withAi(editor) {
    const newEditor = editor;
    const autoCompleteAI = new AutoCompleteAI({
        editor: newEditor,
        apiKey: Cookies.get("Authorization").slice(7),
        apiUrl: process.env.VUE_APP_API_BASE_URL + '/ai/openai/v1/chat/completions', // 你的 AI 服务地址
        onRequest: () => console.log('正在请求 AI 续写...'),
        onSuccess: () => console.log('续写成功！'),
        onError: (err) => console.error('错误:', err.message)
    });

    autoCompleteAI.install();

    return newEditor
}

export default withAi
