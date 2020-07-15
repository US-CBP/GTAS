/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app.controller('CaseDispositionCtrl',
        function ($scope, $rootScope, $http, $mdToast, $filter,
                  gridService, $translate,
                  spinnerService, caseDispositionService, caseModel,
                  ruleCats, caseService, $state, uiGridConstants, $timeout, $interval,$uibModal, $mdDialog, APP_CONSTANTS, configService, paxReportService,  codeTooltipService) {

            spinnerService.hide('html5spinner');
            $scope.casesList;
            $scope.casesListWithCats=[];
            $scope.ruleCats=ruleCats.data;
            $scope.pageSize = 10;
            $scope.pageNumber = 1;
            $scope.emptyString = "";
            $scope.showCountdownLabelFlag = false;
            $scope.trueFalseBoolean = "YES";
            $scope.model = caseModel;

            $scope.errorToast = function (error, toastPosition) {
                $mdToast.show($mdToast.simple()
                    .content(error)
                    .position('top right')
                    .hideDelay(4000)
                    .parent(toastPosition));
            };
            $scope.getCarrierCodeCodeTooltipData = function(flightNumber) {
                let code = flightNumber.substring(0,2);
                let tooltip = codeTooltipService.getCodeTooltipData(code, "carrier");
                return tooltip + " (" + code + ")";
              };

            var exporter = {
                'csv': function () {
                    $scope.gridApi.exporter.csvExport('all', 'all');
                },
                'pdf': function () {
                    $scope.gridApi.exporter.pdfExport('all', 'all');
                }
            };
            $scope.enableEmailNotification =  configService.enableEmailNotificationService().then(function(value) {
                $scope.enableEmailNotification = value.data;
            });
            $scope.isEmailEnabled = function() {
                return $scope.enableEmailNotification === 'true';
            };

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
/*
            $timeout(function () {
                $interval(function () {$scope.refreshCountDown();}, 20000);
            },10000);*/

            var fixGridData = function(grid, row, col, value) {
                if (col.name === 'countdown') {
                    value = row.entity.countDownTimeDisplay;
                }
                if (col.name === 'eta' || col.name === 'etd') {
                   value =   $filter('date')(value, 'yyyy-MM-dd HH:mm');
                }
                if (col.name === 'highPriorityRuleCatId') {
                    value = grid.appScope.casesListWithCats[row.entity.highPriorityRuleCatId];
                }
                return value;
              };

            $scope.deleteRow = function(row) {
                row.entity.status = 'Reviewed';
                caseDispositionService.updatePassengerHitViews(row.entity, 'REVIEWED').then(function(result) {
                }
            );
                if (! $scope.model.displayStatusCheckBoxes.REVIEWED) {
                    const index = $scope.casesDispGrid.data.indexOf(row.entity);
                    $scope.casesDispGrid.data.splice(index, 1);
                }
            };

        $scope.getPaxDetailReport  = function(row) {
            const pax = row.entity;
            $scope.row = row;
            $scope.paxId = pax.paxId;
            $scope.flightId = pax.flightId;

            paxReportService.getPaxDetailReport( pax.paxId, pax.flightId).then(
                function(data){

                    if(data)
                    {
                        var dataArray = data.data;
                        var byteArray = new Uint8Array(dataArray);
                        var a = window.document.createElement('a');
                        a.href = window.URL.createObjectURL(new Blob([byteArray], { type: 'application/pdf' }));
                        a.download = "gtas_event_report";
                        document.body.appendChild(a);
                        a.click();
                        document.body.removeChild(a);
                    }
                    else
                    {
                        consol.log("ERROR! Error in generating GTAS Event Report. No data was retured")
                    }

                });

            return true;
        };

            $scope.reOpen = function(row) {
                row.entity.status = 'Re_Opened';
                caseDispositionService.updatePassengerHitViews(row.entity, 'RE_OPENED').then(function(result) {
                }
            );
                if ( ! $scope.model.displayStatusCheckBoxes.RE_OPENED) {
                    const index = $scope.casesDispGrid.data.indexOf(row.entity);
                    $scope.casesDispGrid.data.splice(index, 1);
                }
            };
            $scope.notify = function(paxId) {
                $scope.paxId = paxId;
               var notificationModalInstance = $uibModal.open({
                    animation: true,
                    ariaLabelledBy: 'modal-title',
                    ariaDescribedBy: 'modal-body',
                    templateUrl:'common/notificationTemplate.html',
                    backdrop: true,
                    controller: 'EmailNotificationModalCtrl',
                    scope: $scope

                });

                notificationModalInstance.result.then(function() {
                    $uibModalInstance.close();
                })

            };

            

            $scope.review = function(row) {
                const pax = row.entity;
                $scope.paxId = pax.paxId;
                $scope.flightId = pax.flightId;
                $mdDialog.show({
                    controller: 'PassengerDetailCtrl',
                    templateUrl: 'pax/pax.detail.comment.html',
                    clickOutsideToClose: true,
                    fullscreen: true,
                    resolve: {
                        passenger: function (paxDetailService) {
                            return paxDetailService.getPaxDetail($scope.paxId, $scope.flightId);
                        }
                        ,
                        user: function (userService) {
                            return userService.getUserData();
                        }
                        ,
                        eventNotes: function(paxNotesService){
                            return paxNotesService.getEventNotes($scope.paxId);
                        },
                        noteTypesList: function(paxNotesService){
                            return paxNotesService.getNoteTypes();
                        }
                        ,
                        ruleCats: function (caseDispositionService) {
                            return caseDispositionService.getRuleCats();
                        }
                        ,
                        ruleHits: function (paxService) {
                            return paxService.getRuleHitsByFlightAndPax($scope.paxId, $scope.paxId);
                        }
                        ,
                        watchlistLinks: function (paxDetailService) {
                            return paxDetailService.getPaxWatchlistLink($scope.paxId)
                        },
                        disableLinks: function() {
                            return true;
                        },
                        $uibModalInstance: function() {
                            
                        }
                    }
                }).then(function(answer) {
                    if (answer === 'reviewed') {
                        $scope.deleteRow(row);
                    } else if (answer === 'fullPax') {
                        window.location.href = APP_CONSTANTS.HOME_PAGE + "#/paxdetail/" + pax.paxId + "/" + pax.flightId;
                    } else if (answer === 'notify') {
                        $scope.notify(pax.paxId);
                    }
                });
            };

            $scope.showPassenger = function (row) {
                const pax = row.entity;
                $scope.row = row;
                $scope.paxId = pax.paxId;
                $scope.flightId = pax.flightId;
                $scope.answer="";
                var paxModalInstance = $uibModal.open({
                    animation: true,
                    backdrop: true,
                    ariaLabelledBy: 'modal-title',
                    ariaDescribedBy: 'modal-body',
                    controller: 'PassengerDetailCtrl',
                    templateUrl: 'pax/pax.detail.modal.html',
                    windowClass: 'my-modal-popup',
                    // clickOutsideToClose: true,
                    // fullscreen: true,
                    size: 'lg',
                    resolve: {
                        passenger: function (paxDetailService) {
                            return paxDetailService.getPaxDetail($scope.paxId, $scope.flightId);
                        }
                        ,
                        user: function (userService) {
                            return userService.getUserData();
                        }
                        ,
                        ruleCats: function (caseDispositionService) {
                            return caseDispositionService.getRuleCats();
                        }
                        ,
                        ruleHits: function (paxService) {
                            return paxService.getRuleHitsByFlightAndPax($scope.paxId, $scope.paxId);
                        }
                        ,
                        watchlistLinks: function (paxDetailService) {
                            return paxDetailService.getPaxWatchlistLink($scope.paxId)
                        },
                        disableLinks: function() {
                            return true;
                        },
                        eventNotes: function(paxNotesService){
                        	return paxNotesService.getEventNotes($scope.paxId);
                        },
                        noteTypesList: function(paxNotesService){
                            return paxNotesService.getNoteTypes();
                        }
                    }
                });
                paxModalInstance.result.then(function(answer) {
                    if (answer === 'reviewed') {
                        $scope.deleteRow(row);
                    } else if (answer === 'fullPax') {
                        window.location.href = APP_CONSTANTS.HOME_PAGE + "#/paxdetail/" + pax.paxId + "/" + pax.flightId;
                    } else if (answer === 'notify') {
                        $scope.notify(pax.paxId);
                    } else if (answer === 'reOpen') {
                        $scope.reOpen(row);
                    }

                });
            };
            $scope.casesDispGrid = {
                data: $scope.casesList,
                paginationPageSizes: [10, 15, 25],
                paginationPageSize: $scope.pageSize,
                paginationCurrentPage: $scope.pageNumber,
                rowHeight: 75,
                useExternalPagination: true,
                enableFiltering: false,
                enableHorizontalScrollbar:  uiGridConstants.scrollbars.NEVER,
                enableVerticalScrollbar:  uiGridConstants.scrollbars.NEVER,
                enableColumnMenus: false,
                multiSelect: false,
                enableExpandableRowHeader: false,
                enableGridMenu: true,
                exporterPdfDefaultStyle: {fontSize: 9},
                exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
                exporterPdfFooter: function ( currentPage, pageCount ) {
                    return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
                },
                exporterPdfPageSize: 'LETTER',
                exporterPdfMaxGridWidth: 500,
                exporterCsvFilename: 'case-disposition.csv',
                exporterExcelFilename: 'case-disposition.xlsx',
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
                    $scope.resolvePage();
                });

                    gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                        $scope.pageNumber = newPage;
                        $scope.pageSize = pageSize;
                        $scope.model.pageSize = pageSize;
                        $scope.model.pageNumber = newPage;
                        $scope.resolvePage();
                    });
                }
            };

            $scope.casesDispGrid.columnDefs = [
                {
                    field: 'countdown',
                    name: 'countdown',
                    width: '20%',
                    minWidth: '210',
                    displayName: $translate.instant('flight.flight'),
                    cellTemplate:
                        '<div style="font-family: \'Roboto Mono\', monospace; font-size: 14px;" >' +
                        '<div class="flex">' +
                        '<span class="sm-pad">' +
                        '<div><span ng-if="row.entity.flightDirection === \'O\'" >{{row.entity.flightNumber}}</span></div>' +
                        '<div><span ng-if="row.entity.flightDirection === \'I\'" >{{row.entity.flightNumber}}</span></div>' +
                        '<div><span ng-class="row.entity.closeToCountDown ? \'badge danger-back danger-border-th\': \'badge info-back info-border-th\'" ng-if="row.entity.flightDirection === \'O\'" tooltip-placement="right" uib-tooltip={{grid.appScope.getCarrierCodeCodeTooltipData(row.entity.flightNumber)}}><i class="fa fa-plane" aria-hidden="true"></i> {{row.entity.countDownTimeDisplay}}</span></div>' +
                        '<div><span ng-class="row.entity.closeToCountDown ? \'badge danger-back danger-border-th\': \'badge info-back info-border-th\'" ng-if="row.entity.flightDirection === \'I\'" tooltip-placement="right" uib-tooltip={{grid.appScope.getCarrierCodeCodeTooltipData(row.entity.flightNumber)}}><i class="fa fa-flip-vertical fa-plane" aria-hidden="true"></i> {{row.entity.countDownTimeDisplay}}</span></div>' +
                        '</span>' +
                        '</div>' +
                        '</div>' +
                        '<div style="font-family: \'Roboto Mono\', monospace; padding-top: 3%; font-size: 14px;" ><span>' +
                        '<div><span><i class="fa fa-arrow-circle-up" aria-hidden="true"></i> {{row.entity.flightOrigin}} {{row.entity.flightETDDate | date:\'MM-dd HH:mm\'}}</div></span>' +
                        '<div><span><i class="fa fa-arrow-circle-down" aria-hidden="true"></i> {{row.entity.flightDestination}} {{row.entity.flightETADate | date:\'MM-dd HH:mm\'}}</div></span>' +
                  '</span>' +
                        '</div>'
                },
                {
                    field: 'highPriorityRuleCatId',
                    name: 'highPriorityRuleCatId',
                    width: '20%',
                    minWidth: '210',
                    displayName: $translate.instant('case.toprulecategory'),
                    cellTemplate: '<div style="font-size: 12px;"><ul>' +
                        '' +
                        '<li ng-repeat="hit in row.entity.hitNames track by $index">{{hit}}</li>' +
                        '</ul>' +
                        '</div>'
                },
                {
                    field: 'lastName',
                    name: 'lastName',
                    width: '20%',
                    minWidth: '156',
                    displayName: $translate.instant('pass.lastNameFirstName'),
                    cellTemplate: '<div style="font-family: \'Roboto Mono\', monospace"><md-button aria-label="type" ng-click="grid.appScope.showPassenger(row)" ' +
                        'class="case-grid md-primary md-button md-default-theme"><div><ul style="list-style-type: none; font-size: 14px; padding-inline-start: 0px">' +
                        '<li>' +
                        '{{COL_FIELD}}, {{row.entity.firstName}}' +
                        '</li>' +
                        '<li>{{row.entity.dob}} / {{row.entity.nationality}} / {{row.entity.gender}}</li>' +
                        '<li>Doc({{row.entity.docType}}): {{row.entity.document}}</li>' +
                        '</ul>' +
                        '</div></md-button></div>'
                },
                {
                    field: 'status',
                    name: 'status',
                    width: '7%',
                    minWidth: '0',
                    displayName: $translate.instant('case.status'),
                    cellTemplate: '<div style="font-size: 14px;">{{COL_FIELD}}</div>'
                },
                {
                    field: 'status',
                    name: 'action',
                    displayName: $translate.instant('case.action'),
                    cellTemplate:
                        '<button ng-if="row.entity.status === \'Reviewed\'" class="btn btn-primary" ng-click="grid.appScope.reOpen(row)" style="margin:5px; font-size: 14px">Re-Open</button>' +
                        '<button ng-if="row.entity.status !== \'Reviewed\'" class="btn btn-primary" ng-click="grid.appScope.review(row)" style="margin:5px; font-size: 14px">{{"btn.review" | translate}}</button>' +
                        '<button ng-if="grid.appScope.isEmailEnabled()" class="btn btn-warning" ng-click="grid.appScope.notify(row.entity.paxId)" style="margin:5px; font-size: 14px"><span class="glyphicon glyphicon-envelope" area-hidden="true"></span> {{"btn.notify" | translate}}</button>' +
                        '<button  type="submit" class="btn btn-info" ng-click="grid.appScope.getPaxDetailReport(row)" style="margin:5px; font-size: 14px">' +
                        '{{\'btn.downloadreportshort\' | translate}}' +
                        '</button>'

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
              var filters = ['paxname', 'flight', 'dispstatus', 'rulecats','etaetdfilter', 'dateLabel', 'date', 'onlyMyRules', 'ruleTypes']; //, 'priority', 'dateLabel', 'date'
              return filters.includes(option);
            };

            $scope.clearFilters = function(){
                let defaultSort = [
                        {column: 'countDown', dir: 'asc'},

                    ];
                let    displayStatusCheckBoxes = {
                        NEW: true,
                        RE_OPENED: true,
                        REVIEWED: false
                    };
               let     ruleTypes = {
                        WATCHLIST: true,
                        USER_RULE: true,
                        GRAPH_RULE: true,
                        MANUAL: true,
                        EXTERNAL_RULE: true,
                        PARTIAL_WATCHLIST: false
                    };
                 let   ruleCatFilter;
                 let   startDate = new Date();
                 let   endDate = new Date();
                endDate.setDate(endDate.getDate() + 1);
                startDate.setMinutes(startDate.getMinutes() - 15);
                ruleCatFilter = caseDispositionService.getDefaultCats();
                $scope.model.pageNumber = 1;
                $scope.model.pageSize = typeof $scope.model.pageSize != "undefined" ? $scope.model.pageSize : 10;
                $scope.model.origin = [];
                $scope.model.dest = [];
                $scope.model.etaStart = startDate;
                $scope.model.ruleCatFilter = ruleCatFilter;
                $scope.model.myRulesOnly = false;
                $scope.model.etaEnd = endDate;
                $scope.model.ruleTypes = ruleTypes;
                $scope.model.sort = defaultSort;
                $scope.model.displayStatusCheckBoxes = displayStatusCheckBoxes;
                $scope.model.withTimeLeft = true;
                $scope.filter();
            };

            $scope.filter = function () {
                spinnerService.show('html5spinner');
                var toastPosition = angular.element(document.getElementById('casesDispGrid'));
                caseDispositionService.getByQueryParams($scope.model).then(
                    function(data){
                        if(data!=null && data.data.cases!=null){
                            if(data.data.cases.length < 1){
                                $scope.errorToast($translate.instant('msg.noresultsfound'), toastPosition);
                            }
                        $scope.casesDispGrid.data = data.data.cases;
                        $scope.casesList = data.data.cases;
                        $scope.casesDispGrid.totalItems = data.data.totalCases;
                            }
                            else{
                            $scope.errorToast($translate.instant('msg.noresultsfound'), toastPosition)
                        }
                        spinnerService.hide('html5spinner');
                    });

            };
            $scope.resolvePage();
        });
}());
