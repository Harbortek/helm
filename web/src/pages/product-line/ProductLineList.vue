<template>
    <content-page>
        <vxe-toolbar size="medium" ref="xToolbar">
            <template #buttons>
                <vxe-button v-action="'PRODUCT_LINE_CREATE'" status="primary" content="新建产品线" @click="onCreateProductLine"></vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-table size="medium" ref="xTable" show-overflow border row-key :row-config="{ isHover: true }"
            :height="tableHeight" :data="tableData">
            <vxe-column field="name" title="产品线名称" header-align="center">

                <template #default="{ row }">
                    <vxe-button type="text" status="primary" @click="gotoProductLine(row)">{{ row.name }}</vxe-button>
                </template>
            </vxe-column>
            <vxe-column field="createBy.name" title="创建者" width="200px" align="center">

                <template #default="{ row }">
                    <a-avatar v-if="row.createBy.icon" class="avatar" size="small" :src="iconUrl(row.createBy.icon)" />
                    <a-avatar v-else class="avatar" size="small" style="backgroundColor:rgb(44,178,174)">{{
                    row.createBy.name }}</a-avatar>
                    {{ row.createBy.name }}
                </template>
            </vxe-column>
            <vxe-column field="createDate" title="创建日期" width="200px" align="center"></vxe-column>
            <vxe-column title="操作" align="center" width="200">

                <template #default="{ row }">
                    <a-space v-action="'PRODUCT_LINE_CREATE'">
                        <vxe-button type="text" @click="onEditProductLine(row)" icon="vxe-icon-edit">编辑</vxe-button>
                        <vxe-button type="text" @click="onDeleteProductLine(row)" icon="vxe-icon-delete">删除</vxe-button>
                    </a-space>
                </template>
            </vxe-column>
        </vxe-table>


        <ProductLineDialog :editMode="editMode" :isShowDialog="isShowDialoog" :productId="currentProductLineId"
            @close="isShowDialoog = false" @ok="onProductLineDialogOK" />
    </content-page>
</template>

<script>
import ContentPage from '@/components/page/content/ContentPage.vue';
import ProductLineDialog from './ProductLineDialog.vue';
import { iconUrl } from "@/utils/util"
import VXETable from "vxe-table";
import Sortable from "sortablejs"
import XEUtils from "xe-utils";
import { findProductLines, createProductLine, updateProductLine, deleteProductLine } from '@/services/product-line/ProductLineService';

export default {
    name: 'ProductLineList',
    components: {
        ContentPage, ProductLineDialog
    },
    data() {
        return {
            editMode: 'create',
            isShowDialoog: false,
            currentProductLineId: undefined,
            tableData: [],
            tableHeight: 400,
            activeTab: 'ASSETS',
        }
    },
    mounted() {
        this.$nextTick(function () {
            this.tableHeight =
                window.innerHeight - (this.$refs.xTable.$el.offsetTop) - 80;
            console.log(this.tableHeight)

            // 监听窗口大小变化
            let self = this;
            window.onresize = function () {
                if (self.$refs.xTable.$el) {
                    self.tableHeight =
                        window.innerHeight - (self.$refs.xTable.$el.offsetTop) - 80;
                }
            };

        })
        this.loadData()
    },
    methods: {
        iconUrl(v) {
            return iconUrl(v)
        },
        loadData() {
            findProductLines().then(resp => {
                this.tableData = resp || []
                // 默认选中第一行
                if (this.tableData.length > 0) {
                    this.$refs.xTable.setRadioRow(this.tableData[0])
                }
            })
        },

        onCreateProductLine() {
            this.editMode = 'create'
            this.currentProductLineId = undefined
            this.isShowDialoog = true
        },
        onEditProductLine(row) {
            this.editMode = 'edit'
            this.currentProductLineId = row.id
            this.isShowDialoog = true
        },
        onDeleteProductLine(row) {
            VXETable.modal.confirm({
                title: '删除产品线',
                content: '「' + row.name + '」的数据将被删除'
            }).then(type => {
                if (type === 'confirm') {
                    deleteProductLine(row.id).then(resp => {
                        this.loadData()
                    })
                }
            })
        },
        onProductLineDialogOK(productLine) {
            if (this.currentProductLineId) {
                productLine.productId = this.currentProductLineId
                updateProductLine(productLine).then(resp => {
                    this.isShowDialoog = false
                    this.$message.success('更新产品线成功')
                    this.loadData()
                })
            } else {
                createProductLine(productLine).then(resp => {
                    this.isShowDialoog = false
                    this.$message.success('创建产品线成功')
                    this.loadData()
                })
            }
        },
        gotoProductLine(row) {
            this.$router.push({
                name: 'productlineMain',
                params: {
                    productLineId: row.id
                }
            })
        }
    },
}
</script>

<style lang="less" scoped>
::v-deep .row--radio {
    background-color: #f0f7ff !important;
    font-weight: bold;

    td:first-child {
        border-left: 4px #1989fa solid !important;
    }
}
</style>