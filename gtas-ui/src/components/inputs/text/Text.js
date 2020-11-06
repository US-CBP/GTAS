import React from "react";
import PropTypes from "prop-types";

import "../Inputs.css";

const TextInput = props => (
  <input
    className={`form-input ${props.className || ""}`}
    name={props.name}
    type={props.inputType}
    placeholder={props.placeholder}
    onChange={props.callback}
    required={props.required}
    aria-required={props.required}
    value={props.inputVal}
    alt={props.alt}
    readOnly={props.readOnly}
  />
);

TextInput.propTypes = {
  inputType: PropTypes.oneOf(["text", "number", "email", "password", "search", "tel"])
    .isRequired,
  name: PropTypes.string.isRequired,
  alt: PropTypes.string.isRequired,
  callback: PropTypes.func.isRequired,
  inputVal: PropTypes.string.isRequired,
  required: PropTypes.string,
  placeHolder: PropTypes.string,
  readOnly: PropTypes.string
};

export default TextInput;
