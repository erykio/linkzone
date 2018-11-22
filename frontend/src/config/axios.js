import {store} from '../store/index'
import axios from 'axios'
import {API_PREFIX} from "../api/endpoints"
import Vue from 'vue'

// add headers before each request
axios.interceptors.request.use((config) => {
    config.headers.common = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${store.getters.accessToken}`
    }
    config.url = `${API_PREFIX}${config.url.startsWith('/') ? '' : '/'}${config.url}`
    return config
});

// handle error response
axios.interceptors.response.use((response) => {
    return response
}, function (error) {
    if (error.response.status === 401) {
        console.log('unauthorized, logging out ...');
        Vue.prototype.$userService.logout()
        // auth.logout();
        // router.replace('/auth/login');
    }
    store.commit('setAPIError', error.response.data)
    store.commit('toggleLoading', false)
    return Promise.reject(error.response)
});