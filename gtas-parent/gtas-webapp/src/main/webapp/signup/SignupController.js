/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function() {
  "use strict";
  app.controller("SignupController", function(
    $state,
    $scope,
    $rootScope,
    $q,
    $stateParams,
    userService,
    $mdToast,
    SignupService,
    Session,
    sessionFactory,
    APP_CONSTANTS,
    USER_ROLES,
    $sessionStorage,
    $location,
    $interval,
    $window,
    $translate,
    $cookies,
    $mdDialog,
    Idle,
    configService,
    $sce
  ) {
    //Set locale here to change language setting for web site
    $translate.refresh($cookies.get(APP_CONSTANTS.LOCALE_COOKIE_KEY));

    var setMessage = function(message) {
      $scope.message = $sce.trustAsHtml(message);
    };

    setMessage("");

    $scope.credentials = {
      username: "",
      email: "",
      physicalLocationId: "",
      supervisor: ""
    };

    var formProcessing = function(processing) {
      $scope.submitted = processing;
    };

    formProcessing(false);
    $scope.submitButtonLabel = "Submit";

    $scope.clearSessionCookie = function() {
      var cookies = $cookies.getAll();
      angular.forEach(cookies, function(v, k) {
        $cookies.remove(k);
      });
    };

    SignupService.physicalLocations()
      .success(function(data, status) {
        $scope.physical_locations = data.map(p => {
          var location = {};
          (location["id"] = p.id), (location["label"] = p.name);

          return location;
        });
      })
      .error(function(data, status) {
        console.log("error processing request");
      });

    $scope.signup = function(credentials) {
      formProcessing(true);
      setMessage("Please wait, We are processing your sign up request.");
      SignupService.signup(credentials)
        .success(function(data, status) {
          if (data) {
            $scope.errorMessage = data.message;
            setMessage($scope.errorMessage);
            formProcessing(false);
          } else {
            formProcessing(true);
            setMessage(
              "Your sign up request has been submitted successfully. <br><br> Once it is reviewed and approved, you will receive a temporary password to login into GTAS. <br><br> -Thank you."
            );
          }
          if (data == "ok" && status != 401) {
            $rootScope.signedup = true;
          }
        })
        .error(function(data, status) {
          formProcessing(false);
          $rootScope.signedup = false;
          $location.path("/error");
        });
    };
  });
})();
