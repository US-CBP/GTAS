/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

import React from "react";
import { Row } from "react-bootstrap";
import PropTypes from "prop-types";
const LabelledInputDisplayWrapper = Component => props => {
  return (
    <div>
      <Row className="control">
        <label>{props.labelText}</label>
      </Row>
      <Row>
        <Component {...props} />
      </Row>
    </div>
  );
};

LabelledInputDisplayWrapper.propTypes = {
  labelText: PropTypes.string.isRequired
};

export default LabelledInputDisplayWrapper;
