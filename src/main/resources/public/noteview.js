
var app = new Vue({
  el: '#app-notes',
  data: {
    notes: [],
    codes: [],
    spinning: false,
    command: '',
    commandResult: '',
    commandFailure: '',
    currentTrace: '',
    currentTraceFile: '',
    traceFileContent: '',
    settings: {},
    currentProject: '',
    noteIndex: null,
    noteHasChanged: false,
    hash: '',
  },


  created: function () {
    //http://localhost:7000/noteview.html?pr=prova4&tr=traces___root&tf=base.md
    this.command = new URLSearchParams(window.top.location.search);
    this.hash = window.location.hash
    this.currentProject = this.command.get('pr');
    this.currentTraceFile = this.command.get('tf');
    this.currentTrace = this.command.get('tr');
    if (this.currentTrace != null && this.currentTraceFile != null) {
      this.$http.get("/project/"+this.currentProject+"/tracebook/"+this.currentTrace+"/"+this.currentTraceFile)
        .then((response) => {
          this.notes = response.body;
          for (let i = 0; i < this.notes.length; i++) {
            var note = this.notes[i]
            note.marked = marked.parse(note.title, { sanitize: false, breaks: true, gfm: true });
            this.notes[i] = note
          }
        })
        .catch((response) => {
          console.log("NOT asked")
          console.log(response)
        });
    }

  },

  computed: {
  },

  methods: {
  },

  directives: {
  }

});

// /noteview.html?pr=${project.name}&tr=${task.traceName}&tf=${task.storyFileName}
function linked(text) {
    if (text.includes('[[')) {
      before = text.substring(0,text.indexOf('[['))
      anchor = text.substring(text.indexOf('[[')+2,text.indexOf(']]'))
      after = text.substring(text.indexOf(']]')+2)
      return before + '[' + anchor + ']('+anchor+'.md)' + after
    }
    return text.toUpperCase()
}
