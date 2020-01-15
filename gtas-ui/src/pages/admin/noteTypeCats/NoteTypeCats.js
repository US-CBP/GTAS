import React from "react";
import Table from "../../../components/table/Table";
import { notetypes } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import { Container } from "react-bootstrap";

const NoteTypeCats = ({ name }) => {
  const cb = function(result) {};

  return (
    <Container fluid>
      <Title title={name}></Title>
      <Table service={notetypes.get} id="noteTypes" callback={cb}></Table>
    </Container>
  );
};

export default NoteTypeCats;
