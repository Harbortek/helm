<template>
    <div class="outliner-wrapper">
        <div class="outliner-header">大纲</div>
        <ul class="directory-list">
            <li v-for="item in directory" :key="item.id" :title="item.title" :data-idx="item.idx"
                :class="`outliner-header-item ce-h${item.level}`"
                :style="`margin-left:${item.indent * 15}px;white-space: nowrap;text-overflow: ellipsis;overflow:hidden;`">
                <a :href="composeHref(item.id)">{{
                    item.title }}</a>
            </li>

        </ul>
    </div>
</template>

<script>
export default {
    props: {
        directory: Array
    },
    data() {
        return {}
    },

    methods: {
        composeHref(id) {
            return `javascript:document.querySelector('[id="${id}"]').scrollIntoView({behavior: 'smooth'})`
        },
        fillEmpty(indent) {
            let str = '';
            for (let i = 0; i < indent; i++) {
                str += ('&nbsp;');
            }
            return str;
        }
    }
}
</script>

<style lang="less" scoped>
.outliner-wrapper {
    width: 100%;
    height: 100%;
    border-right: 1px solid #DEDEDE;

    .outliner-header {
        width: 100%;
        height: 42px;
        line-height: 36px;
        padding-left: 20px;
        border-bottom: 1px solid #DEDEDE;



    }

    .directory-list {
        height: calc(100% - 62px);
        overflow-y: scroll;
        .outliner-header-item::before {
            content: attr(data-idx);
            margin-right: 3px;
            color: #707684;
            font-weight: normal;
            opacity: 50;
        }

        list-style-type: none;
        padding-left: 20px;
        padding-top: 10px;


        .ce-h1 {
            font-weight: bold;
            line-height: 1.5;
            text-indent: 0em;
        }

        .ce-h2 {
            font-style: italic;
            margin-left: 10px;
        }
        .ce-h3 {
            font-style: italic;
            margin-left: 20px;
        }
        .ce-h4 {
            font-style: italic;
            margin-left: 30px;
        }
        .ce-h5 {
            font-style: italic;
            margin-left: 40px;
        }

    }

    .directory-list li {
        margin-bottom: 10px;
    }

    .directory-list a {
        text-decoration: none;
    }

    .directory-list a:hover {
        text-decoration: underline;
    }

}
</style>