/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
  'use strict';
  app.controller('FlightsController', function ($scope, $state, $stateParams, $mdToast, codeService, $filter, $translate,
          flightService,flightSearchOptions, gridService, uiGridConstants, executeQueryService, flights, flightsModel, spinnerService, paxService, codeTooltipService, $timeout, user) {
      $scope.errorToast = function(error){
          $mdToast.show($mdToast.simple()
           .content(error)
           .position('top right')
           .hideDelay(4000)
           .parent($scope.toastParent));
      };

      var refresher = null;

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
      
     /* $scope.updateOnDirectionChange = function(){

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
        
   
      };*/

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
              clearRefresh();
              if ($state.$current.self.name !== 'flights') return;
              spinnerService.show('html5spinner');
              flightService.getFlights($scope.model).then(update).then(startRefresh);
            }
          },
          resolvePage = function () {
            populateAirports();
            fetchMethods[stateName]();
          };

      var clearRefresh = function() {
        clearTimeout(refresher);
      }

      var startRefresh = function() {
        refresher = setTimeout(fetchMethods.flights, 30000);
      }
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
      
     /* var loadFlightDirection = function() {

           var isAdminUser = flightSearchOptions.data.adminUser;
           
           if(isAdminUser!=null && isAdminUser!=undefined && isAdminUser===false)
           {
             var destinationAirports = new Array();
             destinationAirports.push({id: flightSearchOptions.data.userLocation}); 
             $scope.model.dest = destinationAirports;
             flightDirections = flightDirections.splice(2, 1);

           }

        };  */

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
          self.filterSelected = true;

          if (Array.isArray(allAirports)) {
            self.allAirports = allAirports.map(function (contact) {
              contact.lowerCasedName = contact.id.toLowerCase();
              return contact;
            });
          }
          else return;
      });

      var fixGridData = function(grid, row, col, value) {
        if (col.name === 'countDownTimer') {
            value = row.entity.countDown.countDownTimer;
        }
        if (col.name === 'eta' || col.name === 'etd') {
           value =   $filter('date')(value, 'yyyy-MM-dd HH:mm');
        }
        return value;
      }

      
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
          exporterPdfDefaultStyle: {fontSize: 9},
          exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
          exporterPdfFooter: function ( currentPage, pageCount ) {
            return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
          },
          exporterPdfPageSize: 'LETTER',
          exporterPdfMaxGridWidth: 500,
          exporterCsvFilename: 'FlightGid.csv',
          exporterExcelFilename: 'flightGrid.xlsx',
          exporterExcelSheetName: 'Data',
          exporterFieldCallback: function ( grid, row, col, value ){
              return fixGridData (grid, row, col, value);
          },

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
              enableGridMenu: true,
              exporterPdfDefaultStyle: {fontSize: 9},
              exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
              exporterPdfFooter: function ( currentPage, pageCount ) {
                return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
              },
              exporterPdfPageSize: 'LETTER',
              exporterPdfMaxGridWidth: 500,
              exporterCsvFilename: 'FlightsQueryGrid.csv',
              exporterExcelFilename: 'flightsQueryGrid.xlsx',
              exporterExcelSheetName: 'Data',
              exporterFieldCallback: function ( grid, row, col, value ){
                return fixGridData (grid, row, col, value);
              },  
            
             onRegisterApi: function (gridApi) {
                 $scope.gridApi = gridApi;

                 gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                     $scope.model.pageSize = pageSize;
                 });
             }
      };

      $scope.flightsGrid.columnDefs = [
          {
              name: 'passengerCount',
              field: 'passengerCount',
              displayName: $translate.instant('flight.passengers'),
              enableFiltering: false,
              cellTemplate: 
            	  '<a ng-if="grid.appScope.userHasRole(3) || grid.appScope.userHasRole(2) || grid.appScope.userHasRole(1)" ui-sref="flightpax({id: row.entity.id, flightNumber: row.entity.fullFlightNumber, origin: row.entity.origin, dest: row.entity.destination, direction: row.entity.direction})" href="#/flights/{{row.entity.id}}/{{row.entity.fullFlightNumber}}/{{row.entity.origin}}/{{row.entity.destination}}/{{row.entity.direction}}/" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</a>'+
            	  '<a ng-if="!grid.appScope.userHasRole(3) && !grid.appScope.userHasRole(2) && !grid.appScope.userHasRole(1)" href="" class="md-primary md-button md-default-theme disabled" >{{COL_FIELD}}</a>'
          },
          {
              name: 'countDownTimer',
              field: 'countDown.millisecondsFromDate',
              displayName: $translate.instant('flight.countdown'),
              type: 'number',
              enableFiltering: false,
              cellTemplate: '<span ng-class="{\'text-success\': row.entity.countDown.closeToCountDown}">{{row.entity.countDown.countDownTimer}}</span>'
          },
          {
              name: 'listHitCount',
              displayName: $translate.instant('hit.watchlisthits'),
              enableFiltering: false,
              cellClass: "gridService.colorHits",
              sort: {
                  direction: uiGridConstants.DESC,
                  priority: 0
              },
              cellTemplate: '<span ng-if="row.entity.listHitCount" class="badge danger-back danger-border-th">{{row.entity.listHitCount}}</span>'
          },
          {
              name: 'ruleHitCount',
              displayName: $translate.instant('hit.rulehits'),
              enableFiltering: false,
              cellClass: gridService.colorHits,
              sort: {
                  direction: uiGridConstants.DESC,
                  priority: 1
              },
              cellTemplate: '<span ng-if="row.entity.ruleHitCount" class="badge warning-back warning-border-th">{{row.entity.ruleHitCount}}</span>'
          },
          {
              name: 'graphHitCount',
              displayName: $translate.instant('hit.graphhits'),
              enableFiltering: false,
              cellClass: "gridService.colorHits",
              sort: {
                  direction: uiGridConstants.DESC,
                  priority: 0
              },
              cellTemplate: '<span ng-if="row.entity.graphHitCount" class="badge warning-back warning-border-th">{{row.entity.graphHitCount}}</span>'
          },
          {
              name: 'fuzzyHitCount',
              displayName: $translate.instant('hit.partialhits'),
              enableFiltering: false,
              cellClass: "gridService.colorHits",
              sort: {
                  direction: uiGridConstants.DESC,
                  priority: 0
              },
              cellTemplate: '<span ng-if="row.entity.fuzzyHitCount" class="badge info-back info-border-th">{{row.entity.fuzzyHitCount}}</span>'
          },
          {
              name: 'externalHitCount',
              displayName: $translate.instant('hit.external'),
              enableFiltering: false,
              cellClass: "gridService.colorHits",
              sort: {
                  direction: uiGridConstants.DESC,
                  priority: 0
              },
              cellTemplate: '<span ng-if="row.entity.externalHitCount" class="badge warning-back warning-border-th">{{row.entity.externalHitCount}}</span>'
          },
          {
              name: 'direction',
              displayName: $translate.instant('flight.direction'),
              cellTemplate: '<span>{{COL_FIELD}}</span>',
              visible: true
          },
          {
              name: 'fullFlightNumber',
              displayName: $translate.instant('flight.flight'),
              cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
                  + '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(row.entity.carrier,"carrier")}}</div></md-tooltip>{{COL_FIELD}}'
                  + '</md-button>'
          },
          {
              name: 'carrier',
              displayName: $translate.instant('flight.carrier'),
              cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
                  + '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"carrier")}}</div></md-tooltip>{{COL_FIELD}}'
                  + '</md-button>',
              visible: false

          },
          {
              name: 'eta', displayName: $translate.instant('flight.arrival'), type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:mm\''
          },
          {
              name: 'etd', displayName: $translate.instant('flight.departure'), type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:mm\''
          },
          {
              name: 'origin', displayName: $translate.instant('flight.origin'), visible: true,

              cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
                  + '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"airport")}}</div></md-tooltip>{{COL_FIELD}}'
                  + '</md-button>'
          },
          {
              name: 'originCountry', displayName: $translate.instant('doc.country'), visible: false,
              cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
                  + '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"country")}}</div></md-tooltip>{{COL_FIELD}}'
                  + '</md-button>'
          },
          {
              name: 'destination', displayName: $translate.instant('flight.destination'),
              cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
                  + '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"airport")}}</div></md-tooltip>{{COL_FIELD}}'
                  + '</md-button>',
              visible: true

          },
          {
              name: 'destinationCountry', displayName: $translate.instant('pass.destination'),
              cellTemplate: '<md-button aria-label="hits" ng-mouseleave="grid.appScope.resetCountryTooltip()">'
                  + '<md-tooltip class="multi-tooltip" md-direction="left"><div>{{grid.appScope.getCodeTooltipData(COL_FIELD,"country")}}</div></md-tooltip>{{COL_FIELD}}'
                  + '</md-button>',
              visible: false

          }
      ];

    $scope.flightsQueryGrid.columnDefs = $scope.flightsGrid.columnDefs;

    $scope.passengerSubGridColumnDefs =
      [
         {
             name: 'onRuleHitList',
             displayName: $translate.instant('hit.rulehits'),
             cellClass: "rule-hit",
             sort: {
                 direction: uiGridConstants.DESC,
                 priority: 1
             },
             cellTemplate: '<md-button aria-label="hits" ng-click="grid.api.expandable.toggleRowExpansion(row.entity)" disabled="{{row.entity.onRuleHitList|ruleHitButton}}"><span ng-if="row.entity.onRuleHitList" class="badge warning-back warning-border-th">{{+row.entity.onRuleHitList}}</span></md-button>'
         },
         {
             name: 'onWatchList',
             displayName: $translate.instant('hit.watchlisthits'),
             cellClass: gridService.anyWatchlistHit,
             sort: {
                 direction: uiGridConstants.DESC,
                 priority: 0
             },
             cellTemplate: '<span ng-if="row.entity.onWatchListDoc || row.entity.onWatchList" class="badge danger-back danger-border-th">{{+row.entity.onWatchListDoc+row.entity.onWatchList}}</span>'
         },
         {name: 'passengerType',
         displayName: $translate.instant('pass.type')
        },
        {
             name: 'lastName',
             displayName: $translate.instant('pass.lastname'),
             cellTemplate: '<md-button aria-label="type" href="#/paxdetail/{{row.entity.id}}/{{row.entity.flightId}}" title="{{`msg.launchflightpax` | translate}}" target="pax.detail" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
        },
        {
           name: 'firstName',
           displayName: $translate.instant('pass.firstname')
        },
        {
          name: 'middleName',
          displayName: $translate.instant('pass.middlename')
        },
        {
          name: 'fullFlightNumber',
          displayName: $translate.instant('flight.flightnum')
        },
         {
             name: 'eta',
             type: 'date',
             sort: {
                 direction: uiGridConstants.DESC,
                 priority: 2
             },
             cellFilter: 'date:\'yyyy-MM-dd HH:mm\'',
             displayName: $translate.instant('flight.arrival'),
             visible: (stateName === 'paxAll')
         },
         {name: 'etd',
             type: 'date',
             displayName: $translate.instant('flight.departure'),
             cellFilter: 'date:\'yyyy-MM-dd HH:mm\'',
             visible: (stateName === 'paxAll')},
         {
           name: 'gender',
           displayName: $translate.instant('pass.gender')
          },
         {
           name: 'dob',
           displayName: $translate.instant('pass.dob'),
           cellFilter: 'date',
          cellTemplate: '<span>{{COL_FIELD| date:"yyyy-MM-dd"}}</span>'},
         {
           name: 'nationality',
           displayName: $translate.instant('pass.nationality'),
          }
     ];

    $scope.queryPassengersOnSelectedFlight = function (row_entity) {
        $state.go('passengers', {
            flightNumber: row_entity.flightNumber,
            origin: row_entity.origin,
            dest: row_entity.dest
        });
    };
    $scope.filter = function () {
    	$scope.model.searchSubmitFlag='Y';
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
    $scope.userHasRole = function(roleId){
    	var hasRole = false;
    	$.each(user.data.roles, function(index, value) {
    		if (value.roleId === roleId) {
    			hasRole = true;
                }
    		});
    	return hasRole;
    }
    resolvePage();
    mapAirports();
  //  loadFlightDirection();
})
}());
