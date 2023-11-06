    var appTransformers = new Vue({
      el: '#app-transformers',

      data: {
        editing: false,
        newTransformer: { name:'new-transformer', composerMatcher:'', composerTemplate:'',decomposerMatcher:'', decomposerTemplate:'' },
        editingTransformer: { name:'new-transformer', composerMatcher:'', composerTemplate:'',decomposerMatcher:'', decomposerTemplate:'' },
        editedTransformer: { name:'new-transformer', composerMatcher:'', composerTemplate:'',decomposerMatcher:'', decomposerTemplate:'' },
        transfNames: [],
        transformers: [
        {name:'Tran 1', composerMatcher:'comp1', composerTemplate:'comp temp1',decomposerMatcher:'deco1', decomposerTemplate:'deco temp1' },
        {name:'Tran 2', composerMatcher:'comp2', composerTemplate:'comp temp2',decomposerMatcher:'deco2', decomposerTemplate:'deco temp2' }
         ]
      }, // data

      created: function () {

        this.$http.get('/transfnames').then(response => {
            this.transfNames = response.body;
        });
      }, // created


      methods: {
        deleteTransformer: function(transfname) {
            this.$http.delete('/transfdelete/'+transfname).then(response => {
                this.$http.get('/transfnames').then(response => {
                    this.transfNames = response.body;
                });
                console.log('Transformer deleted')
            });
        },
        newTransformer:function () {
            console.log('New transformer')
            this.$http.put("/transformer",this.newTransformer)
                .then(() =>{
                    this.$http.get('/transfnames').then(response => {
                        this.transfNames = response.body;
                    });
                    console.log('saved new')
                });
             this.newTransformer.name='new-transformer';
        },
        editTransformer: function (transfname) {
            this.$http.get('/transformer/'+transfname).then(response => {
                this.editingTransformer = response.body;
            });
            console.log('Edit transformer')
        },
        editSaveTransformer: function() {
            console.log('Edit save transformer')
            this.$http.put("/transformer",this.editingTransformer)
                .then(() =>{
                    console.log('saved')
                    console.log(this.editingTransformer)
                });
        },
        editNameTransformer: function (transfname) {
            this.editedTransformer.name = transfname;
            this.editingTransformer.name = transfname;
        },
        editSaveNameTransformer: function() {
            this.$http.put("/transformername",
                {oldName: this.editedTransformer.name,
                newName:this.editingTransformer.name })
                .then(() =>{
                    this.$http.get('/transfnames').then(response => {
                        this.transfNames = response.body;
                    });
                    console.log('renamed')
                });
        }

      } // methods

    })

