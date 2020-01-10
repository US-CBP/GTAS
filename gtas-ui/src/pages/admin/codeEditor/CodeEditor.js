import React from "react";
import Table from "../../../components/table/Table";
import { employees } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import { Container } from "react-bootstrap";

const CodeEditor = ({ name }) => {
  const cb = function(result) {};

  return (
    <Container fluid>
      <Title title={name}></Title>

      <Table service={employees.get} id="foo" callback={cb}></Table>
    </Container>
  );
};

export default CodeEditor;
