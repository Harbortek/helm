<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" :centered="true">
        <div class="dialog-description dialog-description-info manager-desc">
            <div class="dialog-description-border"></div>
            <div class="dialog-description-container">
                <p class="dialog-description-p style--font-14">
                    请输入名称
                </p>
            </div>
        </div>
        <a-form-model ref="nameForm" layout="horizontal" :model="formData" :rules="rules">
            <a-form-model-item ref="name" label="名称" prop="name">
                <a-input v-model="formData.name" @blur="() => {
                    $refs.name.onFieldBlur();
                }" />
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>

<script>
export default {
    name: 'InputNameDialog',
    props: {
        isShowDialog: {
            required: true
        },
        name: {
            type: String,
            required: true,
        },
        title: {
            type: String,
            required: false,
        }
    },
    watch: {
        name: {
            handler: function (newVal, oldVal) {
                this.formData = {
                    name: newVal
                }
            },
            immediate: true
        }
    },
    data() {
        return {
            formData: {
                name: '',
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
    methods: {
        onOK() {
            this.$refs["nameForm"].validate((valid) => {
                if (valid) {
                    const reuslt = this.formData.name
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

<style></style>