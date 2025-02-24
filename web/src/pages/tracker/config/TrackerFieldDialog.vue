<template>
    <a-modal v-model="visiable" title="编辑工作项属性" @ok="onOK" @cancel="onCancel" width="800px">
        <a-form-model ref="fieldForm" class="field-form" layout="horizontal" :model="formData" :rules="rules">
            <a-form-model-item ref="name" label="属性名称" prop="name">
                <a-input v-model="formData.name" @blur="() => {
        $refs.name.onFieldBlur();
    }" />
            </a-form-model-item>
            <a-form-model-item ref="inputType" label="属性类型" prop="inputType">
                <a-select default-value="TEXT" v-model="formData.inputType" :disabled="editMode === 'edit'">
                    <a-select-option value="TEXT">单行文本</a-select-option>
                    <a-select-option value="TEXT_AREA">多行文本</a-select-option>
                    <a-select-option value="WIKI">带格式文本</a-select-option>

                    <a-select-option value="OPTIONS">单选菜单</a-select-option>
                    <a-select-option value="MULTI_OPTIONS">多选菜单</a-select-option>

                    <a-select-option value="BOOL">是否</a-select-option>
                    <a-select-option value="DATE">日期</a-select-option>
                    <a-select-option value="TIME">时间</a-select-option>
                    <a-select-option value="DURATION">时间间隔</a-select-option>
                    <a-select-option value="INTEGER">整数</a-select-option>
                    <a-select-option value="DECIMAL">浮点数</a-select-option>

                    <a-select-option value="USER">单选用户</a-select-option>
                    <a-select-option value="MEMBERS">多选用户</a-select-option>

                    <a-select-option value="WORK_ITEM">工作项</a-select-option>
                    <!-- <a-select-option value="Sprint">迭代</a-select-option> -->

                    <a-select-option value="TABLE">表格</a-select-option>
                    <a-select-option value="TEST_STEP">测试步骤</a-select-option>

                    <!-- <a-select-option value="COUNTRY">国家</a-select-option>
                    <a-select-option value="LANGUAGE">语言</a-select-option>
                    <a-select-option value="COLOR">颜色</a-select-option> -->
                </a-select>
            </a-form-model-item>
            <a-row>
                <a-col :span="12">
                    <a-form-model-item ref="mandatory" label="必填" prop="mandatory">
                        <a-switch v-model="formData.mandatory" />
                    </a-form-model-item>
                </a-col>
                <a-col :span="12">
                    <a-form-model-item ref="statusSelect" label="在所有状态下必填，排除以下状态" prop="exceptStatus">
                        <tracker-status-select v-model="formData.exceptStatus" :trackerId="tracker.id"
                            :selectMultiple="true" :disabled="!formData.mandatory" />
                    </a-form-model-item>
                </a-col>
            </a-row>
            <template v-if="formData.inputType === 'OPTIONS' || formData.inputType === 'MULTI_OPTIONS'">
                <a-form-model-item ref="enumId" label="属性枚举值" prop="enumId">
                    <enum-category-select v-model="formData.enumId" :projectId="projectId" />
                </a-form-model-item>
            </template>
            <template v-else-if="formData.inputType === 'TABLE'">
                <a-form-model-item label="属性表格列" prop="columns">
                    <vxe-toolbar>
                        <template #tools>
                            <vxe-button type="primary" @click="addTableColumn" size="mini">添加</vxe-button>
                        </template>
                    </vxe-toolbar>
                    <vxe-table ref="xTable" size="mini" :data="formData.columns" :row-config="{ isHover: true }"
                        height="200"
                        :edit-config="{ trigger: 'click', mode: 'cell', icon: 'vxe-icon-edit', showStatus: true }">
                        <vxe-column type="seq" title="序号" header-align="center" align="left" width="60">
                        </vxe-column>
                        <vxe-column title="名称" field="name" header-align="center" align="center" width="200"
                            :edit-render="{}">
                            <template #default="{ row }">
                                {{ row.name }}
                            </template>
                            <template #edit="{ row }">
                                <vxe-input v-model="row.name" />
                            </template>
                        </vxe-column>
                        <vxe-column title="类型" field="inputType" header-align="center" align="center" :edit-render="{}">
                            <template #default="{ row }">
                                {{ row.inputType ? $t('tracker.field.type.' + row.inputType) : '' }}
                            </template>
                            <template #edit="{ row }">
                                <a-select default-value="TEXT" v-model="row.inputType">
                                    <a-select-option value="TEXT">单行文本</a-select-option>
                                    <a-select-option value="TEXT_AREA">多行文本</a-select-option>
                                    <a-select-option value="WIKI">带格式文本</a-select-option>

                                    <a-select-option value="OPTIONS">单选菜单</a-select-option>
                                    <a-select-option value="MULTI_OPTIONS">多选菜单</a-select-option>

                                    <a-select-option value="BOOL">是否</a-select-option>
                                    <a-select-option value="DATE">日期</a-select-option>
                                    <a-select-option value="TIME">时间</a-select-option>
                                    <a-select-option value="DURATION">时间间隔</a-select-option>
                                    <a-select-option value="INTEGER">整数</a-select-option>
                                    <a-select-option value="DECIMAL">浮点数</a-select-option>

                                    <a-select-option value="USER">单选用户</a-select-option>
                                    <a-select-option value="MEMBERS">多选用户</a-select-option>
                                </a-select>
                            </template>
                        </vxe-column>
                        <vxe-column title="属性枚举值" field="enumId" header-align="center" align="center" width="200"
                            :edit-render="{}">
                            <template #default="{ row }">
                                <enum-category-select v-model="row.enumId" :projectId="projectId"
                                    v-if="row.inputType === 'OPTIONS' || row.inputType === 'MULTI_OPTIONS'" />
                            </template>
                            <template #edit="{ row }">
                                <enum-category-select v-model="row.enumId" :projectId="projectId"
                                    v-if="row.inputType === 'OPTIONS' || row.inputType === 'MULTI_OPTIONS'" />
                            </template>
                        </vxe-column>
                        <vxe-column title="操作" header-align="center" width="60">
                            <template #default="{ row }">
                                <vxe-button type="text" circle icon="vxe-icon-delete"
                                    @click="onDeleteTableColumn(row)" />
                            </template>
                        </vxe-column>
                    </vxe-table>
                </a-form-model-item>
            </template>
        </a-form-model>

    </a-modal>
</template>
<script>
import Verte from 'verte';
import 'verte/dist/verte.css';
import {cloneDeep} from 'lodash'
import TrackerStatusSelect from '../../../components/select/TrackerStatusSelect.vue';
import EnumCategorySelect from '../../../components/select/EnumCategorySelect.vue';
export default {
    name: "TrackerFieldDialog",
    components: { Verte, TrackerStatusSelect, EnumCategorySelect },
    data() {
        const that = this;
        const nameValidator = (rule, value, callback) => {
            let found = false;
            let i = 0;
            for (i = 0; i < that.trackerFieldNames.length; i++) {
                if (that.trackerFieldNames[i] === value) {
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
                inputType: '',
                enumId: '',
                mandatory: false,
                exceptStatus: [],
                columns: []
            },
            color: '',
            foregroudColor: '#00000',
            backgroudColor: '#FFFFFF',
            showColorPicker: false,
            tableData: [],
            changedData: [],
            deletedData: [],
            rules: {
                name: [
                    { required: true, message: "请输入属性名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                    { validator: nameValidator, trigger: "change" },
                ],
                inputType: [
                    { required: true, message: "请输入属性名称", trigger: "blur" },
                ],
                enumId: [
                    { required: true, message: "请选择属性枚举值", trigger: "blur" },
                ],
                columns: [{ required: true, message: "请设置表格属性列", trigger: "blur" },

                ],
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
        trackerField: {
            required: true
        },
        tracker: {
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
        trackerFieldNames() {
            let names = []
            this.tracker.trackerFields.forEach(f => {
                if (f.id !== this.trackerField.id) {
                    names.push(f.name)
                }
            })
            return names
        },
        projectId() {
            return this.$route.params.projectId
        }

    },
    watch: {
        trackerField: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.changedData = [];
                    this.deletedData = [];
                }
            }
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        onChangeSelect() {
            console.log('ch', this.formData.enumId)
        },
        closeDialog() {
            this.$emit("close");
        },
        loadData() {
            const _this = this;
            if (this.trackerField) {
                this.formData = {
                    name: this.trackerField.name,
                    inputType: this.trackerField.inputType,
                    mandatory: this.trackerField.mandatory,
                    enumId: this.trackerField.enumId,
                    exceptStatus: [],
                    columns: this.trackerField.columns || [],
                }
                if (this.trackerField.mandatory && this.trackerField.exceptStatus && this.trackerField.exceptStatus.length > 0) {
                    this.trackerField.exceptStatus.forEach(s => { this.formData.exceptStatus.push(s.id) })
                }
            }
        },
        getStatus(id) {
            const statusList = this.tracker.trackerStatuses
            for (let i = 0; i < statusList.length; i++) {
                if (statusList[i].id === id) {
                    return statusList[i]
                }
            }
            return
        },
        onOK() {
            this.$refs["fieldForm"].validate((valid) => {
                if (valid) {
                    let tf = cloneDeep(this.trackerField)
                    tf.name = this.formData.name
                    tf.inputType = this.formData.inputType
                    tf.enumId = this.formData.enumId
                    tf.mandatory = this.formData.mandatory
                    tf.exceptStatus = []
                    tf.columns = this.formData.columns
                    if (this.formData.exceptStatus.length > 0) {
                        this.formData.exceptStatus.forEach(e => {
                            tf.exceptStatus.push(this.getStatus(e))
                        })
                    }
                    this.$emit("ok", tf);
                }
            })
        },
        onCancel() {
            this.$emit("cancel");
        },
        onMandatoryChange(checked) {
            this.formData.mandatory = checked
        },
        addTableColumn() {
            this.formData.columns.push({ name: '列' + (this.formData.columns.length + 1), inputType: 'TEXT' })
        },
        onDeleteTableColumn(row) {
            this.$refs.xTable.remove(row)
            this.formData.columns = this.$refs.xTable.getTableData().tableData
        }
    },
    created() { }
};
</script>
<style lang="less" scoped>
.box {
    width: 20px;
    height: 20px;
    border: solid 1px black;
}
</style>
