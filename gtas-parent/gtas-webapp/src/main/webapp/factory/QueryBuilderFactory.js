/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
app.factory('queryBuilderFactory', function () {
    'use strict';
    return function ($scope, $timeout, jqueryQueryBuilderService, $interval, $mdSidenav) {
        var today = moment().format('YYYY-MM-DD').toString(),
            model = {
                summary: {
                    query: function (obj) {
                        this.title = obj ? obj.title : '';
                        this.description = obj ? obj.description : null;
                    },
                    rule: function (obj) {
                        this.title = obj ? obj.title : '';
                        this.description = obj ? obj.description : null;
                        this.startDate = obj ? obj.startDate : today;
                        this.endDate = obj ? obj.endDate : null;
                        this.enabled = obj ? obj.enabled : true;
                    }
                }
            };

        $scope.prompt = {
            open: function (mode) {
                if ($scope.ruleId === null) {
                    $scope.loadSummary(new model.summary[mode]());
                }
                $scope.selectedMode = mode;
                $mdSidenav(mode)
                    .open()
                    .then(function () {
                        console.log("toggle sidenav is done");
                    });
            },
            cancel: function (mode) {
                this[mode] = false;
                $mdSidenav(mode)
                    .close()
                    .then(function () {
                        console.log("toggle sidenav is done");
                    });
            }
        };

        $scope.setData = {
            query: function (myData) {
                var data = [];
                if (myData === undefined || !Array.isArray(myData)) {
                    $scope.saving = false;
                    return;
                }

                myData.forEach(function (obj) {
                    data.push(obj);
                });
                $scope.qbGrid.data = data;
            },
            rule: function (myData) {
                var temp, data = [];
                myData.forEach(function (obj) {
                    temp = $.extend({}, obj.summary, {
                        id: obj.id,
                        modifiedOn: obj.modifiedOn,
                        modifiedBy: obj.modifiedBy
                    });
                    data.push(temp);
                });
                $scope.qbGrid.data = data;
            }
        };

        $scope.executeQuery = function () {
            var query = $scope.$builder.queryBuilder('getDrools');
            if (query === false) {
                $scope.alertError('Can not execute / invalid query');
                return;
            }
            localStorage['query'] = JSON.stringify(query);
            localStorage['qbTitle'] = $scope.title.length ? $scope.title.trim() : '';
        };

        $scope.ruleId = null;

        $scope.alerts = [];
        $scope.alert = function (type, text) {
            $scope.alerts.push({type: type, msg: text});
            $timeout(function () {
                $scope.alerts[$scope.alerts.length - 1].expired = true;
            }, 4000);
            $timeout(function () {
                $scope.alerts.splice($scope.alerts.length - 1, 1);
            }, 5000);
        };

        $scope.alertSuccess = function (text) {
            $scope.alert('success', text);
        };

        $scope.alertError = function (text) {
            $scope.alert('danger', text);
        };

        $scope.alertInfo = function (text) {
            $scope.alert('info', text);
        };

        $scope.alertWarn = function (text) {
            $scope.alert('warning', text);
        };

        $scope.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        };

        $scope.updateQueryBuilderOnSave = function (myData) {
            if (myData.status === 'FAILURE') {
                $scope.alertError(myData.message);
                $scope.saving = false;
                return;
            }
            if (typeof myData.errorCode !== "undefined") {
                $scope.alertError(myData.errorMessage);
                return;
            }

            jqueryQueryBuilderService.getList().then(function (myData) {
                $scope.setData[$scope.mode](myData.result);
                $interval(function () {
                    var page;
                    if (!$scope.selectedIndex) {
                        page = $scope.gridApi.pagination.getTotalPages();
                        $scope.selectedIndex = $scope.qbGrid.data.length - 1;
                        $scope.gridApi.pagination.seek(page);
                    }
                    $scope.gridApi.selection.clearSelectedRows();
                    $scope.gridApi.selection.selectRow($scope.qbGrid.data[$scope.selectedIndex]);
                    $scope.saving = false;
                }, 0, 1);
            });
        };

        $scope.rowSelection = function (gridApi) {
            $scope.gridApi = gridApi;
            gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                if (row.isSelected) {
                    $scope.loadRuleOnSelection(row);
                } else {
                    $scope.newRule();
                    $scope.gridApi.selection.clearSelectedRows();
                }
            });
        };

        $scope.delete = function () {
            if (!$scope.ruleId) {
                $scope.alertError('No rule loaded to delete');
                return;
            }

            var selectedRowEntities = $scope.gridApi.selection.getSelectedRows();

            selectedRowEntities.forEach(function (rowEntity) {
                var rowIndexToDelete = $scope.qbGrid.data.indexOf(rowEntity);

                jqueryQueryBuilderService.delete($scope.ruleId).then(function (response) {
                    $scope.qbGrid.data.splice(rowIndexToDelete, 1);
                    $scope.newRule();
                });
            });
        };

        $scope.today = today;
        $scope.calendarOptions = {
            format: 'yyyy-mm-dd',
            autoClose: true
        };
        $scope.options = {
            allow_empty: true,
            service: "DROOLS",
            plugins: {
                'bt-tooltip-errors': {delay: 100},
                'sortable': null,
                'filter-description': {mode: 'bootbox'},
                'bt-selectpicker': null,
                'unique-filter': null,
                'bt-checkbox': {color: 'primary'}
            },
            filters: []
        };

        $scope.loadSummary = function (summary) {
            Object.keys(summary).forEach(function (key) {
                $scope[key] = summary[key];
            });
        };

        $scope.formats = ["YYYY-MM-DD"];

        $scope.newRule = function () {
            $scope.ruleId = null;
            $scope.$builder.queryBuilder('reset');
            ////TODO temp had trouble setting md-datepicker default value to null, default value today
            //if (document.querySelector('[ng-model="endDate"] input')) {
            //    document.querySelector('[ng-model="endDate"] input').value = null;
            //}
            //
            ////TODO find angular way to set focus
            //document.querySelector('[autofocus]').focus();
            //if ($scope.gridApi !== undefined) {
            //    $scope.gridApi.selection.clearSelectedRows();
            //    $scope.selectedIndex = null;
            //}
        };
        $scope.ruleId = null;
        $scope.saving = false;
        $scope.save = {
            query: {
                cancel: function () {
                    $scope.prompt.cancel('query');
                },
                prompt: function () {
                    $scope.prompt.open('query');
                },
                confirm: function () {
                    var queryObject, query;
                    if ($scope.saving) {
                        return;
                    }

//                    $scope.saving = true;

                    if ($scope.title && $scope.title.length) {
                        $scope.title = $scope.title.trim();
                    }

                    if (!$scope.title.length) {
                        $scope.alertError('Title summary can not be blank!');
                        $scope.saving = false;
                        return;
                    }
                    query = $scope.$builder.queryBuilder('getDrools');

                    if (query === false) {
                        $scope.saving = false;
                        return;
                    }

                    queryObject = {
                        id: $scope.ruleId,
                        title: $scope.title,
                        description: $scope.description || null,
                        query: query
                    };

                    jqueryQueryBuilderService.save(queryObject).then($scope.updateQueryBuilderOnSave);
                }
            },
            rule: {
                cancel: function () {
                    $scope.prompt.cancel('rule');
                },
                prompt: function () {
                    $scope.prompt.open('rule');
                },
                confirm: function () {
                    var ruleObject, details;

                    if ($scope.saving) {
                        return;
                    }

//                    $scope.saving = true;

                    if ($scope.title && $scope.title.length) {
                        $scope.title = $scope.title.trim();
                    }

                    if (!$scope.title.length) {
                        $scope.alertError('Title summary can not be blank!');
                        $scope.saving = false;
                        return;
                    }

                    if ($scope.ruleId === null) {
                        //if (!startDate.isValid()) {
                        //    $scope.alertError('Dates must be in this format: ' + $scope.formats.toString());
                        //    $scope.saving = false;
                        //    return;
                        //}
                        //if (startDate < $scope.today) {
                        //    $scope.alertError('Start date must be today or later when created new.');
                        //    $scope.saving = false;
                        //    return;
                        //}
                    }

                    if ($scope.endDate) {
                        //if (!endDate.isValid()) {
                        //    $scope.alertError('End Date must be empty/open or in this format: ' + $scope.formats.toString());
                        //    $scope.saving = false;
                        //    return;
                        //}
                        //if (endDate < startDate) {
                        //    $scope.alertError('End Date must be empty/open or be >= startDate: ' + $scope.formats.toString());
                        //    $scope.saving = false;
                        //    return;
                        //}
                    }

                    details = $scope.$builder.queryBuilder('getDrools');

                    if (details === false) {
                        $scope.saving = false;
                        return;
                    }
                    ruleObject = {
                        id: $scope.ruleId,
                        details: details,
                        summary: new model.summary[$scope.mode]($scope)
                    };

                    jqueryQueryBuilderService.save(ruleObject).then($scope.updateQueryBuilderOnSave);
                }
            }
        };
    };
});
