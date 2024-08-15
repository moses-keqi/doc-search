import Vue from 'vue';
import App from './App';
import router from './router';
import axios from 'axios';
import $ from 'jquery';
import ElementUI from 'element-ui';
import './assets/icon/iconfont.css'
import 'element-ui/lib/theme-chalk/index.css';    // 默认主题
// import '../static/css/theme-green/index.css';       // 浅绿色主题
import "babel-polyfill";
import {
    Loading,
    MessageBox,
    Message,
    Notification
} from 'element-ui';
Vue.use(ElementUI, { size: 'small' });
Vue.prototype.$axios = axios;
Vue.prototype.$loading = Loading.service;
Vue.prototype.$msgbox = MessageBox;
Vue.prototype.$alert = MessageBox.alert;
Vue.prototype.$confirm = MessageBox.confirm;
Vue.prototype.$prompt = MessageBox.prompt;
Vue.prototype.$notify = Notification;
Vue.prototype.$message = Message;
Vue.prototype.$jq = $;
Vue.prototype.$utils = {
    parseDate(str) {
        if (typeof str == 'string') {
            var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);
            if (results && results.length > 3)
                return new Date(parseInt(results[1]), parseInt(results[2]) - 1, parseInt(results[3]));
            results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);
            if (results && results.length > 6)
                return new Date(parseInt(results[1]), parseInt(results[2]) - 1, parseInt(results[3]), parseInt(results[4]), parseInt(results[5]), parseInt(results[6]));
            results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2})\.(\d{1,9}) *$/);
            if (results && results.length > 7)
                return new Date(parseInt(results[1]), parseInt(results[2]) - 1, parseInt(results[3]), parseInt(results[4]), parseInt(results[5]), parseInt(results[6]), parseInt(results[7]));
        }
        return null;
    },
    clone:function(obj){
        return JSON.parse(JSON.stringify(obj));
    },
    getSize:function (size) {
        if(size<1024){
            return "1KB"
        }
        if(size>1024&&size<1024*1024){
            return parseInt(size/1024)+"KB"
        }
        if(size>1024*1024&&size<1024*1024*1024){
            return (size/1024/1024).toFixed(2)+"MB"
        }
    },
    getSuffix:function(filename){
        var index = filename.lastIndexOf('.');
        if(index>=0){
            return filename.substring(index+1,filename.length).toLowerCase();
        }else {
            return null;
        }
    },
    getIcon:function (filename) {
        var suffix = this.getSuffix(filename);
        if('pdf'===suffix){
            return 'icon-pdf';
        }else
        if("doc"===suffix||'docx'===suffix){
            return 'icon-wordwenjian';
        }else
        if('pptx'===suffix||'ppt'===suffix){
            return 'icon-ppt';
        }else
        if('xls'===suffix||'xlsx'===suffix){
            return 'icon-excelwenjian';
        }else {
            return 'icon-Document';
        }
    },
    contains(arr, obj) {
        var i = arr.length;
        while (i--) {
            if (arr[i] === obj) {
                return true;
            }
        }
        return false;
    }
}
//使用钩子函数对路由进行权限跳转
router.beforeEach((to, from, next) => {
    const role = localStorage.getItem('ms_username');
    if(!role && to.path !== '/login'){
        next('/login');
    }else if(to.meta.permission){
        // 如果是管理员权限则可进入，这里只是简单的模拟管理员权限而已
        role === 'admin' ? next() : next('/403');
    }else{
        // 简单的判断IE10及以下不进入富文本编辑器，该组件不兼容
        if(navigator.userAgent.indexOf('MSIE') > -1 && to.path === '/editor'){
            Vue.prototype.$alert('vue-quill-editor组件不兼容IE10及以下浏览器，请使用更高版本的浏览器查看', '浏览器不兼容通知', {
                confirmButtonText: '确定'
            });
        }else{
            next();
        }
    }
})

new Vue({
    router,
    render: h => h(App)
}).$mount('#app');
