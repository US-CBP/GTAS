import React from "react";
import Table from "../../components/table/Table";
import { users } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import { Container } from "react-bootstrap";

const Dashboard = () => {
  return (
    <div>
      <Container>
        <Title title="Dashboard"></Title>
      </Container>
      <Container fluid className="box2">
        <Table service={users.get} id="foo"></Table>
      </Container>
    </div>
  );
};

export default Dashboard;
