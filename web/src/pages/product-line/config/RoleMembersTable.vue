<template>
    <div>
        <vxe-table ref="rtable" class="role-members-table" size="medium" :data="tableData" :loading="loading"
            :row-config="{ isHover: true }" show-footer :footer-method="footerMethod" stripe max-height="400"
            :empty-text="emptyText || '暂无数据'">
            <vxe-column :title="title">
                <template #default="{ row }">
                    {{ row.name }}
                    <span class="domain-list-cell-subtext"> {{ row.description }}</span>
                    <span class="tag-role-type">
                        {{ typeName(row) }}
                    </span>
                </template>

                <template #footer="{}">
                    <product-line-role-member-select ref="rmSelect" :productLineId="productLineId" :scope="scope"
                        :exclude="tableData" show-search
                        :getPopupContainer="triggerNode => { return triggerNode.parentNode || document.body }"
                        optionFilterProp='children' @change="onChange" style="width:400px;" />
                </template>
            </vxe-column>
            <vxe-column title="" show-overflow :width="60">

                <template #default="{ row }">
                    <vxe-button style="font-size: 18px;" title="移除" type="text" icon="vxe-icon-close"
                        @click="onClickDelete(row)"></vxe-button>
                </template>
            </vxe-column>
        </vxe-table>
    </div>
</template>

<script>
import ProductLineRoleMemberSelect from "./ProductLineRoleMemberSelect.vue"
export default {
    name: 'RoleMembersTable',
    components: { ProductLineRoleMemberSelect },
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        title: String,
        scope: String,
        productLineId: String,
        value: Array,
        defaultValue: { type: Array, default: () => { return [] } },
        tableHeight: {
            type: Number | String,
            default: 200
        },
        emptyText: String
    },
    data() {
        return {
            loading: false,
            selectIdentity: null,
            tableData: [],
            data: []
        }
    },
    watch: {
        value: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal === null || newVal === undefined) {
                    this.tableData = []
                } else {
                    this.tableData = newVal || []
                }
            }
        },
    },
    computed: {
    },
    mounted: function () {
    },
    methods: {
        onClickDelete: function (row) {
            this.$emit('delete', row)

        },
        deleteRow(row) {
            this.tableData = this.tableData.filter(item => { return item.id != row.id })
        },
        footerMethod: function ({ columns, data }) {
            return [
                columns.map(column => {
                    return null
                })
            ]
        },
        onChange: function (e, v) {
            console.log(v)

            this.tableData.push(v)

            this.$emit('add', v)

            this.$emit('change', this.tableData)

        },
        typeName: function (item) {
            if (item.type === 'IDENTITY_ROLE') {
                return '角色'
            } else if (item.type === 'IDENTITY_SPECIAL_ROLE') {
                return '特殊角色'
            } else if (item.type === 'IDENTITY_USER') {
                return '成员'
            }
        }
    },
    created() {
    },
}
</script>

<style lang="less" scoped>
.tag-role-type {
    margin-left: 8px;
    color: #909090;
    border-color: #909090;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 12px;
    box-sizing: border-box;
    height: 18px;
    line-height: 18px;
    transition: border-color .2s;
    border: 1px solid;
    border-radius: 20px;
    padding: 0px 8px;
}

.domain-list-cell-subtext {
    margin-left: 5px;
    font-size: 12px;
    color: #606060;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 180px;
    flex: 0 0 auto;
    color: var(--gray-80);
}
</style>
