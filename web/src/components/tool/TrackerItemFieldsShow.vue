<template>
  <ItemCustomFieldsShow :fields="fields" v-model="value" :projectId="projectId" :trackerId="trackerItem?.tracker?.id"
    :readOnly="readOnly" @change="onChangeSystemField"></ItemCustomFieldsShow>
</template>

<script>
import _ from 'lodash'
import ItemCustomFieldsShow from '@/components/tool/ItemCustomFieldsShow.vue';
import { changeSystemField,changeCustomerField } from "@/services/tracker/TrackerItemService"
export default {
  name: 'TrackerItemFieldsShow',
  components: { ItemCustomFieldsShow },
  props: {
    projectId: {
      type: String,
    },
    trackerItem: {//不修改属性值
      required: true,
    },
    fields: {
      type: Object,
      required: true,
    },
    readOnly: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      editorInEditMode: false,
      value: '',
      newValue: '',
    }
  },
  computed: {

  },
  watch: {
    value: function (newVal) {

    },
  },
  mounted() {
    this.value = this.getFieldsData(this.fields)
  },
  methods: {
    getSystemProperty(field) {
      let property = _.get(this.trackerItem, field.systemProperty)
      if (property) {
        if (property instanceof Object) {
          return field.systemProperty + ".id"
        } else {
          return field.systemProperty
        }
      }
      if (field.systemProperty.endsWith("Id")) {
        return field.systemProperty.substring(0, field.systemProperty.length - 2) + ".id"
      }
      
      return "";
    },
    getFieldsData(field) {
      if (field.system) {
        var systemProperty = this.getSystemProperty(field)
        if (systemProperty) {
          return _.get(this.trackerItem, systemProperty)
        }
        return "";
      } else {
        if (this.trackerItem?.values) {
          return this.trackerItem?.values[field.id]
        } else {
          return "";
        }
      }
    },
    onChangeSystemField(fieldId, value) {
      if(this.fields.system){
        changeSystemField(this.trackerItem.id, this.fields.systemProperty, value ).then(resp => {
          this.$emit('change', fieldId, value)
        })
      }else{
        changeCustomerField(this.trackerItem.id, fieldId, value).then(resp => {
            this.$emit('change', fieldId, value)
        })
      }
    },
  }
}
</script>

<style lang="less" scoped>
.read-only-div /deep/.ant-input[disabled],
.ant-input[disabled]:hover {
  color: #000000;
  background-color: #ffffff;
  border: none;
  resize: none;
}

.read-only-div /deep/.ant-select-disabled .ant-select-selection {
  background-color: #ffffff;
  border: none;
  resize: none;
  cursor: auto;
}

.read-only-div /deep/.ant-select-arrow {
  display: none;
}
</style>
