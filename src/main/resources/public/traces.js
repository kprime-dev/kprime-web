/*
    var treeData = {
        name: "My Tree",
        children: [
          { name: "hello" },
          { name: "wat" },
          {
            name: "child folder",
            children: [
              {
                name: "child folder",
                children: [{ name: "hello" }, { name: "wat" }]
              },
              { name: "hello" },
              { name: "wat" },
              {
                name: "child folder",
                children: [{ name: "hello" }, { name: "wat" }]
              }
            ]
          }
        ]
      };
    */
    var treeData = {}
 // define the tree-item component
      Vue.component("tree-item", {
        template: "#item-template",
        props: {
          item: Object
        },
        data: function() {
          return {
            isOpen: false,
          };
        },
        computed: {
          isFolder: function() {
            return this.item.children && this.item.children.length;
          }
        },
        methods: {
          toggle: function() {
            if (this.isFolder) {
              this.isOpen = !this.isOpen;
            }
          },
          makeFolder: function() {
            if (!this.isFolder) {
              this.$emit("make-folder", this.item);
              this.isOpen = true;
            }
          }
        }
      });


    var appTraces = new Vue ({
      el: '#app-traces',

      data: {
        fileeditable: false,
        filestory: false,
        filechart: false,
        filedoc: false,
        treeData: treeData,
        spinning: false,
        commandLog: [],
        command: '',
        commandResult: '',
        commandFailure: '',
        currentTrace: '',
        currentSource: '',
        currentTraceFile: '',
        traceFiles: [],
        traces: [],
        traceFileContent: '',
        transformerWhy: '',
        transformers: [], //[{name:'Uno',decompose:true,compose:false},{name:'Due',decompose:false,compose:true}],
        sources: [],
        goals: [],
        settings: {},
        currentProject: '',
        wsMessage: 'x',
        connection: null,
      },

      created: function () {
            this.$http.get('/settings')
                .then(response => {
                    this.settings = response.body;
                    this.currentProject = this.settings.projectName
                    this.currentTrace = this.settings.traceName;
                    if (this.currentTrace.length >0) {
                      this.$http.get("/trace/"+this.currentTrace)
                      .then((response) => {
                          this.traceFiles=response.body;
                      })
                    }
            });
            this.$http.get('/traces').then(response => {
                this.traces = response.body;
            });
            this.$http.get('/tracestree').then(response => {
                this.treeData = response.body;
            });
            this.$http.get('/sources').then(response => {
                this.sources = response.body;
            });
            this.$http.get('/todos').then(response => {
                this.goals = response.body;
            });

      },

    mounted: function(){
            var origin = location.origin
            var server = origin.substring(origin.lastIndexOf('/') + 1);
            var docURL = "ws://"+server+"/docs/1111";
            console.log(docURL);
      let connection = new WebSocket(docURL);
      connection.onmessage = (event) => {
        // Vue data binding means you don't need any extra work to
        // update your UI. Just set the `time` and Vue will automatically
        // update the `<h2>`.
              this.wsMessage = event.data;
        // this.time = event.data;
      }
    },


    computed: {
      compiledMarkdown: function() {
            return marked.parse(this.traceFileContent, { sanitize: true, breaks:true, gfm:true });
      },
      notHasTraces: function() {
            return this.traces.length == 0;
      },
      notHasDatabases: function() {
            return this.databasesXml.length == 0;
      },
      notHasDocs: function() {
            return this.storyMd.length == 0;
      },
      orderedTraces: function () {
        return _.orderBy(this.traces, 'name')
      },
      orderedTraceFiles: function () {
        return _.orderBy(this.traceFiles)
      },
      databasesXml: function() {
            return this.orderedTraceFiles.filter(function(el) {
                    return el.endsWith('db.xml');
                }
            )
      },
      databasesCs: function() {
            return this.orderedTraceFiles.filter(function(el) {
                    return el.endsWith('_cs.xml');
                }
            )
      },
      storyMd: function() {
            return this.orderedTraceFiles.filter(function(el) {
                    return el.endsWith('.md')||el.endsWith('.chart')||el.endsWith('.adoc');
                }
            )
      }

    },
      watch: {
        currentTraceFile: {
            handler: function (currentTraceFile) {
              this.$http.get("/tracefile/"+this.currentTrace+"/"+currentTraceFile)
              .then((response) => {
                   this.fileeditable=!currentTraceFile.endsWith('.xml')
                   this.filestory=currentTraceFile.endsWith('.md')
                   this.filechart=currentTraceFile.endsWith('.chart')
                   this.filedoc=currentTraceFile.endsWith('.adoc')
                  console.log("asked trace content")
                    // updates content textarea
                  this.traceFileContent=response.body;
                    // updates transformers list
                  this.$http.get("/tracetransf/"+this.currentTrace)
                  .then((response) => { // persist all changes to backend
                      console.log("asked trace transf");
                      this.transformers=response.body;
                  });
              })
              .catch((response) => {
                  console.log("NOT asked")
                  console.log(response)
              });
            }
        },
        deep:true
      },

      methods: {
         updateWsMessage : function(event) {
              console.log('ws onmessage')
              console.log(event);
              this.wsMessage = event.data;
         },
         makeFolder: function(item) {
            Vue.set(item, "children", []);
            this.addItem(item);
          },
          addItem: function(item) {
            item.children.push({
              name: "new stuff"
            });
          },
          clickOnGoal: function(goalName) {
            // chiama il server per creare il database in trace
            this.$http.get('/tracegoal/'+goalName).then(response => {
                // aggiorna traces, changes, transformers
                //updateTraces
                this.$http.get('/traces').then(response2 => {
                    this.traces = response2.body;
                });
                //this.commandResult='Trace created.'
                this.commandResult=response.message
            })
            .catch((response) => {
                  this.commandResult='Error creating trace.'
                  console.log(response);
            });
          },
          clickOnSource: function(sourceName) {
            // chiama il server per creare il database in trace
            this.$http.get('/tracesource/'+sourceName).then(response => {
                // aggiorna traces, changes, transformers
                //updateTraces
                this.$http.get('/traces').then(response2 => {
                    this.traces = response2.body;
                });
                //this.commandResult='Trace created.'
                this.commandResult=response.message
            })
            .catch((response) => {
                  this.commandResult='Error creating trace.'
                  console.log(response);
            });
          },
          clickOnDropTrace: function(traceName) {
              if (traceName=='root') {
                alert('ROOT may not be dropped.');
                return;
              }
              this.$http.delete("/trace/"+traceName)
              .then((response) => {
                    this.$http.get('/traces').then(response2 => {
                        this.traces = response2.body;
                    });
              });
          },
          clickOnDropTraceFile: function(traceName,traceFileName) {
              this.$http.delete("/tracefile/"+traceName+"/"+traceFileName)
              .then((response) => {
                  this.$http.get("/trace/"+traceName)
                  .then((response) => {
                      console.log("asked trace files");
                      this.traceFiles=response.body;
                  })
              });
          },
          clickOnTrace: function(traceName) {
              console.log("traceName=["+traceName+"]")
              this.currentTrace = traceName;
              this.currentTraceFile = '';
              this.traceFileContent = '';
              this.$http.get("/trace/"+traceName)
              .then((response) => {
                  console.log("asked trace files");
                  this.traceFiles=response.body;
                  this.commandResult='Trace selected. Now you could edit databases, stories.'
              })
              .catch((response) => {
                  this.commandResult='Error selecting trace.'
                  console.log(response);
              });
          },
          clickOnTraceFile: function(traceFileName) {
              this.currentTraceFile = traceFileName;
          },
          clickOnWhy: function(why) {
            this.transformerWhy=why;
          },
          clickOnSaveFile:  function() {
            var content =
            this.$http.put('/tracesave/'+this.currentTrace+"/"+this.currentTraceFile,this.traceFileContent)
            .then( response => {
                this.commandResult = 'File saved.'
                this.commandFailure = null
            }).catch((error) => {
                this.commandResult = null
                this.commandFailure = 'File saving error. '+error
            });
          },
          clickOnPlayFile:  function() {
            var content =
            this.$http.get('/traceplay/'+this.currentTrace+"/"+this.currentTraceFile)
            .then( response => {
                this.commandResult= response.body
            }).catch((response) => {
                this.commandFailure = 'File playing error.'
            });
          },
          clickOnTransformer: function(transformerName) {
            this.commandFailure=''
            this.$http.get('/traceapply/'+this.currentTrace+'/'+transformerName).then(response => {
                    if (response.body.ok) {
                    this.$http.get("/tracedb/"+this.currentTrace)
                      .then((response) => { // persist all changes to backend
                          console.log("asked trace content")
                          this.traceFileContent=response.body;
                    // updates changes list
                          this.$http.get("/trace/"+this.currentTrace)
                          .then((response) => { // persist all changes to backend
                              console.log("asked trace files");
                              this.traceFiles=response.body;
                          })
                    // updates transformers list
                          this.$http.get("/tracetransf/"+this.currentTrace)
                          .then((response) => { // persist all changes to backend
                              console.log("asked trace transf");
                              this.transformers=response.body;
                          })
                      })
                    } else {
                        this.commandFailure=response.body.message;
                    }

            });
          },
          clickOnCommand: function(cmd) {
            this.command = cmd;
          },
          keyUpCommand: function() {
//            nosideeffects=['facts','select','help','tables','all','xml','tables','add-param','drop-params',
//                'constraints','keys','foreigns','functionals','mappings','add-story','save-as-source'];
            sideeffects=['trace-db','cs-add-table'];
            changeDbCmd=[];
            changeCsCmd=['cs-add-table'];
            this.commandFailure='';
            this.commandResult=''
            this.spinning = true
            //console.log(this.command);
            //this.command = this.command.toLowerCase()
            this.$http.put('/tracecommand', this.command).then(response => {
                this.spinning = false
                //console.log(response);
                var list = response.body.options;
                //console.log(list);
                var cmdInput = document.getElementById("cmd");
                //new Awesomplete(cmdInput ,{ list: list });
                $( "#cmd" ).autocomplete({
                      source: list
                    });
                if (response.body.failure!=='') {
                    this.commandFailure='failure: '+response.body.failure;
                    if (response.body.warning!=='') {
                        this.commandFailure+= ' WARN:' + response.body.warning;
                    }
                } else {
                    this.commandLog = this.commandLog.splice(0,5)
                    this.commandLog.push(this.command);
                    this.commandResult=response.body.message;
                    if (response.body.currentTrace!='')
                        this.currentTrace = response.body.currentTrace
                    if (response.body.currentTraceFile!='')
                        this.currentTraceFile = response.body.currentTraceFile
                    if (response.body.warning!=='') {
                        this.commandFailure+= ' WARN:' + response.body.warning;
                    }
                    // updates traces list
                    this.$http.get('/traces').then(response => {
                        this.traces = response.body;
                    });
                    // updates changes list
                    this.$http.get("/trace/"+this.currentTrace)
                    .then((response) => {
                         console.log("asked trace files");
                         this.traceFiles=response.body;
                    })
                    // updates database view (db filename)
                    if (sideeffects.includes(this.command.split(" ")[0])) {
                        this.$http.get("/tracedb/"+this.currentTrace)
                          .then((response) => {
                              console.log("asked trace content")
                              this.traceFileContent=response.body;
                              // updates transformers list
                              this.$http.get("/tracetransf/"+this.currentTrace)
                              .then((response) => {
                                  console.log("asked trace transf");
                                  this.transformers=response.body;
                              });
                          })
                      } // if has side effect

                }
            })
          },

        },// methods

    })

