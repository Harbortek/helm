<template>
    <div class="user-selector">
        <span class="user-item" v-for="(user, index) in currentUsers" :key="user.id" :style="{ left: index * -6 + 'px' }">
            <a-tooltip>
                <template slot="title">
                    {{ user.name }}
                </template>
                <h-avatar :name="user.name" :icon="user.icon" :isShowName="false"></h-avatar>
            </a-tooltip>
        </span>
        <a-dropdown :trigger="['click']" v-model="visible">
            <a class="ant-dropdown-link" @click="e => e.preventDefault()" style="font-size: 18px;">
                <a-icon type="plus-circle" />
            </a>
            <a-menu slot="overlay" @click="handleMenuClick">
                <a-menu-item key="1">
                    <a-input placeholder="搜索"  v-model="searchKey"></a-input>
                </a-menu-item>
                <a-menu-item v-for="(user, index) in searchedUsers" :key="user.id" @click="handleSelect(user)">
                    <span>
                        <h-avatar :userId="user.id" :name="user.name" :icon="user.icon"></h-avatar>
                    </span>
                    <span style="right: 8px;position: absolute;" v-if="user.checked">
                        <a-icon type="check" />
                    </span>
                </a-menu-item>
            </a-menu>
        </a-dropdown>
    </div>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import {
    findProjectUsers
} from "@/services/tracker/ProjectRoleMemberService";

export default ({
    name: "HUserSelector",
    components: { HAvatar },
    props: {
        selectUserIds:[],
        projectId: {
            required: true
        }
    },
    watch: {
        selectUserIds: {
            immediate: true,
            handler: function (curVal, oldVal) {
                if (this.users.length > 0) {
                    // console.log("selectUserIds", curVal);
                    this.currentUsers = this.users.filter(user => curVal?.find(u => u == user.id));
                }
            }
        },
        currentUsers: {
            immediate: true,
            handler: function (curVal, oldVal) {
                this.fillChecked();
            }
        },
        
    },
    mounted() {
        this.loadProjectUsers();
    },
    data() {
        return {
            users: [],
            visible: false,
            currentUsers: [],
            searchKey: null
        };
    },
    computed: {
        searchedUsers() {
            this.fillChecked();
            if (this.searchKey) {
                return this.users.filter(user => user.name.includes(this.searchKey));
            } else {
                return this.users;
            }
        }
    },
    methods: {
        loadProjectUsers() {
            findProjectUsers(this.projectId).then(resp => {
                this.users = resp.map(t => {
                    t.checked = false;
                    return t;
                });
                this.currentUsers = this.users.filter(user => this.selectUserIds.find(u => u == user.id));
            });
        },
        onChange(v) {
            this.$emit("SELECT_CHANGED", v);
        },
        handleSelect(user){
            if(this.selectUserIds.find(u => u == user.id)){
                const arr =this.selectUserIds.filter(u => u!= user.id)
                this.onChange(arr);
            }else{
                const arr =[user.id].concat(this.selectUserIds);
                this.onChange(arr);  
            }
        },
        handleMenuClick(e) {
            if (e.key === '1') {
                this.visible = true;
            } else {
                this.visible = false;
            }
        },        
        fillChecked() {
            const cusers = this.currentUsers;
            this.users.forEach(user => {
                user.checked = false;
                cusers.forEach(curUser => {
                    if (user.id == curUser.id) {
                        user.checked = true;
                    }
                })
            })
        },
    }
});
</script>
<style lang="less" scoped>
.user-selector {
    display: inline-block;
    position: relative;

    .user-item {
        border: 2px solid rgb(255, 255, 255);
        box-shadow: rgba(0, 0, 0, 0.1) 0px 1px 5px -1px;
        border-radius: 24px;
        position: relative;
        width: 100%;
        height: 100%;
    }
}
</style>
