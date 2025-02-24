<template>
        <config-page style="padding:0; margin: 0;overflow: visible;" description="属性修改权限可以定制对工作项具体属性的修改权限，当用户拥有工作项编辑权限且拥有此属性修改权限时，才可在新建工作项或查看工作项时修改此属性。">
            
            <vxe-input v-model="keyword" placeholder="搜索工作项属性" style="width:240px" type="search" clearable
                    ></vxe-input>
            <vxe-table ref="dataTable" :data="customerFileds" :loading="loading" :row-config="{ isHover: true }"
                >   
                <vxe-column field="name" title="属性名称">
                    <template #default="{ row }">
                        <a-space>
                            <span
                                :style="{ 'padding-left': row.group ? '0' : '20px', 'font-weight': row.group ? '500' : '400' }">{{
                                    row.name
                                }}</span>
                            <a-tag v-if="row.system"> 系统</a-tag>
                        </a-space>
                    </template>
                </vxe-column>
                <vxe-column field="inputType" title="属性类型">
                    <template #default="{ row }">
                        <div v-if="!row.group">{{ $t('tracker.field.type.' + row.inputType) }}</div>
                    </template>
                </vxe-column>
                <vxe-column field="permission" title="权限" align="start">
                    <template #default="{ row }">
                        <div v-if="row.permission">
                            {{$t('tracker.field.permission.type.' + row.permission.type) }}
                        </div>
                    </template>
                </vxe-column>
                <vxe-column field="" title="操作" width="200" align="center">
                    <template #default="{ row }">
                        <div v-if="row.system">
                            <div class="can-not-edit"></div>
                        </div>
                        <div>
                            <vxe-button v-if="!row.group && !row.system" type="text" icon="vxe-icon-edit"
                                @click="onEditFieldPerm(row)">修改权限</vxe-button>
                        </div>
                    </template>
                </vxe-column>
            </vxe-table>
        <tracker-field-dialog :isShowDialog="isShowEditDialog" :editMode="editMode" :trackerField="currentTrackerField"
            :tracker="tracker" @cancel="isShowEditDialog = false" @ok="onEditFieldOK"></tracker-field-dialog>
        <tracker-field-permission-dialog :isShowDialog="isShowEditPermDialog" :editMode="editMode" :trackerField="currentTrackerField"
            :tracker="tracker" @cancel="isShowEditPermDialog = false" @ok="onEditFieldOK"></tracker-field-permission-dialog>
        </config-page>    
</template>
<script>
import {
    findOneTracker,
    updateTracker,
    findTrackerFields, createTrackerField, updateTrackerField, deleteTrackerField
} from "@/services/tracker/TrackerService";
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import TrackerFieldDialog from './TrackerFieldDialog.vue';
import TrackerFieldPermissionDialog from './TrackerFieldPermissionDialog.vue';
import VXETable from "vxe-table";
import Vue from "vue";
export default {
    name: "TrackerFieldsPermission",
    components: { ConfigPage, TrackerFieldDialog,TrackerFieldPermissionDialog },
    computed: {
        customerFileds() {
            let customerFileds = []
            // customerFileds.push({ name: '自定义属性', group: true })
            if (this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                this.tracker.trackerFields.forEach(f => {
                    if (f.system === false&&(new RegExp(this.keyword)).test(f.name)) {
                        customerFileds.push(f)
                    }
                })
            }
            return customerFileds
        },
    },
    data() {
        return {
            tableHeight: 400,
            loading: false,
            keyword: '',
            tracker: {},
            isShowEditDialog: false,
            isShowEditPermDialog: false,
            currentTrackerField: {},
            editMode: 'create',
        };
    },
    mounted() {
        this.$nextTick(function () {
            if (this.$refs.dataTable) {
                this.tableHeight =
                    window.innerHeight - this.$refs.dataTable?.$el.offsetTop - 100;
            }
            // 监听窗口大小变化
            let self = this;
            window.onresize = function () {
                self.tableHeight =
                    window.innerHeight - self.$refs.dataTable?.$el.offsetTop - 100;
            };
        });

        this.loadData()
    },
    methods: {
        routes: function () {
            return [{
                name: "trackerListConfig", meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerListConfig",
                    show: false,
                },
            }, {
                name: "trackerConfigPortal",
                meta: {
                    icon: "blank",
                    title: this.tracker.name,
                    show: false,
                },
            }, {
                name: 'trackerFieldsConfig', meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerFieldsConfig",
                    show: false,
                },
            }]
        },
        loadData() {
            this.loading=true;
            let trackerId = this.$route.params.trackerId
            findOneTracker(trackerId).then(resp => {
                this.tracker = resp;
                console.log(this.tracker.trackerFields)
                if (this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                    // let index = 1
                    // this.tracker.trackerFields.forEach(f => {
                    //     f.id = index++
                    // })
                }
            }).finally(()=>{
                this.loading=false;
            })
        },
        onCopyFromTracker() {

        },
        onCreateFiled() {
            this.editMode = 'create'
            const fieldIds = this.tracker.trackerFields.map(f => f.id);
            let MAXID = fieldIds.reduce((a, b) => {
                return Math.max(a, b);
            });
            this.currentTrackerField = { id: null, name: '', inputType: 'TEXT', system: false, items: [] }
            this.isShowEditDialog = true

        },
        onEditField(row) {
            this.editMode = 'edit'
            this.currentTrackerField = row
            this.isShowEditDialog = true
        },
        onEditFieldPerm(row) {
            this.editMode = 'edit'
            this.currentTrackerField = JSON.parse(JSON.stringify(row))
            this.isShowEditPermDialog = true
        },
        onDeleteField(row) {
            VXETable.modal.confirm({
                title: '删除工作项属性',
                content: '「' + row.name + '」将被删除，该动作不可恢复'
            }).then(type => {
                if (type === 'confirm') {
                    deleteTrackerField(this.tracker.id, row).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」已被删除', status: 'info' })
                        this.loadData()
                    })
                }
            })
        },
        onEditFieldOK(tf) {
            this.isShowEditDialog = false
            this.isShowEditPermDialog = false
            const fieldIds = this.tracker.trackerFields.map(f => f.id);
            // let MAXID = fieldIds.reduce((a, b) => {
            //     return Math.max(a, b);
            // });
            if (tf.id === null) {
                //插入新数据
                createTrackerField(this.tracker.id, tf).then(resp => {
                    this.loadData()
                })
                // this.tracker.trackerFields.push(tf)
                // console.log(this.tracker.trackerFields)
            } else {
                this.currentTrackerField.name = tf.name
                this.currentTrackerField.items = tf.items
                this.currentTrackerField.mandatory = tf.mandatory
                this.currentTrackerField.exceptStatus = tf.exceptStatus
                this.currentTrackerField.permission=tf.permission
                updateTrackerField(this.tracker.id, this.currentTrackerField).then(resp => {
                    this.loadData()
                })
                // this.tracker.trackerFields.forEach(f => {
                //     if (f.id === this.currentTrackerField.id) {
                //         f.name = tf.name
                //         f.items = tf.items
                //         f.mandatory = tf.mandatory
                //         f.exceptStatus = tf.exceptStatus
                //     }
                // })
            }
            
            // let trackerId = this.$route.params.trackerId

            // updateTracker(trackerId, this.tracker).then(resp => {
            //     this.loadData();
            // })
        },
    },
};
</script>
<style lang="less" scoped>
.list-title {
    color: rgba(93, 99, 105);
    font-size: 18px;
    font-weight: 500;
    line-height: 1.5;
}
.table-view {
    .first-col {
        position: relative;
        height: 20px;
    }

    .first-col:before {
        content: "";
        position: absolute;
        left: -14px;
        top: 10px;
        width: 204px;
        height: 1px;
        transform: rotate(13deg);
        background-color: #e8eaec;
    }

    .first-col .first-col-top {
        position: absolute;
        right: 4px;
        top: -10px;
    }

    .first-col .first-col-bottom {
        position: absolute;
        left: 4px;
        bottom: -10px;
    }
}

</style>
