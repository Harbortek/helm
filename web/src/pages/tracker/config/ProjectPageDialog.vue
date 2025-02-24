<template>
    <a-modal v-model="visiable" title="项目页面" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="pageForm" layout="horizontal" :model="formData" :rules="rules">
            <a-form-model-item ref="name" label="名称" prop="name">
                <a-input v-model="formData.name" />
            </a-form-model-item>
            <!-- <a-form-model-item ref="description" label="描述" prop="description">
                <a-input type="textarea" v-model="formData.description" :auto-size="{ minRows: 3, maxRows: 5 }" @blur="() => {
                    $refs.description.onFieldBlur();
                }" />
            </a-form-model-item> -->
            <a-form-model-item ref="type" label="类型" prop="type" v-if="editMode === 'create' && !currentPage?.folder">
                <a-select v-model="formData.type">
                    <a-select-option value="component">{{ $t('page.type.component') }}</a-select-option>
                    <a-select-option value="tracker">{{ $t('page.type.tracker') }}</a-select-option>
                    <a-select-option value="wiki">{{ $t('page.type.wiki') }}</a-select-option>
                    <a-select-option value="report">{{ $t('page.type.report') }}</a-select-option>
                    <a-select-option value="url">{{ $t('page.type.url') }}</a-select-option>
                </a-select>
            </a-form-model-item>
            <a-form-model-item ref="componentType" label="组件" prop="componentType"
                v-if="!currentPage?.folder && formData.type === 'component'">
                <a-select v-model="formData.componentType">
                    <a-select-opt-group v-for="g in Object.keys(componentTypeGroups)" :key="g" :title="g">
                        <a-select-option :value="item.id" :key="item.id" v-for="item in componentTypeGroups[g]">{{
        item.name
    }}</a-select-option>
                    </a-select-opt-group>
                </a-select>
            </a-form-model-item>
            <a-form-model-item ref="trackerId" label="默认工作项" prop="trackerId"
                v-if="!currentPage?.folder && (formData.type === 'tracker' )">
                <tracker-select v-model="formData.trackerId" :projectId="projectId" />
            </a-form-model-item>

            <a-form-model-item ref="url" label="自定义链接" prop="url"
                v-if="!currentPage?.folder && formData.type === 'url'">
                <a-input v-model="formData.url" @blur="() => {
        $refs.url.onFieldBlur();
    }" />
            </a-form-model-item>

            <a-form-model-item label="图标" prop="icon" v-if="currentPage?.folder">
                <icon-select placeholder="请选择一个图标" v-model="formData.icon" />
            </a-form-model-item>

        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import IconSelect from '@/components/icon/IconSelect.vue';
import XEUtils from "xe-utils";
import PAGE_COMPONENTS from '@/components/menu/menu-with-project'
import TrackerSelect from '@/components/select/TrackerSelect.vue';
export default {
    name: "ProjectPageDialog",
    components: { IconSelect, TrackerSelect },
    data() {
        const that = this;
        const nameValidator = (rule, value, callback) => {
            let found = false;
            let i = 0;
            for (i = 0; i < that.pageNames.length; i++) {
                if (that.pageNames[i] === value) {
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
            formData: {
                name: '',
                description: '',
                type: 'wiki',
                folder: false,
                icon: '',
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
                    { validator: nameValidator, trigger: "change" },
                ],
                type: [{ required: true, message: "请选择类型", trigger: "change" },],
                componentType: [{ required: true, message: "请选择组件类型", trigger: "change" },],
                // trackerId: [{ required: true, message: "请选择工作项类型", trigger: "change" },]
            },
            componentTypeList: PAGE_COMPONENTS || [],
            componentTypeGroups: {},
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },
        currentPage: {
            required: true
        },
        pageList: {
            required: true
        },
        projectId: {
            required: true
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
        pageNames() {
            let names = []
            this.pageList.forEach(f => {
                if (f.id !== this.currentPage.id) {
                    names.push(f.name)
                }
            })
            return names
        }
    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }

            }
        }
    },
    mounted() {
        this.componentTypeGroups = XEUtils.groupBy(this.componentTypeList, 'group')
        this.loadData();
    },
    methods: {
        closeDialog: function () {
            this.$emit("close");
        },
        loadData: function () {
            const _this = this;
            if (this.currentPage) {
                this.formData = cloneDeep(this.currentPage)
                this.formData.trackerId = this.currentPage.tracker?.id
            } else {
                this.formData = {
                    name: '',
                    description: '',
                    type: 'wiki',
                    folder: false,
                    icon: '',
                    componentType: '',
                    trackerId: '',
                    url: '',
                }
            }
        },
        onOK: function () {
            this.$refs["pageForm"].validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.currentPage)
                    result.name = this.formData.name
                    result.description = this.formData.description
                    result.type = this.formData.type
                    result.componentType = this.formData.componentType
                    result.tracker = { id: this.formData.trackerId }
                    result.url = this.formData.url
                    result.icon = this.formData.icon

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
