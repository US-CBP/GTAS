/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app.controller('FlightsController', function ($scope, $http, $state, $interval, $stateParams, $mdToast, passengersBasedOnUserFilter, 
            flightService, gridService, uiGridConstants, executeQueryService, flights, flightsModel, spinnerService, paxService, $timeout) {
        $scope.errorToast = function(error){
            $mdToast.show($mdToast.simple()
             .content(error)
             .position('top right')
             .hideDelay(4000)
             .parent($scope.toastParent));
        };
        
        var exporter = {
            'csv': function () {
                $scope.gridApi.exporter.csvExport('all', 'all');
            },
            'pdf': function () {
                $scope.gridApi.exporter.pdfExport('all', 'all');
            }
        };
        function createFilterFor(query) {
            var lowercaseQuery = query.toLowerCase();
            return function filterFn(contact) {
                return (contact.lowerCasedName.indexOf(lowercaseQuery) >= 0);
            };
        }

        /* Search for airports. */
        function querySearch(query) {
            var results = query && (query.length) && (query.length >= 3) ? self.allAirports.filter(createFilterFor(query)) : [];
            return results;
        }

        $scope.model = flightsModel;

        $scope.export = function (format) {
            exporter[format]();
        };

        var self = this, airports,
            stateName = $state ? $state.$current.self.name : 'flights',
            setFlightsGrid = function (grid, response) {
                //NEEDED because java services responses not standardize should have Lola change and Amit revert to what he had;
                var data = stateName === 'queryFlights' ? response.data.result : response.data;
                grid.totalItems = data.totalFlights === -1 ? 0 : data.totalFlights;
                grid.data = data.flights;
                if(!data.flights || data.flights.length == 0){
                    $scope.errorToast("No results found for selected filter criteria");
                }
                spinnerService.hide('html5spinner');
            },
            flightDirections = [
                {label: 'Inbound', value: 'I'},
                {label: 'Outbound', value: 'O'},
                {label: 'Any', value: 'A'}
            ],
            getPage = function () {
                if(stateName === 'queryFlights'){
                    setFlightsGrid($scope.flightsQueryGrid, flights || {flights: [], totalFlights: 0});
                }
                else{
                    setFlightsGrid($scope.flightsGrid, flights || {flights: [], totalFlights: 0});
                }
                
            },
            update = function (data) {
                flights = data;
                getPage();
                spinnerService.hide('html5spinner');
            },
            fetchMethods = {
                queryFlights: function () {
                    var postData, query = JSON.parse(localStorage['query']);
                    postData = {
                        pageNumber: $scope.model.pageNumber,
                        pageSize: $scope.model.pageSize,
                        query: query
                    };
                    spinnerService.show('html5spinner');
                    executeQueryService.queryFlights(postData).then(update);
                },
                flights: function () {
                    spinnerService.show('html5spinner');
                    flightService.getFlights($scope.model).then(update);
                }
            },
            resolvePage = function () {
                populateAirports();
                fetchMethods[stateName]();
            };

        var populateAirports = function () {

            var originAirports = new Array();
            var destinationAirports = new Array();

            angular.forEach($scope.model.origin, function (value, index) {
                originAirports.push(value.id);
            })

            angular.forEach($scope.model.dest, function (value, index) {
                destinationAirports.push(value.id);
            })

            $scope.model.originAirports = originAirports;
            $scope.model.destinationAirports = destinationAirports;
        };

        var mapAirports = function () {
            var originAirports = new Array();
            var destinationAirports = new Array();
            var airport = {id: ""};

            angular.forEach(flightsModel.origins, function (value, index) {
                originAirports.push({id: value});
            });

            angular.forEach(flightsModel.destinations, function (value, index) {
                destinationAirports.push({id: value});
            });
            $scope.model.origin = originAirports;
            $scope.model.dest = destinationAirports;
        };


        self.querySearch = querySearch;
        $http.get('data/airports.json')
            .then(function (allAirports) {
                airports = allAirports.data;
                self.allAirports = allAirports.data.map(function (contact) {
                    //contact.lowerCasedName = contact.name.toLowerCase();
                    contact.lowerCasedName = contact.id.toLowerCase();
                    return contact;
                });
                self.filterSelected = true;
            });
        $scope.selectedFlight = $stateParams.flight;
        $scope.flightDirections = flightDirections;
        $scope.stateName = stateName;
        $scope.flightsGrid = {
            paginationPageSizes: [10, 15, 25],
            paginationPageSize: $scope.model.pageSize,
            paginationCurrentPage: $scope.model.pageNumber,
            useExternalPagination: true,
            useExternalSorting: true,
            useExternalFiltering: true,
            enableHorizontalScrollbar: 0,
            enableVerticalScrollbar: 1,
            enableColumnMenus: false,
            enableExpandableRowHeader: false,
            exporterCsvFilename: 'Flights.csv',
            expandableRowHeight: 200,
            expandableRowTemplate: '<div ui-grid="row.entity.subGridOptions"></div>',

            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;

                gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                    if (sortColumns.length === 0) {
                        $scope.model.sort = null;
                    } else {
                        $scope.model.sort = [];
                        for (var i = 0; i < sortColumns.length; i++) {
                            $scope.model.sort.push({
                                column: sortColumns[i].name,
                                dir: sortColumns[i].sort.direction
                            });
                        }
                    }
                    resolvePage();
                });

                gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                    if ($scope.model.pageNumber !== newPage || $scope.model.pageSize !== pageSize) {
                        $scope.model.pageNumber = newPage;
                        $scope.model.pageSize = pageSize;
                        resolvePage();
                    }
                });
                
                gridApi.expandable.on.rowExpandedStateChanged($scope, function (row) {
                    if (row.isExpanded) {
                 	   row.entity.subGridOptions = {
                 			  columnDefs: $scope.passengerSubGridColumnDefs,
                 			  enableHorizontalScrollbar: 0,
                              enableVerticalScrollbar: 1,
                              
                 	   }
                 	   
                 	   var request ={
                 			   dest:row.entity.destination,
                 			   direction:row.entity.direction,
                 			   etaEnd:new Date(row.entity.etd.substring(0,10).split('-').join(',')),
                 			   etaStart:new Date(row.entity.eta.substring(0,10).split('-').join(',')),
                 			   flightNumber:row.entity.fullFlightNumber,
                 			   origin: row.entity.origin,
                 			   lastname:"",
                 			   pageNumber:1,
                 			   pageSize:5000
                 	   };	   
                 	  spinnerService.show('html5spinner');
                 	   paxService.getPax(row.entity.id, request).then(function(data){
                 		   var passengerHitList = [];
                 		   $.each(data.data.passengers, function(index,value){
                 			 if (value.onRuleHitList || value.onWatchList || value.onWatchListDoc){
                 				 passengerHitList.push(value);
                 			 }
                 		   });
                 		   row.entity.subGridOptions.data=passengerHitList;
                 		  spinnerService.hide('html5spinner');
                 	   });
                    }
                });
            }
        };
        //Front-end pagination configuration object for gridUi
        //Should only be active on stateName === 'queryFlights'
        $scope.flightsQueryGrid = {
                paginationPageSizes: [10, 15, 25],
                paginationPageSize: $scope.model.pageSize,
                paginationCurrentPage: 1,
                useExternalPagination: false,
                useExternalSorting: false,
                useExternalFiltering: false,
                enableHorizontalScrollbar: 0,
                enableVerticalScrollbar: 1,
                enableColumnMenus: false,
                enableExpandableRowHeader: false,
                exporterCsvFilename: 'Flights.csv',
                expandableRowHeight: 200,
                expandableRowTemplate: '<div ui-grid="row.entity.subGridOptions"></div>',
                    
               onRegisterApi: function (gridApi) {
                   $scope.gridApi = gridApi;
                   
                   gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                       $scope.model.pageSize = pageSize;
                   });
                   
                   gridApi.expandable.on.rowExpandedStateChanged($scope, function (row) {
                       if (row.isExpanded) {
                     	   row.entity.subGridOptions = {
                     			   columnDefs: $scope.passengerSubGridColumnDefs	   
                     	   }
                     	   
                     	   var request ={
                     			   dest:row.entity.destination,
                     			   direction:row.entity.direction,
                     			   etaEnd:new Date(row.entity.etd.substring(0,10).split('-').join(',')),
                     			   etaStart:new Date(row.entity.eta.substring(0,10).split('-').join(',')),
                     			   flightNumber:row.entity.fullFlightNumber,
                     			   origin: row.entity.origin,
                     			   lastname:"",
                     			   pageNumber:1,
                     			   pageSize:5000
                     	   };	   
                     	   
                     	  paxService.getPax(row.entity.id, request).then(function(data){
                     		 spinnerService.show('html5spinner');
                    		   var passengerHitList = [];
                    		   $.each(data.data.passengers, function(index,value){
                    			 if (value.onRuleHitList || value.onWatchList || value.onWatchListDoc){
                    				 passengerHitList.push(value);
                    			 }
                    		   });
                    		   spinnerService.show('html5spinner');
                    		   row.entity.subGridOptions.data=passengerHitList;
                     	  });
                        }
                   });
               }
        }

        $scope.flightsGrid.columnDefs = [
            {
                name: 'passengerCount',
                field: 'passengerCount',
                displayName: 'P',
                width: 100,
                enableFiltering: false,
                cellTemplate: '<a ui-sref="flightpax({id: row.entity.id, flightNumber: row.entity.fullFlightNumber, origin: row.entity.origin, destination: row.entity.destination, direction: row.entity.direction, eta: row.entity.eta.substring(0, 10), etd: row.entity.etd.substring(0, 10)})" href="#/flights/{{row.entity.id}}/{{row.entity.fullFlightNumber}}/{{row.entity.origin}}/{{row.entity.destination}}/{{row.entity.direction}}/{{row.entity.eta.substring(0, 10)}}/{{row.entity.etd.substring(0, 10);}}" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</a>'
            },
            {
                name: 'ruleHitCount',
                displayName: 'H',
                width: 50,
                enableFiltering: false,
                cellClass: gridService.colorHits,
                sort: {
                    direction: uiGridConstants.DESC,
                    priority: 0
                }
            },
            {
                name: 'listHitCount',
                displayName: 'L',
                width: 50,
                enableFiltering: false,
                cellClass: gridService.colorHits,
                sort: {
                    direction: uiGridConstants.DESC,
                    priority: 1
                }
            },
            {
                name: 'fullFlightNumber',
                displayName: 'flight.flight', headerCellFilter: 'translate',
                width: 70,
                cellTemplate:'<md-button ng-click="grid.api.expandable.toggleRowExpansion(row.entity)" ng-disabled="row.entity.ruleHitCount === 0 && row.entity.listHitCount === 0" >{{COL_FIELD}}</md-button>'
            },
            {
            	name:'carrier',
            	displayName: 'flight.carrier', headerCellFilter: 'translate',
                width:50
            },
            {
                name: 'eta', displayName:'pass.eta', headerCellFilter: 'translate',
                sort: {
                    direction: uiGridConstants.DESC,
                    priority: 2
                }
            },
            {name: 'etd', displayName:'pass.etd', headerCellFilter: 'translate'},
            {name: 'origin', displayName:'flight.origin', headerCellFilter: 'translate'},
            {name: 'originCountry', displayName:'doc.country', headerCellFilter: 'translate'},
            {name: 'destination', displayName:'flight.destination', headerCellFilter: 'translate'},
            {name: 'destinationCountry', displayName:'add.Country', headerCellFilter: 'translate'}
        ];
        
        $scope.flightsQueryGrid.columnDefs = $scope.flightsGrid.columnDefs;

        $scope.passengerSubGridColumnDefs = 
        	[
             {
                 name: 'onRuleHitList', displayName: 'H', width: 50,
                 cellClass: "rule-hit",
                 sort: {
                     direction: uiGridConstants.DESC,
                     priority: 0
                 },
                 cellTemplate: '<md-button aria-label="hits" ng-click="grid.api.expandable.toggleRowExpansion(row.entity)" disabled="{{row.entity.onRuleHitList|ruleHitButton}}"><i class="{{row.entity.onRuleHitList|ruleHitIcon}}"></i></md-button>'
             },
             {
                 name: 'onWatchList', displayName: 'L', width: 70,
                 cellClass: gridService.anyWatchlistHit,
                 sort: {
                     direction: uiGridConstants.DESC,
                     priority: 1
                 },
                 cellTemplate: '<div><i class="{{row.entity.onWatchList|watchListHit}}"></i> <i class="{{row.entity.onWatchListDoc|watchListDocHit}}"></i></div>'
             },
             {name: 'passengerType', displayName:'T', headerCellFilter: 'translate', width: 50},
             {
                 name: 'lastName', displayName:'pass.lastname', headerCellFilter: 'translate',
                 cellTemplate: '<md-button aria-label="type" href="#/paxdetail/{{row.entity.id}}/{{row.entity.flightId}}" title="Launch Flight Passengers in new window" target="pax.detail" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
             },
             {name: 'firstName', displayName:'pass.firstname', headerCellFilter: 'translate'},
             {name: 'middleName', displayName:'pass.middlename', headerCellFilter: 'translate'},
             {name: 'fullFlightNumber', displayName:'pass.flight', headerCellFilter: 'translate' },
             {
                 name: 'eta',
                 sort: {
                     direction: uiGridConstants.DESC,
                     priority: 2
                 },
                 displayName:'pass.eta', headerCellFilter: 'translate',
                 visible: (stateName === 'paxAll')
             },
             {name: 'etd', displayName:'pass.etd', headerCellFilter: 'translate', visible: (stateName === 'paxAll')},
             {name: 'gender', displayName:'G', headerCellFilter: 'translate', width: 50},
             {name: 'dob', displayName:'pass.dob', headerCellFilter: 'translate', cellFilter: 'date'},
             {name: 'citizenshipCountry', displayName:'add.Country', headerCellFilter: 'translate', width: 75}
         ];     
        
        $scope.queryPassengersOnSelectedFlight = function (row_entity) {
            $state.go('passengers', {
                flightNumber: row_entity.flightNumber,
                origin: row_entity.origin,
                dest: row_entity.dest
            });
        };

        $scope.filter = function () {
            //temporary as flightService doesn't support multiple values yet
            //$scope.model.origin = self.origin.length ? self.origin.map(returnObjectId)[0] : '';
            //$scope.model.dest = self.destination ? self.destination.map(returnObjectId)[0] : '';
        	//There is a delay between datepicker being assigned a new date and it being applied properly to the ng-model;
        	//A small delay insures that the model is updated before the service is called for unusually fast submit requests.
        	$timeout(function(){
        		resolvePage();
        	},500);
        };

        $scope.reset = function () {
            $scope.model.reset();
            resolvePage();
        };

        $scope.getTableHeight = function () {
            if(stateName != 'queryFlights'){
                return gridService.calculateGridHeight($scope.flightsGrid.data.length);
            } // Sets minimal height for front-end pagination controlled variant of grid
            return gridService.calculateGridHeight($scope.model.pageSize);
        };
        resolvePage();
        mapAirports();
        
        //These two watches establish the range of clock times the start/end dates are set to.
        //Datepicker automatically sets it to 0:00:00 if passed a date with no time, 
        //however our initial dates for the model carry with it a time and must be overridden
        $scope.$watch("model.etaStart", function (newValue) {
            newValue.setHours(0);
            newValue.setMinutes(0);
            newValue.setSeconds(0);
        });
        //End date is set to the maximum clock time
        $scope.$watch("model.etaEnd", function (newValue) {
            newValue.setHours(23);
            newValue.setMinutes(59);
            newValue.setSeconds(59);
        });
    });
}());