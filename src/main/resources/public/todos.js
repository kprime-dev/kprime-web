import VueFilterDateFormat from './js/vue-filter-date-format.esm.js';

function filterByKey(list,keyToFilter) {
    console.log(keyToFilter)
    console.log('filterByKey')
    console.log(list)
    var filtered = [];
    var item;
    for (item of list) {
        if (item.key===keyToFilter) { filtered.push(item); }
    }
    return filtered;
}

var NO_DATE = new Date(2000,0,0,0,0,0,0)

Vue.use(VueFilterDateFormat);

Vue.directive('focus', {
    inserted: function (el) {
        el.focus()
    }
})

Vue.component('todo-item', {
    template: '<li>This is a todo</li>'
})

var appTodos = new Vue({
    el: '#app-todos',

    data: {
        vuca: {volatility:0, uncertainty:0, complexity:0, ambiguity:0},
        settings: {},
        currentProject: '',
        currentTrace: '',
        currentDatabase: '',
        traces: [],
        projects: [],
        messageFailure: '',
        messageSuccess: '',
        newTodo: '',
        editing: false,
        todos: [],
        editedTodo : { id:0, title: '', completed: false, hidden:false,},
        editingTodo : { id:0, title: '', completed: false},
        labels: '',
        filterLabel: '',
        filterParent: '',
    },

    created: function () {
        this.$http.get('/settings').then(response => {
                this.settings = response.body;
                this.currentProject = this.settings.projectName;
            });
        this.$http.get('/projects').then(response => { this.projects = response.body; });
    },

    computed: {
        filteredTodos: function() {
              var list = this.todos;
              if (this.currentTrace != '' && this.currentTrace != 'root') {
                list = _.orderBy(filterByKey(this.todos,this.currentTrace+this.currentDatabase), 'priority', 'desc');
              }
              if (this.filterParent.length >0) {
                return list.filter(todo => todo.partof === this.filterParent)
              }
              if (this.newTodo.length >0) {
                  return list.filter(todo => todo.title.includes(this.newTodo));
              }
              if (this.filterLabel == 'ALL') {
                return list
              }
              if (this.filterLabel == '') {
                return list.filter(todo  => todo.labels.trim() === '' )
              }
              return list.filter(todo  => todo.labels.includes(this.filterLabel));
        },
        termsFilteredByName: function() { return this.todos.filter(el => !el.name.indexOf(this.newTodo)) },
        labelFilters : function() { return new Set(this.todos.filter(todo => todo.labels != '').map( todo  => todo.labels)); },
        databasesXml: function() {
            return this.databases.filter(function(el) {
                    return el.endsWith('db.xml');
                }
            )
        },
        databasesCs: function() {
            return this.databases.filter(function(el) {
                    return el.endsWith('_cs.xml');
                }
            )
        },
        databasesMd: function() {
            return this.databases.filter(function(el) {
                    return el.endsWith('.md');
                }
            )
        },
        hasTodos: function() { return this.filteredTodos.length > 0; },
        notHasProjects: function() { return this.projects.length == 0; },
        notHasTodos: function() { return this.filteredTodos.length == 0; },
        notHasDatabases: function() { return this.databases.length == 0; },
        notHasTraces: function() { return this.traces.length == 0; },
        hasCurrentTrace: function() { return this.currentTrace },
        totalEstimate: function() {
            var total = 0;
            for (let single of this.todos) {
                total +=  Number(single.estimate);
            }
            return total;
        },
        totalFilteredEstimate: function() {
            var total = 0;
            for (let single of this.filteredTodos) {
                total +=  Number(single.estimate);
            }
            return total;
        }
    },

    watch: {
        currentProject: {
            handler: function (newCurrentProject) {
                this.$http.get('/context/'+newCurrentProject+'/todo').then(response => {
                    var tmp = response.body;
                    this.currentProject = newCurrentProject;// useless ???
                    this.todos=[]
                    for (var tod in tmp) {
                        this.todos.push(
                            {id:tmp[tod].id,
                            title: tmp[tod].title,
                            completed: tmp[tod].completed,
                            hidden: tmp[tod].hidden,
                            key: tmp[tod].key,
                            dateClosed:  new Date(tmp[tod].dateClosed),
                            dateOpened: new Date(tmp[tod].dateOpened),
                            dateCreated: new Date(tmp[tod].dateCreated),
                            dateDue: new Date(tmp[tod].dateDue),
                            priority: tmp[tod].priority,
                            estimate: tmp[tod].estimate,
                            partof: tmp[tod].partof,
                            assignee: tmp[tod].assignee,
                            isOpened: !tmp[tod].dateOpened.toString().includes('1999'),
                            isClosed: !tmp[tod].dateClosed.toString().includes('1999'),
                            labels: tmp[tod].labels,
                            gid: tmp[tod].gid,
                            }
                            );
                    }
                });

            }
        },
        todos: {
            handler: function (todos) {
    //                    for (let single of this.filteredTodos) {
    //                        single.isOpened = !single.dateOpened.toString().includes('1999')
    //                        single.isClosed = !single.dateClosed.toString().includes('1999')
    //                    }
                if (this.editing) {
                    this.$http.put("/context/"+this.currentProject+"/todo", this.todos)
                    .then((response) => { // persist all changes to backend
                        console.log("saved to server");
                        this.messageFailure = '';
                    })
                    .catch((response) => {
                        console.log("NOT saved");
                        this.messageFailure = response.body.title;
                    });
                    this.editing=false
                }
            },
            deep: true
        }
    },

    methods: {

        gotoTerms: function() { window.location="/dictionary.html" },

        gotoStories: function() { window.location="/noteedit.html" },

        gotoContexts: function() { window.location="/admin.html" },

        parentName: function(parentId) {
            console.log(parentId);
            return this.todos.filter(todo => todo.id == parentId).map( todo => todo.title );
        },

        filterWithLabel: function(label) {
            this.filterLabel = label;
            this.filterParent = "";
        },

        clickOnFilterParent: function(todo) {
            this.filterParent = todo.partof;
            console.log(this.filterParent);
        },

        clickOnMarkdown: function() {
                    this.$http.put("/context/"+this.currentProject+"/todo/markdown", this.todos)
                    .then((response) => {
                        console.log("saved to server");
                        this.messageSuccess = 'Exported Markdown to root dir.';
                        this.messageFailure = '';
                    })
                    .catch((response) => {
                        console.log("NOT saved");
                        this.messageSuccess = '';
                        this.messageFailure = response.body.title;
                    });
        },

        clickOnProject: function(project) { this.currentProject = project.name },

        clickOnRoot: function() {
            this.currentTrace = '',
            this.currentDatabase = '',
            this.databases = [],
            this.vuca = {volatility:0, uncertainty:0, complexity:0, ambiguity:0}
          },

        clickOnTrace: function(traceName) {
              console.log(traceName)
              this.currentTrace = traceName;
              this.currentDatabase = '';
              this.settings.traceName = traceName;
              this.messageFailure ='';
              this.$http.get("/trace/"+traceName)
              .then((response) => {
                  console.log("asked trace files");
                  this.databases=response.body;
                  //this.terms = [];
              })
              .catch((response) => {
                  this.messageFailure = response.body;
                  console.log("NOT asked");
                  console.log(response);
              });
              this.$http.get("/stats/vuca/"+traceName)
              .then((response) => {
                  console.log("asked trace stats");
                  this.vuca.volatility=response.body.volatility;
                  this.vuca.uncertainty=response.body.uncertainty;
                  this.vuca.complexity=response.body.complexity;
                  this.vuca.ambiguity=response.body.ambiguity;
              })
              .catch((response) => {
                  this.messageFailure = response.body;
                  console.log("NOT asked");
                  console.log(response);
              });
          },

        clickOnDatabase: function(databaseName) { this.currentDatabase = databaseName; },

        addTodo: function () {
            var clickAudio = document.getElementById("clickAudio");
            if (clickAudio) clickAudio.play();
            var value = this.newTodo && this.newTodo.trim();
            if (!value) {
                return
            }
            this.todos.push({
                id: new Date().getTime(),
                title: value,
                completed: false,
                hidden:false,
                key:this.currentTrace+this.currentDatabase,
                dateCreated: new Date(),
                dateOpened: NO_DATE,
                dateClosed: NO_DATE,
                dateDue: NO_DATE,
                priority: 0,
                estimate: 1,
                partof: '',
                assignee: '',
                labels: ''
            });
            this.newTodo = '';
            this.editing=true;
        },

        removeTodo: function (todo) {
            var triggerAudio = document.getElementById("triggerAudio");
            if (triggerAudio) triggerAudio.play();
            this.editing=true;
            this.todos.splice(this.todos.indexOf(todo), 1)
        },

        editTodo: function (todo) {
            var triggerAudio = document.getElementById("triggerAudio");
            if (triggerAudio) triggerAudio.play();
            console.log('Edit todo DOING')
            console.log(todo)
            this.editingTodo.id = todo.id;
            this.editingTodo.title = todo.title;
            this.editingTodo.priority = todo.priority;
            this.editingTodo.estimate = todo.estimate;
            this.editingTodo.completed = todo.completed;
            this.editingTodo.partof = todo.partof;
            this.editingTodo.assignee = todo.assignee;
            this.editingTodo.labels = todo.labels;
            this.editingTodo.dateDue = todo.dateDue;
            this.editingTodo.gid = todo.gid;
            this.editedTodo = todo;
            console.log('Edit todo DONE')
        },

        editSaveTodo: function () {
            var clickAudio = document.getElementById("clickAudio");
            if (clickAudio) clickAudio.play();
            console.log('Edit save todo');
            this.editedTodo.title = this.editingTodo.title;
            this.editedTodo.priority = this.editingTodo.priority;
            this.editedTodo.estimate = this.editingTodo.estimate;
            this.editedTodo.partof = this.editingTodo.partof;
            this.editedTodo.assignee = this.editingTodo.assignee;
            this.editedTodo.labels = this.editingTodo.labels;
            this.editedTodo.gid = this.editingTodo.gid;
            this.editedTodo.dateDue = this.editingTodo.dateDue;
            this.editing=true;
        },

        startTodo:function(todo) {
            console.log('Completed todo');
            this.editing=true;
            todo.dateOpened = new Date();
        //            this.$http.put("/todos", this.todos)
        //            .then(() => { // persist all changes to backend
        //                console.log("saved to server")
        //            })

        },

        completeTodo:function(todo) {
            console.log('Started todo');
            this.editing=true;
            if (todo.completed) {
                todo.completed = false;
                todo.dateClosed = NO_DATE;
            } else {
                todo.completed = true;
                todo.dateClosed = new Date();
            }
        //            this.$http.put("/todos", this.todos)
        //            .then(() => { // persist all changes to backend
        //                console.log("saved to server")
        //            })

        }

    }

});