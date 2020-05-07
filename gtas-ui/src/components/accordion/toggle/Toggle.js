import React, { useContext } from "react";
import AccordionContext from "react-bootstrap/AccordionContext";
import { Card, useAccordionToggle } from "react-bootstrap";
import "./Toggle.scss";

const AccordionToggler = props => {
  const currentEventKey = useContext(AccordionContext);
  const isCurrentEventKey = currentEventKey === props.eventKey;

  const toggleHandler = useAccordionToggle(props.eventKey, () => props.callback());

  return (
    <Card.Header block onClick={toggleHandler}>
      {props.children}

      <i
        className={`${
          isCurrentEventKey
            ? "fa fa-angle-double-down fa-lg"
            : "fa fa-angle-double-right fa-lg"
        } toggleIcon`}
      ></i>
    </Card.Header>
  );
};

export default AccordionToggler;
