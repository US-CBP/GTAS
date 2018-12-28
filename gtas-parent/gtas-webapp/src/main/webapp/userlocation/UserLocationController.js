/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */


(function () {
    'use strict';
    app.controller('UserLocationController',
        function ($scope, $state, $http, 
        		userLocationData,userLocationService,
                  spinnerService) {
    	
    	 var stateName = $state ? $state.$current.self.name : 'userlocation';
    	 $scope.userLocation = null;
    	 $scope.userLocationList = userLocationData.data; 
    	 $scope.saveConfMessage = null;
         

             for (var i = 0; i < $scope.userLocationList.length; i++)
             {
            	 if( $scope.userLocationList[i].primaryLocation === true )
            	 	{
            		 	$scope.userLocation = $scope.userLocationList[i].airport;
            		} 
             }
             
               
       $scope.saveCurrentUserLocation = function () {
             	
            	 $scope.saveConfMessage = null;
            	 
            	 if($scope.userLocation!=null)
            	 {
            		  
            		 userLocationService.saveUserLocation($scope.userLocation).then(
                             function(result){
                            	 
                                	 var message = result.data;
                                	 if(message !=undefined && message===true)
                                		 $scope.saveConfMessage = "Office Location Saved Successfuly."
                                	else
                                		 $scope.saveConfMessage = "An error has occurred when saving Office Location."
                               
                             });
            	 }
            	 
        		
        		 
             };
             
          
             

        })
}());