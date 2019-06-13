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
                  ruleCats, caseService, $state, uiGridConstants, $timeout, $interval) {

            spinnerService.hide('html5spinner');
            $scope.casesList = newCases.data.cases;
            $scope.casesListWithCats=[];
            $scope.ruleCats=ruleCats.data;
            $scope.pageSize = 10;
            $scope.pageNumber = 1;
            $scope.emptyString = "";
            $scope.showCountdownLabelFlag = false;
            $scope.trueFalseBoolean = "YES";
            $scope.model = caseDispositionService.getDefaultModel();

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

//            caseDispositionService.getAppConfigAPISFlag().then(function (response) {
//                $timeout(function () {
//                    $scope.showCountdownLabelFlag = ((typeof response !== undefined) && response.data.startsWith("Y")) ? true : false;
//
//                }, 1000);
//            });


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
                    pageNumber:   $scope.pageNumber,
                    pageSize:     $scope.pageSize,
                    sort:     $scope.model.sort,
                    model:        $scope.model
                };
                spinnerService.show('html5spinner');
                caseDispositionService.getPagedCases(postData).then(
                    function(data){
                        $scope.casesDispGrid.data = data.data.cases;
                        $scope.casesList = data.data.cases;
                        $scope.casesDispGrid.totalItems = data.data.totalCases;
                        spinnerService.hide('html5spinner');
                    });
            };

            $scope.refreshCountDown = function () {

                var currentTimeMillis = caseDispositionService.getCurrentServerTime();

                if (!currentTimeMillis)
                {
                   currentTimeMillis = (new Date()).getTime();
                   console.log("Using client side time for cases countdown.");
                }

                for (var i = 0; i < $scope.casesList.length; i++)
                {
                    var etdEtaTimeMillis = 0;
                    if (($scope.casesList[i].flightDirection === "O") || ($scope.casesList[i].flightDirection === "C"))
                    {
                       etdEtaTimeMillis = $scope.casesList[i].flightETDDate;
                    }
                    else
                    {
                      etdEtaTimeMillis = $scope.casesList[i].flightETADate;
                    }

                    var countDownMillis = etdEtaTimeMillis - currentTimeMillis;
                    var countDownSeconds = Math.trunc(countDownMillis/1000);

                    var daysLong = Math.trunc(countDownSeconds/86400);
                    var secondsRemainder1 = countDownSeconds % 86400;
                    var hoursLong = Math.trunc(secondsRemainder1/3600);
                    var secondsRemainder2 = secondsRemainder1 % 3600;
                    var minutesLong = Math.trunc(secondsRemainder2/60);

                    var daysString = (countDownSeconds < 0 && daysLong === 0) ? "-" + daysLong.toString() : daysLong.toString();

                    var countDownString = daysString + "d " + Math.abs(hoursLong) + "h " + Math.abs(minutesLong) + "m";

                    $scope.casesList[i].countDownTimeDisplay = countDownString;
                }

                $scope.casesDispGrid.data = $scope.casesList;

            };

            $timeout(function () {
                $interval(function () {$scope.refreshCountDown();}, 20000);
            },10000);


            $scope.casesDispGrid = {
                data: $scope.casesList,
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
                    $scope.resolvePage();
                });

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
                    cellTemplate: '<md-button aria-label="type" href="#/casedetail/{{row.entity.id}}" title="Launch Case Detail in new window" target="case.detail" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
                },
                {
                    field: 'countdown',
                    name: 'countdown',
                    displayName: 'Countdown Timer', headerCellFilter: 'translate',
                    cellTemplate: '<div><span class="countdown2">{{row.entity.countDownTimeDisplay}}</span></div>'
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
              var filters = ['paxname', 'flight', 'dispstatus', 'withTimeLeft', 'rulecats','etaetdfilter', 'dateLabel', 'casesDate']; //, 'priority', 'dateLabel', 'date'
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
                        $scope.casesList = data.data.cases;
                        $scope.casesDispGrid.totalItems = data.data.totalCases;
                            }
                            else{
                            $scope.errorToast("No Results Found", toastPosition)
                        }
                        spinnerService.hide('html5spinner');
                    });
            };

            $scope.reset = function () {
                $scope.model.name = $scope.emptyString;
                $scope.model.flightNumber = $scope.emptyString;
                $scope.model.status = $scope.emptyString;
                $scope.model.ruleCat = $scope.emptyString;
                $scope.model.etaStart = caseDispositionService.getDefaultStartDate();
                $scope.model.etaEnd = caseDispositionService.getDefaultEndDate();
                $scope.model.displayStatusCheckBoxes = caseDispositionService.getDefaultDispCheckboxes();
                $scope.model.sort = caseDispositionService.getDefaultSort();
                $scope.model.withTimeLeft = caseDispositionService.getDefaultTimeLeft();
                $scope.resolvePage();
            };

        })
}());
