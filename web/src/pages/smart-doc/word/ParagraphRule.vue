<template>
    <div class="rule-panel">
        <fieldset>
            <legend>工作项开始</legend>
            <a-row type="flex" align="middle">
                <a-col flex="250px">
                    <a-radio-group v-model="rule.scope" size="small" @change="onChange">
                        <a-radio-button value="ALL">
                            从整篇文档
                        </a-radio-button>
                        <a-radio-button value="PARAGRAPHS">
                            从指定段落
                        </a-radio-button>
                    </a-radio-group>

                </a-col>
                <a-col flex="auto">
                    <a-select v-model="rule.source" style="width: 300px;padding-right: 10px;" mode="multiple"
                        v-if="rule.scope === 'PARAGRAPHS'" size="small" @change="onChange">
                        <a-select-option :value="heading.id" v-for="heading in  docProps?.headings"
                            :key="heading.numbering">{{
                        heading.numbering + heading.name }}</a-select-option>
                    </a-select>
                </a-col>
                <a-col flex="80px">中抽取内容</a-col>
            </a-row>
            <a-row type="flex" align="middle" style="margin-top: 5px;">
                <a-col flex="30px">当</a-col>
                <a-col flex="300px">
                    <a-radio-group v-model="rule.conditionsMatchType" size="small" @change="onChange">
                        <a-radio-button value="ALL">
                            所有条件都满足
                        </a-radio-button>
                        <a-radio-button value="ANY">
                            任意一个条件满足
                        </a-radio-button>
                    </a-radio-group>
                </a-col>
                <a-col flex="90px">时转换为</a-col>
                <a-col flex="auto">
                    <tracker-select :projectId="projectId" v-model="rule.target" style="width:100%;" size="small"
                        @change="onTrackerTypeChange(rule.target)" />
                </a-col>
            </a-row>
            <conditions-table :data="rule.conditions" :docProps="docProps" @change="e => onConditionsChange(rule, e)" />
        </fieldset>
        <fieldset>
            <legend>工作项内容</legend>
            <a-row type="flex" align="middle" style="margin-top: 5px;">
                <a-col flex="30px">当</a-col>
                <a-col flex="300px">
                    <a-radio-group v-model="rule.nextParagraph.conditionsMatchType" size="small" @change="onChange">
                        <a-radio-button value="ALL">
                            所有条件都满足
                        </a-radio-button>
                        <a-radio-button value="ANY">
                            任意一个条件满足
                        </a-radio-button>
                    </a-radio-group>
                </a-col>
                <a-col flex="auto">将下一段落作为工作项描述</a-col>
            </a-row>
            <conditions-table :data="rule.nextParagraph.conditions" :docProps="docProps"
                @change="e => onConditionsChange(rule.nextParagraph, e)" />
        </fieldset>
        <fieldset>
            <legend>工作项属性</legend>

            <actions-table :rule="rule" :tracker="tracker" :docProps="docProps" :projectId="projectId"
                @change="e => onActionsChange(rule, e)" />

        </fieldset>
        <a-checkbox v-model="rule.firstParagraphAsTitle" @change="onChange">
            <span style="font-size: 12px;">提取第一个段落作为标题</span>
        </a-checkbox>
    </div>
</template>

<script>
import ConditionsTable from './ConditionsTable.vue';
import ActionsTable from './ActionsTable.vue';
import TrackerSelect from '@/components/select/TrackerSelect.vue';
import { findOneTracker } from '@/services/tracker/TrackerService';
import XEUtils from 'xe-utils';
import {cloneDeep} from 'lodash'
export default {
    name: 'ParagraphRule',
    props: {
        data: {
            required: true,
        },
        docProps: {
            type: Object,
            default: () => ({})
        },
        projectId: {
            type: String,
            default: ''
        }
    },
    components: {
        ConditionsTable, ActionsTable, TrackerSelect
    },
    watch: {
        data: {
            handler(val) {
                this.rule = cloneDeep(val)
                if (this.rule.source && Array.isArray(this.rule.source)) {
                    let newSource = []
                    for (let i = 0; i < this.rule.source.length; i++) {
                        const heading = XEUtils.find(this.docProps.headings, item => item.id === this.rule.source[i])
                        if (heading) {
                            newSource.push(heading.id)
                        }
                    }
                    this.rule.source = newSource
                }

                console.log('new rule loaded', this.rule)
                if (this.rule.target) {
                    const trackerId = this.rule.target
                    findOneTracker(trackerId).then(resp => {
                        this.tracker = resp
                    })
                }
            },
            immediate: true,
        }
    },
    data() {
        return {
            rule: {
                type: 'PARAGRAPH',
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
            },
            tracker: {},
        }
    },
    methods: {
        onTrackerTypeChange(trackerId) {
            findOneTracker(trackerId).then(resp => {
                this.tracker = resp
            })
            this.onChange()
        },
        onConditionsChange(rule, conditions) {
            rule.conditions = conditions
            this.onChange()
        },
        onActionsChange(rule, actions) {
            rule.actions = actions
            this.onChange()
        },
        onChange() {
            this.$emit('change', this.rule)
        }
    },
}
</script>

<style lang="less" scoped>
.rule-panel {
    border: none;
    padding: 0px;

    fieldset {
        border: solid 1px #d9d9d9;
        margin-top: 5px;
    }

    legend {
        font-size: 12px;
        font-weight: bold;
    }
}
</style>