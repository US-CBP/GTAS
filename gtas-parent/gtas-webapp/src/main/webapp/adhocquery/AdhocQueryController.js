/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.controller('AdhocQueryCtrl', function ($scope, $rootScope, $mdToast, uiGridConstants, adhocQueryService, searchBarResults, gridService) {
    'use strict;'
	
	$scope.pageSize = 10;
	$scope.pageNumber = 1;
	var _content = $rootScope.searchBarContent.content;
	
	$scope.query = { 
		content : function(newQueryString){
			return arguments.length ? (_content = newQueryString) : _content;
			}
		}
	
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

    $scope.resultsGrid.columnDefs = [
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
            displayName: 'pass.etd', headerCellFilter: 'translate'
        },
        {
            field: 'eta',
            name: 'eta',
            displayName: 'pass.eta', headerCellFilter: 'translate'
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
        column: 'lastName', 
        dir: 'asc'
    };
    
    $scope.getTableHeight = function(){
    	return gridService.calculateGridHeight($scope.pageSize);
    };
    
    $scope.sort = defaultSort;
    
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
    }
});

