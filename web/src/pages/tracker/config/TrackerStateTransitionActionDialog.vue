<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" centered :width="800">
        <a-form-model ref="actionForm" layout="horizontal" :model="formData" :rules="rules" class="action-form"
            size="small">
            <a-form-model-item ref="type" label="动作类型" prop="type">
                <a-select default-value="-" v-model="formData.type">
                    <a-select-option value="ACTION_TYPE_UPDATE_ITEM_PROPERTIES">修改属性值</a-select-option>
                    <a-select-option value="ACTION_TYPE_TRIGGER_STATE_TRANSITION">触发步骤执行</a-select-option>
                    <!-- <a-select-option value="ACTION_TYPE_UPDATE_REFERRING_ITEMS">更新关联工作项</a-select-option> -->
                    <!-- <a-select-option value="ACTION_TYPE_CREATE_NEW_UP_DOWNSTREAM_REFERRING_ITEMS">更新父/子工作项</a-select-option> -->
                    <!-- <a-select-option value="ACTION_TYPE_CREATE_A_NEW_SEQUENTIAL_ID">创建顺序编号</a-select-option> -->
                    <a-select-option value="ACTION_TYPE_EXECUTE_A_CUSTOM_SCRIPT">执行自定义脚本</a-select-option>
                    <a-select-option value="ACTION_TYPE_SEND_A_CUSTOMER_EMAIL_TO_SPECIFIC_RECIPIENTS">发送邮件</a-select-option>
                    <!-- <a-select-option value="ACTION_TYPE_START_A_NEW_REVIEW">开启评审</a-select-option> -->
                    <!-- <a-select-option value="ACTION_TYPE_NEW_BASELINE">创建基线</a-select-option> -->
                    <a-select-option value="ACTION_TYPE_WEBHOOK">消息推送</a-select-option>
                </a-select>
            </a-form-model-item>
            <a-form-model-item ref="expressionEL" label="条件表达式" prop="expressionEL" help="满足条件时触发动作执行，默认为TRUE，表达式必须返回布尔值">
                <a-input v-model="formData.expressionEL" />
            </a-form-model-item>
        </a-form-model>

        <update-item-properties-panel ref="updateItemPropertiesPanel"
            v-if="formData.type === 'ACTION_TYPE_UPDATE_ITEM_PROPERTIES'" :tracker="tracker" :action="action" />

        <trigger-state-transition-panel ref="triggerStateTransitionPanel"
            v-if="formData.type === 'ACTION_TYPE_TRIGGER_STATE_TRANSITION'" :tracker="tracker" :action="action" />
        <update-referring-items-panel ref="updateReferringItemsPanel"
            v-if="formData.type === 'ACTION_TYPE_UPDATE_REFERRING_ITEMS'" :tracker="tracker" :action="action" />
        <update-up-down-referring-items-panel ref="updateUpDownReferringItemsPanel"
            v-if="formData.type === 'ACTION_TYPE_CREATE_NEW_UP_DOWNSTREAM_REFERRING_ITEMS'" :tracker="tracker"
            :action="action" />
        <send-custom-email-panel ref="sendCustomEmailPanel"
            v-if="formData.type === 'ACTION_TYPE_SEND_A_CUSTOMER_EMAIL_TO_SPECIFIC_RECIPIENTS'" :tracker="tracker"
            :action="action" />
        <execute-custom-script-panel ref="executeCustomScriptPanel"
            v-if="formData.type === 'ACTION_TYPE_EXECUTE_A_CUSTOM_SCRIPT'" :tracker="tracker" :action="action" />

        <web-hook-panel ref="webhookPanel" v-if="formData.type === 'ACTION_TYPE_WEBHOOK'" :tracker="tracker"
            :action="action" />
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import UpdateItemPropertiesPanel from './action/UpdateItemPropertiesPanel.vue';
import TriggerStateTransitionPanel from './action/TriggerStateTransitionPanel.vue';
import UpdateReferringItemsPanel from './action/UpdateReferringItemsPanel.vue';
import UpdateUpDownReferringItemsPanel from './action/UpdateUpDownReferringItemsPanel.vue';
import SendCustomEmailPanel from './action/SendCustomEmailPanel.vue';
import ExecuteCustomScriptPanel from './action/ExecuteCustomScriptPanel.vue';
import WebHookPanel from './action/WebHookPanel.vue';

export default {
    name: "TrackerStateTransitionActionDialog",
    components: { UpdateItemPropertiesPanel, TriggerStateTransitionPanel, UpdateReferringItemsPanel, UpdateUpDownReferringItemsPanel, SendCustomEmailPanel, ExecuteCustomScriptPanel, WebHookPanel },
    data() {
        return {
            formData: {
                type: '',
            },
            rules: {
                type: [
                    { required: true, message: "请选择动作类型", trigger: "blur" },
                ]
            },
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },
        action: {
            required: true
        },
        tracker: {
            type: Object,
            required: true,
        }
    },
    computed: {
        title() {
            if (this.editMode === 'create') {
                return '创建后置动作'
            } else {
                return '编辑后置动作'
            }
        },
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
    },
    watch: {
        transition: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        action: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        closeDialog: function () {
            this.$emit("close");
        },
        loadData: function () {
            if (this.action) {
                this.formData.type = this.action.type
                this.formData.expressionEL = this.action.expressionEL
            } else {
                this.formData = {
                    type: '',
                    expressionEL: '',
                }
            }
        },
        onOK: function () {
            this.$refs["actionForm"].validate((valid) => {
                if (valid) {
                    let component = undefined
                    if (this.formData.type === 'ACTION_TYPE_UPDATE_ITEM_PROPERTIES') {
                        component = this.$refs.updateItemPropertiesPanel
                    } else if (this.formData.type === 'ACTION_TYPE_TRIGGER_STATE_TRANSITION') {
                        component = this.$refs.triggerStateTransitionPanel
                    } else if (this.formData.type === 'ACTION_TYPE_UPDATE_REFERRING_ITEMS') {
                        component = this.$refs.updateReferringItemsPanel
                    } else if (this.formData.type === 'ACTION_TYPE_CREATE_NEW_UP_DOWNSTREAM_REFERRING_ITEMS') {
                        component = this.$refs.updateUpDownReferringItemsPanel
                    } else if (this.formData.type === 'ACTION_TYPE_SEND_A_CUSTOMER_EMAIL_TO_SPECIFIC_RECIPIENTS') {
                        component = this.$refs.sendCustomEmailPanel
                    } else if (this.formData.type === 'ACTION_TYPE_EXECUTE_A_CUSTOM_SCRIPT') {
                        component = this.$refs.executeCustomScriptPanel
                    } else if (this.formData.type === 'ACTION_TYPE_WEBHOOK') {
                        component = this.$refs.webHookPanel
                    }
                    component.validate((valid2) => {
                        if (valid2) {
                            this.formData.cotent = component.getData()
                            this.formData.description = component.getDescription()
                            let result = cloneDeep(this.formData)
                            console.log(result)
                            this.$emit("ok", result);
                        }
                    })
                }
            })

        },
        onCancel: function () {
            this.$emit("cancel");
        }
    },
    created() { }
};
</script>
<style lang="less" scoped>
.action-form {
    .ant-form-item {
        margin-bottom: 8px;
    }
}
</style>
