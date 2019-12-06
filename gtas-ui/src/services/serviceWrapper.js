import GenericService from "./genericService";
import { hasData, isObject } from "../utils/text";

const GET = "get";
const DELETE = "delete";
const POST = "post";
const PUT = "put";
const JSON = "application/json";
// const FORM = 'multipart/form-data';

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
function setOps(ctx, ...fxns) {
  // context - allow just a uri string or an object for more params.
  const context = isObject(ctx)
    ? { uri: ctx.uri, contentType: ctx.contentType || JSON }
    : { uri: ctx, contentType: JSON };

  return fxns.reduce(function(obj, fxn) {
    const name = fxn.name === "del" ? "delete" : fxn.name;
    obj[name] = fxn.bind(context);
    return obj;
  }, {});
}

//  CRUD OPERATIONS
function post(body) {
  if (!hasData(body)) throw new TypeError(POSTBODY);

  return GenericService({
    uri: this.uri,
    method: POST,
    contentType: this.contentType,
    body: body
  });
}

function get(id, params) {
  const query = hasData(id) ? `/${id}` : hasData(params) ? params : "";

  return GenericService({ uri: this.uri + query, method: GET });
}

function put(body, id) {
  if (!hasData(body)) throw new TypeError(PUTBODY);
  if (!hasData(id)) throw new TypeError(PUTID);

  const query = `\\${id}`;
  return GenericService({
    uri: this.uri + query,
    method: PUT,
    contentType: this.contentType,
    body: body
  });
}

// APB shd params be an object so we can handle parsing it into a queryparam here?
// PutParams - same as put, but use queryparams rather than a body
function putp(params) {
  if (!hasData(params)) throw new TypeError(PUTPARAMS);

  const query = `${params}`;
  return GenericService({
    uri: this.uri + query,
    method: PUT
  });
}

function del(id) {
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
const LOGINS = "http://localhost:3004/logins";
const WATCHLISTCATS = "http://localhost:3004/watchlistcats";
const FLIGHTS = "http://localhost:3004/flights";
const AUDITLOG = "http://localhost:3004/auditlog?startDate=2019-11-04&endDate=2019-12-02";
const ERRORLOG = "http://localhost:3004/errorlog?startDate=2019-11-04&endDate=2019-12-02";
const CASES = "http://localhost:3004/cases";
const SETTINGSINFO = "http://localhost:3004/settingsinfo";

// ENTITY METHODS
export const company = setOps(COMPANY, get, post);
export const foo = setOps(FOO, get, post, put, del);
export const files = setOps(FILES, get, post, put, del);
export const employees = setOps(EMPLOYEES, get, post);
export const hacks = setOps(HACKS, get);
export const watchlistcats = setOps(WATCHLISTCATS, get);

export const logins = setOps(LOGINS, get, post, put);
export const flights = setOps(FLIGHTS, get, post);
export const auditlog = setOps(AUDITLOG, get);
export const errorlog = setOps(ERRORLOG, get);
export const cases = setOps(CASES, get);
export const settingsinfo = setOps(SETTINGSINFO, get, putp, put);
