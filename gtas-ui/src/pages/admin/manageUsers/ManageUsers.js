import React from "react";
import Table from "../../../components/table/Table";
import { hacks } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import Xl8 from "../../../components/xl8/Xl8";
import { Container } from "react-bootstrap";

const ManageUsers = ({ name }) => {
  const cb = function(result) {};

  return (
    <Xl8>
      <Container fluid>
        <Title title={name}></Title>

        <Table service={hacks.get} id="foo" callback={cb}></Table>
      </Container>
    </Xl8>
  );
};

export default ManageUsers;
