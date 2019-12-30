import React from "react";
import { Badge } from "react-bootstrap";
// import Breadcrumbs from "../breadcrumbs/Breadcrumbs";

// APB - Need to potentially pass in another component or text data from a peer. useEffect.
const Title = props => {
  const cb = () => {};

  return (
    <h1>
      <Badge>{props.title}</Badge>
    </h1>
  );
};

export default Title;
