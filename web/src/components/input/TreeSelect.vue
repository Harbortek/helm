<template>
  <a-tree-select
    v-model="orgId"
    show-search
    style="width: 100%"
    :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
    placeholder="请选择所属部门"
    allow-clear
    :tree-data="treeData"
    tree-default-expand-all
    @change="onChange"
    :disabled="isView"
  />
</template>

<script>
  export default {
    name: 'TreeSelect',
    model: {
      prop: 'value',
      event: 'change.value'
    },
    props: {
      treeData: Array,
      value: String,
      isView: Boolean,
    },
    data() {
      return {
        orgTree: [],
        orgId: this.value,
      }
    },
    watch: {
      value: {
        handler(newV,oldV){
          this.orgId=newV;
        }
      }

    },
    mounted(){
      
    },
    computed: {

    },
    methods: {
     onChange(res){
      this.$emit("getData",res)
     }
    }
  }
</script>
