<template>
    <div>
        <a-form-model ref="form" :model="formData" :rules="rules">
            <a-form-model-item label="收件人" prop="emailTo" :required="true">
                <role-members-table :projectId="projectId" :value="formData.emailTo" scope="SCOPE_TRACKER" title="发送给以下成员"
                    tableHeight="auto" @delete="e => onDelete(e)" @add="e => onAdd(e)" />
            </a-form-model-item>
            <a-form-model-item label="邮件标题" prop="emailSubject" ref="emailSubject">
                <a-input v-model="formData.emailSubject" @blur="e => $refs.emailSubject.onFieldBlur()" />
            </a-form-model-item>
            <a-form-model-item label="邮件内容" prop="emailBody" ref="emailBody">
                <a-textarea v-model="formData.emailBody" placeholder="" :auto-size="{ minRows: 3, maxRows: 5 }"
                    @blur="e => $refs.emailBody.onFieldBlur()" />
            </a-form-model-item>
        </a-form-model>

    </div>
</template>

<script>
import RoleMembersTable from '@/components/table/RoleMembersTable.vue';
import SimpleEditor from '@/components/editor/SimpleEditor.vue';
export default {
    name: 'SendCustomEmailPanel',
    props: {
        tracker: {
            type: Object
        },
        action: {
            type: Object
        }
    },
    components: {
        RoleMembersTable, SimpleEditor
    },
    data() {
        const that = this;
        const emailToValidator = (rule, value, callback) => {
            let found = that.formData.emailTo.length === 0;
            if (!found) {
                return callback();
            } else {
                return callback(new Error("请选择收件人"));
            }
        };
        return {
            formData: {
                emailTo: [],
                emailSubject: '',
                emailBody: '',
            },
            rules: {
                emailTo: [{ validator: emailToValidator, trigger: "change" }],
                emailSubject: [{ required: true, message: "请输入邮件标题", trigger: "blur" }],
                emailBody: [{ required: true, message: "请输入邮件内容", trigger: "blur" }]
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
                    this.formData = JSON.parse(newVal.cotent)
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
            let description = '发送邮件给：' + this.formData.emailTo.map(item => item.name).join('、')
            return description
        },
        validate(callback) {
            this.$refs.form.validate((valid) => {
                callback(valid)
            })
        },
        onDelete(row) {
            this.formData.emailTo = this.formData.emailTo.filter(p => { return p.id != row.id })
        },
        onAdd(row) {
            // this.emailTo.push(row)
        },
    },
}
</script>

<style lang="less" scoped>
.mail-editor {
    line-height: 21px;

    .w-e-text-container p {
        margin: 5px 0;
    }
}
</style>