<template>
    <div class="parameter-panel">
        <a-form-model ref="labelForm" label-width="80px" size="mini">
            <a-form-model-item label="网页地址" class="form-item">
                <a-input class="input-box" v-model="webUrl" placeholder="请输入网页地址" @pressEnter="onChange" />
            </a-form-model-item>
        </a-form-model>
        <!-- <div class="title-bar">
            <span class="title-bar-title">网页地址</span>
        </div>
        <div class="input-bar">
            <a-input class="input-box" v-model="webUrl" size="small" placeholder="请输入网页地址" @pressEnter="onChange" />
        </div> -->
    </div>
</template>

<script>
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'

export default {
    name: 'WebPageEditor',
    props: {
        pageId: {
            type: String,
        },
        component: {
            type: Object,
            required: false
        },
        charts: {
            type: Array,
            required: false
        }
    },
    components: {

    },
    data() {
        return {
            webUrl: '',
        }
    },
    watch: {
        component: function (newVal, oldVale) {
        }
    },
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            if (this.component && this.component.data) {
                const data = JSON.parse(this.component.data) || {}
                this.webUrl = data.webUrl || ''
            }
        },
        onChange() {
            let comp = deepCopy(this.component)
            comp.data = JSON.stringify({ webUrl: this.webUrl })
            console.log(comp)
            this.$emit('change', comp)

        },
    },
}
</script>

<style lang="less" scoped>
.parameter-panel {
    height: 100%;
    padding: 5px;

    .title-bar {
        height: 30px;
        display: flex;
        flex-direction: row;
        align-items: center;
        border-bottom: 1px solid #e0dbdb;

        .title-bar-title {
            flex: 1 1 auto;
            padding: 0 16px;
        }

        .title-bar-button {
            flex: 0 0;
            margin-right: 10px;
        }
    }

    .input-bar {
        height: 40px;
        display: flex;
        flex-direction: row;
        align-items: center;

        .input-box {
            flex: 1 1 auto;
            padding: 0 16px;
        }

        .input-button {
            flex: 0 0;
            margin-right: 10px;
        }
    }

}
</style>