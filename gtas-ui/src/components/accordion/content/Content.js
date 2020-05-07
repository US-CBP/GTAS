import React from "react";
import { Card, Accordion as RBAccordion } from "react-bootstrap";
import Toggle from "../toggle/Toggle";

const AccordionContent = props => {
  const cb = () => {};

  return (
    <Card className="accordion-card" key={props.header}>
      <Toggle eventKey={props.header} callback={cb}>
        {props.header}
      </Toggle>
      <RBAccordion.Collapse eventKey={props.header}>
        <Card.Body>{props.body}</Card.Body>
      </RBAccordion.Collapse>
    </Card>
  );
};

export default AccordionContent;
