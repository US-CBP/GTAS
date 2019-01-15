/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app
        .service('userLocationService', function ($http, $q) {
        	
        	

        	 function getAllUserLocations(){
                 
                 var dfd = $q.defer();
                 dfd.resolve($http({
                     method: 'get',
                     url: "/gtas/getAllUserlocations"
                    
                 }));
                 return dfd.promise;
             }
        	 
        	 function saveUserLocation(userLocation){
                 
        		 var pageRequest = {
                         airport: userLocation
                     };
        		 
                     var dfd = $q.defer();
                     dfd.resolve($http({
                         method: 'post',
                         url: "/gtas/saveUserLocation/",
                         data: pageRequest
                     }));
                     return dfd.promise;
             }
        	   
     
        	
        	
            return ({
               
            	getAllUserLocations: getAllUserLocations,
            	saveUserLocation: saveUserLocation
            });
        })
}());