/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app.controller('CaseDispositionCtrl',
        function ($scope, $http, $mdToast,
                  gridService,
                  spinnerService, caseDispositionService, newCases, caseService, $state) {

            $scope.casesList = newCases.data.cases;


            $scope.errorToast = function (error) {
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

            caseService.getDispositionStatuses().then(function (response) {
                $scope.dispositionStatuses = response.data;
            });

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

            $scope.transitionToCaseDetail = function(x) {
                $state.transition('detail@caseDisposition', {id: x});
            };

            caseService.getDispositionStatuses().then(function (response) {
                $scope.dispositionStatuses = response.data;
            });

            $scope.casesGrid = {
                data: newCases.data.cases,
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
                    field: 'flightNumber',
                    name: 'flightNumber',
                    displayName: 'Flight', headerCellFilter: 'translate',
                    cellTemplate: '<md-button aria-label="type" href="#/casedetail/{{row.entity.flightId}}/{{row.entity.paxId}}" title="Launch Case Detail in new window" target="case.detail" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
                },
                {
                    field: 'lastName',
                    name: 'lastName',
                    displayName: 'Last Name', headerCellFilter: 'translate'
                },
                {
                    field: 'firstName',
                    name: 'firstName',
                    displayName: 'First Name', headerCellFilter: 'translate'
                },
                {
                    field: 'paxName',
                    name: 'paxName',
                    displayName: 'Passenger Name', headerCellFilter: 'translate'
                },
                {
                    field: 'hitType',
                    name: 'hitType',
                    displayName: 'Hit Type'
                },
                {
                    field: 'status',
                    name: 'status',
                    displayName: 'Status'
                }
            ];

            $scope.getTableHeight = function () {
                return gridService.calculateGridHeight($scope.pageSize);
            };

})
}());
