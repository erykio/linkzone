<template>
    <article class="media">
        <div class="media-content">
            <div class="field">
                <p class="control">
                    <b-field :type="{'is-danger': triedToSubmit && errors.has('body')}" :message="triedToSubmit ? errors.first('body') : null">
                        <b-input rows="3" v-validate="{required: true, min: 1, max: 1000}" name="body" v-model="_value" type="textarea"></b-input>
                    </b-field>
                </p>
            </div>
            <div class="field">
                <p class="control">
                    <button class="button" @click="addComment">{{(!isReply ? 'comments.add' : 'comments.add-reply') | t}}</button>
                </p>
            </div>
        </div>
    </article>
</template>

<script>
    export default {
        name: "NewComment",
        props: {
            value: {
                type: String,
                required: true
            },
            isReply: {
                type: Boolean,
                default: false
            }
        },
        data() {
            return {
                triedToSubmit: false
            }
        },
        computed: {
            _value: {
                get: function(){
                    return this.value;
                },
                set: function(newValue){
                    this.$emit('input', newValue)
                }
            }
        },
        methods: {
            addComment() {
                this.triedToSubmit = true;
                this.$validator.validate().then(result => {
                    if (result) {
                        this.$emit('add');
                        this.triedToSubmit = false;
                    }
                });
            },
        }
    }
</script>

<style scoped>

</style>