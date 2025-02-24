<template>
    <div class="contains-container">
        <a-row type="flex" align="middle" style="margin-top: 5px;" v-for="(condition, conditionIndex) in conditions"
            :key="conditionIndex">
            <a-col flex="200px">
                <a-select v-model="condition.conditionId" style="width: 200px;" size="small"
                    @change="onChangeConditionType(condition)">
                    <a-select-option :value="''" :key="'-'">---</a-select-option>
                    <a-select-option :value="item.id" v-for="item in  conditionTypes" :key="item.id">{{
            item.name
        }}</a-select-option>
                </a-select>
            </a-col>
            <a-col flex="auto" style="padding-left: 5px;">
                <a-input v-model="condition.argument" v-if="hasArgument(condition) && argumentType(condition) == 'TEXT'"
                    size="small" @change="onChange"></a-input>
                <a-select v-model="condition.argument" style="width: 100%;" size="small"
                    v-if="hasArgument(condition) && argumentType(condition) == 'STYLE'" @change="onChange">
                    <a-select-option :value="item" v-for="item in  docProps.styles" :key="item">{{ item
                        }}</a-select-option>
                </a-select>
            </a-col>
            <a-col flex="30px">
                <vxe-button type="text" icon="vxe-icon-add" v-if="conditionIndex === conditions.length - 1" size="small"
                    @click="onAddCondition()"></vxe-button>
                <vxe-button type="text" icon="vxe-icon-minus" v-else size="small"
                    @click="onRemoveCondition(conditionIndex)"></vxe-button>
            </a-col>
        </a-row>
    </div>
</template>

<script>
import {cloneDeep} from 'lodash'
import { CONDITION_TYPES } from '@/pages/smart-doc/util/rules'
export default {
    name: 'ConditionsTable',
    props: {
        data: {
            type: Array,
            required: true,
        },
        docProps: {
            type: Object,
            default: () => ({})
        },
    },
    components: {

    },
    data() {
        return {
            conditions: [],
            conditionTypes: cloneDeep(CONDITION_TYPES),
        }
    },
    watch: {
        data: {
            handler(newVal, oldVal) {
                this.conditions = cloneDeep(newVal);
                console.log(this.conditions)
            },
            immediate: true
        }
    },
    methods: {
        hasArgument(condition) {
            let condintionType = this.conditionTypes.filter(item => item.id === condition.conditionId)[0]
            if (condintionType)
                return condintionType.hasArgument
            return false
        },
        argumentType(condition) {
            let condintionType = this.conditionTypes.filter(item => item.id === condition.conditionId)[0]
            if (condintionType)
                return condintionType.argumentType
            return ''
        },
        onChangeConditionType(condition) {
            condition.argument = ''
            this.onChange()
        },
        onAddCondition() {
            this.conditions.push({ conditionId: this.conditionTypes[0].id, argument: '' })
            this.onChange()
        },
        onRemoveCondition(index) {
            this.conditions.splice(index, 1)
            this.onChange()
        },
        onChange() {
            this.$emit('change', this.conditions)
        }
    },
}
</script>

<style></style>