/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app
        .service('oneDayLookoutService', function ($http, $q, Upload) {
        	
        	

        	 function getOneDayLookout(date){
                 
                 var dfd = $q.defer();
                 dfd.resolve($http({
                     method: 'get',
                     url: "/gtas/onedaylookout",
                     params: {
                    	 flightDate:date
                     }
                 }));
                 return dfd.promise;
             }
        	 
        	   
     
        	
        	
            return ({
               
                getOneDayLookout: getOneDayLookout
            });
        })
}());