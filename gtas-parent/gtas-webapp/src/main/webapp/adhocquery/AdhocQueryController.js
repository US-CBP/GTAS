/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
app.controller('AdhocQueryCtrl', function ($scope, $rootScope, $mdToast, $mdDialog, uiGridConstants, adhocQueryService, searchBarResults, gridService) {
    'use strict;'

	$scope.pageSize = 10;
	$scope.pageNumber = 1;
	var _content = $rootScope.searchBarContent.content;

	$scope.query = {
		content : function(newQueryString){
			return arguments.length ? (_content = newQueryString) : _content;
			}
	}

  var timeStart = new Date();

  $scope.resultsGrid = {
  	data: typeof searchBarResults != "undefined" && searchBarResults != null ? searchBarResults.data.result.passengers :null,
  	totalItems: typeof searchBarResults != "undefined" && searchBarResults != null ? searchBarResults.data.result.totalHits :null,
  	paginationPageSizes: [10, 15, 25],
      paginationPageSize: $scope.pageSize,
      paginationCurrentPage: $scope.pageNumber,
      useExternalPagination: true,
      useExternalSorting: true,
      useExternalFiltering: true,
      enableHorizontalScrollbar: 0,
      enableVerticalScrollbar: 1,
      enableColumnMenus: false,
      multiSelect: false,
      minRowsToShow: 10,
      enableExpandableRowHeader: false,
      expandableRowTemplate: '<div ui-grid="row.entity.subGridOptions"></div>',

      onRegisterApi: function (gridApi) {
          $scope.gridApi = gridApi;

          gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
              $scope.pageNumber = newPage;
              $scope.pageSize = pageSize;
              $scope.searchPax();
          });

          gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
              if (typeof sortColumns !== 'undefined' && sortColumns.length > 0) {
                  $scope.sort.column = sortColumns[0].name;
                  $scope.sort.dir = sortColumns[0].sort.direction;
                  $scope.searchPax();
              }
          });
      }
  };
  $scope.cellFormater = function(cell) {
    return timeStart < cell;
  };
  $scope.resultsGrid.columnDefs = [
      {
        field: 'passengerId',
        name: 'passengerId',
        displayName: 'Link Analysis',
        cellTemplate: '<md-button ng-click="grid.appScope.searchLinks(row.entity,$event)"><i class="fa fa-link" aria-hidden="true"></i></md-button>'
      },
      {
          field: 'lastName',
          name: 'lastName',
          displayName: 'pass.lastname', headerCellFilter: 'translate',
          cellTemplate: '<md-button aria-label="type" href="#/paxdetail/{{row.entity.passengerId}}/{{row.entity.flightId}}" title="Launch Flight Passengers in new window" target="pax.detail.{{row.entity.passengerId}}.{{row.entity.flightId}}" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>',
          sort: {
              direction: uiGridConstants.ASC
          }
      },
      {
          field: 'firstName',
          name: 'firstName',
          displayName: 'pass.firstname', headerCellFilter: 'translate'
      },
      {
          field: 'middleName',
          name: 'middleName',
          displayName: 'pass.middlename', headerCellFilter: 'translate'
      },
      {
          field: 'flightNumber',
          name: 'flightNumber',
          displayName: 'pass.flight', headerCellFilter: 'translate',
          cellTemplate: '<div>{{row.entity.carrier}}{{COL_FIELD}}</div>'
      },
      {
          field: 'origin',
          name: 'origin',
          displayName: 'flight.origin', headerCellFilter: 'translate'
      },
      {
          field: 'destination',
          name: 'destination',
          displayName: 'flight.destination', headerCellFilter: 'translate'
      },
      {
          field: 'etd',
          name: 'etd',
          displayName: 'pass.etd', headerCellFilter: 'translate',
          cellTemplate: '<div>{{COL_FIELD | date:"yyyy-MM-dd hh:mm"}}</div>'
      },
      {
          field: 'eta',
          name: 'eta',
          displayName: 'pass.eta', headerCellFilter: 'translate',
          cellTemplate: '<div ng-class="{ \'highlight\': grid.appScope.cellFormater(COL_FIELD)}">{{COL_FIELD | date:"yyyy-MM-dd hh:mm"}}</div>'
      }
  ];

  $scope.msgToast = function(error){
      $mdToast.show($mdToast.simple()
          .content(error)
          .position('top right')
          .hideDelay(4000)
          .parent($scope.toastParent));
  };

  var defaultSort = {
      column: '_score',
      dir: 'desc'
  };

  $scope.getTableHeight = function(){
  	return gridService.calculateGridHeight($scope.pageSize);
  };

  $scope.sort = defaultSort;

  $scope.searchPaxSortByScore = function(){
    $scope.sort.column='_score';
    $scope.sort.dir='desc';
    $scope.searchPax();
  };
	  
  $scope.searchPax = function () {
      return adhocQueryService
      .getPassengers(_content, $scope.pageNumber, $scope.pageSize, $scope.sort)
      .then(function (response) {
          var result = response.data.result;
          $scope.resultsGrid.data = result.passengers;
          $scope.resultsGrid.totalItems = result.totalHits;

          if (result.error !== null) {
              $scope.msgToast(result.error);
          } else {
              $scope.msgToast($scope.resultsGrid.totalItems + " results found");
          }
      });
  };

  $scope.searchLinks = function (pax, ev) {
    $scope.rootPax = pax;
    return adhocQueryService
    .getLinks(pax.passengerId, $scope.pageNumber, $scope.pageSize, $scope.sort)
    .then(function (response) {
        var result = response.data.result;
        if (result.error !== null) {
          $scope.paxLinks = result.error;
          $scope.msgToast(result.error);
        } else {
            $scope.paxLinks = result.passengers;
            $scope.msgToast(result.totalHits + " results found");
            $scope.linkDetail(ev);
        }
    });
  }

  //Modal Configs
  $scope.customFullscreen = false;
  $scope.linkDetail = function(ev) {
    $mdDialog.show({
      controller: DialogController,
      templateUrl: 'dialog/linkDialog.tmpl.html',
      parent: angular.element(document.body),
      scope: $scope.$new(),
      targetEvent: ev,
      clickOutsideToClose:true,
      fullscreen: $scope.customFullscreen // Only for -xs, -sm breakpoints.
    });
  };

  $scope.linkGraph = function(ev) {
    $mdDialog.show({
      controller: DialogController,
      templateUrl: 'dialog/linkGraph.tmpl.html',
      parent: angular.element(document.body),
      scope: $scope.$new(),
      targetEvent: ev,
      clickOutsideToClose:true,
      onComplete: generateGraph,
      fullscreen: $scope.customFullscreen // Only for -xs, -sm breakpoints.
    });

    function generateGraph(scope, element, options) {
      $.getScript('./adhocquery/linkGraph.js', function()
      {
        var myGraph = new window.graph(window.d3, "md-dialog-content"); //http://d3js.org/
        window.init_page(myGraph, $scope.rootPax, $scope.paxLinks);
      });
    }
  };
  function DialogController($scope, $mdDialog, $sce) {
    $scope.hide = function() {
      $mdDialog.hide();
    };
    $scope.cancel = function() {
      $mdDialog.cancel();
    };
  }

});
