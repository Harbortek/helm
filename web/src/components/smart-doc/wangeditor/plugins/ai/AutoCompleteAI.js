import { IDomEditor, DomEditor, SlateEditor } from '@wangeditor/editor'
import { fetchSSE } from './fetchSSE'
// 创建一个名为 AutoCompleteAI 的插件
class AutoCompleteAI {
    // 插件构造函数
    constructor({ editor, apiKey, apiUrl, onRequest, onSuccess, onError }) {
        this.editor = editor // 编辑器实例
        this.apiUrl = apiUrl // AI 服务接口地址
        this.apiKey = apiKey
        this.onRequest = onRequest // 请求开始回调
        this.onSuccess = onSuccess // 请求成功回调
        this.onError = onError // 请求失败回调
        this.isLoading = false // 加载状态
        this.controller = new AbortController();
    }

    // 插件安装方法（必须）
    install() {
        const editor = this.editor

        setTimeout(() => {
            // beforeInput 事件不能识别 ctrl+enter ，所以自己绑定 DOM 事件
            const { $textArea } = DomEditor.getTextarea(editor)
            if ($textArea == null) return
            $textArea.on('keydown', e => {
                if (e.ctrlKey && e.key.toLowerCase() === 'b') {
                    e.preventDefault() // 阻止默认加粗行为
                    this.handleShortcut()
                    return
                }
            })
        })

    }

    // 处理快捷键逻辑
    async handleShortcut() {
        if (this.isLoading) {
            console.warn('请求正在处理中，请稍候')
            return
        }
        this.cancel();
        this.isLoading = true
        this.onRequest?.() // 触发请求开始回调

        // 获取当前选区上下文
        const context = this.getContext()

        this.callAIService(context, this.controller).then(() => {
            this.onSuccess?.() // 触发成功回调
            this.isLoading = false
        }).catch(error => {
            this.onError?.(error) // 触发错误回调
            this.isLoading = false
        })
    }
    cancel() {
        if (this.controller) {
            this.controller.abort();
            this.controller = new AbortController();
        }
    }
    // 获取当前编辑上下文
    getContext() {
        const editor = this.editor
        const selection = editor.selection
        const before = 200;
        const after = 200;
        if (!selection) return ''
        // 获取当前选区的起点和终点
        const { anchor, focus } = selection;

        // 创建一个范围对象
        const currentRange = { anchor, focus };

        // 计算向前扩展的起点
        const beforePoint = SlateEditor.before(editor, anchor, { distance: before }) || anchor;

        // 计算向后扩展的终点
        const afterPoint = SlateEditor.after(editor, focus, { distance: after }) || focus;

        // 创建扩展后的范围
        const extendedRange = {
            anchor: beforePoint,
            focus: afterPoint,
        };

        // 提取扩展范围内的文本
        const ctxText = SlateEditor.string(editor, extendedRange);
        return {
            "stream": true,
            "messages": [
                {
                    "role": "assistant",
                    "content": "你是一个需求撰写者, 帮我简短地补全这段需求的结尾部分, 请智能结束补全, 只需给出补全的文字, 不需要任何解释，回答不超过20个字。"
                },
                {
                    "role": "user",
                    "content": ctxText
                }
            ],
            "model": "",
            "temperature": 0,
            "max_tokens": 64,
            "top_p": 1,
            "frequency_penalty": 0,
            "presence_penalty": 0
        }
    }

    // 调用 AI 服务
    callAIService(
        context,
        controller) {
        let text = ''

        return new Promise((resolve, reject) => {
            fetchSSE(this.apiUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${this.apiKey}`,
                },
                body: JSON.stringify(context),
                signal: controller.signal,
                onMessage: (data) => {
                    let lastResponse;
                    console.log(JSON.parse(data))
                    if (JSON.parse(data).created === "0") {
                        text = text.trim();
                        return resolve();
                    }
                    try {
                        const response = JSON.parse(data);
                        if ((lastResponse = response == null ? void 0 : response.choices) == null ? void 0 : lastResponse.length) {
                            text += response.choices[0].delta.content || '';
                            const tempTxt = response.choices[0].delta.content || ''
                            this.insertContent == null ? void 0 : this.insertContent(tempTxt);
                        }
                    } catch (err) {
                        console.warn("ChatGPT stream SEE event unexpected error", err);
                        return reject(err);
                    }
                },
                onError: (error) => {
                    console.error(error);
                }
            });
        })
    }

    // 插入生成内容
    insertContent(text) {
        const editor = this.editor
        editor.insertText(text) // 在光标位置插入文本
    }
}

export default AutoCompleteAI;