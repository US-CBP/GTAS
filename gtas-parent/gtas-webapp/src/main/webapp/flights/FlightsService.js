/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.service("flightService", function ($http, $q) {
  'use strict';

  function getFlights(pageRequest) {
    //This converts the date to the appropriate time, i.e. 00:00:00 on the start, and 23:59:59 on the end without impacting the front end visuals
    var tmp = jQuery.extend({},pageRequest);
    tmp.etaStart = new Date(Date.UTC(tmp.etaStart.getUTCFullYear(), tmp.etaStart.getMonth(), tmp.etaStart.getDate(),0,0,0));
    tmp.etaEnd = new Date(Date.UTC(tmp.etaEnd.getUTCFullYear(), tmp.etaEnd.getMonth(), tmp.etaEnd.getDate(),23,59,59));

      var dfd = $q.defer();
      dfd.resolve($http({
          method: 'post',
          url: "/gtas/flights/",
          data: tmp
      }));
      return dfd.promise;
  }

  // I transform the error response, unwrapping the application dta from
  // the API response payload.
  function handleError (response) {
      // The API response from the server should be returned in a
      // normalized format. However, if the request was not handled by the
      // server (or what not handles properly - ex. server error), then we
      // may have to normalize it on our end, as best we can.
      if (!angular.isObject(response.data) || !response.data.message) {
          return ( $q.reject("An unknown error occurred.") );
      }
      // Otherwise, use expected error message.
      return ( $q.reject(response.data.message) );
  }

  // I transform the successful response, unwrapping the application data
  // from the API response payload.
  function handleSuccess(response) {
      return ( response.data );
  }
  
  function getFlightDirectionList(){
      
      var dfd = $q.defer();
      dfd.resolve($http({
          method: 'get',
          url: "/gtas/flightdirectionlist"
         
      }));
      return dfd.promise;
  }

  // Return public API.
  return ({
      getFlights: getFlights,
      getFlightDirectionList:getFlightDirectionList
  });
});
