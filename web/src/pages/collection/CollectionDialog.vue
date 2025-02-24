<template>
    <a-modal v-model="visiable" :title="editMode=='create'?'创建集合':'修改集合'" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="collectionForm" layout="horizontal" :model="formData" :rules="rules">
            <a-form-model-item ref="name" label="集合名称" prop="name">
                <a-input v-model="formData.name" @blur="() => {
                    $refs.name.onFieldBlur();
                }" />
            </a-form-model-item>
            <a-form-model-item ref="description" label="描述" prop="description">
                <a-input type="textarea" v-model="formData.description" :auto-size="{ minRows: 3, maxRows: 5 }" @blur="() => {
                    $refs.description.onFieldBlur();
                }" />
            </a-form-model-item>
            
            <a-form-model-item ref="document" label="文档" prop="document">
                <div style="height: 30px;">
                    <vxe-button type="text" style="float:right;z-index: 1;" @click="onSelectTrackerWiki"
                        icon="vxe-icon-paste">文档选择</vxe-button>
                </div>
                <vxe-table ref="optionsTable" :show-header="true" :data="tableData"
                    :row-config="{ isHover: true }" stripe>
                    <vxe-column field="name"  title="文档名称">

                    </vxe-column>
                                                                                                                            
                    <vxe-column field="" title="操作"  width="100">
                        <template #default="{ row }">
                            <vxe-button type="text" icon="vxe-icon-delete" @click="onDeleteWiki(row)"></vxe-button>
                            <!-- <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                                <template slot="title">
                                    新窗口打开
                                </template>
                                <vxe-button type="text" icon="vxe-icon-zoom-in"
                                    @click="onEditWiki(row)"></vxe-button>
                            </a-tooltip> -->
                        </template>
                    </vxe-column>
                </vxe-table>
            </a-form-model-item>
        </a-form-model>
        <tracker-wiki-select-modal :projectId="projectId" :initalData="tableData" ref="trackerWikiSlectModal"
            @trackerWikiSelector="trackerWikiSelector"></tracker-wiki-select-modal>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import TrackerWikiSelectModal from '@/components/dialog/TrackerWikiSelectModal'
export default {
    name: "CollectionDialog",
    components: {TrackerWikiSelectModal},
    data() {
        const that = this;
        const nameValidator = (rule, value, callback) => {
            let found = false;
            let i = 0;
            for (i = 0; i < that.collectionNames.length; i++) {
                if (that.collectionNames[i] === value) {
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
            formData: {
                id: '',
                name: '',
                description: '',
            },
            tableData: [],
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
            },
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        collection: {
            required: true
        },
        collections: {
            required: true
        },
        projectId: {
            required: true
        },
        editMode:{
            required: true,
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
        collectionNames() {
            let names = []
            this.collections.forEach(f => {
                if (f.id !== this.collection.id) {
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
        collection: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        onSelectTrackerWiki(){
            this.$refs.trackerWikiSlectModal.view();
        },
        onDeleteWiki(row){
            this.tableData=this.tableData.filter(v=>v.id!=row.id)
        },
        closeDialog: function () {
            this.$emit("close");
        },
        loadData: function () {
            const _this = this;
            if (this.collection) {
                this.formData = {
                    id: this.collection.id,
                    name: this.collection.name,
                    description: this.collection.description,
                }
                this.tableData = cloneDeep(this.collection.documents)
            } else {
                this.formData = {
                    id: '',
                    name: '',
                    description: '',
                }
            }
        },
        onOK: function () {
            if(this.loading) return;
            this.$refs["collectionForm"].validate((valid) => {
                if (valid) {
                    this.loading=true;
                    let result = cloneDeep(this.formData)
                    result.projectId = this.projectId
                    result.documents = this.tableData
                    this.$emit("ok", result);
                }
            })

        },
        onCancel: function () {
            this.$emit("cancel");
        },
        trackerWikiSelector(select) {
            this.tableData = Object.assign([], select); 
        },
    },
    created() { }
};
</script>
<style lang="less" scoped>
.review-items {
    box-shadow: rgba(0, 0, 0, 0.05) 0px 0px 0px 1px, rgba(0, 0, 0, 0.05) 0px 1px 6px 0px;
    border-radius: 3px;
    height: 48px;
    display: flex;
    -webkit-box-align: center;
    align-items: center;
    padding: 0px 8px 0px 24px;
    -webkit-box-pack: justify;
    justify-content: left;

}

.review-items-total {
    font-weight: bold;
    padding-right: 4px;
}

.review-items-op {
    font-weight: bold;
    color: rgb(0, 102, 255);
    cursor: pointer;
    padding-left: 16px;
    opacity: 0.9;
}
</style>
