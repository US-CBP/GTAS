/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */


(function () {
    'use strict';
    app.controller('AboutGtasCtr',
        function ($scope, $state, $http, aboutGtasService,appVersionNumber) {
    	
    	 var stateName = $state ? $state.$current.self.name : 'aboutgtas';
    	 
    	$scope.gtasVersionNumber = appVersionNumber.data;
    	 
        })
}());