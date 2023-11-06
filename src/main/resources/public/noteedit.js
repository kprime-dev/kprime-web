// visibility filters
var filters = {
all: function(notes) {
  return notes;
},
active: function(notes) {
  return notes.filter(function(note) {
    return !note.completed;
  });
},
selected: function(notes) {
  return notes.filter(function(note) {
    return note.selected;
  });
},
coded: function(notes) {
  return notes.filter(function(note) {
    return note.coded;
  })
}
};

var app = new Vue({
  el: '#app-notes',
  data: {
    notes: [],
    codes: [],
    newTodo: "",
    editedTodo: null,
    visibility: "all",
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
    oldTraceFile: '',
    traceFiles: [],
    traces: [],
    traceFileContent: '',
    transformerWhy: '',
    transformers: [],
    sources: [],
    goal: '',
    goals: [],
    term: '',
    refto: '',
    settings: {},
    currentProject: '',
    noteIndex: null,
    noteHasChanged: false,
    projects: [],
    traceStack: [],
    messageFailure: null,
    templateNames: [],
    goalId: '',
    noterefsto: [], // list of object { label , url }
    notegoals: [], // list of object { label , url }
    noteterms: [], // list of object { label , url }
  },


  created: function () {
    //http://localhost:7000/noteview.html?pr=prova4&tr=traces___root&tf=base.md
    this.command = new URLSearchParams(window.top.location.search);
    this.hash = window.location.hash
    this.goalId = this.command.get('goalid');
    this.currentProject = this.command.get('pr');
    if (this.currentProject) {
        this.currentTraceFile = this.command.get('tf');
        this.currentTrace = this.command.get('tr');
        this.$http.get('/projects').then(response => {
            this.projects = response.body;
        });
        this.$http.get('/templates').then(response => {
            this.templateNames = response.body;
        });
    } else {
        this.$http.get('/settings')
            .then(response => {
                this.settings = response.body;
                this.currentProject = this.settings.projectName
                this.currentTrace = this.settings.traceName;
                var currentDir = '___'; // root dir folder
                if (this.currentTrace!='') currentDir = this.currentTrace;
                var filesUrl = '/context/'+this.currentProject+'/files/'+currentDir;
                this.$http.get(filesUrl).then((response) => {
                  console.log("asked trace files");
                  this.traceFiles=response.body;
                  if (this.currentTrace != null && this.currentTraceFile != null && this.currentProject != null) {
                        this.$http.get('/projects').then(response => {
                            this.projects = response.body;
                        });
                        this.$http.get('/templates').then(response => {
                            this.templateNames = response.body;
                        });
                        var currentDir = '___'; // root dir folder
                        this.currentTrace = currentDir
                        if (this.currentTrace!='') currentDir = this.currentTrace;
                        this.$http.get('/context/'+this.currentProject+'/traces/'+currentDir).then((response) => {
                          this.traces=response.body;
                        });
                        var filesUrl = '/context/'+this.currentProject+'/files/'+currentDir;
                        this.$http.get(filesUrl).then((response) => {
                          console.log("asked trace files");
                          this.traceFiles=response.body;
                        }).catch((response) => {
                          console.log("Error asking trace files :"+response);
                        });
                        if (this.traceFiles.includes('readme.md')) {
                            this.currentTraceFile = 'readme.md';
                            this.$http.get("/project/"+this.currentProject+"/tracebook/"+this.currentTrace+"/"+this.currentTraceFile)
                            .then((response) => { this.parseNotes(response.body)  })
                            .catch((response) => {
                              console.log("NOT asked")
                              console.log(response)
                            });
                        }
                  }
                }).catch((response) => {
                  console.log("Error asking trace files :"+response);
                });
        });
    }
  },

    watch: {

        currentProject: {
            handler: function (newCurrentProject) {
                if (newCurrentProject) {
                    this.currentTrace='___'
                    this.traceStack = []
                    this.$http.get('/context/'+newCurrentProject+'/traces').then(response => {
                        this.traces=response.body;
                    })
                    this.$http.get('/context/'+newCurrentProject+'/files/___').then(response => {
                        this.traceFiles=response.body;
                        if (this.traceFiles.includes('readme.md')) {
                            this.currentTraceFile = 'readme.md';
                            this.$http.get("/project/"+this.currentProject+"/tracebook/"+this.currentTrace+"/"+this.currentTraceFile)
                            .then((response) => { this.parseNotes(response.body)  })
                            .catch((response) => {
                              console.log("NOT asked")
                              console.log(response)
                            });
                        }
                    })
//                    this.$http.get('/context/'+newCurrentProject+'/notes').then(response => {
//                        this.notes = response.body;
//                    });
                    this.notes = [];
                }
            },
        },

        currentTraceFile: {
            handler: function (currentTraceFile) {
                if (currentTraceFile) {
                   this.filechart=currentTraceFile.endsWith('.chart')
                   this.filestory=currentTraceFile.endsWith('.md')
                                                ||currentTraceFile.endsWith('.adoc')
                                                ||currentTraceFile.endsWith('.txt')
                                                ||currentTraceFile.endsWith('.csv')
                                                ||currentTraceFile.endsWith('.json')
                                                ||currentTraceFile.endsWith('.txt')
                }
            }
        },

    },

    computed: {

      traceDirForUrl: function() {
        if (this.traceStack == '') return '___';
        return this.traceStack.join('___');
      },

      traceDirLabel: function() { return this.traceStack.join(' '); },

      hasBackTrace: function() { return this.traceStack.length > 0; },

      notHasProjects: function() { return this.projects.length == 0; },

      filteredTodos: function() { return filters[this.visibility](this.notes); },

      remaining: function() { return filters.active(this.notes).length; },

      allDone: {
            get: function() {
              return this.remaining === 0;
            },
            set: function(value) {
              this.notes.forEach(function(note) {
                note.selected = value;
              });
            }
          },

      notHasTraces: function() { return this.traces.length == 0 && this.traceStack.length == 0; },

      notHasDatabases: function() { return this.databasesXml.length == 0; },

      notHasDocs: function() { return this.storyMd.length == 0; },

      orderedTraces: function () { return _.orderBy(this.traces, 'name') },

      orderedTraceFiles: function () { return _.orderBy(this.traceFiles) },

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
                    return el.endsWith('.md')
                            ||el.endsWith('.chart')
                            ||el.endsWith('.adoc')
                            ||el.endsWith('.txt')
                            ||el.endsWith('.csv')
                            ||el.endsWith('.json');
                    }
                )
          },

      titleChange: function(){ return this.notes.map(note => note.title) }

    },

    filters: {

          pluralize: function(n) {
            return n === 1 ? "item" : "items";
          }

    },

    methods: {

          gotoTerms: function() { window.location="/dictionary.html" },

          gotoGoals: function() { window.location="/todos.html" },

          gotoContexts: function() { window.location="/admin.html" },

          noteCode: function(note) { return note.title.startsWith('>') },

          clickOnPlayFile:  function() {
            var content =
            this.$http.get('/context/'+this.currentProject+'/traceplay/'+this.traceDirForUrl+"/"+this.currentTraceFile)
            .then( response => {
                this.commandResult = response.body
            }).catch((response) => {
                this.commandFailure = 'File playing error.'
            });
          },

          clickOnProject: function(project) {
              document.getElementById("clickAudio").play();
              this.notes = [];
              this.currentProject = project.name
          },

          addTodo: function() {
            let index = this.noteIndex +1;
            var value = this.newTodo && this.newTodo.trim();
            if (!value) {
              return;
            }
            this.notes.splice(index, 0 , {
                id: index,
                title: value,
                marked: marked.parse(value, { sanitize: false, breaks: true, gfm: true  }),
                completed: false,
                coded: false
            })
            this.newTodo = "";
            this.noteIndex = null;
            this.noteHasChanged = true;
            this.saveNotes();
          },

          addCell: function(index){ this.noteIndex = index; },

          addRefTo: function() {
            var value = this.refto && this.refto.trim();
            var newrefto =  new Object();
            newrefto.label = 'refto:'+value;
            newrefto.url = '/project/'+this.currentProject+'/story/'+value;
            this.noterefsto.push(newrefto);
            this.noteHasChanged = true;
            this.saveNotes();
          },

          removeRefTo: function(reftoIndex) {
              this.noterefsto.splice(reftoIndex, 1);
              this.noteHasChanged = true;
              this.saveNotes();
          },

          autocompRefTo: function() {
            var value = this.refto && this.refto.trim();
            if (!value) {
              return;
            }
            this.$http.get('/project/'+this.currentProject+'/forcetree/json/', value).then(response => {
                var result = response.body.children[0].children[1].children;
                var list = result.map(opt => { return opt.name });
                $( "#refto" ).autocomplete({
                      source: list
                });
            })
          },

          addGoal: function() {
            var value = this.goal && this.goal.trim();
            var newgoal =  new Object();
            newgoal.label = 'goal:'+value;
            newgoal.url = '/project/'+this.currentProject+'/todo/'+encodeURIComponent(value);
            this.notegoals.push(newgoal);
            this.noteHasChanged = true;
            this.saveNotes();
          },

          removeGoal: function(goalIndex) {
              this.notegoals.splice(goalIndex, 1);
              this.noteHasChanged = true;
              this.saveNotes();
          },

          autocompGoal: function() {
            var value = this.goal && this.goal.trim();
            if (!value) {
              return;
            }
            this.$http.get('context/'+this.currentProject+'/todo', value).then(response => {
                var result = response.body
                var list = result.map(opt => { return opt.title });
                $( "#goal" ).autocomplete({
                      source: list
                });
            })
          },

          addTerm: function() {
            var value = this.term && this.term.trim();
            var newterm =  new Object();
            newterm.label = 'term:'+value;
            newterm.url = '/project/'+this.currentProject+'/dictionary/'+value;
            this.noteterms.push(newterm);
            this.noteHasChanged = true;
            this.saveNotes();
          },

          removeTerm: function(termIndex) {
              this.noteterms.splice(termIndex, 1);
              this.noteHasChanged = true;
              this.saveNotes();
          },

          autocompTerm: function() {
            var value = this.term && this.term.trim();
            if (!value) {
              return;
            }
            this.$http.get('context/'+this.currentProject+'/terms/root/base', value).then(response => {
                var result = response.body
                var list = result.map(opt => { return opt.name });
                $( "#term" ).autocomplete({
                      source: list
                });
            })
          },

          clear: function(note){
            if(note.hasOwnProperty("commandFailure") && note.commandFailure !== ''){
              note.commandFailure = '';
            }
            if(note.hasOwnProperty("commandResult") && note.commandResult !== ''){
              note.commandResult = '';
            }
            this.noteHasChanged = true;
            this.saveNotes();
            this.$forceUpdate()
            return note;
          },

          clickOnClearStory: function() { this.notes = []; },

          clickOnNewStory: function() {
              this.notes = [];
              this.currentTraceFile = '';
              this.traceFileContent = '';
          },

          clickOnBackTrace: function() {
              this.traceStack.pop();
              this.notes = [];
              console.log(this.traceStack);
              var traceDir = this.traceStack.join('___');
              this.$http.get('/context/'+this.currentProject+'/traces/'+traceDir).then(response => {
                  this.traces=response.body;
              });
              //this.$http.get("/trace/"+traceName).then((response) => {
              if (traceDir == "") {
                traceDir = '___';
              }
              this.$http.get('/context/'+this.currentProject+'/files/'+traceDir).then((response) => {
                  console.log("asked trace files");
                  this.traceFiles=response.body;
                  this.commandResult='Trace selected. Now you could edit databases, stories.'
              })
              .catch((response) => {
                  this.commandResult='Error selecting trace.'
                  console.log(response);
              });

          },

          onCurrentTraceFileFocus: function(event) {
            this.oldTraceFile = this.currentTraceFile;
            console.log('file name:'+this.oldTraceFile);
          },

          onCurrentTraceFileBlur: function(event) {
            if (this.oldTraceFile !== this.currentTraceFile) {
                console.log('changed file name:'+this.currentTraceFile);
                if (this.oldTraceFile === '') this.oldTraceFile = '___'
                var traceDir = this.traceStack.join('___');
                if (traceDir === '') traceDir = '___';
                this.$http.get('/context/'+this.currentProject+'/filerename/'+traceDir+'/'
                  +encodeURIComponent(this.oldTraceFile)
                  +'/'+encodeURIComponent(this.currentTraceFile))
                  .then(response => {
                      this.$http.get('/context/'+this.currentProject+'/traces/'+traceDir).then(response => {
                          this.traces=response.body;
                      })
                      this.$http.get('/context/'+this.currentProject+'/files/'+traceDir).then(response => {
                          this.traceFiles=response.body;
                      })
                  })
            } else console.log('NOT change');
          },

          clickOnTemplate: function(templateName) {
              document.getElementById("clickAudio").play();
              var storyName = "abc";
              var traceDir = this.traceStack.join('___');
              if (!traceDir) traceDir='___';
              this.$http.get('/context/'+this.currentProject+'/template/'+templateName+"/"+storyName+"/"+traceDir).then(response => {
                  this.$http.get('/context/'+this.currentProject+'/files/'+traceDir).then(response => {
                      this.traceFiles=response.body;
                  })
              })
              .catch((response) => {
                  this.commandResult='Error creating new story from template.'
                  console.log(response);
              });
          },

          clickOnTrace: function(traceName) {
              document.getElementById("clickAudio").play();
              this.notes = [];
              this.currentTrace = traceName;
              this.currentTraceFile = '';
              this.traceFileContent = '';
              this.traceStack.push(traceName);
              var traceDir = this.traceStack.join('___');
              if (!traceDir) traceDir='___';
              this.$http.get('/context/'+this.currentProject+'/traces/'+traceDir).then((response) => {
                  this.traces=response.body;
              });
              this.$http.get('/context/'+this.currentProject+'/files/'+traceDir).then((response) => {
                  console.log("asked trace files");
                  this.traceFiles=response.body;
                  this.commandResult='Trace selected. Now you could edit databases, stories.'
              })
              .catch((response) => {
                  this.commandResult='Error selecting trace.'
                  console.log(response);
              });
          },

          clickOnDropTraceFile: function(traceName,traceFileName) {
              document.getElementById("triggerAudio").play();
              var traceDir = this.traceStack.join('___');
              //var traceDir = traceName
              if (!traceDir) traceDir='___';
              this.$http.delete("/context/"+this.currentProject+"/tracefile/"+traceDir+"/"+traceFileName)
                  .then((response) => {
                  this.$http.get('/context/'+this.currentProject+'/files/'+traceDir).then(response => {
                      this.traceFiles=response.body;
                      this.commandResult='Trace updated.'
                  })
              })
              .catch((response) => {
                  this.commandResult='Error deleting trace file .'
                  console.log(response);
              });
          },

          clickOnDropTrace: function(traceName) {
              document.getElementById("triggerAudio").play();
              var traceToDelete = traceName
              var traceDir = this.traceStack.join('___');
              if (!traceDir) traceDir='___';
              if (traceDir.length > 0) traceToDelete = traceDir+"___"+traceName;
              this.$http.delete("/context/"+this.currentProject+"/trace/"+traceToDelete)
                  .then((response) => {
                  this.$http.get('/context/'+this.currentProject+'/traces/'+traceDir).then((response) => {
                      this.traces=response.body;
                        });
              })
              .catch((response) => {
                  this.commandResult='Error deleting trace .'
                  console.log(response);
              });
          },

          runCode: function (note) {
              note.coded = true;
              var command = note.title;
              this.$http.put('/context/'+this.currentProject+'/tracecommand', command).then(response => {
                    var list = response.body.options;
                    var results = response.body;
                    note.commandFailure = '';
                    note.commandResult = '';
                    for (var i=0; i<results.length; i++) {
                        var result = results[i];
                        if (result.failure!=='') {
                            note.commandFailure =  note.commandFailure +'['+(i+1)+']'+ result.failure //+ '</br>---------------</br>';
                        } else if (result.warning!=='') {
                            note.commandFailure = note.commandFailure + '['+(i+1)+']'+ result.warning //+ '</br>---------------</br>';
                        } else if (result.messageType=='text') {
                            note.commandResult = note.commandResult + '['+(i+1)+']'+ result.message //+ '</br>---------------</br>';
                        } else {
                            note.commandResult = note.commandResult + '['+(i+1)+']'+ marked.parse(result.message, { sanitize: false, breaks: true, gfm: true }) //+ '</br>---------------</br>';
                        }
                    }
                    this.$forceUpdate()
                    this.noteHasChanged = true
                    this.saveNotes();
              });
          },

          clickOnTraceFile: function(traceFileName) {
              this.currentTraceFile = traceFileName;
              var traceDir = this.traceStack.join('___');
              if (traceDir=='') traceDir =  '___';
              this.$http.get('/context/'+this.currentProject+"/tracebook/"+traceDir+"/"+traceFileName)
              .then((response) => { this.parseNotes(response.body) })
              .catch((response) => {
                  console.log("NOT asked")
                  console.log(response)
              });
          },

          removeTodo: function(note) {
              document.getElementById("triggerAudio").play();
              this.notes.splice(this.notes.indexOf(note), 1);
              this.noteHasChanged = true;
              this.saveNotes();
          },

          scrollEl: function(note){
            let el = document.getElementById(note.id.toString());
            el.scrollIntoView()
          },

          upTodo: function(note) {
                let index = this.notes.findIndex(e => e.id == note.id);
                if (index > 0) {
                  let note = this.notes[index];
                  this.notes[index] = this.notes[index - 1];
                  this.notes[index - 1] = note;
                  this.$set(this.notes, index, this.notes[index])
              }
              this.scrollEl(note);
              this.noteHasChanged = true;
              this.saveNotes();
          },

          downTodo: function(note) {
            let index = this.notes.findIndex(e => e.id == note.id);
            if (index !== -1 && index < this.notes.length - 1) {
              let note = this.notes[index];
              this.notes[index] = this.notes[index + 1];
              this.notes[index + 1] = note;
              this.$set(this.notes, index, this.notes[index])
            }
            this.scrollEl(note);
            this.noteHasChanged = true;
            this.saveNotes();
          },

          editTodo: function(note, e) {
            let index = this.notes.findIndex(e => e.id == note.id)
            this.noteHasChanged = false;
            if(e.key == 'ArrowDown' && index !== -1 && index < this.notes.length - 1){
                //console.log('arrow down')
              this.beforeEditCache = this.notes[index+1].title;
              this.editedTodo = this.notes[index +1];
            }
             else if(e.key == 'ArrowUp' && index > 0){
                //console.log('arrow up')
              this.beforeEditCache = this.notes[index-1].title;
              this.editedTodo = this.notes[index -1];
            }
            else{
                //console.log('else')
              this.noteHasChanged = true;
              this.beforeEditCache = note.title;
              this.editedTodo = note;
            }
          },

          mergeCells: function(note, index){
            let mergingNote = this.notes[index +1];
            if(!note.title.startsWith('>') && !mergingNote.title.startsWith('>')){
                //let newTitle = note.title.concat('\n' + mergingNote.title);
                let newTitle = note.title.concat('---' + mergingNote.title);
                let newMarked = marked.parse(newTitle.trim(),{ sanitize: false, breaks: true, gfm: true });
                note.title = newTitle;
                note.marked = newMarked;
                this.notes.splice(this.notes.indexOf(mergingNote), 1);
                this.noteHasChanged = true;
                this.saveNotes();
            }
          },

          doneEdit: function(note, e) {
            if (!this.editedTodo) {
              return;
            }
            if(((e.relatedTarget || {}).className) !== "edit"){
              this.editedTodo = null;
            }
            note.title = note.title.trim();
            note.marked = marked.parse(note.title.trim(),{ sanitize: false, breaks: true, gfm: true });
            if (!note.title) {
              this.removeTodo(note);
            } else {
                this.saveNotes();
            }
        },

          saveNotes: function() {
            document.getElementById("clickAudio").play();

            if (this.noteHasChanged === true){
                var fileName  =  this.currentTraceFile
                var isNewFile = false
                if (!fileName) {
                    if (!this.notes[0]) return;
                    fileName = this.notes[0].title.replaceAll('#','').trim().replaceAll(' ','_') + '.md'
                    this.currentTraceFile = fileName
                    isNewFile = true;
                }
                if (!fileName) {
                  return;
                }

                var traceDir = this.traceStack.join('___');
                var underDir = ''
                if (traceDir == '') traceDir = "___";
                if (fileName.indexOf('/')>-1) {
                    var underName = fileName.replaceAll('/','___')
                    underDir = '___'+underName.substring(0,underName.lastIndexOf('___'))
                    fileName = underName.substring(underName.lastIndexOf('___')+3)
                }

                this.$http.put('/context/'+this.currentProject+'/tracebook/'+traceDir+underDir+"/"+fileName,this.notes)
                    .then( response => {
                      console.log("File saved.")
                      this.messageFailure = null;
                      // if new file add to story list.
                      //if (isNewFile) {
                      //  this.traceFiles.push(this.currentTraceFile)
                      //}
                    }).catch((error) => {
                      console.log("File saving error."+error)
                      this.messageFailure = error;
                    });
                this.noteHasChanged = false;
            }
          },

          saveStory: function() {
            document.getElementById("clickAudio").play();
            if(this.noteHasChanged === true){
                // compute story content
                var traceFileContent = ''
                for (note of this.notes) {
                    traceFileContent += note.title.replaceAll('---', '')
                    if (note.commandResult ) traceFileContent += '\n' + '[result:' + note.commandResult +']]' + '\n'
                    if (note.commandFailure) traceFileContent +=  '\n' + '[failure:' + note.commandFailure + ']]' + '\n'
                    //if (note.title.startsWith('>') && note.title.endsWith('\n---\n')) {
                    if (!note.title.startsWith('#'))
                        traceFileContent += '\n' + '---' + '\n'
                    // }
                }
                console.log(traceFileContent)
                // compute file story name
                var fileName  =  this.currentTraceFile
                var isNewFile = false
                if (!fileName) {
                    if (!this.notes[0]) return;
                    fileName = this.notes[0].title.replaceAll('#','').trim().replaceAll(' ','_') + '.md'
                    this.currentTraceFile = fileName
                    isNewFile = true;
                }
                if (!fileName) {
                  return;
                }
                // add refsto to story
                var reftoLinks = ""
                for (refto in this.noterefsto) {
                    reftoLinks += '['+this.noterefsto[refto].label+']('+ this.noterefsto[refto].url + ')\n'
                }
                if (reftoLinks) {
                    reftoLinks = '---\n' + reftoLinks;
                    traceFileContent = traceFileContent + reftoLinks
                }
                // add goals to story
                var goalLinks = ""
                for (goal in this.notegoals) {
                    goalLinks += '['+this.notegoals[goal].label+']('+ this.notegoals[goal].url + ')\n'
                }
                if (goalLinks) {
                    //goalLinks = '## Goals\n' + goalLinks;
                    goalLinks += '---\n';
                    traceFileContent = goalLinks + traceFileContent
                }
                // add terms to story
                var termLinks = ""
                for (term in this.noteterms) {
                    termLinks += '['+this.noteterms[term].label+']('+ this.noteterms[term].url + ')\n'
                }
                if (termLinks) {
                    termLinks = '---\n' + termLinks;
                    traceFileContent = traceFileContent + termLinks
                }
                // send request
                var traceDir = this.traceStack.join('___');
                var underDir = ''
                if (traceDir == '') traceDir = "___";
                if (fileName.indexOf('/')>-1) {
                    var underName = fileName.replaceAll('/','___')
                    underDir = '___'+underName.substring(0,underName.lastIndexOf('___'))
                    fileName = underName.substring(underName.lastIndexOf('___')+3)
                }
            this.$http.put('/context/'+this.currentProject+'/tracesave/'+traceDir+underDir+"/"+fileName,traceFileContent)
                .then( response => {
                  this.messageFailure = null;
                  console.log("File saved.")
                  // if new file add to story list.
                  if (isNewFile) {
                    this.traceFiles.push(fileName)
                  }
                }).catch((error) => {
                  console.log("File saving error."+error)
                  this.messageFailure = error;
                });
            }
            this.noteHasChanged = false;
          },

          cancelEdit: function(note) {
            this.editedTodo = null;
            note.title = this.beforeEditCache;
          },

          removeCompleted: function() { this.notes = filters.active(this.notes); },

          parseNotes: function(serverNotes) {
              this.notes = serverNotes;
              // GAOLS AND REFS
              this.notegoals = [];
              this.noteterms = [];
              this.noterefsto = [];
              for (let i = 0; i < this.notes.length; i++) {
                  var note = this.notes[i];
//                  if (note.title.startsWith('[result:')) {
//                      note.commandResult = note.title;
//                      //note.title = '';
//                  } else
//                  if (note.title.startsWith('[failure:')) {
//                      note.commandFailure = note.title;
//                      //note.title = '';
//                  } else
                  if (note.title.startsWith('[goal:')) {
                      var goalLines = note.title.split('\n');
                      for (index in goalLines) {
                          var goalLine = goalLines[index];
                          var label = goalLine.substring(goalLine.indexOf("[")+1,goalLine.indexOf("]"));
                          var url = goalLine.substring(goalLine.indexOf("(")+1,goalLine.indexOf(")"));
                          if (label) {
                              var newgoal =  new Object();
                              newgoal.label = label;
                              newgoal.url = url;
                              this.notegoals.push(newgoal);
                          }
                      }
                      note.title = '';
                  } else
                  if (note.title.startsWith('[refto:')) {
                            var reftoLines = note.title.split('\n');
                            for (index in reftoLines) {
                                var reftoLine = reftoLines[index];
                                var label = reftoLine.substring(reftoLine.indexOf("[")+1,reftoLine.indexOf("]"));
                                var url = reftoLine.substring(reftoLine.indexOf("(")+1,reftoLine.indexOf(")"));
                                if (label) {
                                    var newrefto =  new Object();
                                    newrefto.label = label;
                                    newrefto.url = url;
                                    this.noterefsto.push(newrefto);
                                }
                            }
                            note.title = '';
                  } else
                  if (note.title.startsWith('[term:')) {
                            var termLines = note.title.split('\n');
                            for (index in termLines) {
                                var termLine = termLines[index];
                                var label = termLine.substring(termLine.indexOf("[")+1,termLine.indexOf("]"));
                                var url = termLine.substring(termLine.indexOf("(")+1,termLine.indexOf(")"));
                                if (label) {
                                    var newterm =  new Object();
                                    newterm.label = label;
                                    newterm.url = url;
                                    this.noteterms.push(newterm);
                                }
                            }
                            note.title = '';
                        } else {
                      note.marked = marked.parse(note.title, { sanitize: false, breaks: true, gfm: true });
                  }
                  this.notes[i] = note;
              }
              this.notes = this.notes.filter( anote => anote.title!='');
              // GOALNOTE
              if (this.goalId) {
                  var goalnote = { id: 0,
                        title: ' + goal '+this.goalId,
                        marked: '',
                        completed: false ,
                        coded: false };
                  goalnote.marked = marked.parse(goalnote.title, { sanitize: false, breaks: true, gfm: true });
                  if (this.notes.length == 0)  {
                    this.notes.push(goalnote);
                  }
                  if (this.notes[0].title.trim().length == 0) {
                    this.notes[0] = goalnote;
                  }
              }
          },

    },


});


