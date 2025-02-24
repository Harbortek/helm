
/**
 * 初始化开发环境，设置微应用地址，生产环境默认访问同一地址
 */
function initEnv() {
  const isDev = (process.env.NODE_ENV === 'development');
  if (isDev) {
    // window[process.env.VUE_APP_MICROS_KEY] =
    // {
    //   '/sys': 'http://localhost:3001',
    //   '/tracker': 'http://localhost:3002',
    //   '/integration': 'http://localhost:3003'
    // }
    // console.warn('开发环境设置子应用地址', process.env.VUE_APP_MICROS_KEY, window[process.env.VUE_APP_MICROS_KEY])
  } else {

  }
}

export default initEnv;
