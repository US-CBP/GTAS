import React from "react";
import { Badge, Container } from "react-bootstrap"
// import Breadcrumbs from "../breadcrumbs/Breadcrumbs";

// APB - Need to potentially pass in another component or text data from a peer. useEffect.
const Title = props => {
  const cb = () => {};

  return (
    <Container><h1><Badge>{props.title}</Badge></h1></Container>
  );
};

export default Title;
