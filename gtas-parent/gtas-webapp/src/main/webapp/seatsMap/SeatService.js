/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function() {
	'use strict';
	app.
		service('seatService', function($http, $q, Upload) {
		
			function getSeatsByFlightId(flightId) {
				var dfd = $q.defer();
				dfd.resolve($http.get("/gtas/flights/seats/" + flightId));
			return dfd.promise;
		}
		
			return ({
				getSeatsByFlightId : getSeatsByFlightId
			});
	});

}());