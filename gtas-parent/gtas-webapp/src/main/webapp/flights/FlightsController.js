/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
  'use strict';
  app.controller('FlightsController', function ($scope, $state, $stateParams, $mdToast, codeService,
          flightService,flightSearchOptions, gridService, uiGridConstants, executeQueryService, flights, flightsModel, spinnerService, paxService, codeTooltipService, $timeout) {
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
      //Service call for tooltip data
      $scope.getCodeTooltipData = function(field, type){
        return codeTooltipService.getCodeTooltipData(field,type);
      }

      $scope.resetCountryTooltip = function(){
        $('md-tooltip').remove();
      };
      
      $scope.updateOnDirectionChange = function(){
        
        
        var isAdminUser = flightSearchOptions.data.adminUser;
          
          if(isAdminUser!=null && isAdminUser!=undefined && isAdminUser===false)
          {
               
            if($scope.model.direction == "I")
            {
            
                var destinationAirports = new Array();
                destinationAirports.push({id: flightSearchOptions.data.userLocation}); 
                $scope.model.dest = destinationAirports;
                
                var originAirports = new Array();
                $scope.model.origin = originAirports;
            }
            else if($scope.model.direction == "O")
            {
              var originAirports = new Array();
              originAirports.push({id: flightSearchOptions.data.userLocation}); 
                $scope.model.origin = originAirports;
                
                var destinationAirports = new Array();
                $scope.model.dest = destinationAirports;
            }
            
          }
        
   
      };

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
                  $scope.queryLimitReached = flights.data.result.queryLimitReached;
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
      
      var loadFlightDirection = function() {
        
        $scope.flightDirectionList = [];
           angular.forEach(flightSearchOptions.data.flightDirectionList, function(item){
             
                       $scope.flightDirectionList.push(item);
                     
                  });
           
           var isAdminUser = flightSearchOptions.data.adminUser;
           
           if(isAdminUser!=null && isAdminUser!=undefined && isAdminUser===false)
           {
             var destinationAirports = new Array();
             destinationAirports.push({id: flightSearchOptions.data.userLocation}); 
             $scope.model.dest = destinationAirports;
           }

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
      
      codeService.getAirportTooltips()
        .then(function (allAirports) {
            self.allAirports = allAirports.map(function (contact) {
              contact.lowerCasedName = contact.id.toLowerCase();
              return contact;
            });
            self.filterSelected = true;
        });

      
      $scope.selectedFlight = $stateParams.flight;
      $scope.flightDirections = flightDirections;
      $scope.stateName = stateName;
      $scope.flightsGrid = {
          paginationPageSizes: [10, 25, 50],
          paginationPageSize: $scope.model.pageSize,
          paginationCurrentPage: $scope.model.pageNumber,
          useExternalPagination: true,
          useExternalSorting: true,
          useExternalFiltering: true,
          enableHorizontalScrollbar: true,
          enableColumnResizing: true,
          enableVerticalScrollbar: 1,
          enableColumnMenus: false,
          enableGridMenu: true,
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

              gridApi.core.on.columnVisibilityChanged( $scope, function( changedColumn ){
                $scope.columnChanged = { name: changedColumn.colDef.name, visible: changedColumn.colDef.visible };
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
              paginationPageSizes: [10, 25, 50],
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
            displayName: 'Passengers',
            enableFiltering: false,
            cellTemplate: '<a ui-sref="flightpax({id: row.entity.id, flightNumber: row.entity.fullFlightNumber, origin: row.entity.origin, destination: row.entity.destination, direction: row.entity.direction, eta: row.entity.eta.substring(0, 10), etd: row.entity.etd.substring(0, 10)})" href="#/flights/{{row.entity.id}}/{{row.entity.fullFlightNumber}}/{{row.entity.origin}}/{{row.entity.destination}}/{{row.entity.direction}}/{{row.entity.eta.substring(0, 10)}}/{{row.entity.etd.substring(0, 10);}}" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</a>'
        },
        {
            name: 'listHitCount',
            displayName: 'Watchlist Hits',
            enableFiltering: false,
            cellClass: "gridService.colorHits",
            sort: {
                direction: uiGridConstants.DESC,
                priority: 0
            },
            cellTemplate:'<span ng-if="row.entity.listHitCount" class="badge danger-back danger-border-th">{{row.entity.listHitCount}}</span>'
        },
        {
            name: 'ruleHitCount',
            displayName: 'Rule Hits',
            enableFiltering: false,
            cellClass: gridService.colorHits,
            sort: {
                direction: uiGridConstants.DESC,
                priority: 1
            },
            cellTemplate:'<span ng-if="row.entity.ruleHitCount" class="badge warning-back warning-border-th">{{row.entity.ruleHitCount}}</span>'
        },
        {
            name: 'graphHitCount',
            displayName: 'Graph Hits',
            enableFiltering: false,
            cellClass: "gridService.colorHits",
            sort: {
                direction: uiGridConstants.DESC,
                priority: 0
            },
            cellTemplate:'<span ng-if="row.entity.graphHitCount" class="badge warning-back warning-border-th">{{row.entity.graphHitCount}}</span>'
        },
        {
            name: 'fuzzyHitCount',
            displayName: 'Partial Hits',
            enableFiltering: false,
            cellClass: "gridService.colorHits",
            sort: {
                direction: uiGridConstants.DESC,
                priority: 0
            },
            cellTemplate:'<span ng-if="row.entity.fuzzyHitCount" class="badge info-back info-border-th">{{row.entity.fuzzyHitCount}}</span>'
        },
        {
            name: 'flightNumber',
            displayName: 'flight.flight', headerCellFilter: 'translate',
            cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()" ng-disabled="row.entity.codeshares.length === 0">'
                +'<md-tooltip class="multi-tooltip" md-direction="left"><div ng-repeat="item in row.entity.codeshares">Codeshare Flight #: {{item.marketingFlightNumber}}</div></md-tooltip>{{COL_FIELD}}'
                +'</md-button>'
        },
        {
          name:'carrier',
          displayName: 'flight.carrier', headerCellFilter: 'translate',
          cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
                +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"carrier")}}</div></md-tooltip>{{COL_FIELD}}'
                +'</md-button>'},
        {
            name: 'eta', displayName:'pass.eta', headerCellFilter: 'translate',
            sort: {
                direction: uiGridConstants.DESC,
                priority: 2
            }
        },
        {name: 'etd', displayName:'pass.etd', headerCellFilter: 'translate'},
        {name: 'origin', displayName:'flight.origin', headerCellFilter: 'translate',
          cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
            +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"airport")}}</div></md-tooltip>{{COL_FIELD}}'
            +'</md-button>'},
        {name: 'originCountry', displayName:'doc.country', headerCellFilter: 'translate',
            cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
          +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"country")}}</div></md-tooltip>{{COL_FIELD}}'
          +'</md-button>'},
        {name: 'destination', displayName:'flight.destination', headerCellFilter: 'translate',
          cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
          +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"airport")}}</div></md-tooltip>{{COL_FIELD}}'
          +'</md-button>'},
        {name: 'destinationCountry', displayName:'pass.destination', headerCellFilter: 'translate',
            cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
          +'<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"country")}}</div></md-tooltip>{{COL_FIELD}}'
          +'</md-button>'}
    ];

    $scope.flightsQueryGrid.columnDefs = $scope.flightsGrid.columnDefs;

    $scope.passengerSubGridColumnDefs =
      [
         {
             name: 'onRuleHitList', displayName: 'Rule Hits',
             cellClass: "rule-hit",
             sort: {
                 direction: uiGridConstants.DESC,
                 priority: 1
             },
             cellTemplate: '<md-button aria-label="hits" ng-click="grid.api.expandable.toggleRowExpansion(row.entity)" disabled="{{row.entity.onRuleHitList|ruleHitButton}}"><span ng-if="row.entity.onRuleHitList" class="badge warning-back warning-border-th">{{+row.entity.onRuleHitList}}</span></md-button>'
         },
         {
             name: 'onWatchList', displayName: 'Watchlist Hits',
             cellClass: gridService.anyWatchlistHit,
             sort: {
                 direction: uiGridConstants.DESC,
                 priority: 0
             },
             cellTemplate: '<span ng-if="row.entity.onWatchListDoc || row.entity.onWatchList" class="badge danger-back danger-border-th">{{+row.entity.onWatchListDoc+row.entity.onWatchList}}</span>'
         },
         {name: 'passengerType', displayName:'T', headerCellFilter: 'translate'},
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
         {name: 'gender', displayName:'Gender', headerCellFilter: 'translate'},
         {name: 'dob', displayName:'pass.dob', headerCellFilter: 'translate', cellFilter: 'date',
          cellTemplate: '<span>{{COL_FIELD| date:"yyyy-MM-dd"}}</span>'},
         {name: 'nationality', displayName:'add.Country', headerCellFilter: 'translate'}
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
        if($scope.gridApi.pagination.getPage() > 1){
            $scope.gridApi.pagination.seek(1);
        }
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
    //toggleDiv and filterCheck required for sidepanel
    $scope.toggleDiv = function(div) {
      var element = document.getElementById(div);
      if(element.classList.contains("active")){
        element.classList.remove("active");
      }
      else {
        element.className +=" active";
      }
    }
    $scope.filterCheck = function(option) {
      var filters = ['origin', 'destination', 'flight', 'direction', 'date'];
      return filters.includes(option);
    }
    resolvePage();
    mapAirports();
    loadFlightDirection();
});
}());
