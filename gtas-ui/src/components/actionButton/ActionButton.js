import React from "react";
import { Link } from "@reach/router";

const ActionButton = props => {
  return (
    <nav className="breadcrumb is-centered" aria-label="breadcrumbs">
      {props.children}
    </nav>
  );
};

export default ActionButton;
