<template>
    <nav class="navbar has-shadow is-spaced" role="navigation" aria-label="main navigation">
        <div class="navbar-brand">
            <router-link class="navbar-item" to="/">
                <img src="../assets/logo.png">
            </router-link>
            <a role="button" class="navbar-burger burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample" @click="showBurger = !showBurger">
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
            </a>
        </div>

        <div v-if="showBurger" class="navbar-menu is-hidden-desktop is-active">
            <div class="navbar-start">
                <div class="navbar-item">
                    <router-link class="navbar-link is-arrowless" to="/">
                        {{'navbar.main' | t}}
                    </router-link>
                </div>
                <div class="navbar-item">
                    <router-link class="navbar-item" :to="{name: 'groupListView'}">
                        {{'navbar.groups' | t}}
                    </router-link>
                    <router-link class="navbar-item" :to="{name: 'groupCreateView'}">{{'groups.add-group'|t}}</router-link>
                </div>
                <div class="navbar-item">
                    <router-link class="navbar-item" :to="{name: 'postCreateView'}">
                        {{'navbar.add-post' | t}}
                    </router-link>
                </div>
                <div class="navbar-item" v-if="isAuthenticated && user">
                    <a class="navbar-link is-arrowless">
                        <figure class="image is-16x16" style="margin-right: 5px; display:inline-block">
                            <img :src="user.avatarUrl">
                        </figure>
                         <span style="display:inline-block">{{user.username}} <b-icon style="margin-left:5px" v-if="user.isAdmin" icon="crown" size="" type="is-warning"></b-icon></span>
                    </a>

                    <div class="navbar-dropdown">
                        <router-link :to="{name: 'userProfileView', params: {username: user.username}}" class="navbar-item">
                            {{'navbar.my-profile' | t}}
                        </router-link>
                        <router-link class="navbar-item" :to="{name: 'userEditView'}">
                            {{'navbar.settings' | t}}
                        </router-link>
                        <hr class="navbar-divider">
                        <a class="navbar-item" @click="logout">
                            {{'navbar.logout' | t}}
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <div id="navbarBasicExample" class="navbar-menu">
            <div class="navbar-start">
                <router-link class="navbar-item" :to="{name: 'groupListView'}"><b-icon style="margin-right:5px;" icon="account-group"></b-icon> {{'navbar.groups' | t}}</router-link>
                <router-link class="navbar-item" :to="{name: 'groupCreateView'}"><b-icon style="margin-right:5px;" icon="plus"></b-icon> {{'groups.add-group'|t}}</router-link>
                <router-link class="navbar-item" :to="{name: 'postCreateView'}"><b-icon style="margin-right:5px;" icon="pencil"></b-icon> {{'navbar.add-post' | t}}</router-link>
            </div>

            <div class="navbar-end">
                <div class="navbar-item has-dropdown is-hoverable" v-if="selectedLanguage">
                    <a class="navbar-link">
                        <img :src="`/${selectedLanguage.icon}`" width="24px"/>
                    </a>
                    <div class="navbar-dropdown has-dropdown is-hoverable">
                        <a class="navbar-item" :class="{'is-active': language.locale === selectedLanguage.locale}" v-for="language in AVAILABLE_LANGUAGES" :key="language.locale" @click="setLocale(language)">
                            <img :src="`/${language.icon}`" width="24px" class="mr-2"/> <span>{{`navbar.${language.locale}` | t}}</span>
                        </a>
                    </div>
                </div>

                <div class="navbar-item has-dropdown is-hoverable" v-if="isAuthenticated && user">
                    <a class="navbar-link">
                        <figure class="image is-16x16" style="margin-right: 5px">
                            <img :src="user.avatarUrl">
                        </figure>
                         {{user.username}} <b-icon style="margin-left:5px" v-if="user.isAdmin" icon="crown" size="" type="is-warning"></b-icon>
                    </a>

                    <div class="navbar-dropdown">
                        <router-link :to="{name: 'userProfileView', params: {username: user.username}}" class="navbar-item">
                            <b-icon style="margin-right:5px;" icon="account"></b-icon> {{'navbar.my-profile' | t}}
                        </router-link>
                        <router-link class="navbar-item" :to="{name: 'userEditView'}">
                            <b-icon style="margin-right:5px;" icon="settings"></b-icon> {{'navbar.settings' | t}}
                        </router-link>
                        <hr class="navbar-divider">
                        <a class="navbar-item" @click="logout">
                            <b-icon style="margin-right:5px;" icon="logout"></b-icon> {{'navbar.logout' | t}}
                        </a>
                    </div>
                </div>
                <div class="navbar-item">
                    <div class="buttons">
                        <template v-if="!isAuthenticated">
                            <router-link :to="{name: 'registerView'}" class="button is-info">
                                <strong>{{'navbar.register' | t}}</strong>
                            </router-link>
                            <router-link :to="{name: 'loginView'}" class="button is-light">
                                {{'navbar.login' | t}}
                            </router-link>
                        </template>
                    </div>
                </div>
            </div>
        </div>
    </nav>
</template>

<script>
    import {AVAILABLE_LANGUAGES} from "../locale";
    import {mapGetters} from 'vuex';
    import {LOCAL_STORAGE_LOCALE_KEY} from '../main';
    import moment from 'moment';

    export default {
        name: "Navbar",
        mounted() {
            this.selectedLanguage = AVAILABLE_LANGUAGES.find(l => l.locale === this.$i18n.locale);
            this.selectedLanguage.active = true;
        },
        data() {
            return {
                AVAILABLE_LANGUAGES,
                selectedLanguage: null,
                showBurger: false
            }
        },
        computed: {
            ...mapGetters(['isAuthenticated', 'user'])
        },
        methods: {
            setLocale(language) {
                this.selectedLanguage.active = false;
                this.$i18n.locale = language.locale;
                this.selectedLanguage = language;
                this.selectedLanguage.active = true;
                localStorage.setItem(LOCAL_STORAGE_LOCALE_KEY, this.$i18n.locale);
                moment.locale(this.$i18n.locale);
            },
            logout() {
                this.$userService.logout();
            },
        }
    }
</script>

<style scoped>

</style>