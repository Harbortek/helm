<template>
    <a-modal v-model="visiable" title="动态枚举脚本" @ok="onOK" @cancel="onCancel" width="800px" centered>
        <div style="height: 52px;margin-top: 0px;">
            <vxe-toolbar>
                <template #buttons>
                    <span style="font-weight: 600;">JavaSCript脚本：</span>
                    <vxe-button status="primary" @click="onTest">测试</vxe-button>
                </template>
                <template #tools>
                    <vxe-button status="default" @click="example">例子</vxe-button>
                </template>
            </vxe-toolbar>
        </div>
        <div class="code-container" :style="{ height: editorHeight + 'px' }">
            <div id="code-editor" ref="code-editor" class="code-editor" style=""></div>
        </div>
        <a-tabs v-model="activeTab" :animated="false">
            <a-tab-pane key="1" tab="运行结果">
                <div style="height: 200px;margin-top: 10px;">

                    <vxe-table ref="xTable" :data="tableData" :row-config="{ isHover: true }" stripe height="200">
                        <vxe-column field="name" title="枚举值" header-align="center" :edit-render="{}">
                            <template #default="{ row }">
                                <span :style="{ color: row.color, backgroundColor: row.backgroudColor }">{{ row.name
                                    }}</span>
                            </template>
                        </vxe-column>
                        <vxe-column field="color" title="颜色" header-align="center" align="center" width="100">
                            <template #default="{ row }">
                                <a-space>
                                    <div class="box" :style="{ 'background-color': row.color }">
                                    </div>

                                    <div class="box" :style="{ 'background-color': row.backgroundColor }">
                                    </div>
                                </a-space>
                            </template>
                        </vxe-column>
                        <vxe-column field="icon" title="图标" header-align="center" align="center" width="80">
                            <template #default="{ row }">
                                <h-icon :type="row.icon" />
                            </template>
                        </vxe-column>
                    </vxe-table>
                </div>
            </a-tab-pane>
            <a-tab-pane key="2" tab="运行日志">
                <div class="log" style="height: 200px;margin-top: 10px;">
                    {{ consoleOutput }}
                </div>
            </a-tab-pane>
        </a-tabs>
    </a-modal>
</template>

<script>
import * as monaco from 'monaco-editor';
import {cloneDeep} from 'lodash'
import { testScript } from '@/services/system/EnumService'
import MonacoEditorCopilot from 'monaco-editor-copilot';
import Cookies from 'js-cookie'
export default {
    name: 'EnumScriptDialog',
    props: {
        enumCategory: {
            type: Object
        },
        projectId: {
            type: String
        },
        isShowDialog: {
            type: Boolean
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
            consoleOutput: '',
            tableData: [],
            activeTab: '1',
        }
    },
    computed: {
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
    },
    watch: {
        visiable() {
            if (this.visiable) {
                this.loadData();
            }
        }
    },
    mounted() {

    },
    methods: {
        loadData() {
            if (this.enumCategory && this.enumCategory.script) {
                this.script = this.enumCategory.script
            } else {
                this.script = ''
            }
            this.$nextTick(() => {
                this.createEditor()
            })
        },
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
            this.script = 'function values() {\n'
                + '\tvar sprintService = context.getBean("sprintService"); \n'
                + '\tvar sprints = sprintService.findSprints(projectId);\n'
                + '\treturn sprints;\n'
                + '}\n'
            this.setScript()
        },
        onTest() {
            const data = cloneDeep(this.enumCategory)
            data.script = this.script
            testScript(data).then(res => {
                if (res.errorMessage && res.errorMessage.length > 0) {
                    this.consoleOutput = res.errorMessage
                    this.activeTab = '2'
                } else {
                    this.consoleOutput = '运行成功'
                    this.activeTab = '1'
                }
                this.tableData = res.data
            })
        },
        onOK() {
            this.$emit('ok', this.getScript())
        },
        onCancel() {
            this.$emit('cancel')
        }
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

.log {
    background-color: #202d40;
    color: #f5f5f5;
    padding: 0.5rem 0;
    font-size: 12px;
    font-family: SFMono-Regular, Consolas, Liberation Mono, Menlo, Courier, monospace !important;
    margin: 0;
    white-space: pre-wrap;

    .line {
        display: -ms-flexbox;
        display: flex;
        -ms-flex-direction: row;
        flex-direction: row;
        line-height: 18px;

        .line-number {
            display: inline-block;
            min-width: 40px;
            color: #777;
            text-align: right;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
            height: 18px;
        }

        .line-content {
            padding-left: 15px;
            padding-right: 15px;
            max-width: 100%;
            word-break: break-word;
            text-wrap: nowrap;
            height: 18px;
        }
    }


}
</style>