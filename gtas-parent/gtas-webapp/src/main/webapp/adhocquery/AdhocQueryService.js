/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.service("adhocQueryService", function ($http, $q) {
    'use strict';

    function getPassengers(query, page, pageSize, sort) {
        var dfd = $q.defer();
        dfd.resolve($http({
            method: 'get',
            url: "/gtas/search/queryPassengers",
            params: { 
                query: query, 
                pageNumber: page, 
                pageSize: pageSize, 
                column: sort.column,
                dir: sort.dir 
            }
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
        getPassengers: getPassengers
    });
});
