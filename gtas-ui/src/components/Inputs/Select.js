import React from 'react';
import PropTypes from 'prop-types';
import './Inputs.css';

function SelectInput(props){
  return (
    <div className="input-select">
      <select type="select" name={props.name} required={props.required} alt={props.alt} onChange={props.callback}
        className="form-input" value={props.selected} disabled={props.readOnly === 'readOnly' ? 'disabled' : ''}>
        <option value="">{props.placeholder}</option>
          {props.options.map((option, idx) => {
          return (
            <option key={idx} value={option}>{option} </option>
          );
        })}
      </select>
    </div>
    )
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
  readOnly: PropTypes.string,
};

export default SelectInput;