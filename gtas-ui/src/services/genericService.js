import PropTypes from "prop-types";
import { hasData } from "../utils/text";
import Cookies from "js-cookie";

async function GenericService(props) {
  let headerObject = props.headers ? { ...props.headers } : {};
  let defaultParam = {
    method: props.method,
    headers: headerObject,
    credentials: "include"
  };

  let paramObject = props.param ? { ...props.param } : {};
  let param = { ...paramObject, ...defaultParam };

  if (hasData(props.body)) {
    param.body = props.body;
  }
  let defaultHeader = {
    "Content-Type": props.contentTypeServer || "application/json;charset=UTF-8",
    Accept: props.contentTypeReceive || "application/json",
    "X-Requested-With": "XMLHttpRequest",
    Connection: "keep-alive",
    "Accept-Encoding": "gzip, deflate",
    Cookie:
      "NG_TRANSLATE_LANG_KEY=en; JSESSIONID=" +
      Cookies.get("JSESSIONID") +
      "; myLocaleCookie=en"
  };
  param.headers = { ...props.headers, ...defaultHeader };

  //TODO - ensure we disable x-powered-by

  if (hasData(props.mode)) {
    param.headers.mode = props.mode;
  }

  return fetch(props.uri, param)
    .then(response => {
      if (response === undefined) {
        return [];
      }
      if (response.status === 401) {
        return { authenticated: false };
      }
      if (response.ok) {
        //todo: MAKE URLS CONFIGURABLE
        if (response.url === "http://localhost:8080/gtas/authenticate") {
          const responseText = response.text();
          return {
            authenticated: true,
            responseText: responseText
          };
        } else {
          return response.json() || [];
        }
      } else {
        const newErr = new Error();

        newErr.name = "Http status code mismatch";
        newErr.message = "The transaction failed, error code was not found";
        newErr.spec_href = "";
        console.log(newErr);
        // throw newErr;
      }
    })
    .catch(error => {
      const newErr = new Error();
      newErr.name = "Connection Error";
      newErr.message = "Connection Error";
      newErr.spec_href = "";
      console.log(error);
      // throw newErr;
    });
}

GenericService.propTypes = {
  uri: PropTypes.string.isRequired,
  method: PropTypes.oneOf(["get", "delete", "post", "put"]).isRequired,
  body: PropTypes.object,
  contentTypeReceive: PropTypes.string,
  mode: PropTypes.string,
  headers: PropTypes.object,
  contentTypeServer: PropTypes.string
};

export default GenericService;
