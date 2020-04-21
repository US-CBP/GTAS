import React from "react";
import Table from "../../../components/table/Table";
import { userService, users } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import Xl8 from "../../../components/xl8/Xl8";
import { Container, Button } from "react-bootstrap";

const ManageUsers = ({ name }) => {
  const cb = function(result) {};
  return (
    <Xl8>
      <Container fluid>
        <Title title={name}></Title>
        <Table
          service={users.get}
          id="users"
          callback={cb}
          ignoredFields={["roles", "password"]}
        ></Table>
      </Container>
    </Xl8>
  );
};

export default ManageUsers;
