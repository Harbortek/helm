<template>
    <div>
        <!-- <a-form-model-item ref="name" label="新项目名称" prop="name">
            <a-row>
                <a-col :span="24">
                    <a-input v-model="formData.name" placeholder="请输入新项目名称" @blur="() => {
                        $refs.name.onFieldBlur();
                    }
                        " />
                </a-col>
            </a-row>

        </a-form-model-item> -->
        <a-form-model-item label="默认复制的数据">
            <span>项目组件
                <a-tooltip :overlayStyle="{ minWidth: '280px',fontSize: '12px' }">
                    <template slot="title">
                        <span>包含当前项目内的组件配置、组件权限与组件视图</span>
                    </template>
                            <span><a-icon type="question-circle-o" /></span>
                </a-tooltip>、
                工作项类型
                <a-tooltip :overlayStyle="{ minWidth: '280px',fontSize: '12px' }">
                    <template slot="title">
                        <span>包含工作项属性、布局、权限、工作流和通知配置</span>
                    </template>
                            <span><a-icon type="question-circle-o" /></span>
                </a-tooltip>、权限配置、迭代配置、项目配置
            </span>
        </a-form-model-item>
        <a-form-model-item label="自定义复制的数据">
            <a-row>
                <a-col></a-col>
            </a-row>
            <a-row>
                <div>
                    <a-checkbox v-model="checkAll" @change="onCheckAllChange" :indeterminate="indeterminate">
                        工作项数据
                    </a-checkbox>
                </div>

                <a-checkbox-group @change="onCheckListChange" v-model="formData.trackerList" style="width:100%;">
                    <a-row type="flex" :align="'middle'" style="margin:10px;background: #f8f8f8;">
                        <a-col :span="6" v-for="item in trackers" :key="item.id">
                            <a-checkbox :value="item.id">{{ item.name }}</a-checkbox>
                        </a-col>
                    </a-row>
                </a-checkbox-group>
            </a-row>
            <a-row>
                <a-col :span="12">
                    <a-checkbox v-model="formData.sprint" value="迭代数据">迭代数据</a-checkbox>
                </a-col>
                <a-col :span="12">
                    <a-checkbox v-model="formData.member">项目成员</a-checkbox>
                </a-col>
            </a-row>
            <!-- <a-row>
                <a-col :span="12">
                    <a-checkbox v-model="formData.attachment">文件 <span style="color:#909090">
                            (工作项内上传的文件)</span></a-checkbox>
                </a-col>
                <a-col :span="12">
                    <a-checkbox v-model="formData.report">项目报表</a-checkbox>
                </a-col>
            </a-row> -->
        </a-form-model-item>
    </div>
</template>

<script>
export default {
    name: 'ProjectCopyTool',
    props: ['formData', 'trackers', 'trackerIds'],
    data() {
        return {
            checkAll: true,
            indeterminate: false,//全选框状态
            
        }
    },
    watch: {
        trackers(newVal){
            console.log("trackers",newVal)
            if(newVal.length>0){
                this.checkAll = true;
                this.indeterminate = false;
            }
        },
    },
    methods: {
        onCheckAllChange() {
            if (this.checkAll) {
                this.formData.trackerList = this.trackerIds;
                this.indeterminate = false;
            } else {
                this.formData.trackerList = [];
            }

        },
        onCheckListChange() {
                console.log("list", this.formData)
            if (this.formData.trackerList.length == this.trackerIds.length) {
                this.checkAll = true;
                this.indeterminate = false;
            } else if (this.formData.trackerList.length == 0) {
                this.checkAll = false;
                this.indeterminate = false;
            } else {
                this.indeterminate = true;
            }
        },
    }
}
</script>

<style lang="less" scoped>
  .head-info{
    text-align: center;
    padding: 0 24px;
    flex-grow: 1;
    flex-shrink: 0;
    align-self: center;
    span{
      color: @text-color-second;
      display: inline-block;
      font-size: 14px;
      margin-bottom: 4px;
    }
    p{
      color: @text-color;
      font-size: 24px;
      margin: 0;
    }
  }
</style>
