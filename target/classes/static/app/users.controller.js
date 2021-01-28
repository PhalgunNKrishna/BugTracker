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
            //var mess = JSON.parse(p);
            const mess = JSON.parse(JSON.stringify(p));
            var admin_message = mess.admin_message;

            console.log("admin message = " + admin_message);

            if (admin_message.length > 40) {
                split_admin = admin_message.split(" ");
                var count = 0;
                var res = "";
                for (var i = 0; i < split_admin.length; i++) {
                    while (count < 40) {
                        res.concat(split_admin[i]);
                        res.concat(" ");
                        console.log("res = " + res);
                        count = count + split_admin[i].length + 1;
                        console.log("res = " + res);
                     } res.concat("<br>");
                     count = 0;
                }
           } else {
                var res = admin_message;
           }

           console.log(res);

           $scope.ad_mess = res;

        });
    };

});
