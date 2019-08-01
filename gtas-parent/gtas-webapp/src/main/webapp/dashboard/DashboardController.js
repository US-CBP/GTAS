/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */

(function () {
    'use strict';
    app.controller('DashboardController',
        function ($state, $scope, $rootScope, $q, $sce, $stateParams, dashboardService, $mdToast, sampleData, ytdRuleHits, ytdAirportStats, configService) {
            var stubChartData = [[], []];
            $scope.colors = ['#337ab7', '#5cb85c', '#dfdfdf'];
            $scope.data = [[], []];
            $scope.dashUrl = "http://localhost:5601/app/kibana#/dashboard/7cfbbdc0-2e13-11e9-81a3-0f5bd8b0a7ac?embed=true&_g=(refreshInterval%3A(pause%3A!t%2Cvalue%3A0)%2Ctime%3A(from%3Anow-3d%2Fd%2Cmode%3Arelative%2Cto%3Anow%2B3d%2Fd))";
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

            configService.kibanaUrl().then(function(value) {
                $scope.dashUrl = value.data;
            });

            $scope.getKibanaUrl = function() {
                return $sce.trustAsResourceUrl($scope.dashUrl);
            };

            $scope.sampleData = sampleData;
            $scope.switchDashboard = function (input) {
                // $scope.sampleData = !$scope.sampleData;
                $state.go(input);
            }
            $scope.numberOfFlights = 0;
            $scope.numberOfFlightsInbound = 0;
            $scope.numberOfFlightsOutbound = 0;
            $scope.numberOfRuleHits = 0;
            $scope.numberOfRuleHitsInbound = 0;
            $scope.numberOfRuleHitsOutbound = 0;
            $scope.numberOfWatchListHits = 0;
            $scope.numberOfWatchListHitsInbound = 0;
            $scope.numberOfWatchListHitsOutbound = 0;
            $scope.numberOfPassengers = 0;
            $scope.numberOfPassengersInbound = 0;
            $scope.numberOfPassengersOutbound = 0;
            $scope.numberOfApisMessages = 0;
            $scope.numberOfPNRMessages = 0;
            $scope.flightsList = [];
            $scope.flightsListInbound = [];
            $scope.flightsListOutbound = [];
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

            var arrOfLats = [];
            var arrOfLongs = [];
            var imagesArrOfAirports = [];
            var anchorLat = [38.944533];
            var anchorLong = [-77.455811];


            // ------------------- AM CHARTS NEW SECTION ------------------

            /**
             * SVG path for target icon
             */
            var targetSVG = "M9,0C4.029,0,0,4.029,0,9s4.029,9,9,9s9-4.029,9-9S13.971,0,9,0z M9,15.93 c-3.83,0-6.93-3.1-6.93-6.93S5.17,2.07,9,2.07s6.93,3.1,6.93,6.93S12.83,15.93,9,15.93 M12.5,9c0,1.933-1.567,3.5-3.5,3.5S5.5,10.933,5.5,9S7.067,5.5,9,5.5 S12.5,7.067,12.5,9z";

            /**
             * SVG path for plane icon
             */
            var planeSVG = "M19.671,8.11l-2.777,2.777l-3.837-0.861c0.362-0.505,0.916-1.683,0.464-2.135c-0.518-0.517-1.979,0.278-2.305,0.604l-0.913,0.913L7.614,8.804l-2.021,2.021l2.232,1.061l-0.082,0.082l1.701,1.701l0.688-0.687l3.164,1.504L9.571,18.21H6.413l-1.137,1.138l3.6,0.948l1.83,1.83l0.947,3.598l1.137-1.137V21.43l3.725-3.725l1.504,3.164l-0.687,0.687l1.702,1.701l0.081-0.081l1.062,2.231l2.02-2.02l-0.604-2.689l0.912-0.912c0.326-0.326,1.121-1.789,0.604-2.306c-0.452-0.452-1.63,0.101-2.135,0.464l-0.861-3.838l2.777-2.777c0.947-0.947,3.599-4.862,2.62-5.839C24.533,4.512,20.618,7.163,19.671,8.11z";

            /**
             * Create the map
             */

            var map = AmCharts.makeChart("chartdiv", {
                "type": "map",
                "theme": "light",
                "dataProvider": {
                    "map": "worldLow",
                    "linkToObject": "WashDC",
                    "images": [{
                        "id": "WashDC",
                        "color": "#000000",
                        "svgPath": targetSVG,
                        "title": "IAD",
                        "latitude": anchorLat[0],
                        "longitude": anchorLong[0],
                        "scale": 1.5,
                        "zoomLevel": 2.74,
                        "zoomLongitude": -20.1341,
                        "zoomLatitude": 49.1712,

                        "lines": [],

                        "images": [{
                            "label": "Flights To IAD",
                            "svgPath": planeSVG,
                            "left": 100,
                            "top": 45,
                            "labelShiftY": 5,
                            "color": "#D35400",
                            "labelColor": "#D35400",
                            "labelRollOverColor": "#D35400",
                            "labelFontSize": 20
                        }
                        ]

                    } // end of "images" object
                        , {

                        }
                    ] // end of "images" array
                },

                "areasSettings": {
                    "unlistedAreasColor": "#616A6B"
                },

                "imagesSettings": {
                    "color": "#D35400",
                    "rollOverColor": "#273746",
                    "selectedColor": "#000000"
                },

                "linesSettings": {
                    "color": "#D35400",
                    "alpha": 0.9
                },

                "balloon": {
                    "drop": true
                },

                "backgroundZoomsToTop": true,
                "linesAboveImages": true,

                "export": {
                    "enabled": false
                }
            });

            $scope.getFlightsAndPassengersAndHitsCount = function (credentials) {

                // inbound flights request
                dashboardService.getFlightsAndPassengersAndHitsCountInbound(credentials.startDate, credentials.endDate).then(function (data) {
                    $scope.numberOfFlightsInbound = data.data.flightsCount;
                    $scope.numberOfRuleHitsInbound = data.data.ruleHitsCount;
                    $scope.numberOfWatchListHitsInbound = data.data.watchListCount;
                    $scope.numberOfPassengersInbound = data.data.passengersCount;
                    $scope.flightsListInbound = data.data.flightsList;

                    if($scope.flightsListInbound) {
                      $scope.flightsListInbound.forEach(function(elem){
                      if(elem.hits)
                      {
                        map["dataProvider"]["images"].push({
                            "svgPath": targetSVG,
                            "title": elem.airportCodeStr,
                            "color": "#CC0000",
                            "latitude": elem.latitude,
                            "longitude": elem.longitude
                        })
                      }
                      else
                        {
                          map["dataProvider"]["images"].push({
                              "svgPath": targetSVG,
                              "color": "#273746",
                              "title": elem.airportCodeStr,
                              "latitude": elem.latitude,
                              "longitude": elem.longitude
                          })
                        }

                        // )
                    });
                    }
                    // map.validateData();
                }); // end of dashboard service

                // outbound flights request
                dashboardService.getFlightsAndPassengersAndHitsCountOutbound(credentials.startDate, credentials.endDate).then(function (data) {
                    $scope.numberOfFlightsOutbound = data.data.flightsCount;
                    $scope.numberOfRuleHitsOutbound = data.data.ruleHitsCount;
                    $scope.numberOfWatchListHitsOutbound = data.data.watchListCount;
                    $scope.numberOfPassengersOutbound = data.data.passengersCount;
                    $scope.flightsListOutbound = data.data.flightsList;

                    if($scope.flightsListOutbound) {
                        $scope.flightsListOutbound.forEach(function(elem) {
                        if(elem.hits)
                        {
                            map["dataProvider"]["images"].push({
                                "svgPath": targetSVG,
                                "title": elem.airportCodeStr,
                                "color": "#CC0000",
                                "latitude": elem.latitude,
                                "longitude": elem.longitude
                            });
                        }
                        else
                        {
                            map["dataProvider"]["images"].push({
                                "svgPath": targetSVG,
                                "color": "#273746",
                                "title": elem.airportCodeStr,
                                "latitude": elem.latitude,
                                "longitude": elem.longitude
                            });
                        }
                    });
                    }
                    map.validateData();
                }); // end of dashboard service

            };

            $scope.getFlightsAndPassengersAndHitsCount($scope.credentials);

        }); // end of controller

}());

