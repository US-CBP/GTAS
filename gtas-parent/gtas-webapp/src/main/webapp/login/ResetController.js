(function () {
    'use strict';
    app.controller('ResetController', function($scope, AuthService) {
        $scope.credentials = {
            username: '',
            password: '',
            passwordConfirm: '',
            resetToken: ''
        };

        $('#user_login').prop('disabled',false);
        $('#user_pass').prop('disabled',false);
        $('#pass_confirm').prop('disabled',false);

        $scope.reset = function (credentials) {
            AuthService.reset(credentials);
        }
    })
}());