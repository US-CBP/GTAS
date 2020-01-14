import React, { useState } from "react";
import PropTypes from "prop-types";

/**
 * **Checkbox and Radio input component.**
 */
const CheckboxInput = props => {
  const [selected, setSelected] = useState(props.selected);

  const onChange = () => {
    setSelected(!selected);
    const filterFormUpdate = {
      name: props.name,
      value: !selected
    };
    props.callback({target: filterFormUpdate});
  };
  const style = (props.className || "undefined").replace("undefined");
  const divstyle = style.replace("checkbox");

  return (
    <div className={divstyle}>
      {` ${props.label}`}
      <input
        name={props.name}
        onChange={onChange}
        className={style}
        type={props.inputType}
        value={props.inputVal}
        checked={selected}
      />
    </div>
  );
};

CheckboxInput.propTypes = {
  inputType: PropTypes.oneOf(["checkbox", "radio", "toggle"]),
  inputVal: PropTypes.any,
  name: PropTypes.string.isRequired,
  alt: PropTypes.string.isRequired,
  options: PropTypes.array.isRequired,
  required: PropTypes.string,
  selected: PropTypes.oneOf(["true", "", undefined]),
  callback: PropTypes.func.isRequired
};
export default CheckboxInput;
