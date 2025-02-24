<template>
    <a-select :mode="mode" v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled"
        v-bind="$attrs">
        <a-select-option v-for="item in properties" :key="item.id" :title="item.name" :value="item.id">
            &nbsp;{{ item.name }}
        </a-select-option>
    </a-select>
</template>

<script>
import {
    findOneTracker
} from "@/services/tracker/TrackerService";

export default ({
    name: "TrackerPropertySelect",
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        projectId: {
            required: true
        },
        trackerId: {
            required: true
        },
        value: {
            required: true
        },
        excludes: {
            required: false,
            default: () => { return [] }
        },
        disabled: false,
        selectFirst: false,
        mode: {
            //'default' | 'multiple' | 'tags' | 'combobox'
            required: false
        },
    },
    watch: {
        trackerId: {
            handler: function (newVal, oldVal) {
                console.log("trackerId changed", newVal)
                this.loadData();
            }
        },
        value: function (curVal, oldVal) {
            if (curVal) {
                this.selectItem = curVal;
            } else {
                if (this.selectFirst && this.properties.length > 0) {
                    this.selectItem = this.properties[0].id;
                    this.onChange(this.selectItem);
                } else {
                    this.selectItem = undefined;
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
            properties: []
        };
    },
    methods: {
        loadData() {
            let that = this;
            if (this.trackerId) {
                findOneTracker(this.trackerId).then(resp => {
                    this.properties = resp.trackerFields || []
                    console.log(this.properties)
                    if (this.selectFirst && this.properties.length > 0) {
                        this.selectItem = this.properties[0].id;
                        this.onChange(this.selectItem);
                    }
                });
            }

        },
        onChange(v) {
            this.$emit("input", v);
            this.$emit("change", v, this.properties.find(item => item.id === v));
        },
    }
});
</script>
