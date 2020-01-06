import { setOps, get, post, del } from "./serviceWrapper";

// hosts
const MOCK = "http://localhost:3004";
const HOST = "http://localhost:8080/gtas";

// errors
const PAXIDREQUIRED = "This method requires a Pax ID param.";
const FLIGHTIDREQUIRED = "This method requires a Flight ID param.";

// ENDPOINTS
/**
 TODO - Need to clean up the endpoints here. There should be no verbs in the uri names (getX, saveX, etc),
 since these operations are specified by the request methods. Also need to better organize how
 we reference ids and nest entities. see https://restfulapi.net/resource-naming/

prefer this:
  GET .../pax/{:paxid}/foo
to this:
  GET .../pax/getFoo?id=paxId
    
*/

const PAX = `${MOCK}/passengers`;
const PAXCASEHISTORY = paxId => {
  if (!paxId) throw new TypeError(PAXIDREQUIRED);

  return `${HOST}/passenger/caseHistory/${paxId}`;
};

const PAXDETAIL = (paxId, flightId) => {
  if (!paxId) throw new TypeError(PAXIDREQUIRED);
  if (!flightId) throw new TypeError(FLIGHTIDREQUIRED);

  return `${HOST}/passengers/passenger/${paxId}/details?flightId=${flightId}`;
};

// getPax(flightId, pageRequest) {
//               //This converts the date to the appropriate time, i.e. 00:00:00 on the start, and 23:59:59 on the end without impacting the front end visuals
//               var tmp = jQuery.extend({},pageRequest);
//               tmp.etaStart = new Date(Date.UTC(tmp.etaStart.getUTCFullYear(), tmp.etaStart.getMonth(), tmp.etaStart.getDate(),0,0,0));
//               tmp.etaEnd = new Date(Date.UTC(tmp.etaEnd.getUTCFullYear(), tmp.etaEnd.getMonth(), tmp.etaEnd.getDate(),23,59,59));
//                 var dfd = $q.defer();
//                 dfd.resolve($http({
//                     method: 'post',
//                     url: '/gtas/flights/flight/' + flightId + '/passengers',
//                     data: tmp
//                 }));
//                 return dfd.promise;
//             }

//           function getAllPax(pageRequest) {
//               //This converts the date to the appropriate time, i.e. 00:00:00 on the start, and 23:59:59 on the end without impacting the front end visuals
//               var tmp = jQuery.extend({},pageRequest);
//               tmp.etaStart = new Date(Date.UTC(tmp.etaStart.getUTCFullYear(), tmp.etaStart.getMonth(), tmp.etaStart.getDate(),0,0,0));
//               tmp.etaEnd = new Date(Date.UTC(tmp.etaEnd.getUTCFullYear(), tmp.etaEnd.getMonth(), tmp.etaEnd.getDate(),23,59,59));
//                 var dfd = $q.defer();
//                 dfd.resolve($http({
//                     method: 'post',
//                     url: '/gtas/passengers/',

const PAXWATCHLISTLINK = paxId => {
  if (!paxId) throw new TypeError(PAXIDREQUIRED);

  // TODO - 1. change endpoint to "gtas/passengers/:id/watchlistlink" and
  // 2. handle both get and post (save) with it.
  return `${HOST}/passengers/passenger/getwatchlistlink?paxId=${paxId}`;
};

// TODO, combine the endpoint here with the above
// const savePaxWatchlistLink(paxId){
//               var dfd = $q.defer();
//               dfd.resolve($http.get("/gtas/passengers/passenger/savewatchlistlink?paxId=" + paxId));
//               return dfd.promise;
//           }

const PAXFLIGHTHISTORY = (paxId, flightId) => {
  if (!paxId) throw new TypeError(PAXIDREQUIRED);
  if (!flightId) throw new TypeError(FLIGHTIDREQUIRED);

  // TODO - change to "passengers/{:paxid}/flighthistory/{:flightId}
  return `${HOST}/passengers/passenger/flighthistory?paxId=${paxId}&flightId=${flightId}`;
};

const PAXFULLTRAVELHISTORY = (paxId, flightId) => {
  if (!paxId) throw new TypeError(PAXIDREQUIRED);
  if (!flightId) throw new TypeError(FLIGHTIDREQUIRED);
  // TODO - "passengers/{:paxid}/travelhistory/{:flightId}
  return `${HOST}/passengers/passenger/travelhistory?paxId=${paxId}&flightId=${flightId}`;
};

const PAXBOOKINGDETAILHISTORY = (paxId, flightId) => {
  if (!paxId) throw new TypeError(PAXIDREQUIRED);
  if (!flightId) throw new TypeError(FLIGHTIDREQUIRED);
  // TODO - "passengers/{:paxid}/bookingdetailhistory/{:flightId}
  return `${HOST}/passengers/passenger/bookingdetailhistory?paxId=${paxId}&flightId=${flightId}`;
};

const PAXDETAILHITHISTORY = paxId => {
  if (!paxId) throw new TypeError(PAXIDREQUIRED);
  // "passengers/{:paxid}/hitdetailhistory
  return `${HOST}/passengers/passenger/hitdetailhistory?paxId=${paxId}`;
};

// pass 5 params to handle GET and POST
const PAXATTACHMENTS = (paxId, username, password, description, file) => {
  if (!paxId) throw new TypeError(PAXIDREQUIRED);

  // passengers/{:id}/attachments for the GET, or maybe access the attachments directly?
  // attachments/{:attachmentId} ???
  // for POST, pass the other props in the body object
  return `${HOST}/getattachments?paxId=${paxId}`;
};

// TODO - handle this in the above function
//             if (!file.$error) {
//                   Upload.upload({
//                       url: '/gtas/uploadattachments',
// data: {
//                           username: username,
//                           password: password,
//                           desc: description,
//                           paxId: paxId,
//                           file: file
//                         }
// }

export const passengers = setOps(PAX, get);
export const paxCaseHistory = paxId => setOps(PAXCASEHISTORY(paxId), get);
export const paxWatchlistLink = paxId => setOps(PAXWATCHLISTLINK(paxId), get, post);
export const paxDetail = (paxId, flightId) => setOps(PAXDETAIL(paxId, flightId), get);
export const paxFlightHistory = (paxId, flightId) =>
  setOps(PAXFLIGHTHISTORY(paxId, flightId), get);
export const paxFullTravelHistory = (paxId, flightId) =>
  setOps(PAXFULLTRAVELHISTORY(paxId, flightId), get, post);
export const paxBookingDetailHistory = (paxId, flightId) =>
  setOps(PAXBOOKINGDETAILHISTORY(paxId, flightId), get);
export const paxDetailHitHistory = paxId => setOps(PAXDETAILHITHISTORY(paxId), get);
export const paxAttachments = (paxId, username, password, description, file) =>
  setOps(PAXATTACHMENTS(paxId, username, password, description, file), get, post, del);
