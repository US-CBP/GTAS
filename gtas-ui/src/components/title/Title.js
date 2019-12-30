import React from "react";
// import Breadcrumbs from "../breadcrumbs/Breadcrumbs";

// APB - Need to potentially pass in another component or text data from a peer. useEffect.
const Title = props => {
  const cb = () => {};

  return (
    <div>
      <div className="navbar column">
        <h2 className={`title ${props.className}`}>{props.title}</h2>
      </div>
      <div>{/* <Breadcrumbs uri={props.uri}></Breadcrumbs> */}</div>
    </div>
  );
};

export default Title;
