import React from "react";
import Table from "../../../components/table/Table";
import { errorlog } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import { Container } from "react-bootstrap";

const ErrorLog = ({ name }) => {
  const cb = function(result) {};

  const visibleCols = ["errorId", "errorCode", "errorDescription", "errorTimestamp"];

  return (
    <Container fluid>
      <Title title={name}></Title>

      <Table
        service={errorlog.get}
        id="errorLog"
        callback={cb}
        header={visibleCols}
      ></Table>
    </Container>
  );
};

export default ErrorLog;
