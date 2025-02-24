<template>
    <a-modal v-model="visiable" title="选择迭代" @ok="onOK" @cancel="onCancel" centered :width="800">
        <vxe-toolbar style="flex: 0 0;">
            <template #tools>
                <vxe-input v-model="keyword" placeholder="搜索迭代" style="width:240px" type="search" clearable></vxe-input>
            </template>
        </vxe-toolbar>
        <vxe-table size="small" ref="xTable" show-overflow border row-key :show-header="true" :auto-resize="true"
            :data="tableData" :checkbox-config="{}" :row-config="{ keyField: 'id', isCurrent: true }" height="300">
            <vxe-column type="checkbox" width="50px" />
            <vxe-column field="name" title="名称" />
            <vxe-column field="owner.name" title="负责人" header-align="center" align="center" width="150px">
                <template #default="{ row }">
                    <template v-if="row.owner.id">
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
import HAvatar from '@/components/avatar/h-avatar.vue';
import {cloneDeep} from 'lodash'
import { formatDate } from '@/utils/DateUtils'
import { findUnPlanedSprints } from '@/services/plan/SprintService'
export default {
    name: "SprintSelectDialog",
    components: {HAvatar},
    data() {
        return {
            keyword: '',
            tableData: []
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        projectId: {
            required: true
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
        loadData: function () {
            findUnPlanedSprints(this.projectId).then(resp => {
                this.tableData = resp
            })
        },
        onOK: function () {
            const records = this.$refs.xTable.getCheckboxRecords()
            if (records.length > 0) {
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
