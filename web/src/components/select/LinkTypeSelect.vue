<template>
    <a-select v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled">
        <a-select-option v-for="item in data" :key="item.id" :title="item.name" :value="item.id">
            {{ item.name }}
        </a-select-option>
    </a-select>
</template>

<script>
import { findLinkTypes } from '@/services/tracker/TrackerLinkTypeService'

export default ({
    name: "LinkTypeSelect",
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        projectId: {
            required: true
        },
        value: {
            required: true
        },
        disabled: false,
        selectFirst: true
    },
    watch: {
        projectId: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        value: function (curVal, oldVal) {
            if (curVal) {
                this.selectItem = curVal;
            } else {
                if (this.selectFirst && this.data.length > 0) {
                    this.selectItem = this.data[0].id;
                    this.onChange(this.selectItem);
                }
            }
        }
    },
    mounted() {
        this.selectItem = this.value
        this.loadData()
    },
    data() {
        return {
            selectItem: "",
            data: []
        };
    },
    methods: {
        loadData() {
            let that = this;
            findLinkTypes(this.projectId).then(resp => {
                that.data = []
                for (let item of resp){
                    that.data.push({
                        id: item.id+'-0',
                        name: item.name
                    });
                    that.data.push({
                        id: item.id+'-1',
                        name: item.oppositeName
                    })
                }

                if (this.selectFirst && this.data.length > 0) {
                    this.selectItem = this.data[0].id;
                    this.onChange(this.selectItem);
                }
            });
        },
        onChange(v) {
            this.$emit("input", v);
            this.$emit("change", v);
        }
    }
});
</script>
