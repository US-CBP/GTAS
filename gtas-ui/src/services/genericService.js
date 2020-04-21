// import PropTypes from "prop-types";
import { hasData } from "../utils/utils";
import Cookies from "js-cookie";

const GenericService = async props => {
  let param = {
    method: props.method,
    headers: {
      ...props.headers,
      Cookie: `NG_TRANSLATE_LANG_KEY=en; JSESSIONID= ${Cookies.get(
        "JSESSIONID"
      )}; myLocaleCookie=en`
    },
    credentials: "include"
  };

  if (hasData(props.body) && !(props.body instanceof FormData)) {
    param.body = JSON.stringify({ ...props.body });
  }

  return fetch(props.uri, param)
    .then(response => {
      if (response === undefined) {
        return [];
      }
      if (response.ok) {
        return response.json().then(res => res.data || res || []);
      } else {
        const newErr = new Error();

        newErr.name = "Http status code mismatch";
        newErr.message = "The transaction failed, error code was not found";
        newErr.spec_href = "";
        // console.log(newErr);
      }
    })
    .catch(error => {
      const newErr = new Error();
      newErr.name = "Connection Error";
      newErr.message = "Connection Error";
      newErr.spec_href = "";
      // console.log(error);
    });
};

// GenericService.propTypes = {
//   uri: PropTypes.string.isRequired,
//   method: PropTypes.oneOf(['get', 'delete', 'post', 'put']).isRequired,
//   body: PropTypes.object,
//   contentTypeReceive: PropTypes.string,
//   mode: PropTypes.string,
//   headers: PropTypes.object,
//   contentTypeServer: PropTypes.string
// };

export default GenericService;
