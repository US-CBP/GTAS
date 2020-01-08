/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

import React, { useState } from "react";
import DatePicker from "react-datepicker";
import inputPasses from "../Inputs.css";

import "../../../../node_modules/react-datepicker/dist/react-datepicker.css";
import PropTypes from "prop-types";
import classes from "./LabelledDateTimePickerStartEnd.css";
import LabelledInputDisplayWrapper from "../LabelledInputDecorator";
import { Container, Row } from "react-bootstrap";
const REQUIRED = "required";

const LabelledDateTimePickerStartEnd = props => {
  const changeEta = event => {
    setStartDate(event);
    props.startMut(event);
    const update = {
      name: "etaStart",
      value: event?.toISOString()
    };
    props.callback(update);
  };

  const changeEtd = event => {
    setEndDate(event);
    props.endMut(event);
    const update = {
      name: "etaEnd",
      value: event?.toISOString()
    };
    props.callback(update);
  };
  const totalClasses = { ...classes, ...inputPasses };
  const [startDate, setStartDate] = useState(props.startDate);
  const [endDate, setEndDate] = useState(props.endDate);

  return (
    <Container style={totalClasses}>
      <div>
        <Row>
          <label>Start Date</label>
        </Row>
        <Row>
          <DatePicker
            selected={startDate}
            onChange={changeEta}
            selectsStart
            {...props}
            startDate={startDate}
            endDate={endDate}
          />
        </Row>
        <Row>
          <label>End Date</label>
        </Row>
        <Row>
          <DatePicker
            selected={endDate}
            onChange={changeEtd}
            selectsEnd
            {...props}
            startDate={startDate}
            endDate={endDate}
            minDate={startDate}
          />
        </Row>
      </div>
    </Container>
  );
};

LabelledDateTimePickerStartEnd.propTypes = {
  name: PropTypes.string.isRequired,
  alt: PropTypes.string.isRequired,
  labelText: PropTypes.string.isRequired,
  inputType: PropTypes.oneOf(["dateTime", "date"]).isRequired,
  callback: PropTypes.func.isRequired,
  inputVal: PropTypes.instanceOf(Date),
  startDate: PropTypes.instanceOf(Date),
  endDate: PropTypes.instanceOf(Date),
  placeholder: PropTypes.string,
  required: PropTypes.oneOf([REQUIRED, "", undefined]),
  isVisible: PropTypes.bool,
  readOnly: PropTypes.string,
  dateRange: PropTypes.object
};
export default LabelledInputDisplayWrapper(
  React.memo(LabelledDateTimePickerStartEnd, (oldProps, newProps) => {
    // True when an update should not happen.
    return oldProps === newProps || oldProps.children === newProps.children;
  })
);
