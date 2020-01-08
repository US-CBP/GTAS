import React from "react";
import Table from "../../../components/table/Table";
import { files } from "../../../services/serviceWrapper";
import { Row, Container } from "react-bootstrap";

const Summary = props => {
  return (
    <Container fluid>
      <Row flex>
        <div className="columns">
          <div className="top">
            <Table service={files.get} id="foo"></Table>
          </div>
        </div>
      </Row>
    </Container>
  );
};

export default Summary;
