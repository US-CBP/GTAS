// import PropTypes from "prop-types";
import { hasData } from "../utils/utils";
import Cookies from "js-cookie";

const GenericService = async props => {
  let param = {
    method: props.method,
    headers: {
      ...props.headers,
      Cookie: `JSESSIONID: ${Cookies.get("JSESSIONID")}`
    },
    credentials: "include"
  };

  // if (hasData(props.body) && !(props.body instanceof FormData)) {
  param.body = props.body;
  // }

  return fetch(props.uri, param)
    .then(response => {
      if (response === undefined) {
        return [];
      }
      if (response.ok) {
        if (response.url.endsWith("/authenticate")) return response;
        return response.json().then(res => res.data || res || response);
      } else {
        return response;
      }
    })
    .catch(error => {
      return error;
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
