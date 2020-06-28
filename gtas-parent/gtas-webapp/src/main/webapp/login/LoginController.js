/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app.controller('LoginController',
        function($state, $scope, $rootScope, $q, $stateParams, userService, $mdToast, AuthService,
                 Session, sessionFactory, APP_CONSTANTS, USER_ROLES, $sessionStorage, $location, $interval,
                 $window, $translate, $cookies, $mdDialog, Idle, configService) {
            //Insure Idle is not watching pre-login
            if(Idle.running()){
                Idle.unwatch();
            }
            //Inform user if they timed out
            if(window.location.search.replace("?", "") === "userTimeout"){
                $mdDialog.show(
                        $mdDialog.alert()
                            .clickOutsideToClose(false)
                            .textContent("Your session has timed out. For security reasons you have been logged out automatically.")
                            .ariaLabel("User Timeout")
                            .ok("OK")
                            .openFrom({
                                left: 1500
                            })
                            .closeTo(({
                                right: 1500
                            }))
                    );
            }
            
            //Set locale here to change language setting for web site
            $translate.refresh($cookies.get(APP_CONSTANTS.LOCALE_COOKIE_KEY));
            $scope.currentUser = {};
            $scope.credentials = {
                j_username: '',
                j_password: ''
            };
            
            $('#user_login').prop('disabled',false);
            $('#user_pass').prop('disabled',false);
            
            
             $scope.clearSessionCookie = function(){
                var cookies = $cookies.getAll();
                angular.forEach(cookies, function (v, k) {
                    $cookies.remove(k);
                });
            };

            $scope.login = function (credentials) {
                AuthService.login(credentials).then(function (user) {
                        if (user.status == 405) {
                            $scope.clearSessionCookie();
                            AuthService.login(credentials).then(
                                function(user){
                                    if (user.status == 405) {
                                        $scope.clearSessionCookie();
                                        AuthService.login(credentials).then(function (user) {
                                            Idle.watch();
                                            if (user.status == 405) {
                                                AuthService.login(credentials).then(
                                                    function(user){
                                                        Idle.watch();
                                                        if ($rootScope.authenticated) {
                                                            AuthService.getCurrentUser().then(function (user) {
                                                                $scope.currentUser.data = user;
                                                            });
                                                        }
                                                    }
                                                );
                                            }
                                        });
                                    }
                                    else if ($rootScope.authenticated) {
                                        AuthService.getCurrentUser().then(function (user) {
                                            $scope.currentUser.data = user;
                                        });
                                    }
                                }
                            );
                        }
                        else if ($rootScope.authenticated) {
                            AuthService.getCurrentUser().then(function (user) {
                                $scope.currentUser.data = user;
                            });
                        }
                    })
                }; // END of AuthService Call

             // END of LOGIN Function
            $scope.$watch('currentUser.data', function (user) {

            	if (angular.isDefined(user)) {
                    console.log("$scope.currentUser has data");
                    Session.create(user.firstName, user.lastName, user.userId,
                        user.roles);
                    $sessionStorage.put(APP_CONSTANTS.CURRENT_USER, user);
                    //window.location.href = APP_CONSTANTS.HOME_PAGE;
                    let oneDayLookoutUser = false;
                    user.roles.forEach(function (role) {
                        if (role.roleDescription === USER_ROLES.ONE_DAY_LOOKOUT) {
                            oneDayLookoutUser = true;
                        }
                    });
                    if (oneDayLookoutUser) {
                        $window.location.href = APP_CONSTANTS.ONE_DAY_LOOKOUT;
                        $scope.homePage = "onedaylookout";
                    } else {
                        configService.defaultHomePage().then(function success(response){
                    		 $scope.homePage = response.data;
                    		 $window.location.href = 'main.html#/'+$scope.homePage;
                        }, function errorMessage(error){
                            $scope.homePage="flights";
                            $window.location.href = 'main.html#/'+$scope.homePage;
                        });
                    }
                }
            });

        });
    }());
