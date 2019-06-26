/* 
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */


(function () {
    'use strict';
    app.controller('OneDayLookoutCtrl',
        function ($scope, $state, $http, $mdToast,
                  gridService, lookoutData,oneDayLookoutService,
                  spinnerService,  $filter, $mdDialog) {
    	
    	 var stateName = $state ? $state.$current.self.name : 'onedaylookout';
    	 $scope.stateName = stateName;
    	 $scope.pageSize = 25;
    	 $scope.lookoutDate = $filter('date')(new Date(), 'yyyy-MM-dd'); //current date
    	 $scope.isTodayButtonDisabled = true;
      
    	 $scope.oneDayLookoutGrid = {
                 data:  lookoutData.data,
                 paginationPageSizes: [10, 25, 50],
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
                     
                     gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef,newValue, oldValue) {
                    	 //update status only when the value is changed
                    	 if(newValue != oldValue){
                    		 oneDayLookoutService.updateEncounteredStatus(rowEntity.caseId,newValue).then(function(resp){
								if(resp.status == 200){
									 let title="Encountered Status Updated Successfully!";
									 let content="New Status: " + newValue;
									$scope.showAlert(title, content);
								}
								else{
									// $scope.encounterUpdateStatus="failure";
									let title="Failed to Update Encounter Status!";
									 let content="Please Try Again!";
									$scope.showAlert(title, content);
								}
                    		 });
                    	 }
                    	 
                     });
                 }
			 };

             $scope.oneDayLookoutGrid.columnDefs = [
            	 
               {
            	   field: 'name',
            	   name: 'name',
            	   enableCellEdit: false,
            	   displayName: 'Name', headerCellFilter: 'translate',
            	   cellTemplate: '<md-button aria-label="type" href="#/casedetail/{{row.entity.caseId}}" title="Launch Case Detail in new window" target="case.detail" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
            	 

               },

               {
            	   field: 'document',
            	   name: 'document',
            	   enableCellEdit: false,
            	   displayName: 'Passport Number', headerCellFilter: 'translate'
               },
               {
            	   field: 'disposition',
            	   name: 'disposition',
            	   enableCellEdit: false,
            	   displayName: 'Disposition', headerCellFilter: 'translate'
               },
               {
            	   field: 'encounteredStatus',
            	   name: 'encounteredStatus',
				   displayName: 'Encountered',
				   width: '20%',
				   enableCellEdit: true,
				   editableCellTemplate: 'ui-grid/dropdownEditor',
				   enableCellEditOnFocus: true,
				   editDropdownValueLabel: 'encounteredStatus',				
				   editDropdownOptionsArray:  [
					{id:'Not Encountered Yet', encounteredStatus:'Not Encountered Yet'},
					{id:'In Progress - add user name to a list', encounteredStatus:'In Progress - add user name to a list'},
					{id:'Denied Boarding', encounteredStatus:'Denied Boarding'},
					{id:'Refused Entry', encounteredStatus:'Refused Entry'},
					{id:'Secondary Positive', encounteredStatus:'Secondary Positive'},
					{id:'Secondary Negative', encounteredStatus:'Secondary Negative'},
					{id:'Missed', encounteredStatus:'Missed'}
					],
				   headerCellFilter: 'translate',
				   cellTooltip:'Double click to make a status change'

               },
               {
            	   field: 'fullFlightNumber',
            	   name: 'fullFlightNumber',
            	   enableCellEdit: false,
            	   displayName: 'Flight', headerCellFilter: 'translate'
               },
	       {
            	   field: 'origDestAirportsStr',
            	   name: 'origDestAirportsStr',
            	   enableCellEdit: false,
            	   displayName: 'Origin/Destination', headerCellFilter: 'translate'
               },
               {
            	   field: 'etaEtdTime',
            	   name: 'etaEtdTime',
            	   enableCellEdit: false,
            	   displayName: 'ETD/ETA', headerCellFilter: 'translate'
               },
               {
            	   field: 'direction',
            	   name: 'direction',
            	   enableCellEdit: false,
            	   displayName: 'Direction', headerCellFilter: 'translate'
               }          
             ];

     	   
       
             
             $scope.getTableHeight = function () {
                 return gridService.calculateGridHeight(3);
             };
    	
    	
          
         
             
             
             $scope.getPreviousDayData = function () {
            	     		            		
            		 var today = new Date();
            		 var yesterday  = new Date();
            		 yesterday.setDate(today.getDate()-1);
            		 $scope.lookoutDate = $filter('date')(yesterday, 'yyyy-MM-dd', 'US') 
            		 $scope.isForwardButtonDisabled = false;
         		 	 $scope.isBackButtonDisabled = true;
         		 	 $scope.resolvePage();
         		 	 $scope.isTodayButtonDisabled = false;
             };
             
             
             
             $scope.getNextDayData = function () {
            	
            	 var today = new Date();
        		 var tomorrow  = new Date();
        		 tomorrow.setDate(today.getDate()+1);
        		 $scope.lookoutDate = $filter('date')(tomorrow, 'yyyy-MM-dd', 'US') 
        		 $scope.isForwardButtonDisabled = true;
     		 	 $scope.isBackButtonDisabled = false;
     		 	 $scope.resolvePage();
     		 	 $scope.isTodayButtonDisabled = false;
        		 
             };

             $scope.getTodaysData = function () {
            	
            	 	//current date
            	 	var currentDate = new Date();
        		 	$scope.lookoutDate = $filter('date')(currentDate, 'yyyy-MM-dd', 'US') 
        		 	$scope.isForwardButtonDisabled = false;
        		 	$scope.isBackButtonDisabled = false;
        		 	$scope.resolvePage();
        		 	$scope.isTodayButtonDisabled = true;
        		 
             };
             
             $scope.resolvePage = function () {
            	 
                 spinnerService.show('html5spinner');
        		 
                 oneDayLookoutService.getOneDayLookout($scope.lookoutDate).then(
                         function(data){
                             $scope.oneDayLookoutGrid.data = data.data;
                             spinnerService.hide('html5spinner');
                             
                         });
                 
			 };
			 
			 $scope.updateEncounteredStatus = function(caseId) {
				 oneDayLookoutService.updateEncounteredStatus(caseId);
			 }

			 $scope.showAlert = function(title, content) {
				 $mdDialog.show(
			      $mdDialog.alert()
			        .clickOutsideToClose(true)
			        .title(title)
			        .textContent(content)
			        .ariaLabel('Alert Status Change')
			        .ok('OK')
			        .openFrom('#left')
			        .closeTo(angular.element(document.querySelector('#right')))
			    );
			  };

		})
		
}());