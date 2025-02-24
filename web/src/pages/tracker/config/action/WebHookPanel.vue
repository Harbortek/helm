<template>
    <div>
        <a-form-model ref="form" :model="formData" :rules="rules" class="webhook-form">
            <a-form-model-item label="执行动作" prop="method" :required="true">
                <a-select v-model="formData.method" style="width:350px;">
                    <a-select-option key="GET" value="GET">GET</a-select-option>
                    <a-select-option key="POST" value="POST">POST</a-select-option>
                </a-select>
            </a-form-model-item>
            <a-form-model-item label="目标地址" prop="url" ref="url">
                <a-input v-model="formData.url" @blur="e => $refs.url.onFieldBlur()" />
            </a-form-model-item>
            <a-form-model-item label="目标用户名" prop="username" ref="username">
                <a-input v-model="formData.username" @blur="e => $refs.username.onFieldBlur()" />
            </a-form-model-item>
            <a-form-model-item label="目标密码" prop="password" ref="password">
                <a-input-password v-model="formData.password" @blur="e => $refs.password.onFieldBlur()" />
            </a-form-model-item>
            <a-form-model-item label="HTTP请求头" prop="header" ref="header" help="每行一个请求头,例如：Accept: application/json">
                <a-textarea v-model="formData.header" placeholder="" :auto-size="{ minRows: 3, maxRows: 5 }"
                    @blur="e => $refs.header.onFieldBlur()" />
            </a-form-model-item>
            <a-form-model-item label="HTTP请求体" prop="header" ref="header" help="默认以工作项作为请求体"
                v-if="formData.method === 'POST'">
                <a-textarea v-model="formData.body" placeholder="" :auto-size="{ minRows: 3, maxRows: 5 }"
                    @blur="e => $refs.header.onFieldBlur()" />
            </a-form-model-item>
        </a-form-model>

    </div>
</template>

<script>
export default {
    name: 'WebHookPanel',
    props: {
        tracker: {
            type: Object
        },
        action: {
            type: Object
        }
    },
    components: {
    },
    data() {
        const that = this;
        return {
            formData: {
                method: 'GET',
                url: '',
                username: '',
                password: '',
                header: '',
                body: '',
            },
            rules: {
                method: [{ required: true, message: "请选择执行动作", trigger: "change" }],
                url: [{ required: true, message: "请输入目标地址", trigger: "blur" }],
                username: [{ required: false, message: "请输入目标用户名", trigger: "blur" }],
                password: [{ required: false, message: "请输入目标密码", trigger: "blur" }],
                header: [{ required: false, message: "请输入HTTP请求头", trigger: "blur" }]
            }
        }
    },
    computed: {
        projectId() {
            return this.tracker.projectId
        },
    },
    watch: {
        action: {
            handler: function (newVal, oldVal) {
                if (newVal.cotent) {
                    this.formData = JSON.parse(newVal.content)
                }
            },
            immediate: true,
        },
    },
    mounted() {

    },
    methods: {
        getData() {
            return JSON.stringify(this.formData)
        },
        getDescription() {
            let description = '发送消息给：' + this.formData.url
            return description
        },
        validate(callback) {
            this.$refs.form.validate((valid) => {
                callback(valid)
            })
        },
    },
}
</script>

<style lang="less" scoped>
.webhook-form {
    .ant-form-item {
        margin-bottom: 8px;
    }
}
</style>