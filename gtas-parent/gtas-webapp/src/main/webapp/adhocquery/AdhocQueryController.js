/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
app.controller('AdhocQueryCtrl', function ($scope, adhocQueryService) {
    'use strict;'

    $scope.resultsGrid = {
        paginationPageSize: 10,
        paginationCurrentPage: 1,
        useExternalPagination: true,
        useExternalSorting: true,
        useExternalFiltering: true,
        enableHorizontalScrollbar: 0,
        enableVerticalScrollbar: 0,
        enableColumnMenus: false,
        multiSelect: false,
        minRowsToShow: 10,
        enableExpandableRowHeader: false,
        expandableRowTemplate: '<div ui-grid="row.entity.subGridOptions"></div>',        

        onRegisterApi: function (gridApi) {
            $scope.gridApi = gridApi;
        }
    };

    $scope.resultsGrid.columnDefs = [
        {
            field: 'lastName',
            name: 'lastName',
            displayName: 'pass.lastname', headerCellFilter: 'translate',
            cellTemplate: '<md-button aria-label="type" href="#/paxdetail/{{row.entity.id}}/{{row.entity.flightId}}" title="Launch Flight Passengers in new window" target="pax.detail.{{row.entity.passengerId}}.{{row.entity.flightId}}" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
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
        }
    ];

    $scope.searchPax = function () {
        return adhocQueryService.getPassengers($scope.query, 1).then(function (response) {
            console.log(response.data.result);
            $scope.resultsGrid.data = response.data.result;
        });
    }
});

