/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app.factory('AuthService', function ($http, Session, $rootScope, $mdToast, APP_CONSTANTS, $location, $cookies, $window) {
        var authService = {};

        $http.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';
        $http.defaults.xsrfCookieName = 'CSRF-TOKEN';
        var csrfToken = $cookies.get('CSRF-TOKEN');
        var sessionId = '';

        authService.getCurrentUser = function() {
            return $http.get("/gtas/user").then(function (response) {
                return response.data;
            });
        };

        var preparePostData = function (credentials) {
            var username = credentials.j_username !== undefined ? credentials.j_username.toUpperCase() : '';
            var password = credentials.j_password !== undefined ? credentials.j_password : '';

            return 'username=' + username
                + '&password=' + encodeURIComponent(password);
        };

        var getResetToken = function () {
            var urlParams = new URLSearchParams(window.location.search);
            return urlParams.get('resetParams') !== undefined ? urlParams.get('token') : '';
        }

        authService.reset = function (payload) {
            sessionId = $cookies.get("JSESSIONID");
            payload['resetToken'] = getResetToken();

            var request =  $http({
                method: 'POST',
                url: '/gtas/password-reset',
                data: payload,
                headers: {
                    "JSESSIONID":""+sessionId,
                    "X-CSRF-TOKEN" : ""+csrfToken,
                    "Content-Type": "application/json",
                    "X-Login-Ajax-call": 'true'
                }
            });

            request.then(function (response) {
                var toastPosition = angular.element(document.getElementById('resetForm'));
                $mdToast.show(
                    $mdToast.simple()
                        .content(response.data.message)
                        .position('top right')
                        .hideDelay(4000)
                        .parent(toastPosition));

                if (response.data.status === 'SUCCESS') {
                    $window.location.href = '/gtas/login.html';
                }
            });
        };

        authService.login = function (credentials) {
            var postData = preparePostData(credentials);
            sessionId = $cookies.get("JSESSIONID");

            return $http({
                method: 'POST',
                url: '/gtas/authenticate',
                data: postData
                ,
                headers: {
                    "JSESSIONID":""+sessionId,
                    "X-CSRF-TOKEN" : ""+csrfToken,
                    "Content-Type": "application/x-www-form-urlencoded",
                    "X-Login-Ajax-call": 'true'
                }
            })
                .success(function (data, status) {

                        if (data == 'ok' && status != 401) {
                            $rootScope.authenticated = true;
                        }
                        else {
                            if (status != 405){
                                var toastPosition = angular.element(document.getElementById('loginForm'));
                                var errorMessage = APP_CONSTANTS.LOGIN_ERROR_MSG;
                                if(status === 406) {
                                    errorMessage = APP_CONSTANTS.LOGIN_ERROR_MAX_ATTEMPTS;
                                }
                                $mdToast.show(
                                    $mdToast.simple()
                                        .content(errorMessage)
                                        .position('top right')
                                        .hideDelay(4000)
                                        .parent(toastPosition));
                            }
                        }

                }).error(function (data, status) {
                    $rootScope.authenticated = false;
                    $location.path('/error');

                });
        }

        authService.isAuthenticated = function () {
            return $rootScope.authenticated;
            //return !!Session.userId;
        };

        authService.isAuthorized = function (authorizedRoles) {
            if (!angular.isArray(authorizedRoles)) {
                authorizedRoles = [authorizedRoles];
            }
            return (authService.isAuthenticated() &&
            authorizedRoles.indexOf(Session.userRoles) !== -1);
        };

        return authService;
    });

    app.service('Session', function () {
        this.create = function (firstName, userId, userRoles) {
            this.firstName = firstName;
            this.userId = userId;
            this.userRoles = userRoles;
        };
        this.destroy = function () {
            this.firstName = null;
            this.userId = null;
            this.userRoles = null;
        };
    });

}());