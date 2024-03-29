import Vue from 'vue'
import Router from 'vue-router'
import LoginView from '../components/auth/LoginView'
import RegisterView from '@/components/auth/RegisterView'
import GroupListView from '@/components/group/GroupListView'
import GroupDetailView from '@/components/group/GroupDetailView'
import GroupEditView from '@/components/group/GroupEditView'
import GroupCreateView from '@/components/group/GroupCreateView'
import PostCreateView from '@/components/post/PostCreateView'
import PostView from '@/components/post/PostView'
import UserProfileView from '@/components/user/UserProfileView'
import UserEditView from '@/components/user/UserEditView'
import PostUpdateView from '@/components/post/PostUpdateView'
import MainView from '@/components/MainView'
import {store} from '../store'

Vue.use(Router);

export const setNextRoute = (route) => {
    store.commit('setNextRoute', {name: route.name, params: route.params, query: route.query});
}

const router = new Router({
    mode: process.env.NODE_ENV === 'production' ? 'history' : 'hash',
    linkExactActiveClass: 'is-active',
    routes: [
        {name: "mainView", path: "/", component: MainView},
        {name: 'loginView', path: '/login', component: LoginView},
        {name: 'registerView', path: '/register', component: RegisterView},
        {name: 'groupListView', path: '/groups', component: GroupListView},
        {name: 'groupDetailView', path: '/groups/:name', component: GroupDetailView, props: true},
        {name: 'groupEditView', path: '/groups/:name/update', component: GroupEditView, meta: {requiresAuth: true}, props: true},
        {name: 'postCreateView', path: '/add/post/:groupName?', component: PostCreateView, meta: {requiresAuth: true}, props: true},
        {name: 'postView', path: '/groups/:name/:postID/:slug', component: PostView, props: true},
        {name: 'postUpdateView',path: '/post/:id/update', component: PostUpdateView, meta: {requiresAuth: true}, props: true},
        {name: 'groupCreateView', path: '/add/group', meta: {requiresAuth: true}, component: GroupCreateView},
        {name: 'userProfileView', path: '/user/:username', component: UserProfileView, props: true},
        {name: 'userEditView', path: '/settings', meta: {requiresAuth: true}, component: UserEditView},
    ],
    scrollBehavior (to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            return { x: 0, y: 0 }
        }
    }
});

router.beforeEach((to, from, next) => {
    // console.warn('to-from', JSON.stringify(to.name), JSON.stringify(from.name));
    if (to.matched.some(record => record.meta.requiresAuth)) {
        // console.log('route requires auth. am i authenticated?', store.getters.isAuthenticated);
        if (!store.getters.isAuthenticated) {
            Vue.prototype.$userService.forceLoginIfNotLoggedIn(to);
            return;
        }
    }
    if (to.name !== from.name) {
        store.commit('toggleLoading', true);
    }
    next();
});

export default router;

// router.beforeRouteEnter((to, from, next) => {
//    console.log('before enter', to)
// });

//router.afterEach((to, from) => {
//    console.log('after')
//})
