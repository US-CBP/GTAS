import React from "react";
import Table from "../../../components/table/Table";
import { users } from "../../../services/serviceWrapper";
import { CardDeck, Card } from "react-bootstrap";
import Accordion from "../../../components/accordion/Accordion";
import "./Summary.scss";

const Summary = props => {
  const accordionData = [
    {
      header: "Event Note History",
      body: "Add some event note history data here"
    },
    {
      header: "Previous Note History",
      body: "Add previous note history here"
    }
  ];

  return (
    <>
      <CardDeck className="summary-card-deck">
        <Card>
          <Card.Header>Passenger Current Hits Summary</Card.Header>
          <Card.Body>
            <Card.Text>Passenger current hit summary goes in her</Card.Text>
          </Card.Body>
        </Card>
        <Card>
          <Card.Header>Documents</Card.Header>
          <Card.Body>
            <Card.Text>PDocumnet data goes in here</Card.Text>
          </Card.Body>
        </Card>
        <Card>
          <Card.Header>Watchlist Name Comparison</Card.Header>
          <Card.Body>
            <Card.Text>Watchlist name comparison goes in here</Card.Text>
          </Card.Body>
        </Card>
        <Card>
          <Card.Header>Event Notes</Card.Header>
          <Card.Body>
            <Card.Text>Event Notes goes in here</Card.Text>
          </Card.Body>
        </Card>
      </CardDeck>

      <Accordion data={accordionData} />
    </>
  );
};

export default Summary;
