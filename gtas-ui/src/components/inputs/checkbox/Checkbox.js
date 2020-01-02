import React, { useState, useRef } from "./node_modules/react";
import PropTypes from "./node_modules/propTypes";

/**
 * **Checkbox and Radio input component.**
 */
const CheckboxInput = props => {
  const [selected, setSelected] = useState(!!props.selected);
  const inputRef = useRef(null);

  const onChange = ev => {
    props.callback(ev);
  };

  // Allow the checkbox/radio text to be clickable by capturing the click event and passing it to the input
  const onClick = ev => {
    ev.stopPropagation();
    setSelected(!selected);
    inputRef.current.click();
  };

  const style = (props.className || "undefined").replace("undefined");
  const divstyle = style.replace("checkbox");

  return (
    <div onClick={onClick} className={divstyle}>
      <input
        ref={inputRef}
        name={props.name}
        onChange={onChange}
        className={style}
        type={props.inputType}
        value={props.inputVal}
        checked={selected}
      ></input>
      {` ${props.inputVal}`}
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
