<template>
    <div class="h-avatar">
        <a-icon type="loading" v-if="loading" />
        <a-avatar v-if="!loading && iconUrl(icon || user?.icon)" :size="size"
            :src="iconUrl(icon || user?.icon)" />
        <a-avatar v-else-if="!loading" :size="size" style="backgroundColor:rgb(44,178,174)">
            {{ name ||user?.name }}
        </a-avatar>
        <template v-if="isShowName"> {{ name || user?.name }} </template>
    </div>
</template>
  
<script>
import { iconUrl } from "@/utils/util"
import { getUser } from '@/services/system/UserService'
export default {
    name: 'HAvatar',
    data() {
        return {
            loading: true,
            user: {}
        }
    },
    props: {
        icon: {
            type: String,
            required: false
        },
        name: {
            type: String,
            required: false
        },
        userId: {
            type: String,
            required: false
        },
        isShowName: {
            type: Boolean,
            default:true,
            required: false
        },
        size:{
            default:24,
            required:false
        }
        
    },
    watch: {
        name: {
            handler(v) {
                if (v) {
                    this.loading = false;
                }
            },
            immediate: true
        },
        userId: {
            handler(v) {
                if (!this.name && v) {
                    this.loading = true;
                    getUser(v).then(res => {
                        this.loading = false;
                        this.user = res;
                    })
                }
            },
            immediate: true
        }
    },
    created() {
    },
    methods: {
        iconUrl: function (v) {
            return iconUrl(v)
        },
        extractColorByName(name) {//传入名字,根据名字生成颜色
	      var temp = [];
	      temp.push("#");
	      for (let index = 0; index < name.length; index++) {
	        temp.push(parseInt(name[index].charCodeAt(0), 10).toString(16));
	      }
	      return temp.slice(0, 5).join('').slice(0, 4);
	    },
    }
}
</script>
  
<style lang="less" scoped>
.h-avatar {
    display: inline-block;
}
</style>
  