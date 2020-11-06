import React from "react";
import Header from "../../components/header/Header";

const Home = props => (
  <div>
    <Header></Header>
    {props.children}
  </div>
);

export default Home;
