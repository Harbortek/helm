<template>
    <a-modal v-model="visiable" width="500px" :footer="null" centered :closable="false">
        <template slot="title">
        </template>
        <a-spin tip="Loading...">
            <div class="spin-content">
            </div>
        </a-spin>
    </a-modal>
</template>

<script>
export default {
    name: 'LoadingDialog',
    props: {
        isShowDialog: {
            type: Boolean,
            default: false
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
}
</script>

<style lang="less" scoped>
.spin-content {
    border: 1px solid #91d5ff;
    background-color: #e6f7ff;
    padding: 30px;
}
</style>