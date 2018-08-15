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
                  spinnerService, caseDispositionService, newCases,
                  ruleCats, caseService, $state, uiGridConstants, $timeout) {

            spinnerService.hide('html5spinner');
            $scope.casesList = newCases.data.cases;
            $scope.casesListWithCats=[];
            $scope.ruleCats=ruleCats.data;
            $scope.pageSize = 10;
            $scope.pageNumber = 1;
            $scope.emptyString = "";
            $scope.showCountdownLabelFlag = false;
            $scope.trueFalseBoolean = "YES";

            $scope.model={
                name: $scope.emptyString,
                flightNumber: $scope.emptyString,
                status : $scope.emptyString,
                priority: $scope.emptyString,
                ruleCat: $scope.emptyString,
                etaStart: $scope.emptyString,
                etaEnd: $scope.emptyString,
                etaEtdFilter: $scope.emptyString,
                etaEtdSortFlag: $scope.emptyString
            };

            $scope.sortEtaEtdFlag=[
                {id: 'flightETADate', name: ''},
                {id: 'flightETADate', name: 'ETA'},
                {id: 'flightETDDate', name: 'ETD'}
            ];

            $scope.sortEtaEtdFlagStatuses=[
                {id: '', name: ''},
                {id: 'asc', name: 'ASCENDING'},
                {id: 'desc', name: 'DESCENDING'}
            ];

            $scope.model.reset = function(){
                angular.forEach($scope.model, function(item, index){
                    index = $scope.emptyString;
                });
            };

            $scope.errorToast = function (error, toastPosition) {
                $mdToast.show($mdToast.simple()
                    .content(error)
                    .position('top right')
                    .hideDelay(4000)
                    .parent(toastPosition));
            };

            var exporter = {
                'csv': function () {
                    $scope.gridApi.exporter.csvExport('all', 'all');
                },
                'pdf': function () {
                    $scope.gridApi.exporter.pdfExport('all', 'all');
                }
            };

            caseDispositionService.getAppConfigAPISFlag().then(function (response) {
                $timeout(function () {
                    $scope.showCountdownLabelFlag = ((typeof response !== undefined) && response.data.startsWith("Y")) ? true : false;

                }, 1000);
            });

            caseService.getDispositionStatuses().then(function (response) {
                $scope.dispositionStatuses = response.data;
                $scope.statusGroup = $scope.dispositionStatuses;
            });

            $scope.assignRuleCats = function(){
                    angular.forEach($scope.ruleCats, function(item, index){
                        $scope.casesListWithCats[item.catId] = item.category;
                    });
            };

            $scope.assignRuleCats();

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
                $scope.statusGroup = $scope.dispositionStatuses;
            });

            $scope.resolvePage = function () {
                var postData = {
                    pageNumber: $scope.pageNumber,
                    pageSize: $scope.pageSize
                };
                spinnerService.show('html5spinner');
                caseDispositionService.getPagedCases(postData).then(
                    function(data){
                        $scope.casesDispGrid.data = data.data.cases;
                        $scope.casesDispGrid.totalItems = data.data.totalCases;
                        spinnerService.hide('html5spinner');
                    });
            };

            $scope.casesDispGrid = {
                data: newCases.data.cases,
                paginationPageSizes: [10, 15, 25],
                totalItems: newCases.data.totalCases,
                paginationPageSize: $scope.pageSize,
                paginationCurrentPage: $scope.pageNumber,
                useExternalPagination: true,
                enableFiltering: true,
                enableHorizontalScrollbar: 0,
                enableVerticalScrollbar: 0,
                enableColumnMenus: false,
                multiSelect: false,
                enableExpandableRowHeader: false,

                onRegisterApi: function (gridApi) {
                    $scope.gridApi = gridApi;

                    gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                        $scope.pageNumber = newPage;
                        $scope.pageSize = pageSize;
                        $scope.resolvePage();
                    });
                }
            };

            $scope.casesDispGrid.columnDefs = [
                {
                    field: 'flightNumber',
                    name: 'flightNumber',
                    displayName: 'Flight', headerCellFilter: 'translate',
                    cellTemplate: '<md-button aria-label="type" href="#/casedetail/{{row.entity.flightId}}/{{row.entity.paxId}}" title="Launch Case Detail in new window" target="case.detail" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
                },
                {
                    field: 'lastName',
                    name: 'countdown',
                    displayName: 'Countdown Timer', headerCellFilter: 'translate',
                    //cellTemplate: '<count-down date="{{row.entity.flightETDDate}}">'
                    cellTemplate: '<div class=\'time-to\' countdown=\'\' date="{{row.entity.flightETDDate}}" currentTime="{{row.entity.currentTime}}"' +
                    'showCountdownLabelFlag="{{grid.appScope.showCountdownLabelFlag}}"> </div>'
                },
                {
                    field: 'highPriorityRuleCatId',
                    name: 'highPriorityRuleCatId',
                    displayName: 'Top Rule Category',
                    cellTemplate: '<span>{{grid.appScope.casesListWithCats[COL_FIELD]}}</span>'
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
                    field: 'status',
                    name: 'status',
                    displayName: 'Status'
                }
            ];

            $scope.getTableHeight = function () {
                return gridService.calculateGridHeight(newCases.data.totalCases);
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
              };
            $scope.filterCheck = function(option) {
              var filters = ['paxname', 'flight', 'dispstatus', 'rulecats','etaetdfilter', 'dateLabel', 'casesDate']; //, 'priority', 'dateLabel', 'date'
              return filters.includes(option);
            };


            $scope.filter = function () {
                spinnerService.show('html5spinner');
                var toastPosition = angular.element(document.getElementById('casesDispGrid'));
                caseDispositionService.getByQueryParams($scope.model).then(
                    function(data){
                        if(data!=null && data.data.cases!=null){
                            if(data.data.cases.length < 1){
                                $scope.errorToast("No Results Found", toastPosition);
                            }
                        $scope.casesDispGrid.data = data.data.cases;
                        $scope.casesDispGrid.totalItems = data.data.totalCases;
                            }
                            else{
                            $scope.errorToast("No Results Found", toastPosition)
                        }
                        spinnerService.hide('html5spinner');
                    });
            };

            $scope.reset = function () {
                $scope.model.reset();
                $scope.model.etaEtdFilter = $scope.emptyString;
                $scope.model.etaEtdSortFlag = $scope.emptyString;
                $scope.resolvePage();
            };

        })
}());
