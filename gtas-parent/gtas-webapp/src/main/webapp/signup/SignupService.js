/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function() {
  "use strict";
  app.factory("SignupService", function(
    $http,
    Session,
    $rootScope,
    $mdToast,
    APP_CONSTANTS,
    $location,
    $cookies
  ) {
    var signupService = {};

    $http.defaults.xsrfHeaderName = "X-CSRF-TOKEN";
    $http.defaults.xsrfCookieName = "CSRF-TOKEN";
    var csrfToken = $cookies.get("CSRF-TOKEN");
    var sessionId = "";

    signupService.getCurrentUser = function() {
      return $http.get("/gtas/user").then(function(response) {
        return response.data;
      });
    };

    signupService.physicalLocations = function() {
      return $http({
        method: "GET",
        url: "/gtas/user/signup/physiclLocations",
        headers: {
          JSESSIONID: "" + sessionId,
          "X-CSRF-TOKEN": "" + csrfToken,
          "Content-Type": "application/json",
          "X-Login-Ajax-call": "true"
        }
      });
    };

    signupService.signup = function(credentials) {
      var postData = JSON.stringify(credentials);
      sessionId = $cookies.get("JSESSIONID");

      return $http({
        method: "POST",
        url: "/gtas/user/signup/new",
        data: postData,
        headers: {
          JSESSIONID: "" + sessionId,
          "X-CSRF-TOKEN": "" + csrfToken,
          "Content-Type": "application/json",
          "X-Login-Ajax-call": "true"
        }
      });
    };

    signupService.getAllNewSignupRequests = function() {
      return $http({
        method: "GET",
        url: "/gtas/user/allNewSignupRequests",
        headers: {
          "Content-Type": "application/json",
          "X-Login-Ajax-call": "true"
        }
      });
    };

    signupService.approveSignupRequest = function(data) {
      return $http({
        method: "POST",
        url: "/gtas/signupRequest/approve",
        data: JSON.stringify(data),
        headers: {
          "Content-Type": "application/json",
          "X-Login-Ajax-call": "true"
        }
      });
    };

    signupService.rejectSignupRequest = function(data) {
      return $http({
        method: "POST",
        url: "/gtas/signupRequest/reject",
        data: JSON.stringify(data),
        headers: {
          "Content-Type": "application/json",
          "X-Login-Ajax-call": "true"
        }
      });
    };

    signupService.isAuthenticated = function() {
      return $rootScope.authenticated;
      // return !!Session.userId;
    };

    signupService.isAuthorized = function(authorizedRoles) {
      if (!angular.isArray(authorizedRoles)) {
        authorizedRoles = [authorizedRoles];
      }
      return (
        signupService.isAuthenticated() &&
        authorizedRoles.indexOf(Session.userRoles) !== -1
      );
    };

    return signupService;
  });

  app.service("Session", function() {
    this.create = function(firstName, userId, userRoles) {
      this.firstName = firstName;
      this.userId = userId;
      this.userRoles = userRoles;
    };
    this.destroy = function() {
      this.firstName = null;
      this.userId = null;
      this.userRoles = null;
    };
  });
})();
