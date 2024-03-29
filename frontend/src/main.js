import Vue from 'vue'
import App from './App.vue'
import router from './router'
import {store} from './store'
import UserService from './services/UserService'
import GroupService from './services/GroupService'
import PostService from './services/PostService'
import CommentService from './services/CommentService'
import './config/axios'
import VueI18n from 'vue-i18n'
import messages from './locale'
import moment from 'moment';
import Buefy from 'buefy'
import VeeValidate from 'vee-validate';

export const LOCAL_STORAGE_LOCALE_KEY = 'locale';

Vue.use(VeeValidate);
Vue.use(Buefy);
Vue.use(VueI18n);

const locale = localStorage.getItem(LOCAL_STORAGE_LOCALE_KEY) || 'en';
moment.locale(locale);

const i18n = new VueI18n({
    locale: locale,
    messages,
    fallbackLocale: 'en',
});

Vue.config.productionTip = false;
Vue.config.performance = process.env.NODE_ENV === 'development';

Vue.prototype.$groupService = new GroupService();
Vue.prototype.$userService = new UserService();
Vue.prototype.$postService = new PostService();
Vue.prototype.$commentService = new CommentService();

Vue.filter('date', str => moment(str).format('DD MMM YYYY'));
Vue.filter('fullDate', str => moment(str).format('MMMM Do YYYY, HH:mm:ss'));
Vue.filter('shortDate', str => moment(str).format('dddd, HH:mm:ss'));
Vue.filter('since', str => moment(str).fromNow());
Vue.filter('t', value => i18n.t(value));

Vue.prototype.$translate = (val) => i18n.t(val);
Vue.prototype.$toggleLoading = (val) => { store.commit('toggleLoading', val); }
Vue.prototype.$success = (msg, args) => { showToast(msg, 'is-success', args); }
Vue.prototype.$danger = (msg, args) => { showToast(msg, 'is-danger', args); }
Vue.prototype.$info = (msg, args) => { showToast(msg, 'is-info', args); }
Vue.prototype.$warning = (msg, args) => { showToast(msg, 'is-warning', args); }

var stepTime = 20;
var docBody = document.body;
var focElem = document.documentElement;
var scrollAnimationStep = function (initPos, stepAmount) {
    var newPos = initPos - stepAmount > 0 ? initPos - stepAmount : 0;
    docBody.scrollTop = focElem.scrollTop = newPos;
    newPos && setTimeout(function () {
        scrollAnimationStep(newPos, stepAmount);
    }, stepTime);
}

function scrollToTop (speed = 500) {
    var topOffset = docBody.scrollTop || focElem.scrollTop;
    var stepAmount = topOffset;
    speed && (stepAmount = (topOffset * stepTime)/speed);
    scrollAnimationStep(topOffset, stepAmount);
}

function showToast (msg, type, args) {
    if (!msg) {
        return;
    }
    Vue.prototype.$toast.open({
        message: i18n.t(msg, args),
        type: type,
        position: 'is-top',
        queue: false
    });
}

Vue.prototype.$userService.autoLogin();

// create the app instance.
// here we inject the router, store and ssr context to all child components,
// making them available everywhere as `this.$router` and `this.$store`.
new Vue({
    router,
    store,
    i18n,
    render: h => h(App)
}).$mount('#app');
