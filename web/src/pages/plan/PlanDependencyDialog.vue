<template>
    <a-modal v-model="visiable" title="选择计划" @ok="onOK" @cancel="onCancel" centered :width="800">
        <vxe-toolbar style="flex: 0 0;">
            <template #buttons>
                <a-radio-group v-model="linkType" name="linkType" default-value="FS">
                    <a-radio value="FS">{{ $t('plan.linktype.FS') }}</a-radio>
                    <a-radio value="FF">{{ $t('plan.linktype.FF') }}</a-radio>
                    <a-radio value="SS">{{ $t('plan.linktype.SS') }}</a-radio>
                    <a-radio value="SF">{{ $t('plan.linktype.SF') }}</a-radio>
                </a-radio-group>
            </template>
            <template #tools>
                <a-input-search v-model="keyword" placeholder="搜索计划" style="width:240px" type="search" clearable
                    @search="onSearch()"></a-input-search>
            </template>
        </vxe-toolbar>
        <vxe-table size="small" ref="xTable" show-overflow border row-key :show-header="true" :auto-resize="true"
            :data="tableData" :checkbox-config="{}" :row-config="{ keyField: 'id', isCurrent: true }" height="300">
            <vxe-column type="checkbox" width="50px" />
            <vxe-column field="name" title="名称" />
            <vxe-column field="owner.name" title="负责人" header-align="center" align="center" width="150px">
                <template #default="{ row }">
                    <template v-if="row.owner?.id">
                        <h-avatar :name="row.owner?.name" :icon="row.owner?.icon"></h-avatar>
                    </template>
                    <template v-else>
                        -
                    </template>
                </template>
            </vxe-column>
            <vxe-column field="planStartDate" title="开始日期" header-align="center" align="center" width="150">
                <template #default="{ row }">
                    {{ formatDate(row.planStartDate) }}
                </template>
            </vxe-column>
            <vxe-column field="planEndDate" title="结束日期" header-align="center" align="center" width="150px">
                <template #default="{ row }">
                    {{ formatDate(row.planEndDate) }}
                </template>
            </vxe-column>
        </vxe-table>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import HAvatar from '@/components/avatar/h-avatar.vue';
import { formatDate } from '@/utils/DateUtils'
export default {
    name: "PlanDependencyDialog",
    components: {HAvatar},
    data() {
        return {
            linkType: 'FS',
            tableData: [],
            keyword: '',
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        projectId: {
            required: true
        },
        data: {
            required: true,
            default: () => { return [] }
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
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }

            }
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        formatDate(v) {
            if (!v) return '-'
            else return formatDate(v)
        },
        loadData() {
            this.tableData = this.data
        },
        onSearch() {
            const keyword = this.keyword
            if (keyword === '') {
                this.tableData = this.data
            } else {
                this.tableData = this.data.filter(item => {
                    return item.name.indexOf(keyword) >= 0
                })
            }
        },
        onOK: function () {
            const records = this.$refs.xTable.getCheckboxRecords()
            if (records.length > 0) {
                records.forEach(item => item.linkType = this.linkType)
                this.$emit('ok', records)
            }
        },
        onCancel: function () {
            this.$emit("cancel");
        }
    },
    created() { }
};
</script>
<style lang="less" scoped></style>
