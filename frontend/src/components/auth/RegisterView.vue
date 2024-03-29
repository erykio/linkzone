<template>
    <section class="hero">
        <div class="hero-body">
            <div class="container has-text-centered">
                <div class="column is-8 is-offset-2">
                    <h3 class="title has-text-grey">{{'registerView.header' | t}}</h3>
                    <p class="subtitle has-text-grey">{{'registerView.hint' | t}}</p>
                    <div class="notification is-danger" v-if="serverErrors.length">
                        <p v-for="error in serverErrors" :key="error">
                            {{`errors.${error}` | t}}
                        </p>
                    </div>
                </div>
                <div class="column is-6 is-offset-3">
                    <div class="box">
                        <b-field :type="{'is-danger': triedToSubmit && errors.has('username')}" :message="triedToSubmit ? errors.first('username') : null">
                            <b-input v-validate="{required: true, min: 3, max: 50}" icon="account" name="username" v-model="form.username" :placeholder="$t('registerView.username')"></b-input>
                        </b-field>
                        <b-field :type="{'is-danger': triedToSubmit && errors.has('password')}" :message="triedToSubmit ? errors.first('password') : null">
                            <b-input v-validate="{ required: true, min: 3, max: 50 }" name="password" icon="lock" type="password" ref="password" v-model="form.password" :placeholder="$t('registerView.password')"></b-input>
                        </b-field>
                        <b-field :type="{'is-danger': triedToSubmit && errors.has('password-confirm')}" :message="triedToSubmit ? errors.first('password-confirm') : null">
                            <b-input v-validate="{ required: true, min: 3, max: 50, confirmed: 'password' }" name="password-confirm" icon="lock" type="password" v-model="form.passwordConfirm" :placeholder="$t('registerView.password-confirm')"></b-input>
                        </b-field>
                        <div class="field">
                            <b-checkbox v-model="form.loginOnSuccess">
                                {{'registerView.log-me-in' | t}}
                            </b-checkbox>
                        </div>
                        <button class="button is-block is-info is-fullwidth" @click="registerAccount">{{'registerView.register' | t}}</button>
                    </div>
                    <p class="has-text-grey">
                        <router-link :to="{name: 'loginView'}">{{'registerView.login' | t}}</router-link>
                    </p>
                </div>
            </div>
        </div>
    </section>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'

    export default {
        name: "RegisterView",
        data() {
            return {
                form: {
                    username: '',
                    email: '',
                    password: '',
                    passwordConfirm: '',
                    loginOnSuccess: true,
                    valid: false,
                },
                triedToSubmit: false,
                serverErrors: []
            }
        },
        created() {
            this.$toggleLoading(false);
        },
        methods: {
            registerAccount() {
                this.triedToSubmit = true;
                this.serverErrors = [];
                    console.log(this.form)

                this.$validator.validate().then(result => {
                    if (result) {
                        this._register();
                    }
                });
            },
            _register() {
                this.$toggleLoading(true);
                this.$userService.register(this.form, ({data}) => {
                    this.$success('registerView.success');
                    if (this.form.loginOnSuccess) {
                        this.$userService.authenticate(this.form.username, this.form.password, () => {
                            this.$toggleLoading(false);
                            this.$info('logged-in');
                            this.goToProfile();
                        });
                    } else {
                        this.$toggleLoading(false);
                        this.goToProfile();
                    }
                }, ({data}) => {
                    if (data.fieldErrors.username) {
                        this.errors.add({field: 'username', msg: this.$t('errors.' + data.fieldErrors.username)}); 
                    }
                    this.serverErrors = data.errors || [];
                })
            },
            goToProfile() {
                this.$router.replace({name: 'userProfileView', params: {username: this.form.username}});
            }
        }
    }
</script>

<style scoped>

</style>