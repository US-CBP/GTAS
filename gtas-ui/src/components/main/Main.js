import React from "react";
import "./Main.css";

const Main = props => {
  const style = props.style || "main";
  return <div className={style}>{props.children}</div>;
};

export default Main;
