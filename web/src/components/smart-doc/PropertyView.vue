<template>
  <div class="PropertyView-wrapper">
    <TrackerItemToolBar class="PropertyView-header" :itemId="workItemId" :tracker-item="trackerItem" @refresh="refresh"
      v-if="currentPropertyView === 'trackerItem'" :readOnly="readOnly" @changeTrackerItem="onChangeTrackerItem"></TrackerItemToolBar>
    <DocProperty :doc="doc" v-if="currentPropertyView === 'document'" @refresh="refresh"></DocProperty>
  </div>
</template>

<script>
import TrackerItemToolBar from '@/components/tool/TrackerItemToolBar'
import DocProperty from './DocProperty.vue'
export default {
  name: 'PropertyView',
  components: {
    TrackerItemToolBar, DocProperty
  },
  props: {
    doc:Object,
    workItemId: String,
    trackerItem: {},
    pageId: Number,
    currentPropertyView: String,
    readOnly: Boolean,
  },
  data() {
    return {
    }
  },
  methods: {
    refresh() {
      this.$emit('refresh')
    },
    onChangeTrackerItem(item) {
      console.log("propertyViewONChangeTrackerItem",item)
      this.$emit('changeTrackerItem',item)
    }
  },
  computed: {
  }
}
</script>

<style scoped>
.PropertyView-wrapper {
  width: 100%;
  height: 100%;
  border-left: 1px solid #DEDEDE;

  .PropertyView-header {
    width: 100%;
    height: 36px;
    line-height: 36px;
    padding-left: 15px;
    border-bottom: 1px solid #DEDEDE;
  }

}
</style>