<template>
    <section class="section is-fullwidth">
        <section class="hero is-primary">
            <div class="hero-body">
                <div class="container">
                <h1 class="title">
                    {{'groups.add-group'|t}}
                </h1>
                </div>
            </div>
        </section>
        <div class="container">
            <div class="column is-6 is-offset-3 mt-2">
                <b-field :type="{'is-danger': triedToSubmit && errors.has('name')}" :message="triedToSubmit ? errors.first('name') : null">
                    <b-input v-validate="'required'" name="name" icon="account-group" v-model="form.name" :placeholder="$t('groups.create-title')"></b-input>
                </b-field>
                <b-field :type="{'is-danger': triedToSubmit && errors.has('description')}" :message="triedToSubmit ? errors.first('description') : null">
                    <b-input type="textarea" v-validate="'required'" name="description" v-model="form.description" :placeholder="$t('groups.create-description')"></b-input>
                </b-field>
                <div class="has-text-centered mt-2">
                    <button class="button is-primary" @click="submit">{{ $t('add') }}</button>
                </div>
            </div>
        </div>
    </section>
</template>

<script>
    export default {
        name: 'GroupCreateView',
        created() {
            this.$toggleLoading(false);
        },
        data() {
            return {
                form: {
                    name: '',
                    description: ''
                },
                triedToSubmit: false
            }
        },
        methods: {
            submit() {
                this.triedToSubmit = true;
                this.$validator.validate().then(result => {
                    if (result) {
                        this._submit();
                    }
                });
            },
            _submit() {
                this.$groupService.addGroup({
                    name: this.form.name,
                    description: this.form.description
                }, ({data}) => {
                    this.$success('groups.added-group');
                    this.$router.push({name: 'groupDetailView', params: {name: data.name}});
                }, ({data}) => {
                    if (data.fieldErrors.name) {
                        this.errors.add({field: 'name', msg: this.$t('errors.' + data.fieldErrors.name)}); 
                    }
                })
            }
        }
    }
</script>

<style scoped>

</style>