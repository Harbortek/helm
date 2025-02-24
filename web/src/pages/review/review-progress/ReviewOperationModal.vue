<template>
    <vxe-modal class="review-operation-modal" :value="value" :title="title" width="500" @hide="handleClose" :mask="true"
        :esc-closable="true">
        <template #default>
            <vxe-form :data="formData" ref="xForm" :rules="formRules" title-align="right" title-width="10" style="padding-left:15px;height: 210px;">
                <vxe-form-item field="status" span="24" :item-render="{}"  :title-colon="false"
                    :title-asterisk="true">
                    <template #default="{ data }">
                        <div style="margin-bottom: 15px;">共选中 <span style="font-weight: bold;">{{selectedItemsTotal}}</span> 个评审项目</div>
                        <vxe-radio v-model="data.status" v-for="item in statusList" :key="item.id" :label="item.id" @change="handleStatusChange(item)">
                            <a-icon theme="filled" :style="{ 'margin-right': '5px', color: item.color }" type="check-circle" />{{item.name}}</vxe-radio>
                        <!-- <vxe-radio v-model="data.status" label="1" @change="handleStatusChange"><a-icon theme="filled" :style="{ 'margin-right': '5px', color: 'rgb(33, 202, 100)' }" type="check-circle" />通过</vxe-radio>
                        <vxe-radio v-model="data.status" label="2" @change="handleStatusChange"><a-icon theme="filled" :style="{ 'margin-right': '5px', color: 'rgb(247, 70, 54)' }" type="close-circle" />不通过</vxe-radio>
                        <vxe-radio v-model="data.status" label="3" @change="handleStatusChange"><a-icon theme="filled" :style="{ 'margin-right': '5px', color: 'rgb(255, 170, 33)' }" type="info-circle" />建议</vxe-radio> -->
                    </template>
                </vxe-form-item>
                <vxe-form-item field="description" span="24" title=" "  :title-colon="false"
                    :title-asterisk="true" :item-render="{}">
                    <template #default="{ data }">
                        <vxe-textarea v-model="data.description" :placeholder="placeholder" rows="2" maxlength="1000"
                            :showWordCount="true"></vxe-textarea>
                    </template>
                </vxe-form-item>
                <vxe-form-item align="left" span="24">
                    <template #default>
                        <vxe-button type="submit" status="primary" @click="onClickOk">提交评审结果</vxe-button>
                        <vxe-button @click="handleClose">取消</vxe-button>
                    </template>
                </vxe-form-item>
            </vxe-form>
        </template>
    </vxe-modal>
</template>
<script lang="js">
import store from '@/store';
import {
  findEnumsByCode
} from "@/services/system/EnumService";
export default {
    name: 'ReviewOperationModal',
    components: {},
    model: {
        prop: 'value',
        event: 'UPDATE_VALUE'
    },
    props: {
        value: {},
        projectId: {},
        selectedRows:{},
        selectedItemsTotal:{},
    },
    data() {
        return {
            title: '批量设置评审结果',
            placeholder: '填写评审结果原因。当评审结果为通过时，此项选填',
            statusList:[],
            formData: {
                status: '1',
                description: '',
            },
            formRules: {
                status: [
                    { required: true, message: '' },
                ],
                description: [
                    { required: false, message: '' }
                ],
            },
            calValuel:{}
        }
    },
    watch:{
        value: {
            handler(newVal) {
                if(newVal&&this.statusList.length==0){
                    findEnumsByCode('PROJECT_REVIEW_TYPE').then((resp) => {
                        resp.forEach(item => {
                            if(item.name!='未评审'){    
                                this.statusList.push(item)
                            }
                        });
                        if(this.statusList.length>0){
                            this.formData.status = this.statusList[0].id;
                        }
                    });
                }
            },
            immediate: true,
        },
    },
    computed: {

    },
    created() {
    },
    methods: {
        onClickOk(){
            this.$refs.xForm.validate().then((resp) => {
                if(!!!resp){
                    let reviewStatuses=[]
                    for(let key in this.selectedRows){
                        if(key!='selected'&&this.selectedRows[key].length>0){
                            this.selectedRows[key].forEach(element => {
                                reviewStatuses.push({
                                    objectId:element,
                                    statusId:this.formData.status,
                                    description:this.formData.description,
                                    reviewerId : store.getters["account/user"].id
                                })
                            });
                        }           
                    }
                    this.$emit('UPDATE_VALUE', reviewStatuses)
                }
            })
           
        },
        handleStatusChange(item) {
            if (item.name== '通过') {
                this.placeholder = '填写评审结果原因。当评审结果为通过时，此项选填'
                this.formRules.description[0].required = false;
            } else if (item.name== '不通过') {
                this.placeholder = '填写评审结果原因。当评审结果为不通过时，此项必填';
                this.formRules.description[0].required = true;
            } else {
                this.placeholder = '请填写建议描述';
                this.formRules.description[0].required = true;
            }
        },
        handleClose() {
            this.$emit('UPDATE_VALUE')
        }
    }
}
</script>

<style lang="less" scoped>
.review-progress-modal {
    width: 100%;
    height: 100%;

}
</style>