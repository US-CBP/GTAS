/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
app.controller('BuildController', function ($scope, $injector, $translate, jqueryQueryBuilderWidget, gridOptionsLookupService, jqueryQueryBuilderService, spinnerService, $mdSidenav, $stateParams, $interval, $timeout, $mdDialog, codeTooltipService) {
    'use strict';
    var todayDate = moment().toDate(),
        queryFlightsLink = document.querySelector('a[href="#/query/flights"]'),
        queryPassengersLink = document.querySelector('a[href="#/query/passengers"]'),
        conditions,
        model = {
            summary: {
                query: function (obj) {
                    this.title = obj ? obj.title : '';
                    this.description = obj ? obj.description : null;
                },
                rule: function (obj) {
                    this.title = obj ? obj.title : '';
                    this.description = obj ? obj.description : null;
                    this.startDate = obj ? obj.startDate : todayDate;
                    this.endDate = obj ? obj.endDate : undefined;
                    this.enabled = obj ? obj.enabled : true;
                    this.ruleCat = obj ? obj.ruleCat : null;
                    this.overMaxHits = obj ? obj.overMaxHits : null;
                }
            }
        },
        resetModels = function (m) {
            //reset all models
            m.query = new model.summary.query();
            m.rule = new model.summary.rule();
        },
        setId = function () {
            var returnValue = null;
            if ($scope.buttonMode === $scope.mode && !$scope.isCopy) {
                returnValue = $scope.ruleId;
            }
            return returnValue;
        },
        mode = $stateParams.mode,
        loadOnSelection = {
            query: function (row) {
                $scope.selectedIndex = $scope.qbGrid.data.indexOf(row.entity);
                $scope.loadQuery();
            },
            rule: function (row) {
                $scope.selectedIndex = $scope.qbGrid.data.indexOf(row.entity);
                jqueryQueryBuilderService.loadRuleById('rule', row.entity.id).then(function (myData) {
                    var result = myData.result;
                    var rules = result.details.rules;
                    $scope.ruleId = result.id;
                    // endDate will not display in calendar widget unless it is a Date object.
                    var endDateLong = result.summary.endDate;
                    if (endDateLong != null)
                    {
                        var endDateObj = new Date(endDateLong);
                        result.summary.endDate = endDateObj;
                    }
                    // startDate will display , but errors are thrown in the console if not a Date object.
                    var startDateLong = result.summary.startDate;
                    var startDateObj = new Date(startDateLong);
                    result.summary.startDate = startDateObj;                    
                    
                    $scope.loadSummary('rule', result.summary);
                    $scope.$builder.queryBuilder('loadRules', result.details);
                });
            }
        };

    $scope.$watch('selectedMode', function () {
        $scope.updateGrid();
    });

    $scope.copyRule = function () {
        $scope.isCopy = true;
        if($scope.mode === 'rule'){
            $scope.prompt.save('rule');
        } else if($scope.mode === 'query'){
            $scope.prompt.save('query');
        }
    };

    $scope.mode = mode;
    $scope.selectedMode = mode;

    $scope.prompt = {
        save: function (buttonMode) {
            $scope.$builder.queryBuilder('setMode', buttonMode);
            conditions = $scope.$builder.queryBuilder('getDrools');

            if (conditions === false) {
                return;
            }
            $scope.buttonMode = buttonMode;

            switch (buttonMode) {
            case 'rule':
                $scope.ruleClone = $.extend({}, $scope.rule);
                if ($scope.isCopy) {
                    $scope.rule.startDate = new Date();
                }
                break;
            case 'query':
                $scope.queryClone = $.extend({}, $scope.query);
                break;
            }
            $mdSidenav(buttonMode).open();
        },
        cancel: function () {
            // could we use $scope.buttonMode here?
            if ($scope.ruleClone) {
                $scope.rule = $.extend({}, $scope.ruleClone);
                $scope.ruleClone = null;
            }
            if ($scope.queryClone) {
                $scope.query = $.extend({}, $scope.queryClone);
                $scope.queryClone = null;
            }
            $scope.isCopy = false;
            $mdSidenav($scope.buttonMode).close();
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

            var startDateObj = $scope.convertUTCDateToLocalDate(obj.summary.startDate);
            obj.summary.startDate = startDateObj.toISOString().substring(0,10);
            if (obj.summary.endDate != null) {
                var endDateObj = $scope.convertUTCDateToLocalDate(obj.summary.endDate);
                obj.summary.endDate = endDateObj.toISOString().substring(0, 10);
            }
            temp = $.extend({}, obj.summary, {
                id: obj.id,
                hitCount: obj.hitCount,
                modifiedOn: obj.modifiedOn,
                modifiedBy: obj.modifiedBy,
                overMaxHits : obj.overMaxHits
            });
            data.push(temp);
            });
            $scope.qbGrid.data = data;
        },
        all: function (myData) {
            var temp, data = [];
            myData.forEach(function (obj) {
                temp = $.extend({}, obj.summary, {
                    id: obj.id,
                    hitCount: obj.hitCount,
                    modifiedOn: obj.modifiedOn,
                    modifiedBy: obj.modifiedBy
                });
                data.push(temp);
            });
            $scope.qbGrid.data = data;
        }
    };

    $scope.getToolbarText = function () {
        switch ($scope.buttonMode) {
            case 'query':
                return $scope.mode === 'query' && $scope.ruleId !== null ? $translate.instant('qry.updatequery') : $translate.instant('qry.savequery');
            case 'rule':
                return $scope.mode === 'rule' && ($scope.ruleId !== null && !$scope.isCopy) ? $translate.instant('qry.updaterule') : $translate.instant('qry.saverule');
        }
    };

    //TODO move out to a service
    $scope.executeQuery = function (e) {
        var m = moment(),
            timestamp = m.day() + '|' + m.hours() + '|' + m.minutes() + '|' + m.seconds(),
            query = $scope.$builder.queryBuilder('getDrools');
            $scope.fixInClauses(query);
    
        if (query === false) {
            $scope.openAlert('Incomplete Query', 'Can not execute / invalid query');
            e.preventDefault();
            return;
        }
        localStorage['query'] = JSON.stringify(query);
        localStorage['qbTitle'] = $scope[$scope.mode].title.length ? $scope[$scope.mode].title.trim() : '';
/*      queryFlightsLink.setAttribute('target', 'qf|' + timestamp); //REMOVED IN TESTING TO SEE IF RESOLVED AUTO LOGOUT PREVENTING LOGIN ISSUES
        queryPassengersLink.setAttribute('target', 'qp|' + timestamp);*/
    };

    $scope.ruleId = null;

    $scope.updateGrid = function () {
        spinnerService.show('html5spinner');
        jqueryQueryBuilderService.getList($scope.selectedMode).then(function (myData) {
            $scope.setData[$scope.mode](myData.result);
            $scope.gridApi.selection.clearSelectedRows();
            //$scope.gridApi.selection.selectRow($scope.qbGrid.data[$scope.selectedIndex]);
            $scope.saving = false;
            spinnerService.hide('html5spinner');
        });
    };

    $scope.updateQueryBuilderOnSave = function (myData) {
        if (myData.status === 'FAILURE') {
            spinnerService.hide('html5spinner');
            alert(myData.message);
            $scope.saving = false;
            return;
        }
        if (typeof myData.errorCode !== "undefined") {
            spinnerService.hide('html5spinner');
            alert(myData.errorMessage);
            return;
        }

        jqueryQueryBuilderService.getList($scope.selectedMode).then(function (myData) {
            $scope.setData[$scope.selectedMode](myData.result);
            $mdSidenav('rule').close();
            $mdSidenav('query').close();
            $scope.isCopy = false;
            $interval(function () {
                $scope.gridApi.selection.clearSelectedRows();
                $scope.saving = false;
                $scope.addNew();
                spinnerService.hide('html5spinner');
            }, 0, 1);
        });
    };

    $scope.rowSelection = function (gridApi) {
        $scope.gridApi = gridApi;
        if ($scope.gridApi.selection) {
            gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                if (row.isSelected) {
                    loadOnSelection[$scope.mode](row);
                } else {
                    $scope.addNew();
                    $scope.gridApi.selection.clearSelectedRows();
                }
            });
        }
    };

    $scope.delete = function () {
        var selectedRowEntities = $scope.gridApi.selection.getSelectedRows().reverse(),
            rowIndexToDelete;

        if (!$scope.ruleId) {
            spinnerService.hide('html5spinner');
            alert('No rule loaded to delete');
            return;
        }
        $scope.addNew();
        spinnerService.show('html5spinner');
        selectedRowEntities.forEach(function (rowEntity) {
            rowIndexToDelete = $scope.qbGrid.data.indexOf(rowEntity);

            jqueryQueryBuilderService.delete(mode, rowEntity.id).then(function () {
                $scope.qbGrid.data.splice(rowIndexToDelete, 1);
                spinnerService.hide('html5spinner');
            });
        });
    };

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

    $scope.loadSummary = function (obj, summary) {
        Object.keys(summary).forEach(function (key) {
            if (key === 'startDate' || key === 'endDate') {
                if (typeof summary[key] === 'string') {
                    var date = new Date((summary[key].replace(/-/g, '/')));
                    if (Object.prototype.toString.call(date) === '[object Date]') {
                        $scope[obj][key] = date;
                    } else {
                        $scope[obj][key] = summary[key];
                    }
                } else {
                    $scope[obj][key] = summary[key];
                }
            } else {
                $scope[obj][key] = summary[key];
            }
        });
    };

    $scope.formats = ["YYYY-MM-DD"];

    $scope.addNew = function () {
        $scope.ruleId = null;
        $scope.$builder.queryBuilder('reset');
        resetModels($scope);
    };
    $scope.ruleId = null;
    $scope.saving = false;
    $scope.save = {
        query: {
            confirm: function () {
                
                var queryObject = {
                    id: setId(),
                    title: $scope.query.title,
                    description: $scope.query.description || null,
                    query: conditions
                };

                if ($scope.saving) {
                    return;
                }

                $scope.saving=true;
                $timeout(function() {
                    $scope.saving=false;
                }, 1000);
               
                if (queryObject.title && queryObject.title.length) {
                    queryObject.title = queryObject.title.trim();
                }

                if (!queryObject.title.length) {
                    alert('Title summary can not be blank!');
                    $scope.saving = false;
                    return;
                }
                
                // fix IN conditions using non-selectized user input.
                $scope.fixInClauses(queryObject.query);

                spinnerService.show('html5spinner');
                jqueryQueryBuilderService.save('query', queryObject).then($scope.updateQueryBuilderOnSave);
            }
        },
        rule: {
            confirm: function () {
                var ruleObject = {
                    id: setId(),
                    details: conditions,
                    summary: $scope.rule
                };

                if ($scope.saving) {
                    return;
                }
                
              
                $scope.saving=true;
                $timeout(function() {
                    $scope.saving=false;
                }, 1000);


                if (ruleObject.summary.title && ruleObject.summary.title.length) {
                    ruleObject.summary.title = ruleObject.summary.title.trim();
                }

                if (ruleObject.summary.title.length === 0) {
                    alert('Title summary can not be blank!');
                    $scope.saving = false;
                    return;
                }

                if ($scope.ruleId === null || $scope.isCopy) {
                    if (ruleObject.summary.startDate.getDate() < new Date().getDate()) {
                        $scope.openAlert('Invalid Start Date', 'Start date must be today or later when created new');
                        $scope.saving = false;
                        return;
                    }
                }

                if (ruleObject.summary.endDate) {
                    if (ruleObject.summary.endDate < ruleObject.summary.startDate) {
                        $scope.openAlert('Invalid End Date', 'End Date must be empty/open or be >= Start Date');
                        $scope.saving = false;
                        return;
                    }
                }
                
                // fix IN conditions using non-selectized user input.
                $scope.fixInClauses(ruleObject.details);
 
                spinnerService.show('html5spinner');
                jqueryQueryBuilderService.save('rule', ruleObject).then($scope.updateQueryBuilderOnSave);
            }
        }
    };

    $injector.invoke(jqueryQueryBuilderWidget, this, {$scope: $scope});

    $scope.qbGrid = gridOptionsLookupService.getGridOptions(mode);
    $scope.qbGrid.columnDefs = gridOptionsLookupService.getLookupColumnDefs(mode);
    $scope.qbGrid.enableRowHeaderSelection = true;
    $scope.qbGrid.enableSelectAll = false;
    $scope.qbGrid.multiSelect = false;
    $scope.qbGrid.exporterCsvFilename = mode + '.csv';
    $scope.qbGrid.exporterExcelFilename = mode + '.xlsx';
    $scope.qbGrid.exporterExcelSheetName= 'Data';
    $scope.qbGrid.exporterPdfHeader = {text: mode, style: 'headerStyle'};
    $scope.qbGrid.onRegisterApi = $scope.rowSelection;

    jqueryQueryBuilderService.getList($scope.selectedMode).then(function (myData) {
        $scope.setData[$scope.mode](myData.result);
    });
    jqueryQueryBuilderService.getRuleCat().then(function(myData) {
      $scope.ruleCategories = myData;
    });
    //query specific
    $scope.loadQuery = function () {
        var obj = $scope.qbGrid.data[$scope.selectedIndex];
        $scope.ruleId = obj.id;
        $scope.loadSummary('query', new model.summary.query(obj));
        $scope.$builder.queryBuilder('loadRules', obj.query);
    };
    
    $scope.fixInClauses = function (ruleArray) {
        
        for (var j = 0; j < ruleArray.rules.length; j++)
        {
            if ((ruleArray.rules[j].operator == "IN") && (ruleArray.rules[j].value.length == 1))
            {
                var values = ruleArray.rules[j].value[0];
                if (values.indexOf(",") > 0)
                {
                    var valuesArray = values.split(",");
                    ruleArray.rules[j].value = valuesArray;
                }
            }
        }       
        
    }
    
    resetModels($scope);

    $scope.buildAfterEntitiesLoaded({deleteEntity: 'HITS'});
    $scope.$scope = $scope;

    $scope.$watch("rule.endDate", function (newValue) {
        var datepicker;
        if (newValue === null || newValue === undefined) {
            $timeout(function () {
                datepicker = document.querySelectorAll('.md-datepicker-input')[1];
                datepicker.value = '';
            }, 5);
        }
    });

    $scope.openAlert = function (title, msg) {
        $mdDialog.show(
            $mdDialog.alert()
                .clickOutsideToClose(false)
                .title(title)
                .textContent(msg)
                .ariaLabel('Invalid Format')
                .ok('OK')
                .openFrom({
                    left: 1500
                })
                .closeTo(({
                    right: 1500
                }))
        );
    };

    $scope.isUpdate = function () {
        if ($scope.ruleId === null) {
            return false;
        }
        return true;
    }
    $scope.$watch(
        function () {
            return $mdSidenav('rule').isOpen();
        },
        function (newValue, oldValue) {
            if (newValue != oldValue && !newValue) {
                $scope.prompt.cancel();
            }
        }
    );
    $scope.$watch(
        function () {
            return $mdSidenav('query').isOpen();
        },
        function (newValue, oldValue) {
            if (newValue != oldValue && !newValue) {
                $scope.prompt.cancel();
            }
        }
    );
    
    $scope.convertUTCDateToLocalDate = function (utcDate) {
        utcDate = new Date(utcDate);
        var localOffset = utcDate.getTimezoneOffset() * 60000;
        var localTime = utcDate.getTime();

        utcDate = localTime - localOffset;

        utcDate = new Date(utcDate);
        //console.log("Converted time: " + utcDate);
        return utcDate;
	}
	
	// Adds tooltip to elements under rule-entity-container, rule-field-container, rule-filter-container
	//rule-operator-container
	$(document).on("mouseenter", "a[id*='bs-select']", function() {
		 $(this).prop('title', codeTooltipService.getCodeTooltipData($(this).text(), "dictionary"));
	});

	// Adds tooltip to rule-value-container (for PNR's Trip Type)
	$(document).on("mouseenter", "select[name='builder_rule_0_value_0']", function() {
		$(this).find('option').each(function(){
				$(this).prop('title', codeTooltipService.getCodeTooltipData($(this).text(), "dictionary"));
		})
		 
	});

	// Adds tooltip to rule-value-container (for Document Types and Passender Types)
	$(document).on("mouseenter", "div[class='selectize-dropdown-content']", function() {
		$(this).find('div').each(function(){
				$(this).prop('title', codeTooltipService.getCodeTooltipData($(this).text(), "dictionary"));
		})
		 
	});
	
	


         
});
