<template>
    <div>
        <div style="height: 52px;margin-top: 0px;">
            <vxe-toolbar>
                <template #buttons>
                    <span style="font-weight: 600;">JavaSCript脚本：</span>
                </template>
                <template #tools>
                    <vxe-button status="default" @click="example">例子</vxe-button>
                </template>
            </vxe-toolbar>
        </div>
        <div class="code-container" :style="{ height: editorHeight + 'px' }">
            <div id="code-editor" ref="code-editor" class="code-editor" style=""></div>
        </div>

    </div>
</template>

<script>
import * as monaco from 'monaco-editor';
import {cloneDeep} from 'lodash'
import MonacoEditorCopilot from 'monaco-editor-copilot';
import Cookies from 'js-cookie'
export default {
    name: 'ExecuteCustomScriptPanel',
    props: {
        tracker: {
            type: Object
        },
        action: {
            type: Object
        }
    },
    components: {
    },
    data() {
        return {
            editorHeight: 248,
            editorOption: {
                tabSize: 2,
                styleActiveLine: true,
                lineNumbers: true,
                line: true,
                lineWrapping: true,
                mode: 'text/javascript',
                theme: 'default',
                hintOptions: {
                    // 自定义提示选项
                    completeSingle: false // 当匹配只有一项的时候是否自动补全
                }
            },
            script: '',
            monacoEditor: null,
        }
    },
    computed: {

    },
    watch: {
        action: {
            handler: function (newVal, oldVal) {
                if (newVal && newVal.content) {
                    this.script = JSON.parse(newVal.cotent)
                }
                this.$nextTick(() => {
                    this.createEditor()
                })
            },
            immediate: true,
        },
    },
    mounted() {

    },
    methods: {
        createEditor() {
            const el = document.getElementById("code-editor")
            if (el) {
                this.monacoEditor = monaco.editor.create(el, {
                    value: this.rawContent, // 初始文字
                    readOnly: false, // 是否只读
                    automaticLayout: true, // 自动布局
                    theme: 'vs-light', // vs | hc-black | vs-dark
                    language: 'JavaScript',
                    minimap: {
                        enabled: false,// 关闭小地图
                    },
                    tabSize: 2, // tab缩进长度
                    fontSize: 14, // 文字大小,
                    scrollBeyondLastLine: false,
                });
                const config = {
                    openaiKey: Cookies.get("Authorization").slice(7),
                    openaiUrl: process.env.VUE_APP_API_BASE_URL + '/ai/openai',
                    assistantMessage: 'use sql mysql',
                    openaiParams: {
                        model: '',
                    },
                };

                const dispose = MonacoEditorCopilot(this.monacoEditor, config);
                this.setScript()
            }
        },
        setScript() {
            monaco.editor.getModels().forEach(model => model.dispose());
            const model = monaco.editor.createModel(
                this.script,
                'JavaScript'
            )

            this.monacoEditor.setModel(model)
            this.monacoEditor.onDidChangeModelContent(() => {
                this.script = this.monacoEditor.getValue()
            })
        },
        getScript() {
            return this.script
        },
        getData() {
            return JSON.stringify(this.script)
        },
        getDescription() {
            return 'JavaScript Code'
        },
        validate(callback) {
            if (this.script && this.script.length > 0) {
                callback(true)
            } else {
                this.$message.error('需要设置脚本内容')
                callback(false)
            }
        },
        example() {
            this.script = 'var trackerItemId = trackerItem.id\n var trackerService = context.getService("trackerService");\n '
            this.setScript()
        },
    },
}
</script>

<style lang="less" scoped>
.code-container {
    background: #f5f6f7;
    box-sizing: border-box;
    min-height: 248px;
    color: var(--deTextPrimary, #1f2329);
    border: solid 1px #ccc;

    .code-editor {
        width: 100%;
        height: 248px;
    }
}
</style>