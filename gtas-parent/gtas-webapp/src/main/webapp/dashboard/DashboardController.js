/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */

(function () {
    'use strict';
    app.controller('DashboardController',
        function($state, $scope, $rootScope, $q, $stateParams, dashboardService, $mdToast, sampleData, ytdRuleHits, ytdAirportStats) {

            var stubChartData = [[],[]];

            //$scope.data = stubChartData;

          //  $scope.refreshAPIPNRStats = function(data){
                $scope.colors = ['#337ab7', '#5cb85c', '#dfdfdf'];

                $scope.labels = ['12 - 1 AM', '1 - 2 AM', '2 - 3 AM', '3 - 4 AM', '4 - 5 AM', '5 - 6 AM', '6 - 7 AM', '7 - 8 AM', '8 - 9 AM'
                    , '9 - 10 AM', '10 - 11 AM', '11 - 12 PM', '12 - 1 PM', '1 - 2 PM', '2 - 3 PM', '3 - 4 PM', '4 - 5 PM'
                    , '5 - 6 PM', '6 - 7 PM', '7 - 8 PM', '8 - 9 PM', '9 - 10 PM', '10 - 11 PM', '11 - 12 AM'];

                // $scope.data = [
                //     [65, 59, 80, 81, 0, 55, 40, 65, 59, 80, 0, 0, 55, 40, 65, 59, 80, 81, 56, 55, 40, 33, 23, 55],
                //     [28, 48, 40, 19, 86, 27, 90, 28, 48, 40, 0, 86, 27, 90, 0, 48, 40, 19, 86, 27, 90, 21, 99, 77]
                // ];

                $scope.data = [[],[]];

                $scope.datasetOverride = [
                    {
                        label: "APIs",
                        borderWidth: 1,
                        type: 'bar'
                    },
                    {
                        label: "PNR",
                        borderWidth: 3,
                        hoverBackgroundColor: "rgba(255,99,132,0.4)",
                        hoverBorderColor: "rgba(255,99,132,1)",
                        type: 'line'
                    }
                ];
           // } // END of refresh Stats

            //$scope.refreshAPIPNRStats(stubChartData);

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

                // process Promise
                var ople = Object.keys(data);
                var apiARR = [], pnrARR = [];

                ople.forEach(function(num_ber) {
                    var items = Object.keys(data[num_ber]);
                    items.forEach(function(item) {
                        if(item === 'api'){
                            apiARR.push(data[num_ber][item]);
                        } else if(item === 'pnr'){
                            pnrARR.push(data[num_ber][item].trim());
                        }
                        //var value = data[num_ber][api];
                        //var valuePNR = data[num_ber][pnr];
                        //console.log(num_ber+': '+item+' = '+value);
                    }); // end of items forEach
                    //console.log('api array '+apiARR.join());
                }); // end outer forEach
                // end process Promise

                stubChartData[0] = [];
                stubChartData[1] = [];
                JSON.parse(JSON.stringify(apiARR), (key, value) => {
                    stubChartData[0].push(value); // log the current property name, the last is "".
                //return parseInt(value);     // return the unchanged property value.
                });

                JSON.parse(JSON.stringify(pnrARR), (key, value) => {
                        stubChartData[1].push(value); // log the current property name, the last is "".
                //return parseInt(value);     // return the unchanged property value.
                 });


                 $scope.data = stubChartData;
               // $scope.refreshAPIPNRStats(stubChartData);
                });
                return jsonData;
            };
            $scope.getMessagesCount($scope.credentials);


            $scope.$on('chart-update', function (evt, chart) {
                console.log('>>> Updated >>>' + chart);
            });

          });



}());

