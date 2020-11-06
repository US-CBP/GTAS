import React from "react";
import "./Title.css";
// import Breadcrumbs from "../breadcrumbs/Breadcrumbs";

// APB - Need to potentially pass in another component or text data from a peer. useEffect.
const Title = props => {
  const cb = () => {};

  return <div className="title">{props.title}</div>;
};

export default Title;
