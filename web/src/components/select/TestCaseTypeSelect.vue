<template>
    <a-select v-model="selectItem" placeholder="请选择测试用例类型" @change="onChange" :disabled="disabled">
        <a-select-option v-for="item in data" :key="item.id" :title="item.name" :value="item.id">
            {{ item.name}}
        </a-select-option>
    </a-select>
</template>

<script>
import {
    findEnumsByCode
} from "@/services/system/EnumService";

export default ({
    name: "TestCaseTypeSelect",
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        value: {
            required: true
        },
        disabled: false,
    },
    watch: {
        value: function (curVal, oldVal) {
            this.selectItem = curVal;
        }
    },
    mounted() {
        this.loadData()
        if(this.value){
            this.selectItem = this.value;
        }
    },
    data() {
        return {
            selectItem: undefined,
            data: []
        };
    },
    methods: {
        loadData() {
            findEnumsByCode('TEST_CASE_TYPE').then(resp => {
                this.data = resp;
                if(!this.selectItem){
                    this.initSelect();
                }
            });
        },
        onChange(v) {
            const e =this.data.find(_=>_.id===v)
            this.$emit("change",v,e);
        },
        //初始化选择 第一项
        initSelect(){
            if(this.data.length>0){
                this.$emit("onInit",this.data[0].id,this.data[0]);
                // this.onChange(this.data[0].id)
            }
        },
    }
});
</script>
