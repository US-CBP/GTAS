import React from "react";
import Table from "../../../components/table/Table";
import { users } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import { Container } from "react-bootstrap";

const CodeEditor = ({ name }) => {
  const cb = function(result) {};

  return (
    <Container fluid>
      <Title title={name}></Title>

      <Table id="foo" callback={cb}></Table>
    </Container>
  );
};

export default CodeEditor;
