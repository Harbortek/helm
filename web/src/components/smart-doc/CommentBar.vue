<template>
    <div class="comment-bar" :style="{height:isShowMessage?'320px':'38px',overflow:'auto'}">
      <vxe-toolbar v-if="!isShowMessage" size="small" class="comment-bar-left">
        <template #buttons>
          <vxe-button @click="isShowMessage=!isShowMessage" type="text" icon="vxe-icon-menu">动态</vxe-button>
        </template>
      </vxe-toolbar>
      <div v-if="isShowMessage" class="task-desc-content" style="overflow:auto">
        <vxe-button @click="cancelDescription" style="position: absolute; right: 30px;" 
            type="text" icon="vxe-icon-close"></vxe-button>
        <tracker-comment style="height:100%" :pageId="pageId" title="动态" />
        
          <!-- <div class="task-desc-btns" style="height:10%" v-show="isShowMessage">
              <div class="task-desc-btns-right">
                  <a-button @click="cancelDescription">关闭</a-button>
              </div>
          </div> -->
      </div>
        
    </div>
</template>

<script>
import SimpleEditor from '@/components/editor/SimpleEditor.vue';
import TrackerComment from '@/pages/tracker/items/TrackerComment.vue';
export default ({
  name: 'CommentBar',
  components: {
    SimpleEditor,TrackerComment
  },
  props: {
    pageId: {
      type: String,
    }
  },
  computed: {

  },
  data() {
    return {
      isShowEditor:false,
      isShowMessage:false,
      comment:'',
      readOnly: false,
    }
  },
  mounted() {
  },
  methods: {
    cancelDescription() {
        this.isShowEditor=false;
        this.isShowMessage=false;
        this.$refs.editor?.blur()
        
    },
    changeComment(){
      console.log("this.comment",this.comment)
    },
  }
})
</script>
<style lang="less" scoped>
.comment-bar {
  width: 100%;
  height: 36px;
  // position: absolute;
  // bottom: 0px;
  background-color: white;
  border-top: 1px solid #DEDEDE;
  z-index: 1;

  .comment-bar-left {
    // position: absolute;
    // left: 5px;
    // top: 0px;
    // bottom: 0;
  }
}

.task-desc-content {
    position: relative;
    padding:10px;
    height: 100%;
    /deep/ .ui-task-detail-side{
      .ui-task-detail-side__inner{
        .task-detail-module{
          padding-top: 0px;
        }
      }
    }  


    .task-desc .task-desc-fullscreen-btn {
        display: flex;
        justify-content: flex-end;
        margin-top: -20px;
    }

    // .richtext-input .richtext-input-editor-wrapper {
    //     position: relative;
    //     height: 30%;
    // }

    .task-desc-btns {
        display: flex;
        justify-content: flex-end;
        margin-top: 10px;
    }
    .richtext-editor-content{
      position: relative;
      width: 100%;
      height: 100%;
    }
}
</style>