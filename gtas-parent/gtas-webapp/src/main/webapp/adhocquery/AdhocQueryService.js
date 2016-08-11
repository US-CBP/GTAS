/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
app.service("adhocQueryService", function ($http, $q) {
    'use strict';

    function getFlights(query, page) {
        var dfd = $q.defer();
        dfd.resolve($http({
            method: 'get',
            url: "/gtas/search/queryFlights",
            params: { query: query, pageNumber: page }
        }));
        return dfd.promise;
    }

    function getPassengers(query, page) {
        var dfd = $q.defer();
        dfd.resolve($http({
            method: 'get',
            url: "/gtas/search/queryPassengers",
            params: { query: query, pageNumber: page }
        }));
        return dfd.promise;
    }

    function handleError (response) {
        if (!angular.isObject(response.data) || !response.data.message) {
            return ( $q.reject("An unknown error occurred.") );
        }
        return ( $q.reject(response.data.message) );
    }

    function handleSuccess(response) {
        return ( response.data );
    }

    // Return public API.
    return ({
        getFlights: getFlights,
        getPassengers: getPassengers
    });
});
