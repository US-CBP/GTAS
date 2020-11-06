import React, { useState } from "react";
import PropTypes from "prop-types";

import "../Inputs.css";

const SelectInput = props => {
  const [selected, setSelected] = useState(props.selected);

  const onChange = ev => {
    setSelected(ev.target.value);
    props.callback(ev);
  };

  return (
    <select
      className={`input-select ${props.className || ""}`}
      type="select"
      name={props.name}
      required={props.required}
      alt={props.alt}
      onChange={onChange}
      className="form-input"
      value={selected}
      disabled={props.readOnly === "readOnly" ? "disabled" : ""}
    >
      <option value="">{props.placeholder}</option>
      {props.options.map(option => {
        return (
          <option key={option.value} value={option.value}>
            {option.label}{" "}
          </option>
        );
      })}
    </select>
  );
};

//APB pass invalid state up to parent
//Validation
//            <option key={idx} value={option} selected={props.selected === option}>{option} </option>

SelectInput.propTypes = {
  name: PropTypes.string.isRequired,
  options: PropTypes.array.isRequired,
  selected: PropTypes.string,
  callback: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  readOnly: PropTypes.string
};

export default SelectInput;
