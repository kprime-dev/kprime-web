
Vue.directive('focus', {
    inserted: function (el) {
        el.focus()
    }
})

var appAdmin = new Vue({
  el: '#app-admin',

  data: {
    successMessage : null,
    errorMessage : null,
    editing : false,
    editingProject: {name:"",location:"",description:"",picUrl:"",gid:"",labels:'',partOf:'',license:'',licenseUrl:''},
    editedProject: {name:"",location:"",description:"",picUrl:"",gid:"",labels:'',partOf:'',license:'',licenseUrl:''},
    settings: { workingDir:'', projectName:'', traceName:'', databaseName: '', changesetName: '', storyName: ''},
    projects: [],
    currentProject: '',
    filterLabel: ''
  },

  created: function () {
    this.$http.get('/settings')
        .then(response => {
            this.settings = response.body;
            this.currentProject = this.settings.projectName;
    });
    this.$http.get('/projects')
        .then(response => {
            this.projects = response.body;
    });
  },

  computed: {

    isCardSelected : function(card) { return card.name === this.settings.projectName },

    labelFilters : function() { return new Set(this.projects.filter(project => project.labels != '').flatMap( project  => project.labels.split(','))) },

    projectFiltered : function() {
            if (this.filterLabel != '')
                return this.projects.filter(project  => project.labels.includes(this.filterLabel));
            else
                return this.projects;
    }

  },

  methods: {

    gotoTerms: function() { window.location="/dictionary.html" },

    gotoGoals: function() { window.location="/todos.html" },

    gotoStories: function() { window.location="/noteedit.html" },

    filterProjects: function(label) { this.filterLabel = label },

    saveWorkingDir: function() {
                this.$http.put("/settings", this.settings)
                .then(() => {
                    var projectLocation = this.settings.workingDir
                    var nameEnd = projectLocation.lastIndexOf("/")
                    var nameStart = projectLocation.lastIndexOf("/",nameEnd-1)
                    var projectName = projectLocation.substring(nameStart+1,nameEnd)

                    var project = { name: projectName, location: projectLocation, labels:'' }
                    this.projects.push(project);
                    this.successMessage = 'OK. Saved ['+this.settings.workingDir+'].'
                    this.errorMessage = null

                    this.$http.put("/projects", this.projects)
                    .then(() => { // persist all changes to backend
                        console.log("Saved projects to server.")
                        this.successMessage = 'OK. Projects Saved .'
                        this.errorMessage = null
                    })
                    .catch((response) => {
                        console.log("Projects NOT saved.")
                        console.log(response)
                        this.errorMessage = 'KO: '+response.body.message
                        this.successMessage = null
                    });

                })
                .catch((response) => {
                    this.errorMessage = 'KO: '+response.body.message
                    this.successMessage = null
                });
    },

    removeProject: function(project) {
        this.projects .splice(this.projects.indexOf(project), 1);
        this.$http.put("/projects", this.projects)
        .then(() => { // persist all changes to backend
            console.log("Saved projects to server.")
            this.successMessage = 'OK. Projects Saved .'
            this.errorMessage = null
        })
        .catch((response) => {
            console.log("Projects NOT saved.")
            console.log(response)
            this.errorMessage = 'KO: '+response.body.message
            this.successMessage = null
        });
    },

    selectProject: function(project) {
            var projectLocation = project.location
            this.settings.workingDir = projectLocation
//                var nameEnd = projectLocation.lastIndexOf("/")
//                var nameStart = projectLocation.lastIndexOf("/",nameEnd-1)
            this.settings.projectName = project.name
            this.settings.traceName = '';
            this.settings.databaseName = '';
            this.$http.put("/settings", this.settings)
            .then(() => {
                this.successMessage = 'OK. Selected ['+this.settings.workingDir+'].'
                this.errorMessage = null
            })
            .catch((response) => {
                this.errorMessage = 'KO: '+response.body.message
                this.successMessage = null
            });
    },

    editProject: function (project) {
        console.log('Edit ')
        console.log(project)
        this.editingProject.name = project.name
        this.editingProject.location = project.location
        this.editingProject.description = project.description
        this.editingProject.picUrl = project.picUrl
        this.editingProject.gid = project.gid
        this.editingProject.labels = project.labels
        this.editingProject.partOf = project.partOf
        this.editingProject.license = project.license
        this.editingProject.licenseUrl = project.licenseUrl
        this.editedProject = project
        console.log(this.editingProject)
    },

    editSaveProject: function () {
        console.log('Edit save project')
        this.editing=true;
        this.editedProject.name = this.editingProject.name
        this.editedProject.location = this.editingProject.location
        this.editedProject.description = this.editingProject.description
        this.editedProject.picUrl  = this.editingProject.picUrl
        this.editedProject.labels  = this.editingProject.labels
        this.editedProject.partOf  = this.editingProject.partOf
        this.editedProject.gid  = this.editingProject.gid
        this.editedProject.license  = this.editingProject.license
        this.editedProject.licenseUrl  = this.editingProject.licenseUrl
        this.$http.put("/projects", this.projects)
        .then(() => { // persist all changes to backend
            console.log("Saved projects to server.")
            this.successMessage = 'OK. Projects Saved .'
            this.errorMessage = null
        })
        .catch((response) => {
            console.log("Projects NOT saved.")
            console.log(response)
            this.errorMessage = 'KO: '+response.body.message
            this.successMessage = null
        });
    }

    },
})

