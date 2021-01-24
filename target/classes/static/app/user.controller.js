(function () {
    'use strict';

     angular
        .module('app')
        .controller('UserController', UserController);

     UserController.$inject = ['$http'];

     function UserController($http) {
        var vm = this;

        vm.tickets = [];
        vm.getAllTickets = getAll;

        init();

        function init() {
            getAll();
        }

        function getAll() {
            var url = "/user/all";
            var ticketsPromise = $http.get(url);
            ticketsPromise.then(function(response) {
                vm.tickets = response.data;
                console.log("yes")
            })
            //.catch(function(err){console.log("nope")});
            .catch(function(err){console.log("no")});
        }
     }
})();

