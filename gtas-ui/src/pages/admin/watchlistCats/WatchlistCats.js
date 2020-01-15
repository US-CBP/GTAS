import React, { useState } from "react";
import Table from "../../../components/table/Table";
import { watchlistcats } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import { Container, Button, Row, Col, Modal } from "react-bootstrap";
import WatchlistModal from "./WatchlistModal";

const WatchlistCats = ({ name }) => {
  const cb = function(result) {};
  const [showModal, setShowModal] = useState(false);
  return (
    <Container fluid>
      <Row>
        <Col sm={4}>
          <Title title={name}></Title>
        </Col>
        <Col sm={{ span: 4, offset: 4 }}>
          <Button variant="outline-dark" onClick={() => setShowModal(true)}>
            Add to Watchlist{" "}
          </Button>
          <WatchlistModal show={showModal} onHide={() => setShowModal(false)} />
        </Col>
      </Row>

      <Table service={watchlistcats.get} id="Watchlist Catagory" callback={cb}></Table>
    </Container>
  );
};

export default WatchlistCats;
