/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

import React from "./node_modules/react";
import Select from "./node_modules/react-select";
import inputPasses from "../Inputs.css";
import { Row } from "./node_modules/react-bootstrap";

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
    <div className={`field ${props.visibleStyle}`} style={finalStyles}>
      <Row className="control" style={finalStyles}>
        <label>{props.labelText}</label>
      </Row>
      <Row style={finalStyles}>
        <div style={finalStyles}>
          <Select style={finalStyles} {...props} onChange={onChange} />
        </div>
      </Row>
    </div>
  );
};

export default LabelledSelectInput;
