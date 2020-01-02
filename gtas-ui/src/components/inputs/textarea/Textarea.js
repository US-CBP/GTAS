import React from "./node_modules/react";
import { PropTypes } from "./node_modules/prop-types";
import "../Inputs.css";

const TextareaInput = props => (
  <textarea
    className="form-input textarea"
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

TextareaInput.propTypes = {
  inputType: PropTypes.oneOf(["textarea"]).isRequired,
  name: PropTypes.string.isRequired,
  alt: PropTypes.string.isRequired,
  callback: PropTypes.func.isRequired,
  inputVal: PropTypes.string,
  required: PropTypes.string,
  placeHolder: PropTypes.string,
  readOnly: PropTypes.string
};

export default TextareaInput;
