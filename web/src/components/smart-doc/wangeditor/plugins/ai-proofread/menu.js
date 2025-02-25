import { Boot } from '@wangeditor/editor'
import { fetchSSE } from '../ai/fetchSSE'
import Cookies from 'js-cookie'
import { showConfirmDialog } from '../ai-translate/dialog'
class ProofreadMenu {
    constructor() {
        this.title = '智能校对'
        this.tag = 'button'
        // this.iconSvg = '<svg>...</svg>' // 这里需要替换成实际的SVG图标
        this.showDropdown = false
    }

    getValue() {
        return ''
    }
    // 是否禁用菜单
    isDisabled(editor) {
        return false
    }
    isActive() {
        return false
    }

    exec(editor) {
        const selection = editor.selection;
        if (selection == null) return

        const selectedText = editor.getSelectionText()
        if (!selectedText) return

        this.proofread(editor, selectedText)
    }

    async proofread(editor, text) {
        const prompt = `请对以下文本进行语法和拼写校对，指出错误并给出修改建议：\n${text}`
        const context = {
            stream: true,
            messages: [
                {
                    role: 'user',
                    content: prompt
                }
            ],
            model: '',
            temperature: 0.7,
            max_tokens: 1000,
            top_p: 1,
            frequency_penalty: 0,
            presence_penalty: 0
        }

        let proofreadResult = ''
        const controller = new AbortController()

        try {
            await new Promise((resolve, reject) => {
                fetchSSE(process.env.VUE_APP_API_BASE_URL + '/ai/openai/v1/chat/completions', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        authorization: `Bearer ${Cookies.get('Authorization').slice(7)}`,
                    },
                    body: JSON.stringify(context),
                    signal: controller.signal,
                    onMessage: (data) => {
                        try {
                            const response = JSON.parse(data)
                            if (response.created === '0') {
                                return resolve()
                            }
                            if (response.choices?.[0]?.delta?.content) {
                                proofreadResult += response.choices[0].delta.content
                            }
                        } catch (err) {
                            console.warn('校对出错：', err)
                            reject(err)
                        }
                    },
                    onError: (error) => {
                        console.error('校对请求失败：', error)
                        reject(error)
                    }
                })
            })

            if (proofreadResult) {
                // 显示校对结果对话框
                const confirmed = await showConfirmDialog(`校对结果：\n${proofreadResult}\n\n是否应用修改？`)
                if (confirmed) {
                    editor.deleteFragment()
                    editor.insertText(proofreadResult.trim())
                }
            }
        } catch (error) {
            console.error('校对过程出错：', error)
        }
    }
}



// 注册菜单
const menuConf = {
    key: 'aiProofread',
    factory() {
        return new ProofreadMenu()
    }
}

export default menuConf;
