import React from "react";
// import Breadcrumbs from "../breadcrumbs/Breadcrumbs";

const Title = (props) => {
  return (
    <div>
      <div className="navbar column">
        <h2 className="title">{props.title}</h2>
      </div>
      <div>
        {/* <Breadcrumbs uri={props.uri}></Breadcrumbs> */}
      </div>
    </div>
  );
};

export default Title;
