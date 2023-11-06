    var appDrivers = new Vue({
      el: '#app-drivers',

      data: {
        uploadSourceName: '',
        meta: '',
        editing: false,
        errorMessage: null,
        successMessage: null,
        drivers: [],
        newDriver: {id:'',type:'',name:'',className:'',driverPattern:'',jarLocation:''},
        editingDriver: {id:'',type:'',name:'',className:'',driverPattern:'',jarLocation:''},
        editedDriver: {id:'',type:'',name:'',className:'',driverPattern:'',jarLocation:''},
      },

      created: function () {
        this.$http.get('/drivers').then(response => {
            this.drivers = response.body;
        });
      },

        watch: {
            drivers: {
                handler: function (drivers) {
                    if (this.editing) {
                        this.$http.put("/drivers", drivers)
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
            editDriver: function (source) {
                console.log('Edit ')
                console.log(source)
                this.editingDriver.id = source.id
                this.editingDriver.type = source.type
                this.editingDriver.name = source.name
                this.editingDriver.jarLocation = source.jarLocation
                this.editingDriver.className = source.className
                this.editedDriver = source
                console.log(this.editingDriver)
            },
            editSaveDriver: function () {
                console.log('Edit save')
                this.editing=true;
                this.editedDriver.id = this.editingDriver.id
                this.editedDriver.type = this.editingDriver.type
                this.editedDriver.name = this.editingDriver.name
                this.editedDriver.jarLocation = this.editingDriver.jarLocation
            },
            addSaveDriver:function () {
                console.log('Add save')
                this.editing=true;
                this.newDriver.id = this.drivers.length+1
                this.drivers.push(this.newDriver);
                this.newDriver = {id:'',type:'',name:'',className:'',driverPattern:'',jarLocation:''};
            },
            removeDriver(source) {
                this.editing=true;
                this.drivers.splice(this.drivers.indexOf(source), 1);
            },
        }
})