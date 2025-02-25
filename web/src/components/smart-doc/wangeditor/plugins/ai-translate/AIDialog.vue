<template>
    <div v-show="visible" class="ai-popup" :style="popupStyle">
        <div class="ai-popup-content">
            <div class="ai-dialog-content">
                <!-- AI响应展示区 -->
                <div class="response-section" v-show="loading || aiResponse">
                    <a-textarea v-model="aiResponse" class="output" :rows="4" readonly />
                </div>
                <!-- 提示词输入区 -->
                <div class="prompt-section">
                    <div class="input-wrapper">
                        <a-input v-model="promptInput" class="input" placeholder="告诉AI下一步该做什么。比如：帮我翻译成英文。" />
                        <vxe-button type="primary" :loading="loading" @click="handleQuickAction(defaultOps)">
                            <template #icon>
                                <span><h-icon type="go" /></span>
                            </template>
                        </vxe-button>
                    </div>
                    <div class="notice">
                        <span><h-icon type="notice" />提示：您可以在上面输入文字或者选择下方的操作</span>
                    </div>
                </div>

            </div>
        </div>
        <!-- 操作按钮区 -->
        <div class="operation-actions-wrapper">
            <div v-if="!loading" class="operation-actions">
                <div class="info">您可以进行以下操作：</div>
                <template v-if="!aiResponse">
                    <div class="item" @click="handleFetch('translate')">
                        <span>
                            <span><h-icon type="translate" />
                            </span>
                            翻译</span>
                    </div>
                    <div class="item" @click="handleFetch('summarize')">
                        <span>
                            <span><h-icon type="summarize" />
                            </span>
                            总结</span>
                    </div>
                    <div class="item" @click="handleFetch('proofread')">
                        <span>
                            <span><h-icon type="proofread" />
                            </span>
                            校对</span>
                    </div>
                    <div class="item" @click="handleFetch('polish')">
                        <span>
                            <span><h-icon type="polish" />
                            </span>
                            润色</span>
                    </div>
                </template>
                <template v-else>
                    <div class="item" @click="handleOperation('append')" :disabled="loading">
                        <span>
                            <span><h-icon type="append" />
                            </span>
                            追加</span>
                    </div>
                    <div class="item" @click="handleOperation('replace')" :disabled="loading">
                        <span>
                            <span><h-icon type="replace" />
                            </span>
                            替换</span>
                    </div>
                    <div class="item" @click="handleOperation('discard')" type="danger">
                        <span>
                            <span><h-icon type="discard" />
                            </span>
                            取消</span>
                    </div>
                </template>
            </div>
        </div>
    </div>
</template>

<script>
import { fetchSSE } from "../ai/fetchSSE"
import Cookies from 'js-cookie'
export default {
    name: 'AIDialog',
    data() {
        return {
            visible: false,
            title: 'AI助手',
            promptText: '',
            promptInput: '',
            aiResponse: '',
            resolve: null,
            reject: null,
            selectedText: '',
            defaultOps: 'translate',
            loading: false,
            mousePosition: {
                x: 0,
                y: 0
            }
        }
    },
    computed: {
        popupStyle() {
            return {
                top: `${this.mousePosition.y + 20}px`,
                left: `${this.mousePosition.x}px`
            }
        },
        submitText() {
            return (this.promptInput.trim() ? this.promptInput : this.promptText) +
                `\n${this.selectedText}` + '\n以上是需要你处理的内容。请按以上要求回答，不做任何其他解释或提示。'
        }
    },
    watch: {
        defaultOps(newVal) {
            this.switchDefaultOps(newVal);
            this.aiResponse = ''; // 清空AI响应框
        }
    },
    created() {
        window.addEventListener('keydown', this.handleKeyDown);
    },
    beforeDestroy() {
        window.removeEventListener('keydown', this.handleKeyDown);
    },
    methods: {
        handleFetch(type) {
            this.switchDefaultOps(type);
            this.handleQuickAction(type);
        },
        handleKeyDown(event) {
            if (event.key === 'Escape' && this.visible) {
                this.handleOperation('discard');
            }
        },
        show(selectedText = '', event) {
            this.selectedText = selectedText
            const selection = window.getSelection();
            const range = selection.getRangeAt(0);
            const rect = range.getBoundingClientRect();
            this.mousePosition = {
                x: rect.left,
                y: rect.bottom
            }
            this.switchDefaultOps(this.defaultOps)
            this.visible = true
            return new Promise((resolve, reject) => {
                this.resolve = resolve
                this.reject = reject
            })
        },
        handleQuickAction(type) {
            this.loading = true
            this.aiResponse = ''
            const context = {
                stream: true,
                messages: [
                    {
                        role: 'user',
                        content: this.submitText
                    }
                ],
                model: '',
                temperature: 0.1,
                max_tokens: 1000,
                top_p: 1,
                frequency_penalty: 0,
                presence_penalty: 0
            }
            const controller = new AbortController()
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
                        if (response.created === '0') return
                        if (response.choices?.[0]?.delta?.content) {
                            this.aiResponse += response.choices[0].delta.content
                        }
                    } catch (err) {
                        console.warn('AI响应解析错误：', err)
                    }
                },
                onError: (error) => {
                    console.error('AI请求失败：', error)
                    this.loading = false
                }
            }).finally(() => {
                this.loading = false
            })
        },
        switchDefaultOps(type) {
            this.defaultOps = type;
            let ops = '';
            switch (type) {
                case 'summarize':
                    ops = `请帮我总结以下内容：\n`
                    break
                case 'translate':
                    ops = `请将以下内容翻译成中文：\n`
                    break
                case 'proofread':
                    ops = `请帮我校对以下内容：\n`
                    break
                case 'polish':
                    ops = `请帮我润色以下内容：\n`
                    break
                default:
                    ops = `请将以下内容翻译成中文：\n`
            }
            this.promptText = ops;
        },
        handleOperation(type) {
            const result = {
                type,
                content: this.aiResponse
            }
            this.resolve(result)
            this.visible = false
            this.reset()
        },
        reset() {
            this.defaultOps = 'translate'
            this.aiResponse = ''
            this.selectedText = ''
            this.resolve = null
            this.reject = null
        }
    }
}
</script>

<style scoped lang="less">
.ai-popup {
    position: fixed;
    z-index: 1000;
    background: transparent;
    width: 450px;
    display: flex;
    align-items: flex-end;
    flex-direction: column;
}

.ai-popup-content {
    display: flex;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    background: white;
    flex-direction: column;

}

.ai-dialog-content {
    display: flex;
    padding: 10px 0px 0px 0px;
    flex-direction: column;
}

.prompt-section {

    .input-wrapper {
        padding: 0 10px 0 10px;
        display: flex;
        flex-direction: row;
        align-items: center;
        border-bottom: 1px solid rgba(0, 0, 0, 0.15);

        .icon {
            font-size: 24px;
        }

        .vxe-button {
            border: none;
        }

        .input {
            width: 400px;

            height: 50px;
            border: none;

            &:focus {
                border: none;
                box-shadow: none;
                outline: none;
            }
        }
    }

    ::v-deep .ant-space {
        align-items: unset;
    }
}

.prompt-section,
.response-section {
    width: 100%;
}

.output {
    border: none;

    &:focus {
        border: none;
        box-shadow: none;
        outline: none;
    }
}

.operation-actions-wrapper {
    background: transparent;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    width: 168px;
    margin: 8px 0 8px 8px;
}

.operation-actions {
    background: white;
    display: flex;
    flex-direction: column;
    width: 100%;
    border-radius: 4px;
    overflow: hidden;

    >.info {
        font-weight: bold;
        border-radius: 0;
        width: 100%;
        display: flex;
        // align-items: center;
        border: none;
        text-align: left;
        padding: 8px 12px;
        margin: 0;
        display: flex;
        flex-direction: column;
        transition: background-color 0.3s;
    }

    >.item {
        font-weight: bold;
        border-radius: 0;
        width: 100%;
        display: flex;
        // align-items: center;
        border: none;
        text-align: left;
        padding: 8px 12px;
        margin: 0;
        display: flex;
        flex-direction: column;
        transition: background-color 0.3s;
        cursor: pointer;

        &:hover {
            background-color: #eee;
        }

        .icon {
            margin-right: 4px;
        }
    }
}

.notice {
    padding: 0 10px 0 10px;
    width: 100%;
    padding: 10px;
    display: flex;
    align-items: center;

    .icon {
        margin-right: 4px;
    }
}

.response-section {
    // background-color: #f5f5f5;
    // border-radius: 4px;
    padding: 8px;
}
</style>