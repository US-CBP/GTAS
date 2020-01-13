import GenericService from "./genericService";
import { hasData, isObject } from "../utils/text";
import Cookies from "js-cookie";

const GET = "get";
const DELETE = "delete";
const POST = "post";
const PUT = "put";
const APPLICATION_JSON = "application/json";
const FORM = "application/x-www-form-urlencoded";

const PUTBODY = "The put method requires a valid body parameter.";
const POSTBODY = "The post method requires a valid body parameter.";
const PUTID = "The put method requires a valid id parameter.";
const PUTPARAMS = "The put method requires parameters.";
const DELETEID = "The delete method requires a valid id parameter.";

// const HEAD = 'head';
// const COMMONTYPES = 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8';
// const PDF = 'application/pdf';
// const BMP = 'image/bmp';
// const JPEG = 'image/jpeg';
// const PNG = 'image/png';
// const WORD = 'application/msword';

// SETOPS - builds an collection of crud operations for the entities to expose as object methods. Static entity data
// (uri) is bound to the op methods here to keep the ops code dry and not force callers to pass it in as a param.
// There are other ways to do it, but it means the setOps/crud ops code won't change for most new endpoints.
export function setOps(ctx, ...fxns) {
  // context - allow just a uri string or an object for more params.
  const context = isObject(ctx)
    ? { uri: ctx.uri, contentType: ctx.contentType || APPLICATION_JSON }
    : { uri: ctx, contentType: APPLICATION_JSON };

  return fxns.reduce(function(obj, fxn) {
    const name = fxn.name === "del" ? "delete" : fxn.name;
    obj[name] = fxn.bind(context);
    return obj;
  }, {});
}

//  CRUD OPERATIONS
export function post(body) {
  if (!hasData(body)) throw new TypeError(POSTBODY);

  return GenericService({
    uri: this.uri,
    method: POST,
    contentType: this.contentType,
    body: JSON.stringify(body)
  });
}

export function authPost(body) {
  Cookies.remove("JSESSIONID");
  if (!hasData(body)) throw new TypeError(POSTBODY);
  const username = body.username !== undefined ? body.username.toUpperCase() : "";
  const password = body.password !== undefined ? body.password : "";
  body = "username=" + username + "&password=" + encodeURIComponent(password);

  return GenericService({
    uri: this.uri,
    method: POST,
    contentType: FORM,
    body: body,
    headers: { "X-Login-Ajax-call": "true" },
    contentTypeServer: "application/x-www-form-urlencoded"
  });
}

export function get(id, params) {
  const query = hasData(id) ? `/${id}` : hasData(params) ? params : "";

  return GenericService({ uri: this.uri + query, method: GET });
}

export function put(id, body) {
  if (!hasData(body)) throw new TypeError(PUTBODY);
  if (!hasData(id)) throw new TypeError(PUTID);

  const query = `\\${id}`;
  return GenericService({
    uri: this.uri + query,
    method: PUT,
    contentType: this.contentType,
    body: JSON.stringify(body)
  });
}

// APB shd params be an object so we can handle parsing it into a queryparam here?
// PutParams - same as put, but use queryparams rather than a body
export function putp(params) {
  if (!hasData(params)) throw new TypeError(PUTPARAMS);

  const query = `${params}`;
  return GenericService({
    uri: this.uri + query,
    method: PUT
  });
}

export function del(id) {
  if (!hasData(id)) throw new TypeError(DELETEID);

  const query = `\\${id}`;
  return GenericService({ uri: this.uri + query, method: DELETE });
}

// APB - ENTITY CONSTANTS and ENTITY METHODS is the only code we should need to touch when adding new endpoints

// ENTITY CONSTANTS
// APB - dummy endpoints. Move these guys to the env file
// const COMPANY = process.env.REACT_APP_SVC_COMPANY;
const COMPANY = "http://localhost:3004/company";
const FOO = "http://localhost:3004/foo";
const FILES = "http://localhost:3004/files";
const EMPLOYEES = "http://localhost:3004/employees";
const HACKS = "http://localhost:3004/hacks";
const LOGINS = "http://localhost:8080/gtas/authenticate";
const USERS = "http://localhost:8080/gtas/user";
const WATCHLISTCATS = "http://localhost:8080/gtas/wl/watchlistCategories";
const FLIGHTS = "http://localhost:8080/gtas/flights";
const AUDITLOG = "http://localhost:3004/auditlog?startDate=2019-11-04&endDate=2019-12-02";
const ERRORLOG = "http://localhost:3004/errorlog?startDate=2019-11-04&endDate=2019-12-02";
const CASES = "http://localhost:8080/gtas/hits";
const SETTINGSINFO = "http://localhost:3004/settingsinfo";
const GETRULECATS = "http://localhost:3004/getRuleCats";
const PAX = "http://localhost:3004/passengers";

// ENTITY METHODS
export const company = setOps(COMPANY, get, post);
export const foo = setOps(FOO, get, post, put, del);
export const files = setOps(FILES, get, post, put, del);
export const employees = setOps(EMPLOYEES, get, post);
export const hacks = setOps(HACKS, get);
export const watchlistcats = setOps(WATCHLISTCATS, get);
export const logins = setOps(LOGINS, authPost);
export const userService = setOps(USERS, get);
export const flights = setOps(FLIGHTS, get, post);
export const auditlog = setOps(AUDITLOG, get);
export const errorlog = setOps(ERRORLOG, get);
export const cases = setOps(CASES, get, post);
export const settingsinfo = setOps(SETTINGSINFO, get, put);
export const getrulecats = setOps(GETRULECATS, get);
export const passengers = setOps(PAX, get);
