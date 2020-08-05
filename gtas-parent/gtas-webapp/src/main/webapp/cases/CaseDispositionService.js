/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app
        .service('caseDispositionService', function ($http, $q, Upload) {

            function getAllCases(){

                var pageRequest = {
                    pageSize: "10",
                    pageNumber: "1",
                    displayStatusCheckBoxes: getDefaultDispCheckboxes(),
                    withTimeLeft: getDefaultTimeLeft(),
                    myRulesOnly: false,
                    ruleTypes:    {
                        WATCHLIST: true,
                        USER_RULE: true,
                        GRAPH_RULE: true,
                        MANUAL: true,
                        EXTERNAL_RULE: true,
                        PARTIAL_WATCHLIST: false
                    },
                    ruleCatFilter: getDefaultCats(),
                    etaStart: getDefaultStartDate(),
                    etaEnd: getDefaultEndDate(),
                    sort : getDefaultSort()
                };
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'get',
                    url: "/gtas/hits/",
                    params: {requestDto: pageRequest},
                    data: '',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                }));
                return dfd.promise;
            }

            function getDefaultEndDate() {
                const DEFAULT_DAYS_FORWARD = 3;
                return getTargetDate(DEFAULT_DAYS_FORWARD);
            }

            function getDefaultStartDate() {
                const DEFAULT_DAYS_BACK = 0;
                return getTargetDate(DEFAULT_DAYS_BACK);
            }

            function getTargetDate(days) {
                let targetDate = new Date();
                targetDate.setDate(targetDate.getDate() + days);
                return targetDate;
            }

            function getDefaultDispCheckboxes() {
                return {
                    NEW: true,
                    RE_OPENED: false,
                    REVIEWED: false
                };
            }
            function getDefaultTimeLeft() {
                return true;
            }
            function getDefaultSort() {
                return [
                    {column: 'countdown', dir: 'asc'}
                ];
            }

            function getPagedCases(params){
                var pageRequest = {
                    pageSize: params.pageSize.toString(),
                    pageNumber: params.pageNumber.toString(),
                    sort: params.sort,
                    lastName: params.model.name,
                    flightNumber: params.model.flightNumber,
                    status: params.model.status,
                    withTimeLeft: params.model.withTimeLeft,
                    ruleCatId: params.model.ruleCat,
                    etaStart: params.model.etaStart,
                    etaEnd: params.model.etaEnd,
                    ruleTypes: params.model.ruleTypes,
                    displayStatusCheckBoxes : params.model.displayStatusCheckBoxes,
                    ruleCatFilter: params.model.ruleCatFilter,
                    myRulesOnly: params.myRulesOnly
                };
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'get',
                    url: "/gtas/hits/",
                    params: {requestDto: pageRequest},
                    data: '',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                }));
                return dfd.promise;
            }

            function getOneHitsDisposition(paramCaseId, paramFlight, paramPax){
                var param = {
                    pageSize: "10",
                    pageNumber: "1",
                    flightId: paramFlight,
                    paxId: paramPax,
                    caseId: paramCaseId
                };
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'post',
                    url: "/gtas/getOneHistDisp/",
                    data: param
                }));
                return dfd.promise;
            }

            function getByQueryParams(model){
                var param = {
                    pageSize: model.pageSize.toString(),
                    pageNumber: model.pageNumber.toString(),
                    flightNumber: model.flightNumber,
                    displayStatusCheckBoxes: model.displayStatusCheckBoxes,
                    ruleCatFilter: model.ruleCatFilter,
                    myRulesOnly: model.myRulesOnly,
                    ruleTypes: model.ruleTypes,
                    withTimeLeft: model.withTimeLeft,
                    lastName: model.name,
                    status: model.status,
                    ruleCatId: model.ruleCat,
                    etaStart: model.etaStart,
                    etaEnd: model.etaEnd,
                    etaEtdFilter: model.etaEtdFilter,
                    etaEtdSortFlag: model.etaEtdSortFlag,
                    sort: model.sort
                };
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'get',
                    url: "/gtas/hits/",
                    params: {requestDto: param},
                    data: '',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                }));
                return dfd.promise;
            }

            function updateHitsDisposition(paramCaseId, paramFlight, paramPax, paramHit, paramComments, paramStatus, 
                                           paramValidHit, file, paramCaseDisposition){
                var requestDto = {
                    pageSize: "10",
                    pageNumber: "1",
                    flightId: paramFlight,
                    paxId: paramPax,
                    hitId: paramHit,
                    caseComments: paramComments,
                    status: paramStatus,
                    caseDisposition: paramCaseDisposition,
                    validHit: paramValidHit,
                    caseId : paramCaseId
                    // ,
                    // multipartFile: file
                };

                var dfd = $q.defer();

                if(file!=null){
                    dfd.resolve(
                    Upload.upload({
                        method: 'post',
                        url: '/gtas/updateHistDispAttachments/',
                        data: {
                            flightId: paramFlight,
                            paxId: paramPax,
                            hitId: paramHit,
                            caseComments: paramComments,
                            status: paramStatus,
                            validHit: paramValidHit,
                            file: file,
                            caseDisposition: paramCaseDisposition,
                            caseId : paramCaseId
                        }
                    }))
                    ;
                }else{
                    dfd.resolve(
                            $http({
                            method: 'post',
                            url: "/gtas/updateHistDisp/",
                            data: requestDto
                            })
                    );
                }
                return dfd.promise;
            }

            function updateGeneralComments(caseId, comment,
                                           caseStatus) {
                const requestDto = {
                    comment: comment,
                    caseId : caseId,
                    caseStatus: caseStatus
                };
                const deferredQuery = $q.defer();
                deferredQuery.resolve(
                    $http({
                        method: 'post',
                        url: "/gtas/addCaseComment/",
                        data: requestDto
                    })
                );

                return deferredQuery.promise;

            }
            function addToOneDayLookout(caseIdParam){
                
                 var dfd = $q.defer();
                 dfd.resolve($http({
                     method: 'get',
                     url: "/gtas/addonedaylookout",
                     params: {
                    	 caseId:caseIdParam
                     }
                 }));
        		 
        	        		 
                 return dfd.promise;
             }
     
                    
            function removeFromOneDayLookoutList(caseIdParam){
                
                var dfd = $q.defer();
                dfd.resolve($http({
                    method: 'get',
                    url: "/gtas/removeonedaylookout",
                    params: {
                   	 caseId:caseIdParam
                    }
                }));
       		 
       	        		 
                return dfd.promise;
            }  
            
            
            function getDispositionStatuses() {
                var dfd = $q.defer();
                dfd.resolve($http.get("/gtas/dispositionstatuses"));
                return dfd.promise;
            }

            function getDefaultModel() {
                const emptyString = "";
                return {
                    name: emptyString,
                    flightNumber: emptyString,
                    displayStatusCheckBoxes: getDefaultDispCheckboxes(),
                    status: emptyString,
                    priority: emptyString,
                    ruleCat: emptyString,
                    etaStart: getDefaultStartDate(),
                    etaEnd: getDefaultEndDate(),
                    withTimeLeft: getDefaultTimeLeft(),
                    sort : getDefaultSort()
                };
            }

            function getHitDispositionStatuses() {
                var dfd = $q.defer();
                dfd.resolve($http.get("/gtas/hitdispositionstatuses"));
                return dfd.promise;
            }

            function getCaseDisposition() {
                var dfd = $q.defer();
                dfd.resolve($http.get("/gtas/casedisposition"));
                return dfd.promise;
            }
            
            function getRuleCats() {
                var dfd = $q.defer();
                dfd.resolve($http.get("/gtas/getRuleCats"));
                return dfd.promise;
            }

            function saveCaseDispAttachments() {
                var dfd = $q.defer();
                dfd.resolve($http.get("/gtas/uploadattachments"));
                return dfd.promise;
            }

            function postManualCase(paramFlight, paramPax, paramRuleCatId, paramComments,file){

                var requestDto = {
                    flightId: paramFlight,
                    paxId: paramPax,
                    ruleCatId: paramRuleCatId,
                    caseComments: paramComments
                };

                var dfd = $q.defer();

                if(file!=null){
                    dfd.resolve(
                        Upload.upload({
                            method: 'post',
                            url: '/gtas/updateHistDispAttachments/',
                            data: {
                                flightId: paramFlight,
                                paxId: paramPax,
                                hitId: paramHit,
                                caseComments: paramComments,
                                status: paramStatus,
                                validHit: paramValidHit,
                                file: file
                            }
                        }))
                    ;
                }else{

                    dfd.resolve(

                        $http({
                            method: 'post',
                            url: "/gtas/createManualCase/",
                            data: requestDto
                        })
                    );
                }

                return dfd.promise;
            }

//            function getAppConfigAPISFlag(){
//
//                var dfd = $q.defer();
//                dfd.resolve($http.get("/gtas/countdownAPISFlag"));
//                return dfd.promise;
//
//            }

            function updatePassengerHitViews(hitViewVo, status) {
                let dfd = $q.defer();
                let updateVo = {
                    flightId: hitViewVo.flightId,
                    passengerId: hitViewVo.paxId,
                    status: status
                };
                dfd.resolve(
                    $http({
                        method: 'post',
                        url: "/gtas/hits/",
                        data: updateVo
                    })
                );
            return dfd.promise;
            }

             function getCurrentServerTime() {
  
                var currentServerTimeMillis = 0;
                
                $.ajax({
                    async: false,
                    url: "/gtas/getCurrentServerTime",
                    success: function (data, status, jqXHR) {
                       currentServerTimeMillis = data;
                     }
                });
                
                return currentServerTimeMillis;
             }

            function getDefaultCats() {
                let ruleList = [];
                getRuleCats().then(function(result){
                    for (let rule of Object.values(result.data)) {
                        ruleList.push({name : rule.name, value: true})
                    }
                });
                return ruleList;
            }
            return ({
                getDispositionStatuses: getDispositionStatuses,
                getHitDispositionStatuses: getHitDispositionStatuses,
                getCaseDisposition: getCaseDisposition,
                getAllCases: getAllCases,
                getOneHitsDisposition: getOneHitsDisposition,
                getRuleCats: getRuleCats,
                getDefaultCats: getDefaultCats,
                updatePassengerHitViews : updatePassengerHitViews,
                updateHitsDisposition: updateHitsDisposition,
                addToOneDayLookout: addToOneDayLookout,
                removeFromOneDayLookoutList: removeFromOneDayLookoutList,
                getPagedCases: getPagedCases,
                postManualCase: postManualCase,
                getByQueryParams: getByQueryParams,
                getCurrentServerTime: getCurrentServerTime,
                getDefaultStartDate: getDefaultStartDate,
                getDefaultEndDate: getDefaultEndDate,
                getDefaultDispCheckboxes: getDefaultDispCheckboxes,
                updateGeneralComments : updateGeneralComments,
                getDefaultSort: getDefaultSort,
                getDefaultTimeLeft: getDefaultTimeLeft,
                getDefaultModel: getDefaultModel
                //getAppConfigAPISFlag: getAppConfigAPISFlag
            });
        })
}());