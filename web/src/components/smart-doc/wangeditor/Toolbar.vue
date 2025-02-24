<script>
import Vue from 'vue';
import { createToolbar, DomEditor } from '@wangeditor/editor';

export default Vue.extend({
  name: 'Toolbar',
  render(h) {
    return h('div', { class: 'toolbar', ref: 'box' });
  },
  data: () => ({
    editor: null,
  }),
  props: ['initConfig', 'editorMode', 'loading'],
  methods: {
    // 创建 toolbar
    create(editor) {
      this.editor = editor;
      if (this.$refs.box == null) return;
      if (editor == null) return;
      if (DomEditor.getToolbar(editor)) return // 不重复创建
      createToolbar({
        editor,
        selector: this.$refs.box,
        config: this.initConfig || {},
        mode: this.editorMode || 'default',
      });
    },
  },
});
</script>
<style lang="less" scoped>
.toolbar {
  width: 100%;
}
</style>