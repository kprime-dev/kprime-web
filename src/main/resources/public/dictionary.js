import VueFilterDateFormat from './js/vue-filter-date-format.esm.js';

    Vue.use(VueFilterDateFormat);

    Vue.directive('focus', {
        inserted: function (el) {
            el.focus()
        }
    })

    var appTerms = new Vue({
      el: '#app-terms',

      data: {
        spinning: false,
        commandLog: [],
        command: '',
        commandResult: '',
        commandFailure: '',
        errorTermMessage: '',
        successTermMessage: '',
        messageFailure: '',
        messageSuccess: '',
        filterLabel: '',
        filter: '  ',
        settings: {},
        currentProject: '',
        projects: [],
        contextId: Date.now().toString(36),
        expertOptions: [],
        termsInfo: {lastUpdate:'-',nextDb:'',prevDb:''},
        currentTrace: 'root',
        currentDatabase: 'base',
        traces: [],
        databases: [],
        terms: [],
        editedTerm : {},
        editingTerm : { name:'', type:'', description:'', url:''},
        isEditingTerm : false,
        isEditingVoc : false,
        editingVocabulary: { prefix:'',namespace:'',description:'',reference:''},
        isEditingSource : false,
        editingSource: { type:'',name:'',driver:'',location:'',user:'',pass:''},
        vocabularies: [],
        contextVocabularies: [],
        vocabularyToAdd: {},
        isVisibleVocabularies: false,
        isEditingSource : false,
        sources: [],
        contextSources: [],
        sourceToAdd: {},
        isVisibleSources: false,
        connection: null,
        wsMessage: "",
        eventList: [],
        isWsClosed: false,
      },

      created: function() {
        this.$http.get('/projects').then(response => {
            this.projects = response.body;
        });
        this.$http.get('/settings').then(response => {
            this.settings = response.body;
            this.currentProject = this.settings.projectName
            this.$http.get('/context/'+this.currentProject+'/termsInfo/root/base').then((response) => {
                this.termsInfo = response.body;
            });
            this.$http.get('/context/'+this.currentProject+'/vocabularies').then((response) => {
                this.contextVocabularies = response.body;
            });
            this.$http.get('/context/'+this.currentProject+'/sources').then((response) => {
                this.contextSources = response.body;
            });
        });
        this.$http.get("/vocabularies").then((response) => {
            this.vocabularies = response.body;
        })
        this.$http.get('/sources').then(response => {
            this.sources = response.body;
        });
      },

      computed: {
        hasCommandResult: function() { return this.command.startsWith('>'); },
        notHasProjects: function() { return this.projects.length == 0; },
        labelFilters : function() {
            return new Set(this.terms.filter(term => term.labels != '').flatMap( term  => term.labels.split(',')))
        },
        termsFilteredByName: function() {
            return this.terms.filter(el => !el.name.indexOf(this.command) || this.command.startsWith(el.name))
        },
        termsFiltered() {
            var list = this.terms.filter( el => {
                return (el.name.startsWith(this.filter+'.')||!el.name.includes('.'));
            })
            if (this.filterLabel == 'ALL') {
                return list
            }
            if (this.filterLabel == '') {
                return list.filter(term  => term.labels.trim() === '' )
            }
            return list.filter(term  => term.labels.includes(this.filterLabel));
        },
        hasTerms: function() { return this.terms.length > 0; },
        notHasTerms: function() { return this.terms.length == 0; },
        hasCurrentTrace: function() { return this.currentTrace.length > 0; },
        notHasCurrentTrace: function() { return this.currentTrace.length == 0; },
        hasCurrentDatabase: function() { return this.currentDatabase.length > 0; },
        notHasCurrentDatabase: function() { return this.currentDatabase.length == 0; },
        notHasTraces: function() { return this.traces.length == 0; },
        notHasDatabases: function() { return this.databases.length == 0; },
        databasesXml: function() {
            return this.databases.filter(function(el) {
                    return el.endsWith('db.xml');
            })
        },
        databasesMd: function() {
            return this.databases.filter(function(el) {
                    return el.endsWith('.md');
                }
            )
        }
      },

      watch: {
        currentProject: {
            handler: function (newCurrentProject) {
                this.$http.get('/context/'+newCurrentProject+'/terms/root/base').then(response => {
                    this.terms = response.body;
                });
                this.$http.get('/context/'+newCurrentProject+'/sources').then(response => {
                    this.contextSources = response.body;
                });
                this.$http.get('/context/'+newCurrentProject+'/termsInfo/root/base').then((response) => {
                    this.termsInfo = response.body;
                });
                this.$http.get('/context/'+this.currentProject+'/vocabularies').then((response) => {
                    this.contextVocabularies = response.body;
                });
            },
        },
        terms: {
            handler: function (terms) {
                if (this.isEditingTerm) {
                    console.log(this.currentTrace)
                    console.log(this.currentDatabase)
                    this.$http.put('/context/'+this.currentProject+'/terms/'+this.currentTrace+'/'+this.currentDatabase, terms)
                    .then((response) => {
                        console.log("saved to server");
                        this.messageFailure = '';
                    })
                    .catch((response) => {
                        console.log("NOT saved");
                        this.messageFailure = response.body.title;
                    });
                    this.isEditingTerm=false
                }
            },
            deep: true
        },
        contextVocabularies: {
            handler: function (vocabularies) {
                if (this.isEditingVoc) {
                    this.$http.put('/context/'+this.currentProject+'/vocabularies', vocabularies)
                    .then((response) => {
                        console.log("saved to server");
                        this.messageFailure = '';
                    })
                    .catch((response) => {
                        console.log("NOT saved");
                        this.messageFailure = response.body.title;
                    });
                    this.isEditingVoc=false
                }
            },
            deep: true
        },
        contextSources: {
            handler: function (sources) {
                if (this.isEditingSource) {
                    this.$http.put('/context/'+this.currentProject+'/sources', sources)
                    .then((response) => {
                        console.log("saved to server");
                        this.messageFailure = '';
                    })
                    .catch((response) => {
                        console.log("NOT saved");
                        this.messageFailure = response.body.title;
                    });
                    this.isEditingSource=false
                }
            },
            deep: true
        }
      },

      methods: {

        gotoGoals: function() { window.location="/todos.html" },

        gotoStories: function() { window.location="/noteedit.html" },

        gotoContexts: function() { window.location="/admin.html" },

        appendCommandLabel: function(option) { this.command = this.command + " " + option.substring(8); },

        clickOnProject: function(project) {
            document.getElementById("clickAudio").play();
            this.currentProject = project.name
        },

        clickOnMarkdown: function() {
                    this.$http.put("/context/"+this.currentProject+"/dictionary/markdown", this.todos)
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

        showVocabularies: function(visible) {this.isVisibleVocabularies = visible; },

        showSources: function(visible) { this.isVisibleSources = visible; },

        filterWithLabel: function(label) { this.filterLabel = label },

        setFilter(newfilter) { this.filter = newfilter; },

        isColumn(termName) { return termName.includes(".") },

        isTable(termCategory) { return termCategory=="table" || termCategory.includes("table,") },

        clickOnRoot: function() {
            this.currentTrace = 'root',
            this.currentDatabase = 'base',
            this.databases = []
          },

        clickOnTrace: function(traceName) {
              this.currentTrace = traceName;
              this.currentDatabase = 'base';
              this.messageFailure ='';
              this.$http.get("/trace/"+traceName)
              .then((response) => {
                  console.log("asked trace files "+traceName);
                  this.databases=response.body;
                  this.terms = [];
              })
              .catch((response) => {
                  this.messageFailure = response.body;
                  console.log("trace NOT asked");
                  console.log(response);
              });
              this.$http.get('/context/'+this.currentProject+'/vocabularies')
              .then((response) => {
                  console.log("asked vocabularies "+this.currentProject);
                  //this.databases=response.body;
                  this.vocabularies = response.body;
              })
              .catch((response) => {
                  this.messageFailure = response.body;
                  console.log("vocabularies NOT asked");
                  console.log(response);
              });
          },

        clickOnPrevDatabase: function(prevDatabaseName) {
               this.currentDatabase = prevDatabaseName
                this.$http.get('/context/'+this.currentProject+'/terms/'+this.currentTrace+'/'+this.currentDatabase).then(response => {
                    this.terms = response.body;
                });
                this.$http.get('/context/'+this.currentProject+'/termsInfo/'+this.currentTrace+'/'+this.currentDatabase).then((response) => {
                    this.termsInfo = response.body;
                });
          },

        clickOnNextDatabase: function(nextDatabaseName) {
               this.currentDatabase = nextDatabaseName
                this.$http.get('/context/'+this.currentProject+'/terms/'+this.currentTrace+'/'+this.currentDatabase).then(response => {
                    this.terms = response.body;
                });
                this.$http.get('/context/'+this.currentProject+'/termsInfo/'+this.currentTrace+'/'+this.currentDatabase).then((response) => {
                    this.termsInfo = response.body;
                });
          },

        clickOnDatabase: function(databaseName) {
            if (this.connection) {
                this.connection.close();
            }
                var origin = location.origin
                var server = origin.substring(origin.lastIndexOf('/') + 1);
                //var docURL = "ws://"+server+"/docs/dic_"+this.currentTrace+"_"+databaseName;
                var docURL = "ws://"+server+"/docs/dic_"+this.currentTrace+"_null";
                console.log(docURL);
              this.connection = new WebSocket(docURL);
              this.connection.onconnect = (event) => {
                this.isWsClosed = false
                console.log("dic onconnect")
              }
              this.connection.error = (event) => {
                console.log("dic connection error")
                console.log(event)
              }
              this.connection.onmessage = (event) => {
                console.log("dic onmessage")
                this.wsMessage = event.data;
                if (event.data) {
                    this.eventList = JSON.parse(this.wsMessage);
                   } else {
                    this.eventList = [];
                   }
              }
              this.connection.onclose = (event) => {
                this.isWsClosed = true
              }
              this.$http.get("/terms/"+this.currentTrace+"/"+databaseName)
              .then((response) => {
                  console.log("asked terms"+this.currentTrace+"/"+databaseName);
                  this.currentDatabase = databaseName;
                  this.terms = response.body;
              });


          },

        // EDIT Vocabulary
        removeVocabulary: function (vocabulary) {
            this.isEditingVoc=true;
            this.contextVocabularies.splice(this.contextVocabularies.indexOf(vocabulary), 1)
        },

        addVocabulary: function () {
            this.isEditingVoc=true;
            var newVocabulary = { prefix:'',namespace:'',description:'',reference:''};
            newVocabulary.prefix = this.editingVocabulary.prefix;
            newVocabulary.namespace = this.editingVocabulary.namespace;
            newVocabulary.description = this.editingVocabulary.description;
            newVocabulary.reference = this.editingVocabulary.reference;
            this.contextVocabularies.push(newVocabulary)
            this.editingVocabulary = { prefix:'',namespace:'',description:'',reference:''};
        },

        removeSource: function (source) {
            this.isEditingSource=true;
            this.contextSources.splice(this.contextSources.indexOf(source), 1)
        },

        addSource: function () {
            this.isEditingSource=true;
            var newSource = { type:'',name:'',driver:'',location:'',user:'',pass:''};
            newSource.id = 1;
            newSource.type = this.editingSource.type;
            newSource.name = this.editingSource.name;
            newSource.driver = this.editingSource.driver;
            newSource.location = this.editingSource.location;
            newSource.user = this.editingSource.user;
            newSource.pass = this.editingSource.pass;
            this.contextSources.push(newSource)
            this.editingSource = { type:'',name:'',driver:'',location:'',user:'',pass:''};
        },

        onAddInstanceVocabulary: function(event) {
            var prefixChoose = event.target.value;
            console.log(prefixChoose);
            for (const voc in this.vocabularies) {
                if (this.vocabularies[voc].prefix === prefixChoose) {
                    this.isEditingVoc = true;
                    this.contextVocabularies.push(this.vocabularies[voc]);
                }
            }
        },

        onAddInstanceSource: function(event) {
            var nameChoose = event.target.value;
            console.log(nameChoose);
            for (const src in this.sources) {
                if (this.sources[src].name === nameChoose) {
                    this.isEditingSource = true;
                    this.contextSources.push(this.sources[src]);
                }
            }
        },

        removeSource: function (source) {
            this.isEditingSource=true;
            this.contextSources.splice(this.contextSources.indexOf(source), 1)
        },

        // EDIT Term
        editTerm: function (term) {
            document.getElementById("triggerAudio").play();
            this.editingTerm.name = term.name;
            this.editingTerm.type = term.type;
            this.editingTerm.description = term.description ;
            this.editingTerm.url = term.url ;
            this.editingTerm.gid = term.gid ;
            this.editedTerm = term;
        },

        editSaveTerm: function () {
            console.log('Edit save term');
            this.editedTerm.type = this.editingTerm.type;
            this.editedTerm.description = this.editingTerm.description ;
            this.editedTerm.url = this.editingTerm.url ;
            this.editedTerm.gid = this.editingTerm.gid ;
            this.isEditingTerm=true;
        },

        addTerm: function () {
            this.isEditingTerm=true;
            var newTerm = { name:'', category:'', relation:'', type:'', description:'', url:'', labels:''};
            newTerm.name = this.editingTerm.name;
            newTerm.type = this.editingTerm.type;
            newTerm.description = this.editingTerm.description ;
            newTerm.url = this.editingTerm.url ;
            this.terms.push(newTerm)
            this.successTermMessage = newTerm.name+' added.';
        },

        removeTerm: function(term) {
            document.getElementById("triggerAudio").play();
            this.isEditingTerm=true;
            this.terms.splice(this.terms.indexOf(term), 1)
        },

        keyUpCommand: function() {
            document.getElementById("clickAudio").play();
            if (this.command.startsWith('>')) {
            var sideeffects=['trace-db','cs-add-table'];
                var changeDbCmd=[];
                var changeCsCmd=['cs-add-table'];
                this.commandFailure='';
                this.commandResult=''
                this.spinning = true
                //var commandLine = this.command.substring(1)+'#'+this.contextId;
                var commandLine = this.command; //+'#'+this.contextId;
                this.$http.put('/context/'+this.currentProject+'/tracecommand', commandLine).then(response => {
                    this.spinning = false
                    var result = response.body.pop()
                    var list = result.options.map(opt => { return '>'+opt });
                    var cmdInput = document.getElementById("cmd");
                    $( "#cmd" ).autocomplete({
                          source: list
                        });
                    if (result.failure!=='') {
                        this.commandFailure='failure: '+result.failure;
                        if (result.warning!=='') {
                            this.commandFailure+= ' WARN:' + result.warning;
                        }
                    } else {
                        this.commandLog = this.commandLog.splice(0,5)
                        this.commandLog.push(this.command);

                        //this.commandResult=result.message;
                        this.commandResult=marked.parse(result.message, { sanitize: true, breaks: true, gfm: true });
                        this.$http.get('/context/'+this.currentProject+'/terms/'+this.currentTrace+'/'+this.currentDatabase)
                          .then((response) => {
                              this.terms = response.body;
                          });
                        this.$http.get('/context/'+this.currentProject+'/termsInfo/'+this.currentTrace+'/'+this.currentDatabase)
                          .then((response) => {
                              this.termsInfo = response.body;
                          });

                        this.expertOptions = result.options
//                        response.body.options.forEach(function(option) {
//                            this.expertOptions=[];
//                            if (option.startsWith('expert::')) {
//                                this.expertOptions.push(option)
//                            }
//                        });

    //                    if (response.body.currentTrace!='')
    //                        this.currentTrace = response.body.currentTrace
    //                    if (response.body.currentTraceFile!='')
    //                        this.currentTraceFile = response.body.currentTraceFile
    //                    if (response.body.warning!=='') {
    //                        this.commandFailure+= ' WARN:' + response.body.warning;
    //                    }
                        // updates traces list
//                        this.$http.get('/traces').then(response => {
//                            this.traces = response.body;
//                        });
                        // updates changes list
//                        this.$http.get("/trace/"+this.currentTrace)
//                        .then((response) => {
//                             console.log("asked trace files");
//                             this.traceFiles=response.body;
//                        })

                        // updates database view (db filename)

    //                        this.$http.get("/tracedb/"+this.currentTrace)
    //                          .then((response) => {
    //                              console.log("asked trace content")
    //                              this.traceFileContent=response.body;
    //                              // updates transformers list
    //                              this.$http.get("/tracetransf/"+this.currentTrace)
    //                              .then((response) => {
    //                                  console.log("asked trace transf");
    //                                  this.transformers=response.body;
    //                              });
    //                          })
    //                      } // if has side effect

                    }
                })
            } else {
              this.isEditingTerm=true;
              var termName = '';
              var termDesc = '';
              var posSemiColumn = this.command.indexOf(':');
              if (posSemiColumn > 0) {
                termName = this.command.substring(0,posSemiColumn).trim();
                termDesc = this.command.substring(posSemiColumn + 1).trim();
                var newTerm = { name:termName, category:'', relation:'', type:'', description:termDesc, url:'', labels:''};
                this.terms.push(newTerm)
                this.command = "";
              }
            };
          },
      }

    })

