import React from "react";
import PropTypes from "prop-types";

//APB - Broken in .map
//APB - validation
//APB pass invalid state up to parent

const CheckboxInput = props => {
  return (
    <div className="input-group">
      {props.options.map(item => {
        return (
          <label key={item} className="form-label">
            <input
              className="form-checkbox"
              onChange={props.callback}
              type={props.inputType}
              defaultValue={item}
            >
              {" "}
              {item}
            </input>
          </label>
        );
      })}
    </div>
  );
};

CheckboxInput.propTypes = {
  inputType: PropTypes.oneOf(["checkbox", "radio"]),
  name: PropTypes.string.isRequired,
  alt: PropTypes.string.isRequired,
  options: PropTypes.array.isRequired,
  required: PropTypes.string,
  selected: PropTypes.array,
  callback: PropTypes.func.isRequired
};
export default CheckboxInput;
