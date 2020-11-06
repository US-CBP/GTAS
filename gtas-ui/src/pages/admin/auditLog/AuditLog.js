import React from "react";
import Table from "../../../components/table/Table";
import { auditlog } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import { Container } from "react-bootstrap";

const AuditLog = ({ name }) => {
  const cb = function(result) {};

  const visibleCols = ["actionType", "status", "message", "user", "timestamp"];

  return (
    <Container>
      <Title title={name}></Title>

      <Table service={auditlog.get} id="foo" callback={cb} header={visibleCols}></Table>
    </Container>
  );
};

export default AuditLog;
