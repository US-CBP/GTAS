/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

import React from "react";
import Select from "react-select";
import inputPasses from "../Inputs.css";
import LabelledInputDecorator from "../LabelledInputDecorator";

const LabelledSelectInput = props => {
  let minWidth = { "min-width": "100%" };
  const finalStyles = { ...minWidth, ...inputPasses };

  const onChange = event => {
    let returnValue;
    if (props.ReturnStringArray && event !== null) {
      returnValue = event.map(e => e.value);
    } else if (props.ReturnString && event !== null) {
      returnValue = event.value;
    } else {
      returnValue = event;
    }

    const update = {
      name: props.name,
      value: returnValue
    };
    props.callback(update);
  };

  return (
    <div style={finalStyles}>
      <Select style={finalStyles} {...props} onChange={onChange} />
    </div>
  );
};

export default LabelledInputDecorator(LabelledSelectInput);
