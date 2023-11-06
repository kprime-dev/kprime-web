
    var appUsers = new Vue({
      el: '#app-users',

      data: {
        editing : false,
        successMessage : null,
        errorMessage : null,
        newUser: { id:'', name: '', role:'', memberOf:'', pass: '', email:''},
        editingUser: { id:'', name: '',  role:'', memberOf:'', pass: '', email:''},
        editedUser: { id:'', name: '', role:'', memberOf:'', pass: '', email:''},
        users: []
      },

      created: function () { // get todos from backend when vue is ready
        this.$http.get('/users').then(response => {
            this.users = response.body;
        });
      },

        watch: {
            users: {
                handler: function (users) {
                    if (this.editing) {
                        this.$http.put("/users", users)
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

        editUser: function (user) {
            console.log('Edit ')
            console.log(user)
            this.editingUser.id = user.id
            this.editingUser.name = user.name
            this.editingUser.pass = user.pass
            this.editingUser.email = user.email
            this.editingUser.role = user.role
            this.editingUser.memberOf = user.memberOf
            this.editedUser = user
            console.log(this.editingUser)
        },
        editSaveUser: function () {
            console.log('Edit save user')
            this.editing=true;
            this.editedUser.name = this.editingUser.name
            this.editedUser.pass = this.editingUser.pass
            this.editedUser.email = this.editingUser.email
            this.editedUser.role = this.editingUser.role
            this.editedUser.memberOf = this.editingUser.memberOf
        },

        addSaveUser:function () {
            this.editing=true;
            this.newUser.id = this.users.length+1
            this.users.push(this.newUser);
            this.newUser = { id:'', name: '',  pass: '', email:'', role:'', memberOf:''};
        },

        removeUser: function (user) {
            this.editing=true;
            this.users.splice(this.users.indexOf(user), 1)
        },
    }

    })

