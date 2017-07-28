/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
app.controller('CasesCtrl', function ($scope, newCases, $sce, caseService, gridService) {
    'use strict;'
	$scope.pageSize = 10;

    $scope.hitTypeIcon = function (hitType) {
        var icons = '&nbsp;';
        if (hitType) {
            if (hitType.includes('R')) {
                icons += '<i class="fa fa-flag" style="color:red"></i>&nbsp;';
            }
            if (hitType.includes('P')) {
                icons += '<i class="fa fa-user" style="color:rgb(255, 176, 22)"></i>&nbsp;';
            }
            if (hitType.includes('D')) {
                icons += '<i class="fa fa-file" style="color:rgb(255, 176, 22)"></i>';
            }
        }
        return $sce.trustAsHtml(icons);
    };

    caseService.getDispositionStatuses().then(function (response) {
        $scope.dispositionStatuses = response.data;
    });

    $scope.casesGrid = {
        data: newCases.data,
        paginationPageSizes: [10, 15, 25],
        paginationPageSize: $scope.pageSize,
        enableFiltering: true,
        enableHorizontalScrollbar: 0,
        enableVerticalScrollbar: 0,
        enableColumnMenus: false,
        multiSelect: false,
        enableExpandableRowHeader: false,

        onRegisterApi: function (gridApi) {
            $scope.gridApi = gridApi;

            gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                $scope.pageSize = pageSize;
            });
        }
    };

    $scope.casesGrid.columnDefs = [
        {
            field: 'lastName',
            name: 'lastName',
            displayName: 'pass.lastname', headerCellFilter: 'translate',
            cellTemplate: '<md-button aria-label="type" href="#/paxdetail/{{row.entity.passengerId}}/{{row.entity.flightId}}" title="Launch Flight Passengers in new window" target="pax.detail.{{row.entity.passengerId}}.{{row.entity.flightId}}" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
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
            field: 'hitType',
            name: 'hitType',
            displayName: 'Hit Type',
            cellTemplate: '<div ng-bind-html="grid.appScope.hitTypeIcon(COL_FIELD)"></div>'
        },
        {
            field: 'flightNumber',
            name: 'flightNumber',
            displayName: 'pass.flight', headerCellFilter: 'translate',
            cellTemplate: '<div>{{row.entity.carrier}}{{COL_FIELD}}</div>'
        },
        {
            field: 'flightEtd',
            name: 'flightEtd',
            displayName: 'pass.etd', headerCellFilter: 'translate',
            cellFilter: 'date: "MM/dd/yyyy hh:mm:ss"',
            enableFiltering: false
        },
        {
            field: 'flightEta',
            name: 'flightEta',
            displayName: 'pass.eta', headerCellFilter: 'translate',
            cellFilter: 'date: "MM/dd/yyyy hh:mm:ss"',
            enableFiltering: false
        },
        {
            field: 'flightDirection',
            name: 'flightDirection',
            displayName: 'Direction'
        },
        {
            field: 'createDate',
            name: 'createDate',
            displayName: 'Last Updated',
            cellFilter: 'date',
            enableFiltering: false
        },
        {
            field: 'status',
            name: 'status',
            displayName: 'Status'
        }
    ];

    $scope.getTableHeight = function(){
    	return gridService.calculateGridHeight($scope.pageSize);
    };
});
