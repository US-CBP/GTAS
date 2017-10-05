/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app
        .service('caseDispositionService', function ($http, $q) {

            function getAllCases(){
                var pageRequest = {
                    pageSize: "10",
                    pageNumber: "1"
                };
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'post',
                    url: "/gtas/getAllCaseDispositions/",
                    data: pageRequest
                }));
                return dfd.promise;
            }

            function getPagedCases(params){
                var pageRequest = {
                    pageSize: params.pageSize.toString(),
                    pageNumber: params.pageNumber.toString()
                };
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'post',
                    url: "/gtas/getAllCaseDispositions/",
                    data: pageRequest
                }));
                return dfd.promise;
            }

            function getOneHitsDisposition(paramFlight, paramPax){
                var param = {
                    pageSize: "10",
                    pageNumber: "1",
                    flightId: paramFlight,
                    paxId: paramPax
                }
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'post',
                    url: "/gtas/getAllCaseDispositions/",
                    data: param
                }));
                return dfd.promise;
            }

            function updateHitsDisposition(paramFlight, paramPax, paramHit, paramComments, paramStatus, paramValidHit){
                var requestDto = {
                    pageSize: "10",
                    pageNumber: "1",
                    flightId: paramFlight,
                    paxId: paramPax,
                    hitId: paramHit,
                    caseComments: paramComments,
                    status: paramStatus,
                    validHit: paramValidHit
                }
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'post',
                    url: "/gtas/updateHistDisp/",
                    data: requestDto
                }));
                return dfd.promise;
            }

            function getDispositionStatuses() {
                var dfd = $q.defer();
                dfd.resolve($http.get("/gtas/dispositionstatuses"));
                return dfd.promise;
            }

            function getHitDispositionStatuses() {
                var dfd = $q.defer();
                dfd.resolve($http.get("/gtas/hitdispositionstatuses"));
                return dfd.promise;
            }

            function getRuleCats() {
                var dfd = $q.defer();
                dfd.resolve($http.get("/gtas/getRuleCats"));
                return dfd.promise;
            }

            return ({
                getDispositionStatuses: getDispositionStatuses,
                getHitDispositionStatuses: getHitDispositionStatuses,
                getAllCases:getAllCases,
                getOneHitsDisposition:getOneHitsDisposition,
                getRuleCats:getRuleCats,
                updateHitsDisposition:updateHitsDisposition,
                getPagedCases: getPagedCases
            });
        })
}());