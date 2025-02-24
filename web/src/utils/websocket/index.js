import bus from '@/utils/bus'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import store from '@/store'
const STORE_CURRENT_USER = 'account/user';
const STORE_CURRENT_PROJECT_ID = 'project/currentProjectId';
class HWebsocket {
  constructor() {
    this.ws_url = `${process.env.VUE_APP_API_BASE_URL}/ws`
    this.client = null
    // this.channels = [
    //   {
    //     topic: '/smart-doc-msg-topic',
    //     event: 'smart-doc-msg-topic-call'
    //   }
    // ]
    this.timer = null
    // this.initialize()
  }

  initialize(callback) {
    this.connection(callback)
    const _this = this
    this.timer = this.isLoginStatus() && setInterval(() => {
      this.isLoginStatus() || this.destroy()
      try {
        _this.client && _this.client.send('heart detection')
      } catch (error) {
        console.error('Disconnection reconnection...')
        _this.connection()
      }
    }, 5000)
  }

  destroy() {
    this.timer && clearInterval(this.timer)
    this.disconnect()
    return true
  }

  reconnect(callback) {
    this.initialize(callback)
  }

  isLoginStatus() {
    return !!store.getters[STORE_CURRENT_USER];
  }
  send(url, body) {
    this.client.send(url, {}, JSON.stringify(body || {}));
  }
  connection(callback) {
    if (!this.isLoginStatus()) {
      return
    }
    const socket = new SockJS(this.ws_url, null,
      { transports: ['websocket', 'xhr-polling', 'jsonp-polling'] }
    )
    /* const socket = new SockJS('http://localhost:8081' + this.ws_url) */

    this.client = Stomp.over(socket)
    this.client.debug = null;
    const heads = {
      /* Authorization: '', */
      'userId': store.getters[STORE_CURRENT_USER].id,
      'projectId':store.getters[STORE_CURRENT_PROJECT_ID]
    }

    this.client.connect(
      heads,
      res => {
        callback && callback();
        bus.$emit('ws_connected')
        // 连接成功 订阅所有主题
        // this.subscribe()
      },
      err => {
        // bus.$emit('ws_error_connection',err)
        // 连接失败 打印错误信息
        console.error(err)
      }
    ).bind(this)
  }

  subscribe(topic, callback) {
    this.client.subscribe(topic, res => {
      res && res.body && bus.$emit(topic, res.body)
      callback && callback(res?.body)
    })
  }

  disconnect() {
    this.client && this.client.disconnect()
  }
}

const result = new HWebsocket()
export const getSocket = () => {
  return result
}
export default {
  install(Vue) {
    // 使用$$前缀，避免与Element UI的冲突
    Vue.prototype.$hws = result
  }
}

