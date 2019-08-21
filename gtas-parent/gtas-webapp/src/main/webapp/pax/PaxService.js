/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
(function () {
  'use strict';
  app
      .service('paxDetailService', function ($http, $q, Upload) {
         function getPaxCaseHistory(paxId) {
               var dfd = $q.defer();
               dfd.resolve($http.get("/gtas/passenger/caseHistory/" + paxId));
               return dfd.promise;
           }
        
          function getPaxDetail(paxId, flightId) {
              var dfd = $q.defer();
              dfd.resolve($http.get("/gtas/passengers/passenger/" + paxId + "/details?flightId=" + flightId));
              return dfd.promise;
          }

          function getPaxWatchlistLink(paxId){
              var dfd = $q.defer();
              dfd.resolve($http.get("/gtas/passengers/passenger/getwatchlistlink?paxId=" + paxId));
              return dfd.promise;
          }
          function savePaxWatchlistLink(paxId){
              var dfd = $q.defer();
              dfd.resolve($http.get("/gtas/passengers/passenger/savewatchlistlink?paxId=" + paxId));
              return dfd.promise;
          }

          function getPaxFlightHistory(paxId, flightId){
              var dfd = $q.defer();
              dfd.resolve($http.get("/gtas/passengers/passenger/flighthistory?paxId=" + paxId + "&flightId="+ flightId));
              return dfd.promise;
          }

          function getPaxFullTravelHistory(paxId, flightId){
            var dfd = $q.defer();
            dfd.resolve($http.get("/gtas/passengers/passenger/travelhistory?paxId=" + paxId + "&flightId=" + flightId));
            return dfd.promise;
          }

          function getPaxBookingDetailHistory(paxId, flightId){
              var dfd = $q.defer();
              dfd.resolve($http.get("/gtas/passengers/passenger/bookingdetailhistory?paxId=" + paxId + "&flightId=" + flightId));
              return dfd.promise;
          }

          function getPaxAttachments(paxId){
            var dfd = $q.defer();
            dfd.resolve($http.get('/gtas/getattachments?paxId='+ paxId));
            return dfd.promise;
          }

          function savePaxAttachments(username, password, description, paxId, file){
            if (!file.$error) {
                  Upload.upload({
                      url: '/gtas/uploadattachments',
                      data: {
                        username: username,
                        password: password,
                        desc: description,
                        paxId: paxId,
                        file: file
                      }
                  }).progress(function (evt) {
                    //progress tracker potentially
                  }).success(function (data, status, headers, config) {
                      return "success";
                  });
            }
          };

          function deleteAttachment(attId){
             var dfd = $q.defer();
               dfd.resolve($http.post("/gtas/deleteattachment", attId));
               return dfd.promise;
          }

          return ({getPaxCaseHistory: getPaxCaseHistory,
              getPaxDetail: getPaxDetail,
                  getPaxFlightHistory: getPaxFlightHistory,
                  getPaxFullTravelHistory: getPaxFullTravelHistory,
                  getPaxBookingDetailHistory: getPaxBookingDetailHistory,
                  getPaxAttachments: getPaxAttachments,
                  savePaxAttachments: savePaxAttachments,
                  deleteAttachment: deleteAttachment,
                  getPaxWatchlistLink: getPaxWatchlistLink,
                  savePaxWatchlistLink: savePaxWatchlistLink
                  });
      })
      .service('caseService', function ($http, $q) {
          function createDisposition(disposition) {
              var dfd = $q.defer();
              dfd.resolve($http.post("/gtas/disposition", disposition));
              return dfd.promise;
          }

          function getDispositionStatuses() {
              var dfd = $q.defer();
              dfd.resolve($http.get("/gtas/dispositionstatuses"));
              return dfd.promise;
          }

          function getAllCases(){
            var dfd = $q.defer();
            dfd.resolve($http.get("/gtas/allcases"));
            return dfd.promise;
          }

          function createOrEditDispositionStatus(dispStatusObj){
            var dfd = $q.defer();
            dfd.resolve($http.post("/gtas/createoreditdispstatus",dispStatusObj));
            return dfd.promise;
          }

          function deleteDispositionStatus(dispStatusObj){
            var dfd = $q.defer();
            dfd.resolve($http.post("/gtas/deletedispstatus", dispStatusObj));
            return dfd.promise;
          }

          return ({
              getDispositionStatuses: getDispositionStatuses,
              createDisposition: createDisposition,
              getAllCases:getAllCases,
              createOrEditDispositionStatus:createOrEditDispositionStatus,
              deleteDispositionStatus:deleteDispositionStatus
          });
      })
      .service("paxService", function (userService, $rootScope, $http, $q) {

          function getPassengersBasedOnUser(paxModel){
              var today = new Date();
              //first request
               return userService.getUserData().then( function( user ) {
                  if(user.data.filter!=null) {
                      if (user.data.filter.flightDirection)
                          paxModel.model.direction = user.data.filter.flightDirection;
                      if (typeof user.data.filter.etaStart  != undefined && user.data.filter.etaStart != null) {
                          paxModel.model.starteeDate = new Date();
                          paxModel.model.etaStart.setDate(today.getDate() + user.data.filter.etaStart);
                      }
                      if (typeof user.data.filter.etaEnd  != undefined && user.data.filter.etaEnd != null) {
                          paxModel.model.endDate = new Date();
                          paxModel.model.etaEnd.setDate(today.getDate() + user.data.filter.etaEnd);
                      }
                      if (user.data.filter.originAirports != null)
                          paxModel.model.origin = paxModel.model.origins = user.data.filter.originAirports;
                      if (user.data.filter.destinationAirports != null)
                          paxModel.model.dest = paxModel.model.destinations = user.data.filter.destinationAirports;
                  }
                  //second request
                  return getAllPax(paxModel.model);
              });
          }


          function getPax(flightId, pageRequest) {
            //This converts the date to the appropriate time, i.e. 00:00:00 on the start, and 23:59:59 on the end without impacting the front end visuals
            var tmp = jQuery.extend({},pageRequest);

            tmp.etaStart = new Date(Date.UTC(tmp.etaStart.getUTCFullYear(), tmp.etaStart.getMonth(), tmp.etaStart.getDate(),0,0,0));
            tmp.etaEnd = new Date(Date.UTC(tmp.etaEnd.getUTCFullYear(), tmp.etaEnd.getMonth(), tmp.etaEnd.getDate(),23,59,59));
              var dfd = $q.defer();
              dfd.resolve($http({
                  method: 'post',
                  url: '/gtas/flights/flight/' + flightId + '/passengers',
                  data: tmp
              }));
              return dfd.promise;
          }

          function getAllPax(pageRequest) {
            //This converts the date to the appropriate time, i.e. 00:00:00 on the start, and 23:59:59 on the end without impacting the front end visuals
            var tmp = jQuery.extend({},pageRequest);
            tmp.etaStart = new Date(Date.UTC(tmp.etaStart.getUTCFullYear(), tmp.etaStart.getMonth(), tmp.etaStart.getDate(),0,0,0));
            tmp.etaEnd = new Date(Date.UTC(tmp.etaEnd.getUTCFullYear(), tmp.etaEnd.getMonth(), tmp.etaEnd.getDate(),23,59,59));
              var dfd = $q.defer();
              dfd.resolve($http({
                  method: 'post',
                  url: '/gtas/passengers/',
                  data: tmp
              }));
              return dfd.promise;
          }

          function getPaxDetail(passengerId, flightId) {
              var url = "/gtas/passengers/passenger/" + passengerId + "/details?flightId=" + flightId;
              return $http.get(url).then(function (res) {
                  return res.data;
              });
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

          function broadcast(flightId) {
              $rootScope.$broadcast('handleBroadcast', flightId);
          }

          function getRuleHits(passengerId) {
              var request = $http({
                  method: "get",
                  url: "/gtas/hit/passenger" + (passengerId ? "?passengerId=" + passengerId : ""),
                  params: {
                      action: "get"
                  }
              });
              return (request.then(handleSuccess, handleError));
          }

          function getRuleHitsByFlightAndPax(passengerId, flightId) {
              var request = $http({
                  method: "get",
                  url: "/gtas/hit/flightpassenger" + (passengerId ? "?passengerId=" + passengerId : "") + (flightId ? "&flightId=" + flightId : ""),
                  params: {
                      action: "get"
                  }
              });

              return (request.then(function(res){
                return formatHitDetails(res);
              }));
          }

          function broadcastRuleID(ruleID) {
              $rootScope.$broadcast('ruleIDBroadcast', ruleID);
          }

          //Open up rule hits summaries to break apart details for display.
          function formatHitDetails(ruleSummaryHits){
            var ruleHitsList = [];
            if(angular.isDefined(ruleSummaryHits) && ruleSummaryHits.data.length > 0){
              $.each(ruleSummaryHits.data, function(index,value){
                var hitDetail = value.hitsDetailsList[0]; //First object in this 'array' contains the values needed for the front-end display
                hitDetail.category = value.category;
                ruleHitsList.push(hitDetail); 
              });
              }
            return ruleHitsList;
            };

          // Return public API.
          return ({
              getPax: getPax,
              getAllPax: getAllPax,
              broadcast: broadcast,
              getRuleHits: getRuleHits,
              getRuleHitsByFlightAndPax: getRuleHitsByFlightAndPax,
              getPaxDetail: getPaxDetail,
              broadcastRuleID: broadcastRuleID,
              getPassengersBasedOnUser: getPassengersBasedOnUser
          });
      });
}());
