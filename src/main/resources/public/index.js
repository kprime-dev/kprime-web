var appIndex = new Vue({
    el: '#app-index',

    data: {
        currentUser: '',
        currentProject: '',
        avatarUrl: '',
        contextLabels: [],
        contextLogoUrl: '',
        contextGID: '',
    },

    created: function () {
        this.$http.get('/indexModel').then(response => {
            this.avatarUrl = response.body.avatarUrl;
            this.currentUser = response.body.currentUser;
            this.currentProject = response.body.currentProject;
            this.contextLabels = response.body.contextLabels;
            this.contextLogoUrl = response.body.contextLogoUrl;
            this.contextGID = response.body.contextGID;
        });
    },

    computed: {
        hasAvatarUrl: function() {
            return this.avatarUrl;
        },
        hasCurrentUser: function() {
            return this.currentUser;
        },
        hasCurrentProject: function() {
            return this.currentProject;
        }
    }

});