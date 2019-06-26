/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
app.controller('AdminCtrl', function ($scope, $mdDialog, $mdSidenav, gridOptionsLookupService, userService, settingsInfo, defaultSettingsService, auditService, codeService, caseService, errorService, $location, $mdToast, $document, $http, $rootScope) {
  'use strict';
  var CARRIER = 'carrier';
  var COUNTRY = 'country';
  var AIRPORT = 'airport';
  var CODESIDEBAR = 'codeSidebar';

  $scope.activeCodeTab = CARRIER; //sub-tab list under 'Code Editor' tab
  $scope.codeColList = {};
  $scope.isEdited = false;
  $scope.OK = false;

  // CODE fields so we can generate the sidebar dynamically for each type.
  // could also pull these from the coldefs:
  // airportGrid.coldefs.map((col) => col.field), and filter the fields to exclude the "Edit" and "Id" fields?
  $scope.codeColList[CARRIER] = [{name: 'iata', len: 2}, {name: 'name', len: 255}];
  $scope.codeColList[AIRPORT] = [{name: 'iata', len: 3}, {name: 'icao', len: 4}, {name: 'name', len: 255}, {name: 'city', len: 255}, {name: 'country', len: 3}];
  $scope.codeColList[COUNTRY] = [{name: 'iso2', len: 2}, {name: 'iso3', len: 3}, {name: 'name', len: 255}, {name: 'isoNumeric', len: 3}];

  $scope.settingsInfo = settingsInfo.data;
  var that = this;
  this.successToast = function(msg){
      $mdToast.show($mdToast.simple()
                .content(msg)
                .position('top right')
                .hideDelay(2000)
                .parent($scope.toastParent));
  }
  var setUserData = function (data) {
    $scope.userGrid.data = data;
  };

  var setAuditData = function (data) {
    $scope.auditGrid.data = data;
    if(data && data.length > 0){
        that.successToast('Audit Log Data Loaded.');
    } else {
        that.successToast('Filter conditions did not return any Audit Log Data.');
    }
  };
  var setErrorData = function (data) {
          $scope.errorGrid.data = data;
          if(data && data.length > 0){
             that.successToast('Error Log Data Loaded.')
          } else {
             that.successToast('Filter conditions did not return any Error Log Data.');
          }
      };
  var setupUserGrid = function(){
      $scope.userGrid = gridOptionsLookupService.getGridOptions('admin');
      $scope.userGrid.columnDefs = gridOptionsLookupService.getLookupColumnDefs('admin');
  }
  var setupAuditGrid = function(){
      $scope.auditGrid = gridOptionsLookupService.getGridOptions('audit');
      $scope.auditGrid.columnDefs = gridOptionsLookupService.getLookupColumnDefs('audit');
  }
  var setupErrorGrid = function(){
      $scope.errorGrid = gridOptionsLookupService.getGridOptions('error');
      $scope.errorGrid.columnDefs = gridOptionsLookupService.getLookupColumnDefs('error');
  }

var setupCodeGrids = function(){
  var gridTemplate = gridOptionsLookupService.getGridOptions('code');

  $scope.carrierGrid = Object.assign({}, gridTemplate);
  $scope.carrierGrid.columnDefs = gridOptionsLookupService.getLookupColumnDefs(CARRIER);

  $scope.countryGrid = Object.assign({}, gridTemplate);
  $scope.countryGrid.columnDefs = gridOptionsLookupService.getLookupColumnDefs(COUNTRY);

  $scope.airportGrid = Object.assign({}, gridTemplate);
  $scope.airportGrid.columnDefs = gridOptionsLookupService.getLookupColumnDefs(AIRPORT);
}

var setCodeData = function (data) {
  var tab = $scope.activeCodeTab;
  if (tab === AIRPORT) $scope.airportGrid.data = data;
  
  if (tab === COUNTRY) $scope.countryGrid.data = data;
  
  if (tab === CARRIER) $scope.carrierGrid.data = data;

  $scope.OK = true;
};

$scope.refreshActiveCodeGrid = function(){
  $mdSidenav(CODESIDEBAR).close();
  $scope.activeCodeTab = $scope.activeCodeTab || CARRIER;

  codeService.getAllCodes($scope.activeCodeTab).then(setCodeData, $scope.errorToast);
};

  var selectRow = function(gridApi) {
      // set gridApi on scope
      $scope.gridApi = gridApi;
      if($scope.gridApi.selection)
          $scope.gridApi.selection.on.rowSelectionChanged($scope,
                  function(row) {
                    if($scope.selectedTabIndex == 1 || $scope.selectedTabIndex == 2){
                      $scope.selectedItem = row.entity;
                      if(row.isSelected){
                        //This converts a string into a JSON object, eliminates back-end change req
                        $scope.actionData = JSON.parse($scope.selectedItem.actionData);
                        $scope.actionTarget = JSON.parse($scope.selectedItem.target);
                        $scope.showAuditDetails = true;
                      } else {
                            $scope.showAuditDetails = false;
                      }
                    }
                  });
      //$scope.gridApi.core.notifyDataChange( uiGridConstants.dataChange.OPTIONS);
  };

  var selectErrorRow = function(gridApi) {
      // set gridApi on scope
      $scope.errorGridApi = gridApi;
      $scope.errorGridApi.selection.on.rowSelectionChanged($scope,
              function(row) {
                if($scope.selectedTabIndex == 2 && row.isSelected){
                      $scope.selectedErrorItem = row.entity;
                } else {
                    $scope.selectedErrorItem = null;
                }
              });
  };

  $scope.selectedTabIndex = 0;

  setupUserGrid();
  setupAuditGrid();
  setupErrorGrid();
  setupCodeGrids();

  $scope.auditGrid.onRegisterApi = selectRow;
  $scope.errorGrid.onRegisterApi = selectErrorRow;

  $scope.$watch('selectedTabIndex', function(current, old){
    switch ( current){
      case 0:
        userService.getAllUsers().then(setUserData);
        break;
      case 1:
        $scope.toastParent = $document[0].getElementById('AuditFilterPanel');
        $scope.refreshAudit();
        break;
      case 2:
        $scope.toastParent = $document[0].getElementById('ErrorFilterPanel');
        $scope.refreshError();
        break;
      case 3:
        $scope.refreshActiveCodeGrid();
        break;
      case 4:
        break;
    }
  });


  $scope.viewCodes = function(tab) {
    $scope.activeCodeTab = tab;
    $scope.toastParent = $document[0].getElementById($scope.activeCodeTab+'Grid');
    $scope.refreshActiveCodeGrid();
  }
  
  $scope.openSidebarEdit = function (row) {
    $scope.codeAction = 'Edit';
    $scope.rowSelectedRaw = row;
    $scope.rowSelected = Object.assign({}, row);  //new copy for mutations

    $mdSidenav(CODESIDEBAR).open();
  };

  $scope.openSidebarAdd = function () {
    $scope.codeAction = 'Add';
    $scope.rowSelectedRaw = {};
    $scope.rowSelected = {};
    $scope.codeAction = 'Add';

    $mdSidenav(CODESIDEBAR).open();
  };

  $scope.saveCode = function () {
    $scope.toastParent = $document[0].getElementById($scope.activeCodeTab+'Grid');
    var action = $scope.codeAction;
    var isEqual = true;

    // am deliberately using != instead of !== to allow type coersion in the comparison below
    // until we add logic to enforce types in the inputs for any non-string fields.
    for (var field of $scope.codeColList[$scope.activeCodeTab]) {
      if ($scope.rowSelected[field.name] != $scope.rowSelectedRaw[field.name]) {
        isEqual = false;
      }
    }

    delete $scope.rowSelected.$$hashKey;

    if (!isEqual){
      var tab = $scope.activeCodeTab;

      if (action === 'Edit') {
        codeService.updateCode(tab, $scope.rowSelected)
        .then(function(response) {
          if (response.status === 'FAILURE') {
            $scope.toastParent = $document[0].getElementById(CODESIDEBAR);
            $scope.errorToast('The record cannot be saved with this data');
          }
          else {
            $scope.refreshActiveCodeGrid();
            that.successToast("Code successfully saved.");
            $scope.OK = false;
          }
        });
      }
      else
        codeService.createCode(tab, $scope.rowSelected).then($scope.refreshActiveCodeGrid, $scope.errorToast);

      //sync the tooltip collections in memory
      refreshTooltips();
    }
    else {
      $scope.toastParent = $document[0].getElementById(CODESIDEBAR);
      $scope.errorToast('No changes to the record were detected');
    }
  };


  function refreshTooltips() {
    switch ($scope.activeCodeTab) {
      case CARRIER:
        $rootScope.refreshCarrierTooltips();
        break;
      case COUNTRY:
        $rootScope.refreshCountryTooltips();
        break;
      case AIRPORT:
        $rootScope.refreshAirportTooltips();
        break;
    }
  }

  $scope.deleteCode = function () {
    var tab = $scope.activeCodeTab;

    $scope.toastParent = $document[0].getElementById(tab+'Grid');

    var r = confirm("Are you sure you want to delete this code?");
    if (r == true) {
      codeService.deleteCode(tab, $scope.rowSelected.id).then($scope.refreshActiveCodeGrid, $scope.errorToast);
      refreshTooltips();

      if($scope.OK) {
        that.successToast("Code successfully deleted.");
        $scope.OK = false;
      }
    }
  };

  $scope.restoreCode = function () {
    var tab = $scope.activeCodeTab;

    $scope.toastParent = $document[0].getElementById(tab+'Grid');

    var r = confirm("Are you sure you want to restore this code to its original value?");
    if (r == true) {
      codeService.restoreCode(tab, $scope.rowSelected).then($scope.refreshActiveCodeGrid, $scope.errorToast);
      refreshTooltips();

      if($scope.OK) {
        that.successToast("Code successfully restored.");
        $scope.OK = false;
      }
    }
  };

  $scope.restoreAllCodes = function () {
    var tab = $scope.activeCodeTab;

    $scope.toastParent = $document[0].getElementById(tab+'Grid');

    var r = confirm(`Are you sure you want to restore all ${tab.toUpperCase()} codes to their original values?`);
    if (r == true) {
      codeService.restoreAllCodes(tab).then($scope.refreshActiveCodeGrid, $scope.errorToast);
      refreshTooltips();

      if($scope.OK) {
        that.successToast(`All ${tab} codes successfully restored.`);
        $scope.OK = false;
      }
    }
  };

  $scope.createUser = function () { $location.path('/user/new'); };
  $scope.lastSelectedUser = function (user) { localStorage['lastSelectedUser'] = JSON.stringify(user); };

  $scope.showAuditDetails = false;
  $scope.auditActions = auditService.auditActions;
  var today = new Date();
  $scope.auditModel = {action:null, user:null, timestampStart:today, timestampEnd:today};
  $scope.errorModel = {code:null, timestampStart:today, timestampEnd:today};

  $scope.refreshAudit = function(){
      var model = $scope.auditModel;
      $scope.showAuditDetails = false;
      auditService.getAuditData(model.action, model.user, model.timestampStart, model.timestampEnd).then(setAuditData, $scope.errorToast);
  };
  $scope.refreshError = function(){
      var model = $scope.errorModel;
      $scope.selectedErrorItem = null;
      errorService.getErrorData(model.code, model.timestampStart, model.timestampEnd).then(setErrorData, $scope.errorToast);
  };
  $scope.errorToast = function(error){
      $mdToast.show($mdToast.simple()
        .content(error)
        .action('OK')
        .highlightAction(true)
        .position('top right')
        .hideDelay(0)
        .parent($scope.toastParent));
  };

  $scope.dispObj = {};

  $scope.saveOrEditStatus= function(){
    caseService.createOrEditDispositionStatus($scope.createDispStatusVo())
    .then(function(response){
      //produce success message
      if(response.status === 200){
        $scope.errorToast(response.data.message);
        $scope.loadDispStatuses();
      }else{
        $scope.errorToast(response.data.message);
      }
    });
  };

  $scope.deleteStatus = function(){
    caseService.deleteDispositionStatus($scope.createDispStatusVo())
    .then(function(response){
      //produce success message
      if(response.status === 200){
        $scope.errorToast(response.data.message);
        $scope.loadDispStatuses();
      }else{
        $scope.errorToast(response.data.statusText);
      }
    }).error(function(response){
      $scope.errorToast(response.data.statusText + " You may not remove a status already saved to an existing case");
    });

  };

  $scope.loadDispStatuses = function(){
    caseService.getDispositionStatuses()
    .then(function(response){
      $scope.dispositionStatus = response.data;
      $scope.dispObj.currentDispStatus = '-1';
      $scope.dispObj.currentStatusName = '';
      $scope.dispObj.currentStatusDescription = '';
    });
  }

  $scope.adjustUiForStatusObj = function(dispId){
      $scope.dispObj.currentStatusName = '';
        $scope.dispObj.currentStatusDescription = '';

      $.each($scope.dispositionStatus, function(index,value){
        if(value.id.toString() === dispId){
          $scope.dispObj.currentStatusDescription = value.description;
          $scope.dispObj.currentStatusName = value.name;
          return value;
        }
      });
  };

  $scope.createDispStatusVo = function(){
    var dispId = null;
    if($scope.dispObj.currentDispStatus != '-1' ){
      dispId = $scope.dispObj.currentDispStatus;
    }

    var dispVo = {
        name:$scope.dispObj.currentStatusName,
        description:$scope.dispObj.currentStatusDescription,
        id:dispId
    };

    return dispVo;
  }

  $scope.saveSettings = function() {
    defaultSettingsService.saveSettings($scope.settingsInfo)
    .then(function success(response) {
      alert("Success Save Settings");
    },function error(response){
      alert("Error when saving settings");
    });
  }

  $scope.loadDispStatuses();

  $scope.userManagement = function (user, ev) {
    if(user==null){
      $scope.saveType = "create"
    }
    else {
      $scope.saveType ="update";
    }
    $scope.user = user;
    $scope.userModal(ev);
  }
  //Modal Configs
  $scope.customFullscreen = false;
  $scope.userModal = function(ev) {
    $mdDialog.show({
      controller: DialogController,
      templateUrl: 'dialog/user.tmpl.html',
      parent: angular.element(document.body),
      scope: $scope.$new(),
      targetEvent: ev,
      clickOutsideToClose:true,
      fullscreen: $scope.customFullscreen // Only for -xs, -sm breakpoints.
    });
  };
  function DialogController($scope, $mdDialog, $sce) {
    $scope.hide = function() {
      $mdDialog.hide();
    };
    $scope.cancel = function() {
      $mdDialog.cancel();
    };
  }
});
