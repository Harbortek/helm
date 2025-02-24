<template>
  <div>
    <a-modal
      v-if="visiable"
      v-model="visiable"
      width="1200px"
      title="批量删除工作项"
      centered
      @ok="onOK"
      @cancel="onCancel"
    >
      <template slot="footer">
        <vxe-button content="取消" @click="onCancel"></vxe-button>
        <vxe-button @click="onPrevStep" content="上一步"></vxe-button>
        <vxe-button status="primary" @click="onOK" content="确认"></vxe-button>
      </template>
      <div class="table-box">
          <span style="color: #87888a"
              >已选择 1 个项目，1 个工作项类型，共 {{this.selectedRows.length}} 个工作项，以下操作将批量删除工作项。</span>
          

            <div style="margin-top: 10px;padding: 10px 0;border-top: 1px solid #d4d6d9;">确认删除以下工作项。</div>

            <div class="page-header">
              <div class="page-description page-description-info manager-desc">
                <div class="page-description-border"></div>
                <div class="page-description-container">
                  <a-icon type="warning" style="color:#f59300;margin-right: 5px;height: 20px;line-height: 25px;" />
                  <p class="page-description-p style--font-14">
                    子工作项会因父工作项的删除而被删除。
                  </p>
                </div>
              </div>
            </div>
            <div style="height: calc(100% - 130px); margin-top: 10px;">
              <vxe-table ref="vxeTable" row-id="id" height="auto" :loading="loading" style="cursor: pointer;" :data="selectedRows"
                :row-config="{ isHover: true }">
                  <vxe-column field="itemNo" title="编号" width="100px">
                      <template #default="{ row }">
                          {{projectKeyName+'-'+row.itemNo}}
                      </template>
                  </vxe-column>
                  <vxe-column field="name" title="标题" min-width="200" show-overflow>
                    <template #default="{ row }">
                      <div>
                        <a-icon :component="row.icon"/>&nbsp;
                        <span>{{ row.name }}</span>
                      </div>
                    </template>
                  </vxe-column>
                  <vxe-column field="tracker.name" title="工作项类型" min-width="100"></vxe-column>
                  <vxe-column field="createDate" title="创建时间" min-width="100" show-overflow></vxe-column>
                  <vxe-column field="createBy.name" title="创建者1" min-width="100">
                    <template #default="{ row }">
                        <h-avatar :name="row.createBy?.name" :icon="row.createBy?.icon"></h-avatar>
                    </template>
                  </vxe-column>
                  <vxe-column field="owner.name" title="负责人" min-width="100">
                    <template #default="{ row }">
                        <div v-if="row.owner">
                          <h-avatar :name="row.owner?.name" :icon="row.owner?.icon"></h-avatar>
                        </div>
                        <div v-else>未分配</div>
                    </template>
                  </vxe-column>
                  <vxe-column field="lastModifiedDate" title="更新时间" min-width="100" show-overflow></vxe-column>
                  <vxe-column field="project.name" title="所属项目" min-width="100"></vxe-column>
                  <vxe-column field="priority.name" title="优先级" min-width="100">
                    <template #default="{ row }">
                        <a-tag style="border:none"
                            :style="{ color: row.priority.color, backgroundColor: row.priority.backgroundColor }">{{
                                row.priority.name }}</a-tag>
                    </template>
                  </vxe-column>
                  <vxe-column field="status.name" title="状态" min-width="100">
                    <template #default="{ row }">
                        <div class="transition-status">
                            <span class="ui-tag-status"
                                :style="{ color: row.meaning?.color, 'border-color': row.meaning?.color }">{{
                                    row.status?.name }}</span>
                        </div>
                    </template>
                  </vxe-column>
                  <vxe-column field="sprint.name" title="所属迭代" min-width="100"></vxe-column>
                  <vxe-column field="closeDate" title="关闭时间" min-width="100"></vxe-column>
              </vxe-table>
            </div>
      </div>
    </a-modal>
    <vxe-modal v-model="isShowLoading" top="50" id='loadMessage' :duration="-1" type="message" :showHeader="false" :showFooter="false" :maskClosable="false">
        <a-icon style="font-size:20px;" type="loading" />
        <span style="margin-left:10px;font-size:18px;color: #202d40;line-height: 24px;font-weight: 600;">正在删除，请稍后...</span>
    </vxe-modal>
  </div>
</template>
<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import { cloneDeep } from 'lodash';
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import PrioritySelect from '@/components/select/PrioritySelect.vue';
import SprintSelect from '@/components/select/SprintSelect.vue';
import { batchDeleteTrackerItem} from "@/services/tracker/TrackerItemService";
import { VXETable } from 'vxe-table';


export default {
  name: "TrackerItemBatchDeleteModal",
  components: {ConfigPage,ProjectUserSelect, PrioritySelect, SprintSelect, HAvatar},
  data() {
    return {
      loading: false,
      selectedRows: [],
      isShowLoading: false,
    };
  },
  props: {
    projectId: {
      required: true,
    },
    isShowDialog: {
      required: true,
    },
    initalData: {
      required: false,
    },
    projectKeyName: {
      required: false,
    }
  },
  computed: {
    visiable: {
      get() {
        return this.isShowDialog;
      },
      set(newValue) {
        return newValue;
      },
    },
  },
  watch: {
    isShowDialog: {
      handler: function (newVal, oldVal) {
        if (newVal) {
          this.selectedRows=cloneDeep(this.initalData);
        }
      },
    },
  },
  mounted() {},
  methods: {
    onPrevStep(){
      this.$emit("onPrevStep")
    },
    onOK() {
      this.isShowLoading=true;
      let itemIds=this.selectedRows.map(item=>item.id);
      batchDeleteTrackerItem(itemIds).then(res=>{
         this.isShowLoading=false;
         this.$emit("refresh");
         this.onCancel();
        VXETable.modal.message({ status: 'success', content: '批量删除成功' })
      }).catch((error) => {
        VXETable.modal.message({ status: 'error', content: '批量删除失败' })
        this.isShowLoading=false;
      })
      // this.onCancel();
    },
    onCancel() {
      this.selectedRowKeys = [];
      this.selectedRows = [];
      this.$emit("cancel");
    },
  },
  created() {},
};
</script>
<style lang="less" scoped>
.avatar-uploader {
  padding: 0 30px;
  /deep/ .ant-upload {
    width: 100%;
    height: 128px;
  }
}
.table-box {
  height: 650px;
  box-sizing: border-box;
  direction: ltr;
  position: relative;
  will-change: transform;
  overflow-y: auto;
  transition: height 0.25s;
}


.config-page {
  margin: 0;
  overflow: auto;
  padding: 20px;
  background: #fff;
  height: 100%;
  flex: 1 0 auto;
  display: flex;
  flex-direction: column;

  .page-header {
    width: 100%;
    flex: 0 0 auto;
    line-height: 1;
    margin-top: 150px;

    .ant-breadcrumb {
      color: rgba(93, 99, 105);
      font-size: 18px;
      line-height: 26px;
    }

    .ant-breadcrumb a {
      color: rgba(93, 99, 105);
      font-size: 18px;
      line-height: 26px;
    }

  }

  .page-content {
    padding-top: 10px;
  }

  .page-content.autofit {
    height: 100%;
  }

  .header-footer-panel {
    // height: 100%;
    height: calc(100vh - 162px);
    display: flex;
    flex-direction: column;

    .panel-header {}

    .panel-body {
      // height: 100%;/
      height: calc(100vh - 240px);
      flex: 1 1 auto;
    }

    .panel-footer {}
  }
}



.page-description {
  display: flex;
  line-height: 1.5;
  box-sizing: border-box;
  border-bottom: 1px solid #e8e8e8;
  border-radius: 0;
  border-right: 1px solid #e8e8e8;
  border-top: 1px solid #e8e8e8;
}

.page-description-border {
  border-radius: 0;
  flex: none;
  width: 4px;
}

.page-description-info .page-description-border {
  // background: #338fe5;
  background: #f59300;
}

.page-description-container {
  background: #fff;
  display: flex;
  flex: auto;
  padding: 10px;
}

.page-description-p {
  margin: 0 0 0 0;
  padding: 0;
  white-space: pre-wrap;
}

.style--font-14 {
  font-size: 14px;
  line-height: 22px;
}
.transform-list-header {
    height: 48px;
    font-size: 15px;
    font-weight: 500;
    position: relative;
    display: flex;
    align-items: center;
    border-bottom: 1px solid #dfe1e5;
    border-top: 1px solid #d4d6d9;
    .header-item{
        align-items: center;
        width: 320px;
        padding-right: 10px;
        text-overflow: ellipsis;
    }
}
.transition-status {
    min-width: 110px;
    display: flex;
    align-items: center;
    margin-right: 20px;
    white-space: nowrap;

    .ui-tag-status {
        max-width: 110px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 12px;
        box-sizing: border-box;
        display: inline-block;
        height: 20px;
        line-height: 18px;
        transition: border-color .2s;
        border: 1px solid;
        border-radius: 20px;
        padding: 0px 8px;
    }
}
</style>
