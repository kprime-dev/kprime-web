    var appTraces = new Vue ({
      el: '#app-traces',

      data: {
        fileeditable: false,
        filestory: false,
        filechart: false,
        filedoc: false,
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
        wsMessage: '[]',
        connection: null,
        wsClosed: false,
        eventList: [],
      },

      created: function () {
        this.$http.get('/events')
            .then(response => {
                this.eventList = response.body;
            });
      },

    mounted: function(){
            var origin = location.origin
            var server = origin.substring(origin.lastIndexOf('/') + 1);
            var docURL = "ws://"+server+"/docs/1111";
            console.log(docURL);
      let connection = new WebSocket(docURL);
      connection.onmessage = (event) => {
        this.wsMessage = event.data;
        this.eventList = JSON.parse(this.wsMessage);
      }
      connection.onclose = (event) => {
        this.wsClosed = true
      }
    },

      methods: {
          keyUpCommand: function() {
            sideeffects=['trace-db','cs-add-table'];
            changeDbCmd=[];
            changeCsCmd=['cs-add-table'];
            this.commandFailure='';
            this.commandResult=''
            this.spinning = true
            //console.log(this.command);
            //this.command = this.command.toLowerCase()
            this.$http.put('/context/___/tracecommand', this.command).then(response => {
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

