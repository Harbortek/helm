
<template>
  <a-popover v-model="dropdownVisible" trigger="click" placement="top" :overlayClassName="'dropdown-popover'">
      <template slot="content">
          <div style="width: 480px;height: 470px;">
            <div class="container">
              <div class="header">
                <div class="header-left">手工录入</div>
                <div class="header-right">快捷输入：可从数据表中直接从朋友两列进行填入</div>
              </div>
              <div class="main">
                <div class="left">
                  <div style="margin-top: 5px;">查询值</div>
                  
                  <div class="option-item" >
                    <a-textarea v-model="queryValue" style="min-height: 325px;z-index: 2;background: rgba(235,234,239,.15);"/>  
                    <div class="tip" v-if="!queryValue">
                      <div style="margin-bottom: 10px;">查询值必填</div>
                      <div>请一行填写一个</div>
                      <div>最多添加10000个</div>
                      <div>确定时会自动过滤查询值和显示名称均重复的值</div>
                    </div>
                  </div>
                  <div class="add-all-button">
                    <span>已添加{{ queryValue.trim().split('\n').length}}行</span>
                    <a-button size="small" type="link" icon="delete" @click="clearSelectionQuery">清空</a-button>
                  </div>
                </div>
                <div class="right">
                  <div style="margin-top: 5px;">显示名称</div>
                  <div class="option-item">
                    <a-textarea v-model="showValue" style="min-height: 325px;z-index: 2;background: rgba(235,234,239,.15);" /> 
                    <div class="tip" v-if="!showValue">
                      <div style="margin-bottom: 10px;">显示名称非必填</div>
                      <div>非填写时默认查询值与显示名称一致</div>
                    </div>
                  </div>
                  <div class="footer-box">
                    <span>已添加{{ showValue.trim().split('\n').length}}行</span>
                    <a-button size="small" type="link" icon="delete" @click="clearSelectionShow">清空</a-button>
                  </div>
                </div>
              </div>
              <div class="footer" style="justify-content: flex-end;">
                <div>
                  <a-button size="small" @click="cancel" style="margin-right: 10px;">取消</a-button>
                  <a-button size="small" type="primary" @click="confirm">确认</a-button>
                </div>
              </div>
            </div>
          </div>
      </template> 
      <a-button icon="edit" size="small"><span style="font-size: 12px;">手工录入</span></a-button>
  </a-popover>
</template>


<script>
import moment from 'moment';

export default {
  name: 'ManualInputPopover',
  props: {
    options: {
      type: Array,
      required: true
    },
    excludeMode:{
      type:Boolean,
      default:false
    }
  },
  data() {
    return {
      queryValue:'',
      showValue:'',
      dropdownVisible:false,
    }
  },
  watch:{
    dropdownVisible(newVal){
      if(newVal){
        this.queryValue = this.options.map(item => item.key).join('\n');
        this.showValue = this.options.map(item => item.value).join('\n');
        if(this.showValue===''){
          this.showValue = this.queryValue
        }
      }
    }
  },
  mounted() {
    
  },
  computed: {
    
  },
  methods: {
    
    clearSelectionQuery() {
      this.queryValue = '';
    },
    clearSelectionShow() {
      this.showValue = '';
    },
    cancel() {
      this.dropdownVisible=false
    },
    confirm() {
      let query=[...new Set(this.queryValue.split('\n'))].filter(item=>{return item!=''});
      let show = [...new Set(this.showValue.split('\n'))].filter(item=>{return item!=''});
      if(this.showValue===''){
        show=query;
      }
      if(show.length>0&&show.length!=query.length){
        this.$message.error('查询值与显示名称行数不一致');
      }else if(query.length==0){
        this.$message.error('查询值不能为空');
      }else{
        let result = query.reduce((arr, key, index) => {
          arr.push({ key:key,value:show[index] });
          return arr;
        }, []);
        this.$emit('ok',result)
        this.dropdownVisible=false
        this.queryValue = query.join('\n');
        this.showValue = show.join('\n');
      }
    },
    
  }
}
</script>

<style scoped lang="less">
::v-deep .ant-form-item-label > label {
  font-size: 12px;
}
::v-deep span{
  font-size: 12px;
}
::v-deep input,div{
  font-size: 12px;
}
::v-deep .ant-popover-inner-content{
  padding-bottom: 0px !important;
  margin-bottom: -10px !important;
}

.container {
  display: flex;
  flex-direction: column;
  height: 100%;
  font-size: 12px;
}
.header {
  display: flex;
  padding: 8px;
  .header-left {
    font-size: 14px;
    margin-left: -10px;
  }
  .header-right {
    margin-left: 20px;
    color:rgba(0,0,0,.4);
  }
}
.main {
  display: flex;
  flex: 1;
  overflow: hidden;
}
.left,
.right {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  border-top: 1px solid #eaeaea;
}
.left {
  position: relative;
  /* margin-right: 8px; */
  .tip{
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;    
    width: 136px;
    text-align: center;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    align-content: center;
    justify-content: center;
    color: rgba(0,0,0,.4);
    z-index: 1;
    margin: auto;
  }
}
.right {
  /* margin-left: 8px; */
  border-top: 1px solid #eaeaea;
  border-left: 1px solid #eaeaea;
  position: relative;
  .tip{
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;    
    width: 136px;
    text-align: center;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    align-content: center;
    justify-content: center;
    color: rgba(0,0,0,.4);
    z-index: 1;
    margin: auto;
  }
}
.a-divider {
  height: 100%;
}
.option-item {
  display: flex;
  align-items: center;
  padding: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.check-space {
  width: 16px;
  display: inline-block;
}
.add-all-button {
  margin-top: auto; /* 固定到最下方 */
  width: 97%;
  margin-bottom: 5px;
  height: 26px;
}
.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-top: 1px solid #eaeaea;
  margin-bottom: -8px;
}
.right .footer-box {
  margin-top: auto; /* 固定到右侧最下方 */
  margin-bottom: 5px;
  height: 30px;
  border-top: 1px solid #eaeaea;
  padding-top: 3px;
}
.search-box {
  border: none; /* 移除边框 */
  margin-bottom: 8px;
}
.dropdown-popover ::v-deep .ant-popover-arrow{
  display: none;
}
</style>