var app = new Vue({

el: '#app',
    data: {
        textToSearch: '',
        searchResult: [],
        messageFailure: '',
        message: 'Hello Vue! <a href="http://www.google.com">Google</a>'
    },
    created: function () {
    },
    computed: {
        hasResult: function() {
            return this.searchResult.length > 0;
        },
    },
    methods: {
          clickOnSearch: function() {
              this.$http.get("/search/"+this.textToSearch)
              .then((response) => {
                  this.searchResult=response.body;
              })
              .catch((response) => {
                  this.messageFailure = response.body;
                  console.log(response);
              });
          }
    }

})
