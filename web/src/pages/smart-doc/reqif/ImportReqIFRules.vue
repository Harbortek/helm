<template>
    <div style="display: flex;flex-direction: column;height: 100%;">
        <div style="flex:0 0 42px;">
            <div style="margin-top: 10px;margin-left: 5px;">
                <p>
                    解析ReqIF文件中的内容，转换内容作为工作项。
                </p>
            </div>
        </div>
        <vxe-toolbar style="margin-right: 5px;flex:0 0 32px;">
            <template #tools>
                <a-space>
                    <a-button @click="onLoadRule" icon="load">加载</a-button>
                    <a-button @click="onSaveRule" icon="save">保存</a-button>
                </a-space>
            </template>
        </vxe-toolbar>
        <div class="rules-conatiner" ref="rulesContainer" :style="{ height: rulesHeight + 'px' }">

            <a-card v-for="(rule, index) in config.rules" :key="index" style="margin-top: 5px;">
                <template #title>
                    <a-row type="flex" justify="space-between" align="middle" :gutter="16"
                        style="font-size: 14px;font-weight: bold;">
                        <a-col flex="200px">{{ rule.specTypeName }} 映射为</a-col>
                        <a-col flex="auto">
                            <tracker-select :projectId="projectId" :showInternal="true" v-model="rule.target"
                                style="width:100%;" size="small" @change="onTrackerTypeChange(rule.target)" />
                        </a-col>
                        <a-col flex="50px">

                        </a-col>
                    </a-row>
                </template>

                <SpecTypeRule :data="rule" :projectId="projectId" @change="e => onRuleChange(e, index)">
                </SpecTypeRule>
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
import SpecTypeRule from './SpecTypeRule.vue';
import SaveConfigDialog from './SaveConfigDialog.vue'
import {cloneDeep} from 'lodash'
import LoadConfigDialog from './LoadConfigDialog.vue';
import TrackerSelect from '@/components/select/TrackerSelect.vue';

export default {
    name: 'ImportWordRules',
    props: {
        data: {
            type: Array,
            default: () => []
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
        SpecTypeRule, SaveConfigDialog,
        LoadConfigDialog, TrackerSelect
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
            let newRuleMap = config.rules.reduce((map, element) => (map[element.specTypeName] = element, map), {});

            for (let i = 0; i < this.config.rules.length; i++) {
                let rule = this.config.rules[i]
                if (newRuleMap[rule.specTypeName]) {
                    rule.target = newRuleMap[rule.specTypeName].target

                    let newFieldMap = newRuleMap[rule.specTypeName].fieldMappings.reduce((map, element) => (map[element.reqIFAttributeName] = element, map), {});
                    for (let j = 0; j < rule.fieldMappings.length; j++) {
                        let fieldRule = rule.fieldMappings[j]
                        if (newFieldMap[fieldRule.reqIFAttributeName]) {
                            fieldRule.trackerFieldId = newFieldMap[fieldRule.reqIFAttributeName].trackerFieldId
                            if (fieldRule.type === 'ENUMERATION') {
                                let newEnumValueMap = newFieldMap[fieldRule.reqIFAttributeName].enumMapping.reduce((map, element) => (map[element.reqIFValue] = element, map), {});
                                for (let k = 0; k < fieldRule.enumMapping.length; k++) {
                                    let enumValue = fieldRule.enumMapping[k]
                                    if (newEnumValueMap[enumValue.reqIFValue]) {
                                        enumValue.trackerEnumValueId = newEnumValueMap[enumValue.reqIFValue].trackerEnumValueId
                                    }
                                }
                            }
                        }
                    }
                }
            }

            console.log(this.config)
            this.showLoadDialog = false
            this.onChange()
        },
        onChange() {
            this.$emit('change', this.config)
        },
        onTrackerTypeChange(trackerId) {
            this.onChange()
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