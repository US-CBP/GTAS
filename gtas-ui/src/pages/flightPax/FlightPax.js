import React from "react";
import Table from "../../components/table/Table";
import { passengers } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";
import { Container } from "react-bootstrap";

const FlightPax = () => {
  const cb = function(result) {};

  return (
    <Container fluid>
      <div className="box2">
        <Title title="Flight Passengers"></Title>
        <Link to="/gtas/paxdetail">Passenger Details</Link>
        <Table service={passengers.get} id="foo" callback={cb}></Table>
      </div>
    </Container>
  );
};

export default FlightPax;
