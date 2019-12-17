import React from "react";
import { Badge } from "react-bootstrap"
// import Breadcrumbs from "../breadcrumbs/Breadcrumbs";

const Title = (props) => {
  return (
    <h1><Badge>{props.title}</Badge></h1>
  );
};

export default Title;
