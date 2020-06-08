import GenericService from "./genericService";
import { hasData } from "../utils/utils";

const GET = "get";
const DELETE = "delete";
const POST = "post";
const PUT = "put";
const AJSON = "application/json";
const FORM = "application/x-www-form-urlencoded";

const LOGINHEADER = {
  "X-Login-Ajax-call": "true",
  "Content-Type": FORM,
  "X-Requested-With": "XMLHttpRequest",
  Accept: "application/json, text/plain, */*",
  "Accept-Encoding": "gzip, deflate, br"
};

const BASEHEADER = { "Content-Type": AJSON, Accept: AJSON };
const PUTBODY = "The put method requires a valid body parameter.";
const POSTBODY = "The post method requires a valid body or data parameter.";
const PUTID = "The put method requires a valid id parameter.";
const PUTPARAMS = "The put method requires parameters.";
const DELETEID = "The delete method requires a valid id parameter.";

function get(uri, headers, id, params) {
  const query = hasData(id) ? `/${id}` : hasData(params) ? params : "";
  return GenericService({ uri: uri + query, method: GET, headers: headers });
}

function post(uri, headers, body, data) {
  if (
    !hasData(body) &&
    !(body instanceof FormData) &&
    !(body instanceof URLSearchParams) &&
    !(data instanceof URLSearchParams) &&
    !hasData(data)
  )
    throw new TypeError(POSTBODY);

  return GenericService({
    uri: uri,
    method: POST,
    headers: headers,
    body: body
  });
}

function put(uri, headers, id, body) {
  if (!hasData(body)) throw new TypeError(PUTBODY);
  if (!hasData(id)) throw new TypeError(PUTID);

  const query = `\\${id}`;

  return GenericService({
    uri: uri + query,
    method: PUT,
    body: body,
    headers: headers
  });
}

function del(uri, id) {
  if (!hasData(id)) throw new TypeError(DELETEID);

  return GenericService({ uri: `${uri}\\${id}`, method: DELETE });
}

// APB - ENTITY CONSTANTS and ENTITY METHODS is the only code we should need to touch when adding new endpoints
const BASE_URL = process.env.REACT_APP_BASE_URL;

const LOGIN = `${BASE_URL}gtas/authenticate`;
const USERS = `${BASE_URL}gtas/users/`;
const WLCATS = `${BASE_URL}gtas/wl/watchlistCategories`;
const WLCATSPOST = `${BASE_URL}gtas/wlput/wlcat/`;
const FLIGHTS = `${BASE_URL}gtas/flights`;
const AUDITLOG = `${BASE_URL}auditlog?startDate=2019-11-04&endDate=2019-12-02`;
const ERRORLOG = `${BASE_URL}gtas/errorlog`;
const CASES = `${BASE_URL}gtas/hits`;
const SETTINGSINFO = `${BASE_URL}gtas/settingsinfo`;
const GETRULECATS = `${BASE_URL}getRuleCats`;
const PAX = `${BASE_URL}passengers`;
const LOADERSTATISTICS = `${BASE_URL}gtas/api/statistics`;
const RULE_CATS = `${BASE_URL}gtas/getRuleCats`;
const NOTE_TYPES = `${BASE_URL}gtas/passengers/passenger/notetypes`;
const LOGGEDIN_USER = `${BASE_URL}gtas/user`;

// ENTITY METHODS
export const users = {
  get: (id, params) => get(USERS, BASEHEADER, id, params),
  put: (id, body) => put(USERS, BASEHEADER, id, body),
  post: body => post(USERS, BASEHEADER, body)
};
export const watchlistcats = {
  get: (id, params) => get(WLCATS, BASEHEADER, id, params),
  post: body => post(WLCATS, BASEHEADER, body)
};

export const watchlistcatspost = { post: body => post(WLCATSPOST, BASEHEADER, body) };
export const userService = { get: (id, params) => get(USERS, BASEHEADER) };
export const flights = { get: (id, params) => get(FLIGHTS, BASEHEADER) };
export const auditlog = { get: (id, params) => get(AUDITLOG, BASEHEADER) };
export const errorlog = { get: (id, params) => get(ERRORLOG, BASEHEADER) };
export const cases = { get: (id, params) => get(CASES, BASEHEADER) };
export const ruleCats = { get: (id, params) => get(RULE_CATS, BASEHEADER) };
export const settingsinfo = { get: (id, params) => get(SETTINGSINFO, BASEHEADER) };
export const getrulecats = { get: (id, params) => get(GETRULECATS, BASEHEADER) };
export const passengers = { get: (id, params) => get(PAX, BASEHEADER) };
export const loaderStats = { get: (id, params) => get(LOADERSTATISTICS, BASEHEADER) };
export const notetypes = { get: (id, params) => get(NOTE_TYPES, BASEHEADER) };
export const loggedinUser = { get: (id, params) => get(LOGGEDIN_USER, BASEHEADER) };

export const login = {
  post: body => {
    const username = body["username"].toUpperCase();
    const password = encodeURIComponent(body["password"]);
    const creds = new URLSearchParams(`username=${username}&password=${password}`);

    return post(LOGIN, LOGINHEADER, creds).then(res => {
      if (!res.ok) return res;

      return get(LOGGEDIN_USER, BASEHEADER);
    });
  }
};
