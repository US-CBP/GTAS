import React from "react";
import { Col, Row, Form } from "react-bootstrap";
import PaxInfoRow from "../paxInfo/PaxInfoRow";

const FlightBadge = props => {
  return (
    <Form>
      <Row>
        <Col lg="3" md="3" sm="3">
          <div className="fa fa-plane"> {props.flightnumber}</div>
        </Col>
        <Col>
          <PaxInfoRow
            leftlabel={<i className="fa fa-arrow-circle-up"> {props.origin}</i>}
            rightlabel={props.etd}
          ></PaxInfoRow>
          <PaxInfoRow
            leftlabel={<i className="fa fa-arrow-circle-down"> {props.destination}</i>}
            rightlabel={props.eta}
          ></PaxInfoRow>
        </Col>
      </Row>
    </Form>
  );
};

export default FlightBadge;
