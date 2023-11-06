
    var appActors = new Vue({
      el: '#app-actors',

      data: {
        editing : false,
        successMessage : null,
        errorMessage : null,
        newActor: { id:'', name: '', role:'', memberOf:'', pass: '', email:''},
        editingActor: { id:'', name: '',  role:'', memberOf:'', pass: '', email:''},
        editedActor: { id:'', name: '', role:'', memberOf:'', pass: '', email:''},
        actors: [],
        contextName : ''
      },

      created: function () { // get todos from backend when vue is ready
        this.$http.get('/settings').then(response => {
            this.contextName = response.body.projectName;            
            this.$http.get('/context/'+this.contextName+'/actors').then(response => {
                this.actors = response.body;
            });
        });
      },

        watch: {
            actors: {
                handler: function (actors) {
                    if (this.editing) {
                        this.$http.put('/context/'+this.contextName+"/actors", actors)
                        .then(() => { // persist all changes to backend
                            console.log("Saved to server on context:"+contextName)
                            this.successMessage = 'OK. Saved .'
                            this.errorMessage = null
                        })
                        .catch((response) => {
                            console.log("NOT saved on context:"+contextName)
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

        editActor: function (user) {
            console.log('Edit ')
            console.log(user)
            this.editingActor.id = user.id
            this.editingActor.name = user.name
            this.editingActor.pass = user.pass
            this.editingActor.email = user.email
            this.editingActor.role = user.role
            this.editingActor.memberOf = user.memberOf
            this.editedActor = user
            console.log(this.editingActor)
        },
        editSaveActor: function () {
            console.log('Edit save user')
            this.editing=true;
            this.editedActor.name = this.editingActor.name
            this.editedActor.pass = this.editingActor.pass
            this.editedActor.email = this.editingActor.email
            this.editedActor.role = this.editingActor.role
            this.editedActor.memberOf = this.editingActor.memberOf
        },

        addSaveActor:function () {
            this.editing=true;
            this.newActor.id = this.actors.length+1
            this.actors.push(this.newActor);
            this.newActor = { id:'', name: '',  pass: '', email:'', role:'', memberOf:''};
        },

        removeActor: function (user) {
            this.editing=true;
            this.actors.splice(this.actors.indexOf(user), 1)
        },
    }

    })

