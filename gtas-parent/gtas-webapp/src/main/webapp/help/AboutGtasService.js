/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app
        .service('aboutGtasService', function ($http, $q) {
        	
        	

        	 function getApplicationVersionNumber(){
                 
                 var dfd = $q.defer();
                 dfd.resolve($http({
                     method: 'get',
                     url: "/gtas/applicationVersionNumber"
                    
                 }));
                 return dfd.promise;
             }
        	 
        	 
            return ({
               
            	getApplicationVersionNumber: getApplicationVersionNumber
            });
        })
}());