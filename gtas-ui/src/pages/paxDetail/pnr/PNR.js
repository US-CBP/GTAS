import React from "react";
import Table from "../../../components/table/Table";
import { hacks } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";

const PNR = () => {
  return (
    <div className="container">
      <Title title="PNR"></Title>

      <div className="columns">
        <div className="top">
          <Table service={hacks.get} id="foo"></Table>
        </div>
      </div>
    </div>
  );
};

export default PNR;
