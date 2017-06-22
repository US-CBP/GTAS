/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app
        .service("dashboardService", function ($rootScope, $http, $q) {

            function getFlightsAndPassengersAndHitsCount(startDate, endDate) {
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'get',
                    url: '/gtas/getFlightsAndPassengersAndHitsCount',
                    params: {
                        startDate: startDate,
                        endDate: endDate
                    }
                }));
                return dfd.promise;
            }

            function getFlightsAndPassengersAndHitsCountInbound(startDate, endDate) {
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'get',
                    url: '/gtas/getFlightsAndPassengersAndHitsCountInbound',
                    params: {
                        startDate: startDate,
                        endDate: endDate
                    }
                }));
                return dfd.promise;
            }

            function getFlightsAndPassengersAndHitsCountOutbound(startDate, endDate) {
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'get',
                    url: '/gtas/getFlightsAndPassengersAndHitsCountOutbound',
                    params: {
                        startDate: startDate,
                        endDate: endDate
                    }
                }));
                return dfd.promise;
            }

            function getMessagesCount(startDate, endDate) {

                    var request = $http({
                        method: 'get',
                        url: '/gtas/getMessagesCount',
                        params: {
                            startDate: startDate,
                            endDate: endDate
                        }
                    });
                    return (request.then(handleSuccess, handleError));

            }

            function getYtdRulesCount(){

                var request = $http({
                    method: 'get',
                    url: '/gtas/getYtdRulesCount'
                });
                return (request.then(handleSuccess, handleError));
            }

            function getYtdAirportStats(){

                var request = $http({
                    method: 'get',
                    url: '/gtas/getYtdAirportStats'
                });
                return (request.then(handleSuccess, handleError));
            }

            function handleSuccess(response) {
                return (response.data);
            }

            function handleError(response) {
                if (!angular.isObject(response.data) || !response.data.message) {
                    return ($q.reject("An unknown error occurred."));
                }
                return ($q.reject(response.data.message));
            }

            // Return public API.
            return ({
            	getFlightsAndPassengersAndHitsCount: getFlightsAndPassengersAndHitsCount,
                getFlightsAndPassengersAndHitsCountInbound: getFlightsAndPassengersAndHitsCountInbound,
                getFlightsAndPassengersAndHitsCountOutbound: getFlightsAndPassengersAndHitsCountOutbound,
                getMessagesCount: getMessagesCount,
                getYtdAirportStats: getYtdAirportStats,
                getYtdRulesCount: getYtdRulesCount

            });
        });
}());
