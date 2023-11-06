    var appSources = new Vue({
      el: '#app-sources',

      data: {
        uploadSourceName: '',
        meta: '',
        editing: false,
        errorMessage: null,
        successMessage: null,
        sources: [],
        drivers: [],
        newSource: {id:"",type:"xml",name:"",driver:"",location:"",user:"",pass:"",license:"",licenseUrl:""},
        editingSource: {id:"",type:"",name:"",driver:"",location:"",user:"",pass:"",license:"",licenseUrl:""},
        editedSource: {id:"",type:"",name:"",driver:"",location:"",user:"",pass:"",license:"",licenseUrl:""},
        driverSelected: {}
      },

      created: function () {
        this.$http.get('/sources').then(response => {
            this.sources = response.body;
        });
        this.$http.get('/drivers').then(response => {
            this.drivers = response.body;
        });
      },

      watch: {
            sources: {
                handler: function (sources) {
                    if (this.editing) {
                        this.$http.put("/sources", sources)
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
                this.editingSource.driver = source.driver
                this.editingSource.location = source.location
                this.editingSource.user = source.user
                this.editingSource.pass = source.pass
                this.editingSource.license = source.license
                this.editingSource.licenseUrl = source.licenseUrl
                this.editingSource.driverUrl = source.driverUrl
                this.editedSource = source
                console.log(this.editingSource)
            },
            editSaveSource: function () {
                console.log('Edit save source')
                this.editing=true;
                this.editedSource.id = this.editingSource.id
                this.editedSource.type = this.editingSource.type
                this.editedSource.name = this.editingSource.name
                this.editedSource.driver = this.editingSource.driver
                this.editedSource.location = this.editingSource.location
                this.editedSource.user = this.editingSource.user
                this.editedSource.pass  = this.editingSource.pass
                this.editedSource.license  = this.editingSource.license
                this.editedSource.licenseUrl  = this.editingSource.licenseUrl
                this.editedSource.driverUrl  = this.editingSource.driverUrl
            },
            addSaveSource:function () {
                this.editing=true;
                this.newSource.id = this.sources.length+1
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
            },
            editingWith(event) {
                console.log(driverName);
                var driverName = event.target.value;
                if (driverName=='h2') {
                    this.editingSource.driver = 'org.h2.Driver';
                    this.editingSource.driverUrl = '';
                } else if (driverName=='mysql') {
                    this.editingSource.driver = 'com.mysql.cj.jdbc.Driver';
                    this.editingSource.driverUrl = '';
                } else if (driverName=='psql') {
                    this.editingSource.driver = 'org.postgresql.Driver';
                    this.editingSource.driverUrl = '';
                } else {
                    var driverChoosed = this.drivers.filter(driver => driver.name == driverName)[0]
                    console.log(driverChoosed)
                    this.editingSource.driver = driverChoosed.className;
                    this.editingSource.driverUrl = driverChoosed.jarLocation;
                }
            },
        }
})