/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
app.controller('AdminCtrl', function ($scope, gridOptionsLookupService, userService, auditService, caseService, errorService, $location, $mdToast, $document, $http) {
    'use strict';
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
        }
      });
    
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
    		console.log(response);
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
    
    $scope.loadDispStatuses();
    
});
