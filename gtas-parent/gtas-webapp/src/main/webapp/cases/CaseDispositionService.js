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
                var dfd = $q.defer();
                dfd.resolve($http.get("/gtas/getAllCaseDispositions"));
                return dfd.promise;
            }

            return ({
                getDispositionStatuses: getDispositionStatuses,
                getAllCases:getAllCases,
                getOneHitsDisposition:getOneHitsDisposition,
                getOneHitsDispositionComments:getOneHitsDispositionComments,
                updateHitsDisposition:updateHitsDisposition,
                updateCase:updateCase
            });
        })
}());