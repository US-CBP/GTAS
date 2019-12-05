import PropTypes from "prop-types";
import HttpStatus from "../utils/HttpStatus";
import { hasData } from "../utils/text";

//APB Axios or just Fetch??? Keep it simple?
// APB - need mock endpoint for testing

async function GenericService(props) {
  console.log(props.uri);

  let param = {
    method: props.method,
    headers: {}
  };

  if (hasData(props.body)) {
    param.body = JSON.stringify(props.body);
    param.headers = {
      "Content-Type": props.contentType || "application/json;charset=UTF-8",
      Accept: props.contentType || "application/json",
      "X-CSRF-TOKEN": "null",
      "Access-Control-Allow-Origin": "urigoeshere",
      Vary: "Origin",
      "Access-Control-Allow-Headers": "Origin, Content-Type, X-Auth-Token",
      "X-Requested-With": "XMLHttpRequest",
      Connection: "keep-alive",
      "Accept-Encoding": "gzip, deflate",
      Cookie: "NG_TRANSLATE_LANG_KEY=en; JSESSIONID=sessionidgoeshere; myLocaleCookie=en"
    };
  }

  if (hasData(props.mode)) {
    param.headers.mode = props.mode;
  }

  return fetch(props.uri, param)
    .then(response => {
      if (response === undefined) {
        return [];
      }
      if (response.ok) {
        const result = response.json() || [];
        return result;
      } else {
        console.log("response", response);
        console.log("some error");
        const err = HttpStatus(response.status);
        const newErr = new Error();

        if (hasData(err)) {
          newErr.name = `${err.code}: ${err.phrase}`;
          newErr.message = err.description;
          newErr.spec_href = err.spec_href;
        } else {
          newErr.name = "Http status code mismatch";
          newErr.message = "The transaction failed, error code was not found";
          newErr.spec_href = "";
        }
        throw newErr;
      }
    })
    .catch(error => {
      console.log(error);
    });
}

GenericService.propTypes = {
  uri: PropTypes.string.isRequired,
  method: PropTypes.oneOf(["get", "delete", "post", "put"]).isRequired,
  body: PropTypes.object,
  contentType: PropTypes.string,
  mode: PropTypes.string
};

export default GenericService;
