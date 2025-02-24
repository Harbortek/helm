<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel">
        <div>
            <a-form-model ref="form" :model="formData" layout="horizontal" label-width="80px" size="small"
                class="formatter-form" :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">
                <a-form-model-item :label="$t('chart.value_formatter_type')">
                    <a-radio-group v-model="formData.type" @change="getExampleValue">
                        <a-radio v-for="radio in typeList" :key="radio.value" :value="radio.value">{{ $t('chart.' +
                            radio.name) }}</a-radio>
                    </a-radio-group>
                </a-form-model-item>

                <a-form-model-item v-show="formData.type !== 'auto'" :label="$t('chart.value_formatter_decimal_count')">
                    <a-input-number v-model="formData.decimalCount" :min="0" :max="10" size="small"
                        @change="getExampleValue" />
                </a-form-model-item>

                <a-form-model-item v-show="formData.type !== 'percent'" :label="$t('chart.value_formatter_unit')">
                    <a-select v-model="formData.unit" :placeholder="$t('chart.pls_select_field')" size="small"
                        @change="getExampleValue">
                        <a-select-option v-for="item in unitList" :key="item.value" :title="$t('chart.' + item.name)"
                            :value="item.value">{{ $t('chart.' + item.name) }}</a-select-option>
                    </a-select>
                </a-form-model-item>

                <a-form-model-item :label="$t('chart.value_formatter_suffix')">
                    <a-input v-model="formData.suffix" size="small" clearable :placeholder="$t('commons.input_content')"
                        @change="getExampleValue" />
                </a-form-model-item>

                <a-form-model-item :label="$t('chart.value_formatter_thousand_separator')">
                    <a-checkbox v-model="formData.thousandSeparator" @change="getExampleValue" />
                </a-form-model-item>

                <a-form-model-item :label="$t('chart.value_formatter_example')">
                    <span>{{ exampleResult }}</span>
                </a-form-model-item>
            </a-form-model>
        </div>
    </a-modal>
</template>

<script>
import { formatterItem, formatterType, unitList, valueFormatter } from '@/pages/smart-page/util/formatter'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
export default {
    name: 'ValueFormatDialog',
    props: {
        isShowDialog: {
            required: true
        },
        formatterItem: {
            type: Object,
            required: true
        },
    },
    watch: {
        formatterItem: {
            handler: function (newVal, oldVal) {
                this.formData = deepCopy(newVal.formatterCfg) || { type: 'auto' }
            },
            immediate: true
        }
    },
    data() {
        return {
            formData: {
                type: '',
            },
            rules: {
                name: [
                    { required: true, message: "请输入名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                ]
            },
            typeList: formatterType,
            unitList: unitList,
            exampleResult: '20000000'
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
        title() {
            return this.$t('chart.value_formatter') + ' - ' + this.formatterItem.name
        }
    },
    created() {
    },
    mounted() {
        this.getExampleValue()
    },
    methods: {
        getExampleValue() {
            this.exampleResult = valueFormatter(20000000, this.formData)
        },
        onOK() {
            this.$refs["form"].validate((valid) => {
                if (valid) {
                    const reuslt = this.formData
                    this.$emit("ok", reuslt);
                }
            })
        },
        onCancel() {
            this.$emit("cancel");
        }
    },

}
</script>

<style lang="less" scoped>
.formatter-form {
    // .ant-form {
    //     width: 100%;
    // }

    // .ant-form-item {
    //     margin-bottom: 0;
    // }

    // .ant-form label {
    //     font-size: 12px;
    // }

    // .ant-form-item-label {
    //     display: inline-block;
    //     overflow: hidden;
    //     line-height: 28px;
    //     white-space: nowrap;
    //     text-align: right;
    //     vertical-align: middle;
    // }

    // .ant-form-item-control {
    //     position: relative;
    //     line-height: 28px;
    //     zoom: 1;
    // }
}
</style>