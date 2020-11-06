import React, { useState } from "react";
import { Col } from "react-bootstrap";

import "./SideNav.scss";

const SideNav = props => {
  const [open, setOpen] = useState(false);

  const toggleOpen = val => {
    setOpen(val);
  };

  return (
    <div className={`"filter-container" ${props.className}`}>
      <div
        className={`${open ? "main-overlay-open" : "main-overlay"}`}
        onClick={() => toggleOpen(false)}
      ></div>
      <div className="filter-toggle" onClick={() => toggleOpen(!open)}>
        <div className="text-center margintop20">
          <i className={`${open ? "arrow-hide" : "arrow-open"} fa fa-2x arrow`}></i>
        </div>
      </div>
      <div className={`${open ? "filter-open" : "filter"} filter-bg`}></div>
      <div className={`${open ? "filter-open" : "filter"} filter-screen`}></div>
      <div className={`${open ? "filter-open filter-fg-open" : "filter filter-fg"}`}>
        <Col>{props.children}</Col>
        <br></br>
      </div>
    </div>
  );
};

export default SideNav;
