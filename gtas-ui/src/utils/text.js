// APB - shd add handling for other naming patterns like underscores and dashes, and maybe
// reference an enum of well-know acronyms to preserve casing there.
export function titleCase(input) {
  let str = input;

  let result = str
    .replace(/_|-/g, " ")
    .replace(/(\b[a-z](?!\s))/g, x => x.toUpperCase())
    .replace(/^[a-z]|[^\sA-Z][A-Z]/g, function(str2, idx) {
      return idx === 0
        ? str2.toUpperCase()
        : `${str2.substr(0, 1)} ${str2.substr(1).toUpperCase()}`;
    })
    .replace(/^\s*/, "");
  return result;
}

export function hasData(obj) {
  if (typeof obj === "object" || typeof obj === "undefined")
    return Object.keys(obj || {}).length > 0;
  else return String(obj).trim().length > 0;
}

export function asArray(data) {
  return !hasData(data) ? [] : Array.isArray(data) ? data : [data];
}

export function asSpreadArray(data) {
  return !hasData(data) ? [] : Array.isArray(data) ? data : [...data];
}

export function isObject(data) {
  return data instanceof Object && data.constructor === Object;
}

export function getEndpoint(str) {
  return str
    .split("/")
    .pop()
    .split("\\")
    .pop();
}

// returns a random int with "length" digits
export function randomIntOfLength(length) {
  let digits = rerunArray(length, randomInt, 0, 10);

  return Number(
    digits.reduce((acc, currentValue) => {
      return acc.concat(currentValue);
    }, "")
  );
}

export function randomInt(min = 0, max = 10) {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min)) + min;
}

// Function repeater. Outer fxn returns an array of the results.
export function rerunArray(count, cb, ...params) {
  let resultArray = [];
  rerun(count, cb);

  // APB - play
  function rerun(count, cb) {
    if (count) {
      resultArray.push(cb(...params));
      return rerun(--count, cb);
    }
  }
  return resultArray;
}

export function getCrumbs(uri) {
  if (uri === undefined) return ["No URI was passed to getCrumbs"];

  //return the positive array (falsy values excluded)
  return uri.split("/").filter(Boolean);
}
