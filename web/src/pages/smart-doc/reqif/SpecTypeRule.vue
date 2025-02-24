<template>
    <div class="rule-panel">
        <a-row type="flex">
            <a-col v-for="(item, index) in  rule.fieldMappings " :key="index" :gutter="16" flex="400px">
                <a-row type="flex" align="middle" style="margin-top: 5px;" :gutter="16">
                    <a-col flex="200px" style="text-align: right;">
                        <span>{{ item.reqIFAttributeName }}</span>
                    </a-col>
                    <a-col flex="auto">
                        <a-select v-model="item.trackerFieldId" style="width: 100%" @change="onChange">
                            <a-select-option v-for="(field, idx) in trackerFields" :key="idx" :value="field.id">{{
                field.name }}</a-select-option>
                        </a-select>
                    </a-col>
                    <a-col flex="50px">
                        <a-button v-if="item.type === 'ENUMERATION'" icon="setting" type="link"
                            @click="onItemExpand(item)"></a-button>
                    </a-col>
                </a-row>

                <a-row v-if="item.type === 'ENUMERATION' && item.open">
                    <a-col flex="auto">
                        <a-row v-for="(enumItem, enumIndex) in item.enumMapping" :key="enumIndex" type="flex"
                            align="middle" style="margin-top: 5px;" :gutter="16">
                            <a-col flex="200px" style="text-align: right;">
                                <span>{{ enumItem.reqIFValue }}</span>
                            </a-col>
                            <a-col flex="auto">
                                <a-select v-model="enumItem.trackerEnumValueId" style="width: 100%" @change="onChange">
                                    <a-select-option
                                        v-for="(optionItem) in trackerFieldEnumValues(item.trackerFieldId) "
                                        :key="optionItem.id" :value="optionItem.id">{{
                optionItem.name }}</a-select-option>
                                </a-select>
                            </a-col>
                            <a-col flex="50px">

                            </a-col>
                        </a-row>
                    </a-col>
                </a-row>
            </a-col>
        </a-row>
        <div style="margin-top: 5px;">
            <a-select v-model="fieldToAdd" style="width: 200px;">
                <a-select-option v-for="(field, idx) in remainedTrackerFields" :key="idx" :value="field.id">{{
                field.name }}</a-select-option>
            </a-select>
            <a-button icon="plus" type="link" @click="onAddField"></a-button>
        </div>
    </div>
</template>

<script>
import { findOneTracker } from '@/services/tracker/TrackerService';
import XEUtils, { getType } from 'xe-utils';
import {cloneDeep} from 'lodash'
import { ca } from 'date-fns/locale';

export default {
    name: 'SpecTypeRule',
    props: {
        data: {
            required: true,
        },
        projectId: {
            type: String,
            default: ''
        }
    },
    components: {
    },
    watch: {
        data: {
            handler(val) {
                this.rule = cloneDeep(val)
                console.log(this.rule)
                this.rule.fieldMappings.forEach(element => {
                    if (element.open === undefined) {
                        element.open = false
                    }
                });

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
    computed: {
        remainedTrackerFields() {
            return this.trackerFields.filter(item => !this.rule.fieldMappings.find(field => field.trackerFieldId === item.id))
        },
        trackerFields() {
            if (this.tracker && this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                return this.tracker.trackerFields.filter(item => item.inputType !== 'ITEM_NO' && item.inputType !== 'WORK_ITEM' && item.inputType !== 'STATUS_TYPE' && item.inputType !== 'WORK_ITEM_TYPE' && item.inputType !== 'PROJECT')
            }
            return []
        },
    },
    data() {
        return {
            rule: {
                target: ''
            },
            tracker: {},
            fieldToAdd: '',
        }
    },
    methods: {
        onChange() {
            this.$emit('change', this.rule)
        },
        onItemExpand(item) {
            item.open = !item.open
            this.rule = { ...this.rule }
            console.log(this.rule.fieldMappings)
        },
        trackerFieldEnumValues(trackerFieldId) {
            let trackerField = this.trackerFields.filter(item => item.id === trackerFieldId)[0]
            if (trackerField && trackerField.inputType === 'STATUS') {
                console.log(this.tracker.trackerStatuses)
                return this.tracker.trackerStatuses
            }

            if (trackerField && trackerField.items) {
                return trackerField.items
            }
            return []
        },
        onAddField() {
            let selectedField = this.trackerFields.filter(item => item.id === this.fieldToAdd)[0];

            if (selectedField) {
                let enumValues = this.trackerFieldEnumValues(selectedField.id)
                let enumMapping = []
                let type = this.getType(selectedField.inputType)

                if (enumValues.length > 0) {
                    enumValues.forEach(item => {
                        enumMapping.push({
                            reqIFValue: item.name,
                            trackerEnumValueId: item.id,
                        })
                    })
                }
                this.rule.fieldMappings.push({
                    open: false,
                    trackerFieldId: selectedField.id,
                    enumMapping: enumMapping,
                    reqIFAttributeName: selectedField.name,
                    type: type
                })
                let remained = this.remainedTrackerFields
                if (remained.length > 0) {
                    this.fieldToAdd = remained[0].id
                } else {
                    this.fieldToAdd = ''
                }
                this.onChange()
            }

        },
        getType(inputType) {
            let type = 'STRING'
            switch (inputType) {
                case 'BOOL':
                    type = 'BOOLEAN'
                    break;
                case 'DATE':
                    type = 'DATE'
                    break;
                case 'DECIMAL':
                    type = 'REAL'
                    break
                case 'DURATION':
                    type = 'INTEGER'
                    break;
                case 'INTEGER':
                    type = 'INTEGER'
                    break;
                case 'OPTIONS':
                case 'MULTI_OPTIONS':
                case 'STATUS':
                    type = 'ENUMERATION'
                    break;
                default:
                    type = 'STRING'
            }
            return type
        }
    },
}
</script>

<style></style>