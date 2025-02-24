<template>
    <div style="width: 100%;">
        <a-form-model ref="layoutFormBar" :model="layoutForm" label-width="80px" :layout="'horizontal'"
            :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">
            <a-form-model-item label="列数" class="form-item form-item-slider">
                <a-slider v-model="layoutForm.columnNums" show-input :show-input-controls="false" input-size="mini" :min="1"
                    :max="5" @change="changeLayout('columnNums')" />
            </a-form-model-item>
            <!-- <a-form-model-item label="行高" class="form-item form-item-slider">
                <a-slider v-model="layoutForm.rowHeight" show-input :show-input-controls="false" input-size="mini" :min="50"
                    :max="500" :step="100" @change="changeLayout('rowHeight')" />
            </a-form-model-item>
            <a-form-model-item label="间隔" class="form-item form-item-slider">
                <a-slider v-model="layoutForm.gap" show-input :show-input-controls="false" input-size="mini" :min="0"
                    :max="100" :step="10" @change="changeLayout('gap')" />
            </a-form-model-item> -->

        </a-form-model>
    </div>
</template>

<script>
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import { DEFAULT_COMMON_CANVAS_STYLE_STRING } from '@/pages/smart-page/util/panel'
export default {
    name: 'LayoutSelector',
    props: {
        pageStyle: {
            required: true,
        }
    },
    data() {
        return {
            layoutForm: deepCopy(DEFAULT_COMMON_CANVAS_STYLE_STRING).layout,
        }
    },
    watch: {
        pageStyle: {
            handler(val) {
                this.layoutForm = deepCopy(val.layout)
            },
            immediate: true,
            deep: true,
        }
    },
    methods: {
        changeLayout(property) {
            this.$emit('change', this.layoutForm)
        }
    },
}
</script>

<style></style>