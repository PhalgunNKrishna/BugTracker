var app = angular.module("User", []);

app.controller("UsersController", function($scope, $http) {

    $http.get("http://localhost:7070/user/all")
    .success(function(result) {
        $scope.tickets = result.tickets;
    });

    $scope.view = function(tid) {
        $http.get("http://localhost:7070/user/" + tid)
        .success(function(p) {
            $scope.p = p;
            console.log("page");
        });
    };

});
