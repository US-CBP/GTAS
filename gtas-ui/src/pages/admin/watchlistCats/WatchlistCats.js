import React from "react";
import Table from "../../../components/table/Table";
import { watchlistcats } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import { Container } from "react-bootstrap";

const WatchlistCats = ({ name }) => {
  const cb = function(result) {};

  return (
    <Container fluid>
      <Title title={name}></Title>

      <Table service={watchlistcats.get} id="foo" callback={cb}></Table>
    </Container>
  );
};

export default WatchlistCats;
