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
                  spinnerService,  $filter) {
    	
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
                 }
             };

             $scope.oneDayLookoutGrid.columnDefs = [
            	 
               {
            	   field: 'name',
            	   name: 'name',
            	   displayName: 'Name', headerCellFilter: 'translate',
            	   cellTemplate: '<md-button aria-label="type" href="#/casedetail/{{row.entity.caseId}}" title="Launch Case Detail in new window" target="case.detail" class="md-primary md-button md-default-theme" >{{COL_FIELD}}</md-button>'
            	 

               },

               {
            	   field: 'document',
            	   name: 'document',
            	   displayName: 'Passport Number', headerCellFilter: 'translate'
               },
               {
            	   field: 'disposition',
            	   name: 'disposition',
            	   displayName: 'Disposition', headerCellFilter: 'translate'
               },
               {
            	   field: 'encountered',
            	   name: 'encountered',
            	   displayName: 'Encountered'
               },
               {
            	   field: 'fullFlightNumber',
            	   name: 'fullFlightNumber',
            	   displayName: 'Flight', headerCellFilter: 'translate'
               },
	       {
            	   field: 'origDestAirportsStr',
            	   name: 'origDestAirportsStr',
            	   displayName: 'Origin/Destination', headerCellFilter: 'translate'
               },
               {
            	   field: 'etaEtdTime',
            	   name: 'etaEtdTime',
            	   displayName: 'ETD/ETA', headerCellFilter: 'translate'
               },
               {
            	   field: 'direction',
            	   name: 'direction',
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
          
             

        })
}());