/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */

(function () {
    'use strict';
    app.controller('DashboardController',
        function($state, $scope, $rootScope, $q, $stateParams, dashboardService, $mdToast, sampleData, ytdRuleHits, ytdAirportStats) {

            $scope.sampleData = sampleData;
            $scope.switchDashboard= function(input){
                //$scope.sampleData = !$scope.sampleData;
                $state.go(input);
            }
            $scope.numberOfFlights = 0;
            $scope.numberOfRuleHits = 0;
            $scope.numberOfWatchListHits = 0;
            $scope.numberOfPassengers = 0;
            $scope.numberOfApisMessages = 0;
            $scope.numberOfPNRMessages = 0;
            $scope.rulesList = ytdRuleHits;
            $scope.airportStats = ytdAirportStats;
            $scope.credentials = {
                beforeDate: '',
                startDate: '',
                endDate: ''
            };
            //
            var d = new Date();
            var n = d.toString();

            var today = moment();
            var tomorrow = moment(today).add(3, 'day');
            var yesterday = moment(today).add(-1, 'day');

            $scope.credentials.startDate = today.format('YYYY-MM-DD');
            $scope.credentials.endDate = tomorrow.format('YYYY-MM-DD');
            $scope.credentials.beforeDate = yesterday.format('YYYY-MM-DD');

            $scope.getFlightsAndPassengersAndHitsCount = function (credentials) {
            	// two arguments were used before but get date info from server side now.
                dashboardService.getFlightsAndPassengersAndHitsCount(credentials.startDate,credentials.endDate ).then(function (data){
                    $scope.numberOfFlights = data.data.flightsCount;
                    $scope.numberOfRuleHits = data.data.ruleHitsCount;
                    $scope.numberOfWatchListHits = data.data.watchListCount;
                    $scope.numberOfPassengers = data.data.passengersCount;
                });
            };

            $scope.getFlightsAndPassengersAndHitsCount($scope.credentials);


            $scope.getMessagesCount = function (credentials) {

            var jsonData =  dashboardService.getMessagesCount(credentials.beforeDate,credentials.startDate ).then(function (data){
                    //$scope.numberOfApisMessages = data.data.apisMessageCount;
                    //$scope.numberOfPNRMessages = data.data.pnrMessageCount;
                });
                return jsonData;
            };
            //$scope.getMessagesCount($scope.credentials);

          });

}());

