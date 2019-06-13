/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.controller('UserSettingsController', function ($scope, $state, $interval,$http, $stateParams,user,userService, $location, flightsModel,filterService,$mdToast, codeService) {

  $scope.userCredentials = {
      firstName: '',
      lastName: '',
      newPassword:'',
      confirmPassword:''
  };
  $scope.updateCredentials=function()
  {
    $scope.success=false;
    $scope.error=false;
    
      user.data.firstName=$scope.userCredentials.firstName;
      user.data.lastName=$scope.userCredentials.lastName;
      user.data.password=$scope.userCredentials.newUserPassword;
      userService.updateUser(user.data).
          then(
          function (result) {
            if(result.status === "SUCCESS")
            {
               $scope.successMessage="Your credentials were updated successfully." ;
                   $scope.success=true;
            }
            else if(result.status === "FAILURE" || result.data.status === "undefined")
            {
               $scope.successMessage="Your credentials were not updated due to errors." ;
                   $scope.error=true;
            }
             
          }
      );
  };
  init=function()
  {
      $scope.userCredentials.firstName=user.data.firstName;
      $scope.userCredentials.lastName=user.data.lastName;
     $scope.userCredentials.newPassword=user.data.password;
     $scope.userCredentials.confirmPassword=user.data.password;
      $scope.success=false;
      $scope.error=false;
  };
  init();
  
  $scope.hideToast = function(){
    $mdToast.hide();
  };
  
  $scope.displayPasswordRules = function(){
    $mdToast.show({
      hideDelay   : 0,
          position    : 'top right',
          ok:"OK",
          template    : '<md-toast style="height:100%;margin-top:160px;"><div class="md-toast-content" style="height:100%">Password Criteria:'+
        '<ul><li>10 to 20 characters</li>'+
        '<li>At least one special character (!@#$%^&*)</li>'+
        '<li>At least one number</li>'+
        '<li>At least one letter</li>'+
        '<li>At least one upper case character</li>'+
        '<li>At least one lower case character</li>'+
        '</ul></div></md-toast>'
     });
  };
  

  //---------------------------------------- Filter JS -----------------------

  var toastPosition = angular.element(document.getElementById('filterForm'));
  var self = this, airports, 
          userData = function (user) {
          $scope.user = user.data;      $scope.filter.userId=user.data.userId;
          
          if(user.data.filter!=null) {
              if(user.data.filter.originAirports) {
                  $scope.filter.originAirports=user.data.filter.originAirports;
                  }
              if(user.data.filter.destinationAirports)    {
                 $scope.filter.destinationAirports=user.data.filter.destinationAirports;
                  }
              if(typeof user.data.filter.etaStart  != undefined && user.data.filter.etaStart != null)
                  $scope.filter.etaStart=user.data.filter.etaStart;
              if(typeof user.data.filter.etaEnd  != undefined && user.data.filter.etaEnd != null)
                  $scope.filter.etaEnd=user.data.filter.etaEnd;
              if(user.data.filter.flightDirection)
                  $scope.filter.flightDirection=user.data.filter.flightDirection;
              mapAirports();
          }
      },
      initFilter=function() {
          $scope.success=false;
          $scope.error=false;
          userService.getUserData().then(userData);
      },
     
      alert = function (content) {
          $mdToast.show(
              $mdToast.simple().content(content).position("top right").hideDelay(4000).parent(angular.element(document.getElementById('filterForm')))
          );
      },
      filterCreated  = function (filter) { alert('Filter has been saved');},
      filterUpdated  = function (filter) { alert('Filter has been updated'); };
      
  $scope.origins=[];
  $scope.destinations=[];   

  $scope.flightDirections = [
      {label: 'Inbound', value: 'I'},
      {label: 'Outbound', value: 'O'},
      {label: 'Any', value: 'A'}
  ];

  $scope.etaStartDays = [
      {label: 'Today', value: '0'},
      {label: 'Today-1', value: '-1'},{label: 'Today-2', value: '-2'}, {label: 'Today-3', value: '-3'},
      {label: 'Today-4', value: '-4'},{label: 'Today-5', value: '-5'}, {label: 'Today-6', value: '-6'},
      {label: 'Today-7', value: '-7'},{label: 'Today-8', value: '-8'}, {label: 'Today-9', value: '-9'},
      {label: 'Today-10', value: '-10'}
  ];

  $scope.etaEndDays = [
      {label: 'Today', value: '0'},
      {label: 'Today+1', value: '1'},{label: 'Today+2', value: '2'}, {label: 'Today+3', value: '3'},
      {label: 'Today+4', value: '4'},{label: 'Today+5', value: '5'}, {label: 'Today+6', value: '6'},
      {label: 'Today+7', value: '7'},{label: 'Today+8', value: '8'}, {label: 'Today+9', value: '9'},
      {label: 'Today+10', value: '10'}
  ];

  $scope.filter = {
      userId:'', originAirports: [], destinationAirports: [], flightDirection:flightsModel.direction,  etaStart:-1,etaEnd:1
  };
  $scope.setFilter=function() {
      populateAirports();
      if($scope.user.filter==null ) {
          filterService.setFilter($scope.filter, $scope.user.userId).
          then(filterCreated);
      }
      else {
          filterService.updateFilter($scope.filter, $scope.user.userId).then(filterUpdated);
      }
  };

 

  initFilter();
  function createFilterFor(query) {
      var lowercaseQuery = query.toLowerCase();
      return function filterFn(contact) {
          return (contact.lowerCasedName.indexOf(lowercaseQuery) >= 0);
      };
  };
  
  $scope.querySearch = function(query){
      var results = query && (query.length) && (query.length >= 3) ? self.allAirports.filter(createFilterFor(query)) : [];
      return results;
  };
  
  var populateAirports = function(){
      var originAirports = new Array();
      var destinationAirports = new Array();
      angular.forEach($scope.origins,function(value,index){
          originAirports.push(value.id);
      });
      angular.forEach($scope.destinations,function(value,index){
          destinationAirports.push(value.id);
      });
      $scope.filter.originAirports = originAirports;
      $scope.filter.destinationAirports = destinationAirports;
  };
  
  var mapAirports = function(){

      var originAirports = new Array();
      var destinationAirports = new Array();
      var airport = {  id: "" };
      angular.forEach($scope.filter.originAirports,function(value,index){
          originAirports.push({ id: value  });
      });
      angular.forEach($scope.filter.destinationAirports,function(value,index){
          destinationAirports.push({id: value});
      });
      $scope.origins= originAirports;
      $scope.destinations = destinationAirports;
  };
  
  // $http.get('data/airports.json')
  codeService.getAirportTooltips()
      .then(function (allAirports) {
          self.allAirports = allAirports.map(function (contact) {
              contact.lowerCasedName = contact.id.toLowerCase();
              return contact;
          });
          $scope.filterSelected = true;
      });
  
 
});
