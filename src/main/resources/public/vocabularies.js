    var appVocabularies = new Vue({
      el: '#app-vocabularies',

      data: {
        uploadVocabularyName: '',
        meta: '',
        editing: false,
        errorMessage: null,
        successMessage: null,
        vocabularies: [],
        newVocabulary: {prefix:"",namespace:"",description:"",reference:""},
        editingVocabulary: {prefix:"",namespace:"",description:"",reference:""},
        editedVocabulary: {prefix:"",namespace:"",description:"",reference:""},
      },

      created: function () {
        this.$http.get('/vocabularies').then(response => {
            this.vocabularies = response.body;
        });
      },

        computed: {
        },
        watch: {
            vocabularies: {
                handler: function (vocabularies) {
                    if (this.editing) {
                        this.$http.put("/vocabularies", vocabularies)
                        .then(() => {
                            console.log("Saved to server.")
                            this.successMessage = 'OK. Saved .'
                            this.errorMessage = null
                        })
                        .catch((response) => {
                            console.log("NOT saved.")
                            console.log(response)
                            this.errorMessage = 'KO: '+response.body.message
                            this.successMessage = null
                        });
                    };
                },
                deep: true
            }
        },

        methods: {
            editVocabulary: function (vocabulary) {
                console.log('Edit ')
                console.log(vocabulary)
                this.editingVocabulary.prefix = vocabulary.prefix
                this.editingVocabulary.description = vocabulary.description
                this.editingVocabulary.namespace = vocabulary.namespace
                this.editingVocabulary.reference = vocabulary.reference
                this.editedVocabulary = vocabulary
                console.log(this.editingVocabulary)
            },
            editSaveVocabulary: function () {
                console.log('Edit save vocabulary')
                this.editing=true;
                this.editedVocabulary.prefix = this.editingVocabulary.prefix
                this.editedVocabulary.description = this.editingVocabulary.description
                this.editedVocabulary.namespace = this.editingVocabulary.namespace
                this.editedVocabulary.reference = this.editingVocabulary.reference
            },

            addSaveVocabulary:function () {
                this.editing=true;
                console.log(this.newVocabulary.driver)
                this.vocabularies.push(this.newVocabulary);
                this.newVocabulary = {prefix:"",namespace:"",description:"",reference:""};
            },
            removeVocabulary(vocabulary) {
                this.editing=true;
                this.vocabularies.splice(this.vocabularies.indexOf(vocabulary), 1);
            },
            uploadOn(vocabulary) {
             this.uploadVocabularyName = vocabulary.name
            }
        }
})