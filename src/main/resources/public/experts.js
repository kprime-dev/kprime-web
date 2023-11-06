    var appSources = new Vue({
      el: '#app-experts',

      data: {
        uploadSourceName: '',
        meta: '',
        editing: false,
        errorMessage: null,
        successMessage: null,
        experts: [],
        newExpert: {id:"",type:"xml",name:"",driver:"",location:"",user:"",pass:"",license:"",licenseUrl:""},
        editingSource: {id:"",type:"",name:"",driver:"",location:"",user:"",pass:"",license:"",licenseUrl:""},
        editedSource: {id:"",type:"",name:"",driver:"",location:"",user:"",pass:"",license:"",licenseUrl:""},
      },

      created: function () {
        this.$http.get('/experts').then(response => {
            this.experts = response.body;
        });
      },

        computed: {
        },
        watch: {
            sources: {
                handler: function (sources) {
                    if (this.editing) {
                        this.$http.put("/experts", sources)
                        .then(() => { // persist all changes to backend
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
            licenseClass: function (source) {
                if (!source.licenseUrl || source.licenseUrl.lenght===0) {
                    return "btn btn-outline-primary"
                } else {
                    return "btn btn-primary"
                }
            },
            editSource: function (source) {
                console.log('Edit ')
                console.log(source)
                this.editingSource.id = source.id
                this.editingSource.type = source.type
                this.editingSource.name = source.name
                if (this.editingSource.type==="h2") {
                    this.editingSource.driver = "org.h2.Driver";
                } else if (this.editingSource.type==="psql") {
                    this.editingSource.driver = "org.postgresql.Driver";
                } else {
                    this.editingSource.driver = "";
                }
                this.editingSource.location = source.location
                this.editingSource.user = source.user
                this.editingSource.pass = source.pass
                this.editingSource.license = source.license
                this.editingSource.licenseUrl = source.licenseUrl
                this.editedSource = source
                console.log(this.editingSource)
            },
            editSaveSource: function () {
                console.log('Edit save source')
                this.editing=true;
                this.editedSource.id = this.editingSource.id
                this.editedSource.type = this.editingSource.type
                this.editedSource.name = this.editingSource.name
                console.log(this.editedSource.type)
                console.log(this.editedSource.type==="h2")
                if (this.editedSource.type==="h2") {
                    this.editedSource.driver = "org.h2.Driver";
                } else if (this.editedSource.type==="psql") {
                    this.editedSource.driver = "org.postgresql.Driver";
                } else {
                    this.editedSource.driver = "";
                }
                this.editedSource.location = this.editingSource.location
                this.editedSource.user = this.editingSource.user
                this.editedSource.pass  = this.editingSource.pass
                this.editedSource.license  = this.editingSource.license
                this.editedSource.licenseUrl  = this.editingSource.licenseUrl
            },

            addSaveSource:function () {
                this.editing=true;
                this.newSource.id = this.sources.length+1
                if (this.newSource.type==="h2") {
                    this.newSource.driver = "org.h2.Driver"
                } else if (this.newSource.type==="psql") {
                    this.newSource.driver = "org.postgresql.Driver"
                } else {
                    this.newSource.driver = "";
                }
                console.log(this.newSource.driver)
                this.sources.push(this.newSource);
                this.newSource = {id:"",type:"xml",name:"",driver:"",location:"",user:"",pass:"",license:"",licenseUrl:""};
            },
            removeSource(source) {
                this.editing=true;
                this.sources.splice(this.sources.indexOf(source), 1);
            },
            showMeta(source) {
              this.$http.get("/meta/"+source.name)
              .then((response) => {
                   this.meta = response.body.content
                   console.log(response.body.error)
                  //this.successMessage = 'OK. Saved .'
                  //this.errorMessage = null
              })
              .catch((response) => {
                  console.log(response)
                  //this.errorMessage = 'KO: '+response.body.message
                  //this.successMessage = null
              });
            },
            uploadOn(source) {
             this.uploadSourceName = source.name
            }
        }
})