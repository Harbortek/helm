<template>
    <div class="review-wrapper">
        <vxe-toolbar size="medium">
            <template #buttons>
                <vxe-button v-action="'REVIEW_CREATE'" status="primary" :content="'创建评审'" @click="onCreateReview()"></vxe-button>
            </template>
            <template #tools>
                <vxe-form :data="searchData">

                    <vxe-form-item title="项目" field="projectId" :item-render="{}">
                        <template #default="{ data }">
                            <project-select style="width:130px;" v-model="data.projectId" placeholder="" @change="onSearch"
                                :allowClear="true"></project-select>
                        </template>
                    </vxe-form-item>
                    <vxe-form-item title="状态" field="status" :item-render="{}">
                        <template #default="{ data }">
                            <a-select style="width: 120px" v-model="data.status" allowClear :showSearch="true"
                                :filterOption="onFilterStatus" @change="onSearch" placeholder="请选择">
                                <a-select-option v-for="item in statusList" :key="item.value" :value="item.value">
                                    {{ item.label }}
                                </a-select-option>
                            </a-select>
                        </template>
                    </vxe-form-item>
                    <!-- <vxe-form-item title="发起人" field="creator" :item-render="{}">
                        <template #default="{ data }">
                            <project-user-select style="width:180px;" v-model="data.creator" :projectId="searchData.projectId"
                                @change="onSearch" />
                        </template>
                    </vxe-form-item>
                    <vxe-form-item title="评审人" field="reviewer" :item-render="{}">
                        <template #default="{ data }">
                            <project-user-select style="width:180px;" v-model="data.reviewer" :projectId="searchData.projectId"
                                @change="onSearch" />
                        </template>
                    </vxe-form-item> -->
                    <vxe-form-item title="评审名称" field="title" :item-render="{}">
                        <template #default="{ data }">
                            <vxe-input v-model="data.title" placeholder="请输入关键字" @change="onSearch"></vxe-input>
                        </template>
                    </vxe-form-item>
                </vxe-form>
            </template>
        </vxe-toolbar>




        <vxe-table show-overflow row-id="id" :loading="loading" :data="tableData">
            <vxe-column field="name" title="评审名称" sortable></vxe-column>
            <vxe-column field="status" title="项目" align="center">
                <template #default="{ row }">
                    <span>{{ row.project?.name }}</span>
                </template>
            </vxe-column>
            <vxe-column field="status" title="状态" align="center">
                <template #default="{ row }">
                    <span class="status__IN_PROGRESS" v-if="row.status === 'IN_PROGRESS'">进行中</span>
                    <span class="status__DONE" v-if="row.status === 'DONE'">已结束</span>
                </template>
            </vxe-column>
            <vxe-column field="passRate" title="评审通过率" align="center"
                :title-help="{ message: '评审通过率 = 评审结果为通过的任务数 / 评审任务总数', icon: 'vxe-icon-question-circle-fill' }">
                <template #default="{ row }">
                    <span :style="{ color: row.passRate == 1 ? 'green' : '' }">{{ `${((row.passRate || 0) * 100).toFixed(2)}%`
                    }}</span>
                </template>

            </vxe-column>
            <vxe-column field="dueDate" title="截止日期" align="center"></vxe-column>
            <vxe-column field="dones" title="已评">
                <template #default="{ row }">
                    <span>{{ `${row.dones.length || 0} / ${row.reviewers.length} 人` }}</span>
                </template>
            </vxe-column>
            <vxe-column field="creator" title="发起人" align="center">
                <template #default="{ row }">
                    <h-avatar :userId="row.creator?.id || row.creator" :name="row.creator?.name"
                        :icon="row.creator?.icon"></h-avatar>
                </template>
            </vxe-column>
            <vxe-column field="createDate" title="发起时间" align="center">
                <template #default="{ row }">
                    <span>{{ row.createDate | formatDate }}</span>
                </template>


            </vxe-column>
            <vxe-column title="操作" header-align="center" align="center">
                <template #default="{ row }">
                    <a @click="onView(row)" v-action="'REVIEW_LIST'">查看</a>
                    <a-dropdown v-action="'REVIEW_CREATE'">
                        <a class="ant-dropdown-link" @click="e => e.preventDefault()">
                            更多 <a-icon type="down" />
                        </a>
                        <a-menu slot="overlay">
                            <a-menu-item v-if="row.status === 'IN_PROGRESS'">
                                <a @click="onEdit(row)">编辑</a>
                            </a-menu-item>
                            <a-menu-item v-else>
                                <a @click="onCreateReview(row)">重新发起</a>
                            </a-menu-item>
                            <a-menu-item>
                                <a type="danger" style="color:rgb(248, 29, 34)" @click="onDelete(row.id)">删除</a>
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                </template>
            </vxe-column>
        </vxe-table>

        <vxe-pager :loading="loading" :current-page="tablePage.currentPage" :page-size="tablePage.pageSize"
            :total="tablePage.totalResult" :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="onPageChange">
        </vxe-pager>
        <review-modal ref="reviewModal" v-model="reviewModalOpend" :mode="reviewModalMode"
            @REVIEW_SAVE="hanleReviewSave"></review-modal>

        <review-progress-modal ref="reviewProgressModal" v-model="reviewProgressModalOpend" @REVIEW_SAVE="hanleReviewSave"
            @REVIEW_LIST_RELOAD="handleReload"></review-progress-modal>
    </div>
</template>

<script lang="js">
import ProjectSelect from "@/components/select/ProjectSelect.vue";
import HAvatar from '@/components/avatar/h-avatar.vue';
import {
    findEnumsByCode
} from "@/services/system/EnumService";
import ContentPage from '@/components/page/content/ContentPage.vue';
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import ReviewModal from './ReviewModal.vue';
import ReviewProgressModal from '././review-progress/ReviewProgressModal.vue';
import VXETable from "vxe-table";
import { findAll, findOneReview, updateOneReview, deleteOneReview, findReviewStatusList, findUsers } from '@/services/review/ReviewService';
import { formatDate } from '@/utils/DateUtils.js'
export default {
    name: 'ReviewList',
    components: { ContentPage, ProjectUserSelect, ReviewModal, ReviewProgressModal, HAvatar, ProjectSelect },
    data() {
        return {
            reviewModalOpend: false,
            reviewModalMode: 'ADD',
            reviewProgressModalOpend: false,
            statusList: [],
            reviewerList: [],
            creatorList: [],
            searchData: {},
            loading: false,
            tableData: [],
            PROJECT_REVIEW_TYPES: [],
            tablePage: {
                currentPage: 1,
                pageSize: 10,
                totalResult: 0
            },
            projectId: null
        }
    },
    // computed: {
    //     projectId() {
    //         return this.$route.params.projectId
    //     },
    // },
    created() {
        this.loadEnums();
        this.loadStatusList();
        this.loadTableData();

    },
    filters: {
        formatDate(value) {
            return formatDate(Date.parse(value));
        },
    },
    methods: {
        onSearch() {
            this.loadTableData();
        },
        onCreateReview(row) {
            this.reviewModalOpend = true;
            this.reviewModalMode = 'ADD';
            // if(reviewId){
            this.$refs.reviewModal.loadReview(row?.project?.id, row?.id);
            // }
        },
        onFilterStatus(value, option) {
            return option.componentOptions.children[0].text.indexOf(value) >= 0
        },
        onDelete(id) {
            const p = VXETable.modal.confirm('您确定要删除吗？')
            p.then((res) => {
                if (res === 'confirm') {
                    deleteOneReview(id).then(() => {
                        this.loadTableData();
                    })
                }
            })
        },
        onView(row) {
            this.reviewProgressModalOpend = true;
            this.$refs.reviewProgressModal.loadReview(row.project?.id, row.id);
        },
        onEdit(row) {
            this.reviewModalOpend = true;
            this.reviewModalMode = 'EDIT';
            this.$refs.reviewModal.loadReview(row.project?.id, row.id);
        },
        loadTableData() {
            findAll({
                page: this.tablePage.currentPage-1,
                size: this.tablePage.pageSize,
            }, this.searchData).then(resp => {
                this.calcPassRate(resp.content);
                this.tablePage.totalResult = parseInt(resp.totalElements);
                this.loading = false;
            })
        },
        calcPassRate(content) {
            let that = this;
            this.loadEnums().then(() => {
                let vv = content;
                for (let i = 0; i < vv.length; i++) {
                    let data = vv[i];
                    const reviewStatuses = data.reviewStatuses;
                    let rss = {};
                    reviewStatuses.map(rs => {
                        for (let j = 0; j < this.PROJECT_REVIEW_TYPES.length; j++) {
                            const prt = this.PROJECT_REVIEW_TYPES[j];
                            if (prt.id == rs.statusId) {
                                if ("通过" == prt.name) {
                                    if (rss[rs.objectId]) {
                                        rss[rs.objectId] = rss[rs.objectId] + 1;
                                    } else {
                                        rss[rs.objectId] = 1;
                                    }
                                    break;
                                }

                            }
                        }


                    })
                    let cnt = 0;
                    for (let key in rss) {
                        cnt = cnt + Math.floor(rss[key] / data.reviewers.length);
                    }
                    data.passRate = cnt / data.total;
                }
                that.tableData = content;
            });
        },
        loadEnums() {
            const that = this;
            let p = new Promise((reslove, reject) => {
                if (that.PROJECT_REVIEW_TYPES.length > 0) {
                    reslove();
                } else {
                    findEnumsByCode('PROJECT_REVIEW_TYPE').then((resp) => {
                        that.PROJECT_REVIEW_TYPES = resp || [];
                        reslove();
                    })
                }
            })
            return p;

        },
        onPageChange({ currentPage, pageSize }) {
            this.tablePage.currentPage = currentPage
            this.tablePage.pageSize = pageSize
            this.loadTableData();
        },

        loadStatusList() {
            findReviewStatusList().then(resp => {
                this.statusList = resp;
            });

        },
        hanleReviewSave(vo) {
            this.loadTableData();
        },
        handleReload() {
            this.loadTableData();
        }
    }
}
</script>

<style lang="less" scoped>
.review-wrapper {
    width: 100%;
    height: 100%;
    padding: 0px 10px 10px 10px;
    background-color: white;

    .status__IN_PROGRESS {
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        display: inline-block;
        padding: 0px 4px;
        line-height: 16px;
        border: 1px solid rgba(255, 170, 33, 0.5);
        border-radius: 2px;
        font-size: 12px;
        color: rgb(255, 170, 33);
    }

    .status__DONE {
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        display: inline-block;
        padding: 0px 4px;
        line-height: 16px;
        border: 1px solid rgba(109, 118, 148, 0.5);
        border-radius: 2px;
        font-size: 12px;
        color: rgb(109, 118, 148);
    }
}
</style>