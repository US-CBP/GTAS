/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
app.controller('CasesCtrl', function ($scope, newCases) {
		'use strict;'
		
	$scope.casesGrid = {
			data: newCases.data,
            paginationPageSizes: [10, 15, 25],
            paginationPageSize: 10,
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
            }    
        };
	
	$scope.casesGrid.columnDefs = [
	                               {
                                       field: 'lastName',
                                       name: 'lastName',
                                       displayName:'pass.lastname', headerCellFilter: 'translate',
                                       cellTemplate: '<md-button aria-label="type" href="#/paxdetail/{{row.entity.passengerId}}/{{row.entity.flightId}}" title="Launch Flight Passengers in new window" target="pax.detail.{{row.entity.passengerId}}.{{row.entity.flightId}}" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
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
                                       field: 'createDate',
                                       name: 'createDate',
                                       displayName:'Last Updated',
                                       cellFilter:'date'
                                   },
                                   {
                                	   field: 'status',
                                       name: 'status',
                                       displayName:'Status'
                                   }
                               ];
	});

