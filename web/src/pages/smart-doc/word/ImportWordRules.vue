<template>
    <div style="display: flex;flex-direction: column;height: 100%;">
        <div style="flex:0 0 42px;">
            <div style="margin-top: 10px;margin-left: 5px;">
                <p>
                    解析Word文件中的段落或者表格，抽取内容作为工作项。
                </p>
            </div>
        </div>
        <vxe-toolbar style="margin-right: 5px;flex:0 0 32px;">
            <template #buttons>
                <a-dropdown :trigger="['click']">
                    <a-button style="margin-left: 8px" icon="plus"> 新建规则 <a-icon type="down" /> </a-button>
                    <a-menu slot="overlay" @click="onCreateRule">
                        <a-menu-item key="PARAGRAPH">段落抽取规则</a-menu-item>
                        <a-menu-item key="TABLE">表格抽取规则</a-menu-item>
                    </a-menu>
                </a-dropdown>
            </template>
            <template #tools>
                <a-space>
                    <a-button @click="onLoadRule" icon="load">加载</a-button>
                    <a-button @click="onSaveRule" icon="save">保存</a-button>
                </a-space>
            </template>
        </vxe-toolbar>
        <div class="rules-conatiner" ref="rulesContainer" :style="{ height: rulesHeight + 'px' }">
            <a-card v-for="(rule, index) in config.rules" :key="index">
                <template #title>
                    <span>规则{{ index + 1 }} </span>
                    <span v-if="rule.type === 'PARAGRAPH'">段落抽取规则</span>
                    <span v-else>表格抽取规则</span>
                </template>

                <template #extra>
                    <a-col flex="30px"><vxe-button type="text" icon="vxe-icon-delete"
                            @click="onRemoveRule(index)"></vxe-button></a-col>
                </template>
                <paragraph-rule v-if="rule.type === 'PARAGRAPH'" :data="rule" :docProps="docProps"
                    :projectId="projectId" @change="e => onRuleChange(e, index)" />
                <table-rule v-if="rule.type === 'TABLE'" :data="rule" :docProps="docProps" :projectId="projectId"
                    @change="e => onRuleChange(e, index)" />
            </a-card>
        </div>
        <save-config-dialog :showDialog="showSaveDialog" :config="configToSave" :projectId="projectId" @ok="onSaveOK"
            @cancel="
                    showSaveDialog = false" />
        <load-config-dialog :showDialog="showLoadDialog" :projectId="projectId" @ok="onLoadConfig" @cancel="
                    showLoadDialog = false" />
    </div>
</template>

<script>
import ParagraphRule from './ParagraphRule.vue';
import TableRule from './TableRule.vue';
import SaveConfigDialog from './SaveConfigDialog.vue'
import {cloneDeep} from 'lodash'
import LoadConfigDialog from './LoadConfigDialog.vue';
export default {
    name: 'ImportWordRules',
    props: {
        data: {
            type: Array,
            default: () => []
        },
        docProps: {
            type: Object,
            default: () => ({})
        },
        projectId: {
            type: String,
            default: ''
        },
        pageId: {
            type: String,
            default: ''
        },
    },
    components: {
        ParagraphRule, TableRule, SaveConfigDialog,
        LoadConfigDialog
    },
    watch: {
        data: {
            handler(val) {
                this.config.rules = cloneDeep(val)
                console.log(this.config.rules)
            },
            immediate: true
        }
    },
    data() {
        return {
            config: {
                name: '',
                rules: [],
            },
            configToSave: {},
            showSaveDialog: false,
            showLoadDialog: false,
            rulesHeight: 600,
        }
    },
    mounted() {
        setTimeout(() => {
            this.$nextTick(() => {
                if (this.$refs.rulesContainer) {
                    this.rulesHeight =
                        window.innerHeight - this.$refs.rulesContainer.offsetTop - 100;
                }

                // 监听窗口大小变化
                let self = this;
                window.onresize = function () {
                    if (self.$refs.rulesContainer) {
                        self.rulesHeight =
                            window.innerHeight - self.$refs.rulesContainer.offsetTop - 100;
                    }
                };
            })
        }, 100)
    },
    methods: {
        onCreateRule({ key }) {
            if (key === 'PARAGRAPH') {
                this.config.rules.push({
                    type: key,
                    scope: 'ALL',
                    conditionsMatchType: 'ANY',
                    conditions: [{ conditionId: '', argument: '' }],
                    nextParagraph: false,
                    nextParagraph: {
                        conditionsMatchType: 'ANY',
                        conditions: [{ conditionId: '', argument: '' }]
                    },
                    actions: [],
                    firstParagraphAsTitle: true,
                })
            } else {
                this.config.rules.push({
                    type: key,
                    scope: 'ALL',
                    conditionsMatchType: 'ANY',
                    conditions: [{ conditionId: '', argument: '' }],
                    conversions: [{
                        conditionsMatchScope: 'ALL',
                        target: '',
                        conversionType: 'BY_TABLE',
                        conditionsMatchType: 'ANY',
                        conditions: [{ conditionId: '', argument: '' }],
                        columnMap: {},
                        actions: [],
                    }]
                })
            }
            this.onChange()
        },
        onRemoveRule(index) {
            this.config.rules.splice(index, 1)
            this.onChange()
        },
        onLoadRule() {
            this.showLoadDialog = true
        },
        onSaveRule() {
            this.configToSave = cloneDeep(this.config)
            this.showSaveDialog = true
        },
        onSaveOK(config) {
            this.config = config
            this.showSaveDialog = false
            this.onChange()
        },
        onRuleChange(rule, index) {
            console.log('rule changed', rule, index)
            this.config.rules[index] = cloneDeep(rule)
            this.onChange()
        },
        onLoadConfig(config) {
            this.config = config
            this.showLoadDialog = false
            this.onChange()
        },
        onChange() {
            this.$emit('change', this.config)
        }
    },
}
</script>

<style lang="less" scoped>
.rules-conatiner {
    margin: 5px;
    overflow-y: auto;
}
</style>