/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app.controller('WatchListController', function ($scope, gridOptionsLookupService, $q, watchListService, $mdSidenav, $interval, spinnerService, $timeout, $mdDialog, $sce) {
        var watchlist = {},
            tabs = [],
            model = {
                Document: function (entity) {
                    this.id = entity ? entity.id : null;
                    this.documentType = entity ? entity.documentType : null;
                    this.documentNumber = entity ? entity.documentNumber : null;
                    this.categoryId = entity ? entity.categoryId : null;
                },
                Passenger: function (entity) {
                    this.id = entity ? entity.id : null;
                    this.firstName = entity ? entity.firstName : null;
                    this.lastName = entity ? entity.lastName : null;
                    this.dob = entity ? entity.dob : undefined;
                    this.categoryId = entity ? entity.categoryId : null;
                },
                WatchlistCategory: function (entity) {
                    this.label = entity ? entity.name : null;
                    this.description = entity ? entity.description : null;
                }
            },
            resetModels = function (m) {
                //resets all models
                m.Document = new model.Document();
                m.Passenger = new model.Passenger();
                m.wlCategoryModel = new model.WatchlistCategory();
            },
            isItTrashTime = function (rows) {
                $scope.disableTrash = $scope.gridApi.selection.getSelectedRows(rows).length === 0;
            },
            exporter = {
                'csv': function () {
                    $scope.gridApi.exporter.csvExport('all', 'all');
                },
                'pdf': function () {
                    $scope.gridApi.exporter.pdfExport('all', 'all');
                }
            };

        $scope.export = function (format) {
            exporter[format]();
        };


        $scope.model = {};
        $scope.wlCategoryModel = new model.WatchlistCategory();

        $scope.watchlistGrid = gridOptionsLookupService.getGridOptions('watchlist');
        $scope.watchlistGrid.importerDataAddCallback = function (grid, newObjects) {
            if ($scope.validateNewObjects(newObjects)) {
                var valid = true;
                if($scope.activeTab === "Passenger"){
                    $.each(newObjects, function(index,value){
                        if(!$scope.validateDateFormat(value.dob, index)){
                            valid = false;
                            return false;
                        };
                    });
                }
                if(valid){
                    $scope.showConfirm(grid, newObjects);
                }
            } else {
                $scope.openAlert('The format of the file you have uploaded is invalid.');
            }
        };
        $scope.watchlistGrid.paginationPageSizes = [10, 15, 25];
        $scope.watchlistGrid.paginationPageSize =  $scope.model.pageSize;
        $scope.watchlistGrid.paginationCurrentPage =  $scope.model.pageNumber;
        $scope.watchlistGrid.finishImport = function (grid, newObjects) {
            spinnerService.show('html5spinner');
            var listName = $scope.activeTab;
            watchListService.deleteListItems(watchlist.types[listName].entity, listName).then(function (response) {
                var objectType = $scope.activeTab,
                    watchlistType = watchlist.types[objectType],
                    columnTypeDict = {},
                    entity = watchlistType.entity,
                    watchlistItems = [],
                    columnType,
                    value,
                    terms,
                    ready = true;

                watchlistType.columns.forEach(function (column) {
                    columnTypeDict[column.name] = column.type;
                });

                newObjects.forEach(function (obj) {
                    terms = [];
                    Object.keys(obj).forEach(function (key) {
                        if (['$$hashKey', 'id'].indexOf(key) === -1) {
                            columnType = columnTypeDict[key];
                            value = obj[key];
                            if (!value) {
                                ready = false;
                            }
                            terms.push({entity: entity, field: key, type: columnType, value: value});
                        }
                    });
                    watchlistItems.push({id: null, action: 'Create', terms: terms});

                });
                if (ready) {
                    watchListService.addItems(objectType, entity, watchlistItems).then(function (response) {
                        $scope.getListItemsFor(objectType);
                        spinnerService.hide('html5spinner');
                    });
                }
            });
        };
        $scope.watchlistGrid.onRegisterApi = function (gridApi) {
            $scope.gridApi = gridApi;
            gridApi.selection.on.rowSelectionChanged($scope, isItTrashTime);
            gridApi.selection.on.rowSelectionChangedBatch($scope, isItTrashTime);
            gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                $scope.model.pageSize = pageSize;
            });
        };

        $scope.documentTypes = [
            {id: "P", label: "PASSPORT"},
            {id: "V", label: "VISA"}
        ];

        $scope.categories = {};
        watchListService.getWatchlistCategories().then(function(res){
            $scope.watchlistCategories =  res.data;
            $scope.wlCatagoryGrid.data =  res.data;
        	$scope.watchlistCategories.forEach(function(item){
        		
        		$scope.categories[item.id]=item.label;
        		
        	});
        });
        
        watchlist.types = {
            "Document": {
                entity: "DOCUMENT",
                icon: "file",
                columns: gridOptionsLookupService.getLookupColumnDefs('watchlist').DOCUMENT
            },
            "Passenger": {
                entity: "PASSENGER",
                icon: "user",
                columns: gridOptionsLookupService.getLookupColumnDefs('watchlist').PASSENGER
            }
        };
        $scope.data = {};
        $scope.watchlistGrid.enableRowHeaderSelection = true;
        $scope.watchlistGrid.enableSelectAll = true;
        $scope.watchlistGrid.multiSelect = true;
        $scope.watchlistGrid.columnDefs = watchlist.types.Document.columns;

        $scope.updateGridIfData = function (listName) {
            $scope.gridApi.selection.clearSelectedRows();
            $scope.allSelected = false;
            $scope.disableTrash = true;
            $scope.icon = watchlist.types[listName].icon;
            $scope.activeTab = listName;
            $scope.watchlistGrid.columnDefs = watchlist.types[listName].columns;
            $scope.watchlistGrid.exporterCsvFilename = 'watchlist-' + listName + '.csv';
            $scope.watchlistGrid.data = $scope.data[listName];
            $scope.watchlistGrid.exporterExcelFilename = 'watchlist-' + listName + '.xlsx';
            $scope.watchlistGrid.exporterExcelSheetName= 'Data';
        };

        $scope.getListItemsFor = function (listName) {
            spinnerService.show('html5spinner');
            watchListService.getListItems(watchlist.types[listName].entity, listName).then(function (response) {
                var obj, data = [], items = response.data.result.watchlistItems,
                    setTerm = function (term) {
                        obj[term.field] = term.value;
                    };
                if (items === undefined) {
                    $scope.watchlistGrid.data = [];
                    return false;
                }
                items.forEach(function (item) {
                    obj = {id: item.id};
                    item.terms.forEach(setTerm);
                    data.push(obj);
                });
                $scope.data[listName] = data;
                $scope.updateGridIfData(listName);
                spinnerService.hide('html5spinner');
            });
        };

        $scope.saveWlCategory = function () {
            watchListService.saveCategory($scope.wlCategoryModel).then(function () {
                watchListService.getWatchlistCategories().then(function(res){
                    $scope.watchlistCategories =  res.data;
                    $scope.wlCatagoryGrid.data =  res.data;
                    $scope.watchlistCategories.forEach(function(item){
                        $scope.categories[item.id]=item.label;
                    });
                });
                resetModels($scope);
                $mdSidenav('wlCatSave').close();
            });
        };

        $scope.getSaveStateText = function (activeTab) {
            return 'Save ';
            // todo listen to broadcast, and return save or update
        };

        $scope.updateGrid = function (listName) {
            if ($scope.data[listName]) {
                $scope.updateGridIfData(listName);
                return;
            }
            $scope.getListItemsFor(listName);
        };

        Object.keys(watchlist.types).forEach(function (key) {
        	var glyphicon = null;
        	if(key === "Passenger"){
        		glyphicon = $sce.trustAsHtml('<i class="glyphicon glyphicon-user"></i>');
        	} else if( key === "Document"){
        		glyphicon = $sce.trustAsHtml('<i class="glyphicon glyphicon-file"></i>');
        	} else{
        		glyphicon = null;
        	}
            tabs.push({title: key, icon: glyphicon});
        });

        $scope.tabs = tabs;
        $scope.activeTab = tabs[0].title;
        $scope.icon = tabs[0].icon;
        $scope.rowSelected = null;

        $scope.Add = function () {
            var mode = $scope.activeTab;
            resetModels($scope);
            $scope[mode] = new model[mode]();
            if (mode === "Passenger" && $scope[mode].dob !== undefined) {
                $scope[mode].dob = moment($scope[mode].dob).toDate();
            }
            $mdSidenav('save').open();
        };

        $scope.addWlCategory = function () {
            resetModels($scope);
            $scope.watchlistCategory = new model.WatchlistCategory();
            $mdSidenav('wlCatSave').open();
        };

        $scope.saveRow = function () {
            var objectType = $scope.activeTab,
                watchlistType = watchlist.types[objectType],
                columnTypeDict = {},
                entity = watchlistType.entity,
                method = !$scope[objectType].id ? 'addItem' : 'updateItem',
                terms = [],
                columnType,
                value,
                ready = true;

            watchlistType.columns.forEach(function (column) {
                columnTypeDict[column.name] = column.type;
            });

            Object.keys($scope[objectType]).forEach(function (key) {
                if (['$$hashKey', 'id'].indexOf(key) === -1) {
                    columnType = columnTypeDict[key];
                    value = $scope[objectType][key];
                    if (!value) {
                        ready = false;
                    }
                    if (columnType === 'date') {
                        value = moment(value).format('YYYY-MM-DD');
                    }
                    terms.push({entity: entity, field: key, type: columnType, value: value});
                }        
            });
            if (ready) {
                watchListService[method](objectType, entity, $scope[objectType].id, terms).then(function (response) {
                    if ($scope[$scope.activeTab].id === null) {
                        $scope[$scope.activeTab].id = response.data.result[0];
                        $scope.watchlistGrid.data.unshift($scope[$scope.activeTab]);
                    }
                    $scope.gridApi.selection.clearSelectedRows();
                    $scope.rowSelected = null;
                    $scope.getListItemsFor($scope.activeTab);
                    $mdSidenav('save').close();
                });
            }
        };

        $scope.editRecord = function (row) {
            $scope.gridApi.selection.clearSelectedRows();
            $scope.gridApi.selection.selectRow(row);
            $scope[$scope.activeTab] = $.extend({}, row);
            if ($scope.activeTab === "Passenger" && $scope[$scope.activeTab].dob !== undefined) {
                $scope[$scope.activeTab].dob = moment($scope[$scope.activeTab].dob).toDate();
            }
            //broadcast save or update
            $scope.rowSelected = row;
            $mdSidenav('save').open();
        };

        $scope.removeRow = function () {
            var rowIndexToDelete,
                watchlistItems = [{id: $scope.rowSelected.id, action: 'Delete', terms: null}];
            spinnerService.show('html5spinner');
            watchListService.deleteItems($scope.activeTab, $scope.activeTab, watchlistItems).then(function () {
                rowIndexToDelete = $scope.watchlistGrid.data.indexOf($scope.rowSelected);
                $scope.watchlistGrid.data.splice(rowIndexToDelete, 1);
                $scope.rowSelected = null;
                $scope.disableTrash = true;
                spinnerService.hide('html5spinner');
                $mdSidenav('save').close();
            });
        };

        $scope.removeRows = function () {
            var selectedRowEntities = $scope.gridApi.selection.getSelectedRows(),
                constructItem = function (rowEntity) {
                    return {id: rowEntity.id, action: 'Delete', terms: null};
                },
                watchlistItems = selectedRowEntities.map(constructItem);
            spinnerService.show('html5spinner');
            watchListService.deleteItems($scope.activeTab, $scope.activeTab, watchlistItems).then(function () {
                var rowIndexToDelete;
                selectedRowEntities.reverse();
                selectedRowEntities.forEach(function (rowEntity) {
                    rowIndexToDelete = $scope.watchlistGrid.data.indexOf(rowEntity);
                    $scope.watchlistGrid.data.splice(rowIndexToDelete, 1);
                });
                $scope.gridApi.selection.clearSelectedRows();
                spinnerService.hide('html5spinner');
            });
        };

        $scope.updateWatchlistService = function () {
            if ($scope.updating) {
                return false;
            }
            $scope.updating = true;
            spinnerService.show('html5spinner');
            watchListService.compile().then(function () {
                spinnerService.hide('html5spinner');
                $scope.updating = false;
            });
        };

        $scope.$scope = $scope;

        $scope.showConfirm = function (grid, newObjects) {
            var confirm = $mdDialog.confirm()
                .title('WARNING: Your Imported data may contain duplicate watch list items!')
                .textContent('Are you certain you wish to import data with the imported file\'s?')
                .ariaLabel('Import WatchList')
                .ok('Confirm Import')
                .cancel('Cancel');

            $mdDialog.show(confirm).then(function () {
                $scope.watchlistGrid.finishImport(grid, newObjects);
            }, function () {
                return false;
            });
        };

        $scope.openAlert = function (msg) {
            $mdDialog.show(
                $mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title('Invalid CSV Format')
                    .textContent(msg)
                    .ariaLabel('Invalid File Format')
                    .ok('OK')
                    .openFrom({
                        left: 1500
                    })
                    .closeTo(({
                        right: 1500
                    }))
            );
        };
        //overriding CSV.error in order to halt throwing error to console, instead morph into mddialog message to the user.
        CSV.error = function (err) {
            var msg = CSV.dump(err);
            CSV.reset();
            $scope.openAlert(msg);
        };

        //Sometimes CSV.js allows certain files despite being not CSV, this is an additional check that it matches our object requirements.
        $scope.validateNewObjects = function (newObjects) {
            var valid;
            if (newObjects) {
                valid = true;
                if ($scope.activeTab === 'Document') {
                    $.each(newObjects, function (index, value) {
                        if (!newObjects[index] || !newObjects[index].documentNumber || !newObjects[index].documentType) {
                            valid = false;
                            return false;
                        }
                    });
                } else {
                    $.each(newObjects, function (index, value) {
                        if (!newObjects[index] || !newObjects[index].dob || !newObjects[index].firstName || !newObjects[index].lastName) {
                            valid = false;
                            return false;
                        }
                    });
                }
            }
            return valid;
        };

        $scope.validateDateFormat = function(date, index){
            var valid = true;
            if(date.search('-') < 2){
                //BAD FORMAT, INCORRECT DELINEATOR
                 $scope.openAlert('Accepted Date Format Is yyyy-mm-dd. Invalid dilineator found at index: '+index);
                 valid = false;
            }else {
                var dateArry = date.split('-');
                if(dateArry[0].length != 4){
                    //BAD FORMAT, YEAR NOT FIRST
                    $scope.openAlert('\n Accepted Date Format Is yyyy-mm-dd. Invalid YEAR location found at index: '+index);
                    valid = false;
                }else if(dateArry[1] < 1 || dateArry[1] > 12){
                    //BAD FORMAT, MONTH OUT OF RANGE
                    $scope.openAlert('Accepted Date Format Is yyyy-mm-dd. Invalid MONTH value found at index: '+index);
                    valid = false;
                }else if(dateArry[2] < 1 || dateArry[2] > 31){
                    //BAD FORMAT, DAY OUT OF RANGE
                    $scope.openAlert('Accepted Date Format Is yyyy-mm-dd. Invalid DAY value found at index: '+index);
                    valid = false;
                }
            }
            return valid;
        }
        $scope.showWLTypesGrid = true;
        $scope.wlCatagoryGrid = {
            paginationPageSizes: [10, 15, 20],
            paginationPageSize: 10,           
            columnDefs: gridOptionsLookupService.getLookupColumnDefs('watchlist').CATEGORY,
            enableGridMenu: true,
            exporterCsvFilename: 'watch-list-types.csv',
            exporterExcelFilename: 'watch-list-types.xlsx',
            exporterExcelSheetName: 'Data'

        };
        $scope.wlCatagoryGrid.onRegisterApi = function (gridApi) {
            $scope.wlGridApi = gridApi;
        }

    });
}());
