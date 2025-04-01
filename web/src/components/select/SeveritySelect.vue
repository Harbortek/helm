<template>
    <vxe-select v-model="selectItem" :mode="mode" placeholder="请选择" @change="onChange" :disabled="disabled" v-bind="$attrs">
        <vxe-option v-for="item in data" :key="item.id" :label="item.name" :value="item.id">
            <span>({{ item.name }})</span>
        </vxe-option>
    </vxe-select>
</template>

<script>
import {
    findEnumsByCode
} from "@/services/system/EnumService";

export default ({
    name: "SeveritySelect",
    components:{ },
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        value: {
            required: true
        },
        disabled: false,
        mode: false,
    },
    watch: {
        value: {
            immediate: true,
            handler: function (curVal, oldVal) {
                this.selectItem = curVal;
                // console.log('this.selectItem', this.selectItem)
            }
        }
    },
    mounted() {
        this.loadData();
    },
    data() {
        return {
            selectItem: "",
            data: []
        };
    },
    methods: {
        loadData() {
            if(this.data.length==0){
                findEnumsByCode('TRACKER_SEVERITY').then(resp => {
                    this.data = resp;
                });
            }
            
        },
        onChange(e) {
            console.log(e)
            let val=this.data.find(item=>item.id==e.value)
            this.$emit("change", e.value,val);
        },
    }
});
</script>
<style lang="less" scoped>

</style>
