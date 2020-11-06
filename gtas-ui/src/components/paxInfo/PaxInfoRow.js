import React from "react";
import { Col, Form, Row } from "react-bootstrap";
import "./PaxInfo.css";

const PaxInfoRow = props => {
  return (
    <Row>
      <Form.Label column sm="5" size="sm" className="left-label">
        {props.leftlabel}
      </Form.Label>
      <Col>
        <Form.Control
          plaintext
          readOnly
          defaultValue={props.rightlabel}
          className="right-label"
        />
      </Col>
    </Row>
  );
};

export default PaxInfoRow;
