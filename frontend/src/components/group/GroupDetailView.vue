<template>
    <section class="section is-fullwidth" v-if="group">
        <div style="position: relative;">
            <div v-if="group.bannerUrl" :style="`background-image: url('${$groupService.getBannerUrl(group)}'); background-size: cover; background-position: center; height: 200px;`"></div>
            <div v-else style="height: 100px; background-color: grey"></div>

            <div style="position: absolute" :style="{'bottom': group.bannerUrl ? '50px': '15px'}">
                <div class="container">
                    <div class="column is-8 is-offset-2">
                        <div style="display: flex;flex-direction: row;width:300px">
                            <p class="image is-48x48">
                                <img class="is-rounded" :src="$groupService.getLogoUrl(group)">
                            </p>
                            <p class="title is-4" style="color:white;margin-top:9px;margin-left:1rem;text-shadow: 3px 3px 7px rgba(0,0,0,0.78);">{{group.name}}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <section class="">
            <div class="container">
                <div class="column is-10 is-offset-1">
                    <nav class="breadcrumb" aria-label="breadcrumbs">
                        <ul>
                            <li><router-link :to="{name: 'groupListView'}">{{'groupListView' |t}}</router-link></li>
                            <li class="is-active"><a href="#" aria-current="page">{{group.name}}</a></li>
                        </ul>
                    </nav>
                    <moderator v-if="group.isModerator"></moderator>

                    <div class="box is-hidden-desktop">
                        <p class="title is-4">{{group.name}} <span class="is-pulled-right"><sub-toggler :group="group"></sub-toggler></span></p>
                        <p class="subtitle is-6">{{group.description}}</p>

                        <small class="ml-2">{{group.createdAt | date}}</small>

                        <div class="field is-grouped">
                            <p class="control">
                                <router-link class="button" :to="{name: 'groupEditView', params: {name: group.name}}">{{'groups.update-group'|t}}</router-link>
                            </p>
                            <p class="control">
                                <router-link class="button" :to="{name: 'postCreateView', params: {groupName: group.name}}">{{'groups.add-post-in-group' | t}}</router-link>
                            </p>
                        </div>
                    </div>

                    <div class="columns">
                        <div class="column">
                            <post-list :is-moderator="group.isModerator" :posts="posts"></post-list>
                            <pagination :pagination="pagination" @change="handleChange"/>
                        </div>
                        <div class="column is-narrow is-hidden-touch mt-2">
                            <div class="card" style="width:300px">
                            <header class="card-header">
                                <p class="card-header-title">
                                {{group.name}}
                                </p>
                                <sub-toggler :group="group"></sub-toggler>
                            </header>
                            <div class="card-content">
                                <div class="content">
                                    <nav class="level">
                                        <div class="level-item has-text-centered">
                                            <div>
                                            <p class="heading">{{'groups.subs'|t}}</p>
                                            <p class="title is-5">{{group.subscribers}}</p>
                                            </div>
                                        </div>
                                        <div class="level-item has-text-centered">
                                            <div>
                                            <p class="heading">{{'groups.posts'|t}}</p>
                                            <p class="title is-5">{{group.postCount}}</p>
                                            </div>
                                        </div>
                                    </nav>
                                    <nav class="level">
                                        <div class="level-item has-text-centered">
                                            <div>
                                            <p class="heading">{{'groups.created-time'|t}}</p>
                                            <p class="title is-5">{{group.createdAt | date}}</p>
                                            </div>
                                        </div>
                                    </nav>
                                    <p>{{group.description}}</p>
                                </div>
                            </div>
                            <footer class="card-footer">
                                <router-link class="card-footer-item" :to="{name: 'postCreateView', params: {groupName: group.name}}">{{'groups.add-post-in-group' | t}}</router-link>
                            </footer>
                            <footer class="card-footer" v-if="group.isAdministrator">
                                <router-link class="card-footer-item" :to="{name: 'groupEditView', params: {name: group.name}}">{{'groups.update-group'|t}}</router-link>
                            </footer>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </section>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'
    import Pagination from '../includes/Pagination'
    import PostList from '../post/PostList'
    import {getPaginationFromResponse} from '../../utils/utils';
    import SubToggler from './SubToggler';
    import Moderator from './Moderator'
    import {POST_TYPES} from "../../services/PostService";

    export default {
        name: 'GroupDetailView',
        props: ['name'],
        components: {Pagination, PostList, SubToggler, Moderator},
        mounted() {
            this.init();
        },
        data() {
            return {
                group: null,
                posts: [],
                pagination: {},
                POST_TYPES,
                admins: [],
                mods: []
            }
        },
        watch: {
            '$route'(to, from) {
                this.init();
            },
        },
        methods: {
            init() {
                this.$groupService.getGroupDetail(this.name, ({data}) => {
                    this.group = data;
                    this.mods = this.group.moderators.map(user => user.username);
                    this.admins = this.group.administrators.map(user => user.username);
                    this.getPosts({});
                })
            },
            handleChange(pageNumber) {
                this.getPosts({page: pageNumber})
            },
            getPosts(pagination = {}) {
                this.$toggleLoading(true);
                this.$groupService.getPosts(this.group, pagination, ({data}) => {
                    this.$toggleLoading(false);
                    this.pagination = getPaginationFromResponse(data);
                    this.posts = data.content;
                })
            }
        }
    }
</script>

<style scoped>

</style>