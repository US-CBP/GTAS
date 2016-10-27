/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app.controller('PassengerDetailCtrl', function ($scope, passenger, $mdToast, spinnerService, user, paxDetailService, caseService) {
        $scope.passenger = passenger.data;
        $scope.isLoadingFlightHistory = true;        
        
        $scope.saveDisposition = function(){
        	var disposition = {
                    'passengerId':$scope.passenger.paxId,
                    'flightId':$scope.passenger.flightId,
                    'statusId':$scope.currentDispStatus,
                    'comments':$scope.currentDispComments,
                    'user':user.data.userId,
                    'createdBy': user.data.userId,
                    'createdAt': new Date()
                };

        	spinnerService.show('html5spinner');
        	caseService.createDisposition(disposition)
        	.then(function(response){
        		spinnerService.hide('html5spinner');
        		//Clear input, reload history
        		$scope.currentDispStatus="-1";
        		$scope.currentDispComments="";
        		//This makes it palatable to the front-end
        		$.each($scope.dispositionStatus, function(index,value){
        			if(value.id === parseInt(disposition.statusId)){
        				var status = {status:value.name};
        				$.extend(disposition,status);
        			}
        		});
        		if($scope.passenger.dispositionHistory != null && typeof $scope.passenger.dispositionHistory.length != "undefined"){
        			//Add to disposition length without service calling if success
        			$scope.passenger.dispositionHistory.push(disposition);
        		} else{
        			$scope.passenger.dispositionHistory = [disposition];
        		}
        	});
        }
        
        caseService.getDispositionStatuses()
        .then(function(response){
        	$scope.dispositionStatus = response.data;
        });
        
        paxDetailService.getPaxFlightHistory($scope.passenger.paxId)
        .then(function(response){
            $scope.isLoadingFlightHistory = false;
            $scope.passenger.flightHistoryVo = response.data;
        });
        
        
    });
    app.controller('PaxController', function ($scope, $injector, $stateParams, $state, $mdToast, paxService, sharedPaxData, uiGridConstants, gridService,
                                              jqueryQueryBuilderService, jqueryQueryBuilderWidget, executeQueryService, passengers,
                                              $timeout, paxModel, $http, spinnerService) {
        
        $scope.errorToast = function(error){
            $mdToast.show($mdToast.simple()
             .content(error)
             .position('top right')
             .hideDelay(4000)
             .parent($scope.toastParent));
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

        $scope.searchSort = querySearch;
        $scope.model = paxModel.model;

        var self = this, airports,
            stateName = $state.$current.self.name,
            ruleGridColumns = [{
                name: 'ruleTitle',
                displayName: 'Title',
                cellTemplate: '<md-button aria-label="title" class="md-primary md-button md-default-theme" ng-click="grid.appScope.ruleIdClick(row)">{{COL_FIELD}}</md-button>'
            }, {
                name: 'ruleConditions',
                displayName: 'Conditions',
                field: 'hitsDetailsList[0]',
                cellFilter: 'hitsConditionDisplayFilter'
            }],
            setSubGridOptions = function (data, appScopeProvider) {
                data.passengers.forEach(function (entity_row) {
                    if (!entity_row.flightId) {
                        entity_row.flightId = $stateParams.id;
                    }
                    entity_row.subGridOptions = {
                        appScopeProvider: appScopeProvider,
                        columnDefs: ruleGridColumns,
                        data: []
                    };
                });
            },
            setPassengersGrid = function (grid, response) {
                //NEEDED because java services responses not standardize should have Lola change and Amit revert to what he had;
                var data = stateName === 'queryPassengers' ? response.data.result : response.data;
                setSubGridOptions(data, $scope);
                grid.totalItems = data.totalPassengers === -1 ? 0 : data.totalPassengers;
                grid.data = data.passengers;
                if(!grid.data || grid.data.length == 0){
                    $scope.errorToast('No results found for selected filter criteria');
                }
                spinnerService.hide('html5spinner');
            },
            getPage = function () {
                if(stateName === "queryPassengers"){
                    setPassengersGrid($scope.passengerQueryGrid, passengers);
                }else{
                    setPassengersGrid($scope.passengerGrid, passengers);
                }
            },
            update = function (data) {
                passengers = data;
                getPage();
                spinnerService.hide('html5spinner');
            },
            fetchMethods = {
                'queryPassengers': function () {
                    var postData, query = JSON.parse(localStorage['query']);
                    postData = {
                        pageNumber: $scope.model.pageNumber,
                        pageSize: $scope.model.pageSize,
                        query: query
                    };
                    spinnerService.show('html5spinner');
                    executeQueryService.queryPassengers(postData).then(update);
                },
                'flightpax': function () {
                    spinnerService.show('html5spinner');
                    paxService.getPax($stateParams.id, $scope.model).then(update);
                },
                'paxAll': function () {
                    spinnerService.show('html5spinner');
                    paxService.getAllPax($scope.model).then(update);
                }
            },
            resolvePage = function () {
                populateAirports();
                fetchMethods[stateName]();
            },
            flightDirections = [
                {label: 'Inbound', value: 'I'},
                {label: 'Outbound', value: 'O'},
                {label: 'Any', value: 'A'}
            ];

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
                $scope.filterSelected = true;
            });
        $scope.flightDirections = flightDirections;

        $injector.invoke(jqueryQueryBuilderWidget, this, {$scope: $scope});
        $scope.stateName = $state.$current.self.name;
        $scope.ruleIdClick = function (row) {
            $scope.getRuleObject(row.entity.ruleId);
        };

        $scope.getRuleObject = function (ruleID) {
            jqueryQueryBuilderService.loadRuleById('rule', ruleID).then(function (myData) {
                $scope.$builder.queryBuilder('readOnlyRules', myData.result.details);
                $scope.hitDetailDisplay = myData.result.summary.title;
                document.getElementById("QBModal").style.display = "block";

                $scope.closeDialog = function () {
                    document.getElementById("QBModal").style.display = "none";
                };
            });
        };

        $scope.isExpanded = true;
        $scope.paxHitList = [];
        $scope.list = sharedPaxData.list;
        $scope.add = sharedPaxData.add;
        $scope.getAll = sharedPaxData.getAll;

        $scope.getPaxSpecificList = function (index) {
            return $scope.list(index);
        };

        $scope.buildAfterEntitiesLoaded();
        
        $scope.passengerGrid = {
                paginationPageSizes: [10, 15, 25],
                paginationPageSize: $scope.model.pageSize,
                paginationCurrentPage: $scope.model.pageNumber,
                useExternalPagination: true,
                useExternalSorting: true,
                useExternalFiltering: true,
                enableHorizontalScrollbar: 0,
                enableVerticalScrollbar: 0,
                enableColumnMenus: false,
                multiSelect: false,
                enableExpandableRowHeader: false,
                expandableRowTemplate: '<div ui-grid="row.entity.subGridOptions"></div>',

                onRegisterApi: function (gridApi) {
                    $scope.gridApi = gridApi;

                    gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                        $scope.model.pageNumber = newPage;
                        $scope.model.pageSize = pageSize;
                        resolvePage();
                    });

                    gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                        if (sortColumns.length === 0) {
                            $scope.model.sort = null;
                        } else {
                            $scope.model.sort = [];
                            for (var i = 0; i < sortColumns.length; i++) {
                                $scope.model.sort.push({column: sortColumns[i].name, dir: sortColumns[i].sort.direction});
                            }
                        }
                        resolvePage();
                    });

                    gridApi.expandable.on.rowExpandedStateChanged($scope, function (row) {
                        if (row.isExpanded) {
                            paxService.getRuleHits(row.entity.id).then(function (data) {
                                row.entity.subGridOptions.data = data;
                            });
                        }
                    });
                }
            };
        //Front-end pagination configuration object for gridUi
        //Should only be active on stateName === 'queryPassengers'
        $scope.passengerQueryGrid = {
            paginationPageSizes: [10, 15, 25],
            paginationPageSize: $scope.model.pageSize,
            paginationCurrentPage: 1,
            useExternalPagination: false,
            useExternalSorting: false,
            useExternalFiltering: false,
            enableHorizontalScrollbar: 0,
            enableVerticalScrollbar: 0,
            enableColumnMenus: false,
            multiSelect: false,
            enableExpandableRowHeader: false,
            minRowsToShow: 10,
            expandableRowTemplate: '<div ui-grid="row.entity.subGridOptions"></div>',

            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
                
                gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                    $scope.model.pageSize = pageSize;
                });
                
                gridApi.expandable.on.rowExpandedStateChanged($scope, function (row) {
                    if (row.isExpanded) {
                        paxService.getRuleHits(row.entity.id).then(function (data) {
                            row.entity.subGridOptions.data = data;
                        });
                    }
                });
            }    
        };

        if (stateName === 'queryPassengers') {
            $scope.passengerQueryGrid.columnDefs = [
                {
                    field: 'onRuleHitList',
                    name: 'onRuleHitList',
                    displayName: 'H',
                    width: 50,
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
                {
                    field: 'passengerType',
                    name: 'passengerType',
                    displayName:'T',
                    width: 50},
                {
                    field: 'lastName',
                    name: 'lastName',
                    displayName:'pass.lastname', headerCellFilter: 'translate',
                    cellTemplate: '<md-button aria-label="type" href="#/paxdetail/{{row.entity.id}}/{{row.entity.flightId}}" title="Launch Flight Passengers in new window" target="pax.detail.{{row.entity.id}}.{{row.entity.flightId}}" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
                },
                {
                    field: 'firstName',
                    name: 'firstName',
                    displayName:'pass.firstname', headerCellFilter: 'translate'},
                {
                    field: 'middleName',
                    name: 'middleName',
                    displayName:'pass.middlename', headerCellFilter: 'translate'
                },
                {
                    field: 'flightNumber',
                    name: 'flightNumber',
                    displayName:'pass.flight', headerCellFilter: 'translate',
                    cellTemplate: '<div>{{row.entity.carrier}}{{COL_FIELD}}</div>'
                },
                {
                    field: 'flightOrigin',
                    name: 'flightOrigin',
                    displayName:'pass.origin', headerCellFilter: 'translate'
                },
                {
                    field: 'flightDestination',
                    name: 'flightDestination',
                    displayName:'pass.destination', headerCellFilter: 'translate'
                },
                {
                    field: 'etaLocalTZ',
                    name: 'etaLocalTZ',
                    sort: {
                        direction: uiGridConstants.DESC,
                        priority: 2
                    },
                    displayName:'pass.eta', headerCellFilter: 'translate'
                },
                {
                    field: 'etdLocalTZ',
                    name: 'etdLocalTZ',
                    displayName:'pass.etd', headerCellFilter: 'translate'
                },
                {
                    field: 'gender',
                    name: 'gender',
                    displayName:'G',
                    width: 50},
                {
                    name: 'dob',
                    displayName:'pass.dob', headerCellFilter: 'translate',
                    cellFilter: 'date'
                },
                {
                    name: 'citizenshipCountry',
                    displayName:'add.Country', headerCellFilter: 'translate',
                    width: 75
                }
            ];
        } else {
            $scope.passengerGrid.columnDefs = [
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
                {name: 'passengerType', displayName:'T', width: 50},
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
                {name: 'gender', displayName:'G', width: 50},
                {name: 'dob', displayName:'pass.dob', headerCellFilter: 'translate', cellFilter: 'date'},
                {name: 'citizenshipCountry', displayName:'add.Country', headerCellFilter: 'translate', width: 75}
            ];
        }

        var populateAirports = function(){

            var originAirports = new Array();
            var destinationAirports = new Array();

            angular.forEach($scope.model.origin,function(value,index){
                originAirports.push(value.id);
            })

            angular.forEach($scope.model.dest,function(value,index){
                destinationAirports.push(value.id);
            })

            $scope.model.originAirports = originAirports;
            $scope.model.destinationAirports = destinationAirports;
        };

        var mapAirports = function(){

            var originAirports = new Array();
            var destinationAirports = new Array();
            var airport = { id: "" };
            
            if($scope.model.origin ) {
                if ($scope.model.origin instanceof Array ){
                    angular.forEach($scope.model.origin, function (value, index) {
                        if(value instanceof Object) {
                            originAirports.push({id:value.id});
                        }else{
                            originAirports.push({id: value});
                        }
                    });
                }else{
                    originAirports.push({id: $scope.model.origin});
                }
                $scope.model.origin = originAirports;
            }
            
            if($scope.model.dest ) {
                if ($scope.model.dest instanceof Array ) {
                  angular.forEach($scope.model.dest, function (value, index) {
                    if(value instanceof Object) {
                        destinationAirports.push({id:value.id});
                    }else{
                      destinationAirports.push({id: value});
                    }
                  });
                }else{
                    destinationAirports.push({id: $scope.model.dest});
                }
                $scope.model.dest = destinationAirports;   
            }
            
        };

        $scope.filter = function () {
            resolvePage();
        };

        $scope.reset = function () {
            paxModel.reset();
            resolvePage();
        };

        $scope.getTableHeight = function () {
            if( stateName != "queryPassengers"){
                return gridService.calculateGridHeight($scope.passengerGrid.data.length);
            } // Sets minimal height for front-end pagination controlled variant of grid
            return gridService.calculateGridHeight($scope.model.pageSize);
        };

        getPage();
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
