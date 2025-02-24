<template>
    <content-page>
        <vxe-toolbar size="medium" ref="xToolbar">
            <template #buttons>
                <vxe-button v-action="'PRODUCT_LINE_CREATE'" content="新增产品" @click="onCreateProduct(null)"></vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-table size="medium" ref="xTable" show-overflow border row-key :row-config="{ isHover: true }"
            :height="tableHeight" :data="tableData"
            :tree-config="{ transform: true, accordion: false, line: true, rowField: 'id', parentField: 'parentId' }">
            <vxe-column field="name" title="产品名称" tree-node header-align="center">
            </vxe-column>
            <vxe-column field="project.name" title="关联项目" align="center">
            </vxe-column>
            <vxe-column field="project.owner" title="项目所有者" align="center">

                <template #default="{ row }">
                    <a-avatar v-if="row.project?.owner?.icon" class="avatar" size="small"
                        :src="iconUrl(row.project?.owner?.icon)" />
                    <a-avatar v-else-if="row.project?.owner?.name" class="avatar" size="small"
                        style="backgroundColor:rgb(44,178,174)">{{
                    row.project?.owner?.name }}</a-avatar>
                    {{ row.project?.owner?.name }}
                </template>
            </vxe-column>
            <vxe-column field="createBy.name" title="创建者" width="100px">

                <template #default="{ row }">
                    <a-avatar v-if="row.createBy.icon" class="avatar" size="small" :src="iconUrl(row.createBy.icon)" />
                    <a-avatar v-else class="avatar" size="small" style="backgroundColor:rgb(44,178,174)">{{
                    row.createBy.name }}</a-avatar>
                    {{ row.createBy.name }}
                </template>
            </vxe-column>
            <vxe-column field="createDate" title="创建日期" width="200px"></vxe-column>
            <vxe-column title="操作" header-align="center" align="right" width="120">

                <template #default="{ row }">
                    <a-space>
                        <vxe-button type="text" @click="onCreateProduct(row)" icon="vxe-icon-add"></vxe-button>
                        <vxe-button type="text" @click="onEditProduct(row)" icon="vxe-icon-edit"></vxe-button>
                        <vxe-button type="text" @click="onDeleteProduct(row)" icon="vxe-icon-delete"></vxe-button>
                    </a-space>
                </template>
            </vxe-column>
        </vxe-table>
        <ProductDialog :editMode="editMode" :isShowDialog="isShowDialoog" :product="currentProduct"
            :productLineId="productLineId" @close="isShowDialoog = false" @ok="onProductDialogOK" />
    </content-page>
</template>

<script>
import ContentPage from '@/components/page/content/ContentPage.vue';
import ProductDialog from '@/pages/product-line/product/ProductDialog.vue'
import { findProducts, createProduct, updateProduct, deleteProduct } from '@/services/product-line/ProductService';
import VXETable from 'vxe-table'
import { iconUrl } from "@/utils/util"
export default {
    name: 'ProductList',
    components: {
        ContentPage, ProductDialog
    },
    // props: {
    //     productLineId: {
    //         required: true,
    //     }
    // },
    data() {
        return {
            tableData: [],
            tableHeight: 400,
            editMode: 'create',
            isShowDialoog: false,
            currentProduct: {},
        }
    },
    computed: {
        productLineId() {
            return this.$route.params.productLineId
        }
    },
    watch: {
        productLineId: {
            immediate: true,
            handler(newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        }
    },
    mounted() {
        this.$nextTick(function () {
            this.tableHeight =
                window.innerHeight - (this.$refs.xTable.$el.offsetTop) - 80;

            // 监听窗口大小变化
            let self = this;
            window.onresize = function () {
                if (self.$refs.xTable?.$el) {
                    self.tableHeight =
                        window.innerHeight - (self.$refs.xTable?.$el.offsetTop) - 80;
                }
            };

        })
    },
    methods: {
        iconUrl(v) {
            return iconUrl(v)
        },
        loadData() {
            findProducts(this.productLineId).then(resp => {
                this.tableData = resp
                this.$nextTick(function () {
                    this.$refs.xTable.setAllTreeExpand(true)
                })
            })
        },
        onCreateProduct(parent) {
            this.editMode = 'create'
            this.currentProduct = { parent: parent, name: '', description: '' }
            this.isShowDialoog = true
        },
        onEditProduct(row) {
            this.editMode = 'edit'
            this.currentProduct = row
            this.isShowDialoog = true
        },
        onDeleteProduct(row) {
            if (row.children && row.children.length > 0) {
                this.$message.error('产品下有子产品，无法删除!')
                return
            }

            VXETable.modal.confirm({
                title: '删除特征',
                content: '「' + row.name + '」的数据将被删除'
            }).then(type => {
                if (type === 'confirm') {
                    deleteProduct(this.productLineId, row.id).then(resp => {
                        this.loadData()
                    })
                }
            })
        },
        onProductDialogOK(product) {
            if (product.id) {
                updateProduct(this.productLineId, product).then(resp => {
                    this.isShowDialoog = false
                    this.$message.success('更新产品成功')
                    this.loadData()
                })
            } else {
                product.productLineId = this.productLineId
                if (product.parent && product.parent.id) {
                    product.parentId = product.parent.id
                }

                createProduct(this.productLineId, product).then(resp => {
                    this.isShowDialoog = false
                    this.$message.success('创建产品成功')
                    this.loadData()
                })
            }
        },
    },
}
</script>

<style></style>