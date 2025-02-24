import { Icon } from "ant-design-vue";
import Vue from "vue";
let CustomIcon = null;
const HIcon = {
  name: "HIcon",
  props: {
    type: {
      type: String,
      default: null,
    },
  },
  computed: {
    customIcon() {
      return Vue.options.components[this.type];
    },
  },
  render(h) {
    const dynamicProps = {
      on: this.$listeners,
      props: Object.assign(this.$attrs, {
        type: this.type,
      }),
    };
    let customIcon = this.customIcon;
    if (customIcon) {
      return h(customIcon, dynamicProps);
    } else {
      return h(Icon, dynamicProps);
    }
  },
};
HIcon.install = function (Vue, options = {}) {
  const { scriptUrl } = options.HIcon || {};
  CustomIcon = scriptUrl
    ? Icon.createFromIconfontCN({
        scriptUrl,
      })
    : null;
  Vue.component(HIcon.name, HIcon);
};
export default HIcon;
