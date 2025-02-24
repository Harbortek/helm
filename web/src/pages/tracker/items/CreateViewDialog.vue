<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel">
        <a-form-model ref="viewForm" layout="horizontal" :model="formData" :rules="rules">
            <a-form-model-item ref="name" label="视图名称" prop="name">
                <a-input v-model="formData.name" @blur="() => {
                    $refs.name.onFieldBlur();
                }" />
            </a-form-model-item>
            <a-form-model-item label="视图类型" prop="viewType" v-show="editMode != 'edit'">
                <!-- <a-radio-group v-model="formData.viewType">
                    <a-radio class="radioStyle" :value="'PRIVATE'">
                        私有
                    </a-radio>
                    <a-radio class="radioStyle" :value="'PUBLIC'">
                        公共
                    </a-radio>
                </a-radio-group> -->
                <vxe-radio-group v-model="formData.viewType">
                    <vxe-radio label="PRIVATE" content="私有"></vxe-radio>
                    <vxe-radio label="PUBLIC" content="公共"></vxe-radio>
                </vxe-radio-group>
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import { findViewsByObjectId} from "@/services/tracker/ViewService"
export default {
    name: "CreateViewDialog",
    components: {},
    data() {
        const that = this;
        const nameValidator = (rule, value, callback) => {
            let found = false;
            let i = 0;
            for (i = 0; i < that.viewNames.length; i++) {
                if (that.viewNames[i] === value) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return callback();
            } else {
                return callback(new Error("已存在同样名称的数据"));
            }
        };
        return {
            length: 0,
            formData: {
                name: '',
                viewType: 'PRIVATE',
            },
            rules: {
                name: [
                    { required: true, message: "请输入视图名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                    { validator: nameValidator, trigger: "change" },
                ],
                viewType: [
                    { required: true, message: "请输入视图类型", trigger: "blur" },
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
        view: {
            required: false
        },
        views: {
            required: false,
            type: Array,
            default: () => { return [] }
        },
        objectId:{
            required: false
        }
    },
    computed: {
        title() {
            if (this.editMode === 'create') {
                return '创建视图'
            } else if(this.editMode === 'edit') {
                return '重命名视图'
            } else if(this.editMode === 'saveAs') {
                return '另存为视图'
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
        viewNames() {
            let names = []
            if(this.views.length > 0){
                this.length=this.views.length
                this.views.forEach(f => {
                    if (f.id !== this.view.id) {
                        names.push(f.name)
                    }
                })
            }else if(this.objectId){
                this.userId = this.$store.getters['account/user'].id;
                findViewsByObjectId(this.objectId,this.userId,false).then(resp => {
                    this.length=resp.length
                    resp.forEach(f => {
                        this.views.push(f)
                        names.push(f.name)
                    })
                })      
            }
            return names
        }
    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if(newVal&&this.editMode === 'edit'){
                    this.loadData();
                }
            }
        },
    },
    mounted() {
    },
    methods: {
        loadData() {
            if (this.view) {
                this.formData = {
                    name: this.view.name,
                    viewType: this.view.viewType
                }
            }
        },
        onOK() {
            this.$refs["viewForm"].validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.view)
                    result.name = this.formData.name
                    result.viewType = this.formData.viewType
                    result.ordinary=this.length
                    this.views.push(result)
                    this.$emit("ok", result);
                }
            })

        },
        onCancel() {
            this.$emit("cancel");
        }
    },
    created() { }
};
</script>
<style lang="less" scoped>
.radioStyle {
    display: block;
    height: 30px;
    line-height: 30px;
}
</style>
