<template>
    <a-modal v-model="visiable" title="创建基线" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="baselineForm" layout="horizontal" :model="formData" :rules="rules">
            <a-form-model-item ref="name" label="基线名称" prop="name">
                <a-input v-model="formData.name" @blur="() => {
                    $refs.name.onFieldBlur();
                }" />
            </a-form-model-item>
            <a-form-model-item ref="name" label="基线类型" prop="type">
                <a-select v-model="formData.type" style="width:100%" placeholder="请选择基线类型">
                    <a-select-option value="PROJECT">项目基线</a-select-option>
                    <a-select-option value="COLLECTION">集合基线</a-select-option>
                    <a-select-option value="DOCUMENT">文档基线</a-select-option>
                </a-select>
                <!-- <a-input v-model="formData.type" @blur="() => {
                    $refs.name.onFieldBlur();
                }" /> -->
            </a-form-model-item>
            <a-form-model-item ref="collection" label="集合" prop="collectionId" v-if="formData.type === 'COLLECTION'">
                <CollectionSelect v-model="formData.collectionId" :projectId="projectId" />
            </a-form-model-item>
            <a-form-model-item ref="collection" label="文档" prop="documentId" v-if="formData.type === 'DOCUMENT'">
                <DocumentSelect v-model="formData.documentId" :projectId="projectId" />
            </a-form-model-item>
            <a-form-model-item ref="description" label="描述" prop="description">
                <a-input type="textarea" v-model="formData.description" :auto-size="{ minRows: 3, maxRows: 5 }" @blur="() => {
                    $refs.description.onFieldBlur();
                }" />
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import CollectionSelect from '@/components/select/CollectionSelect'
import DocumentSelect from '@/components/select/DocumentSelect'
export default {
    name: "BaselineDialog",
    components: {CollectionSelect, DocumentSelect},
    data() {
        const that = this;
        const nameValidator = (rule, value, callback) => {
            let found = false;
            let i = 0;
            for (i = 0; i < that.baselineNames.length; i++) {
                if (that.baselineNames[i] === value) {
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
            loading: false,
            documentList:[],
            formData: {
                name: '',
                type: '',
                collectionId: '',
                transitionFrom: '',
                transitionTo: '',
                documentId:'',
            },
            rules: {
                name: [
                    { required: true, message: "请输入基线名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                    { validator: nameValidator, trigger: "change" },
                ],
                type: {required: true,message: "请选择基线类型",},
                collectionId:{required: true,message: '请选择集合'},
                documentId:{required: true,message: '请选择文档'},
            },
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        projectId: {
            required: true
        },
        baseline: {
            required: true
        },
        baselines: {
            required: true
        },
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
        baselineNames() {
            let names = []
            this.baselines.forEach(f => {
                if (f.id !== this.baseline.id) {
                    names.push(f.name)
                }
            })
            return names
        }
    },
    watch: {
        isShowDialog: {
            handler: function(newVal, oldVal){
                if(newVal){
                    this.loading=false;
                }
            }
        },
        baseline: {
            handler: function (newVal, oldVal) {
                this.loadData();
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
            if (this.baseline) {
                this.formData = {
                    type: this.baseline.type,
                    name: this.baseline.name,
                    description: this.baseline.description,
                }
            } else {
                this.formData = {
                    name: '',
                    type: '',
                    description: '',
                }
            }
        },
        onOK: function () {
            if(this.loading) return;
            this.$refs["baselineForm"].validate((valid) => {
                if (valid) {
                    this.loading=true;
                    let result = cloneDeep(this.baseline)
                    result.type=this.formData.type
                    result.name = this.formData.name
                    result.collectionId=this.formData.collectionId
                    result.documentId=this.formData.documentId
                    result.description = this.formData.description
                    this.$emit("ok", result);
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
<style lang="less" scoped></style>
